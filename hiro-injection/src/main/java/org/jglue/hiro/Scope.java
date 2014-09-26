package org.jglue.hiro;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.function.Supplier;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

import org.jglue.hiro.internal.AbstractInjector;

import com.sun.tools.attach.VirtualMachine;

public abstract class Scope {

	static {

		String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
		int p = nameOfRunningVM.indexOf('@');
		String pid = nameOfRunningVM.substring(0, p);

		File f = new File("/home/bryn/work/jglue/hiro/hiro-agent/target/hiro-agent-0.0.1-SNAPSHOT.jar");

		try {
			VirtualMachine vm = VirtualMachine.attach(pid);
			vm.loadAgent(f.getAbsolutePath());
			vm.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	static {

	}

	private Method defineClassMethod = getDeclareMethod();

	private static Method getDeclareMethod() {
		try {
			Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class,
					byte[].class, int.class,

					int.class, ProtectionDomain.class });
			defineClassMethod.setAccessible(true);
			return defineClassMethod;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private ClassPool pool = ClassPool.getDefault();

	private Class<? extends Injector> getInjectorClass() {
		try {
			return (Class<? extends Injector>) Class.forName(getClass().getName() + "$Injector", false, getClass()
					.getClassLoader());
		} catch (ClassNotFoundException notFound) {
			synchronized (getClass()) {
				try {
					return (Class<? extends Injector>) Class.forName(getClass().getName() + "$Injector", false, getClass()
							.getClassLoader());
				} catch (ClassNotFoundException stillNotFound) {
					try {
						CtClass scopeClass = pool.get(getClass().getName());
						CtClass abstractInjectorClass = pool.get(AbstractInjector.class.getName());
						CtClass injectorClass = scopeClass.makeNestedClass("Injector", true);
						injectorClass.setSuperclass(abstractInjectorClass);
						CtClass injectorInterface = pool.get(Injector.class.getName());

						injectorClass.addInterface(injectorInterface);
						injectorClass.addField(new CtField(scopeClass, "scope", injectorClass));
						injectorClass.addConstructor(CtNewConstructor.make("public Injector(org.jglue.hiro.Injector parent, "
								+ scopeClass.getName() + " scope){super(parent);this.scope = scope;}", injectorClass));

						String getSource = "public java.lang.Object get(java.lang.Class c) {Object parentResult = getParent() != null ? getParent().get(c) : null; if(parentResult != null) {return parentResult;}";
						for (CtField f : scopeClass.getFields()) {
							if (f.hasAnnotation(Produces.class)) {
								getSource += "if(" + f.getType().getName() + ".class.isAssignableFrom(c)) {return this.scope."
										+ f.getName() + ";}";
							}
						}
						for (CtMethod m : scopeClass.getMethods()) {
							if (m.hasAnnotation(Produces.class)) {
								getSource += "if(" + m.getReturnType().getName()
										+ ".class.isAssignableFrom(c)) {return this.scope." + m.getName() + "();}";
							}
						}
						getSource += "return null;}";

						injectorClass.addMethod(CtNewMethod.make(getSource, injectorClass));
						byte[] bytecode = injectorClass.toBytecode();
						defineClassMethod.invoke(getClass().getClassLoader(), injectorClass.getName(), bytecode, 0,
								bytecode.length, getClass().getProtectionDomain());
						return (Class<? extends Injector>) Class.forName(injectorClass.getName(), false, getClass()
								.getClassLoader());

					} catch (Exception e) {

						throw new RuntimeException(e);
					}
				}

			}
		}

	}

	public void execute(Runnable runnable) {
		Injector previousInjector = current.get();
		try {
			try {
				Class<? extends Injector> injectorClass = getInjectorClass();
				Injector injector = injectorClass.getConstructor(Injector.class, getClass()).newInstance(previousInjector, this);
				current.set(injector);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			runnable.run();
		} finally {
			current.set(previousInjector);
		}
	}

	public <T> T execute(Supplier<T> supplier) {
		return supplier.get();
	}

	private static ThreadLocal<Injector> current = new ThreadLocal<>();

	public static Injector currentInjector() {
		return current.get();
	}

}
