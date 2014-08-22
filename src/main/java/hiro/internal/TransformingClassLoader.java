package hiro.internal;

import hiro.Resource;

import java.io.InputStream;
import java.nio.charset.Charset;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import javax.inject.Inject;

import com.google.common.io.Files;

public class TransformingClassLoader extends ClassLoader {
	private ClassPool pool = ClassPool.getDefault();

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> existingClass = findLoadedClass(name);
		if (existingClass != null) {
			return existingClass;
		}
		if (name.startsWith("java.") || name.startsWith("hiro")) {
			// Only bootstrap class loader can define classes in java.*
			return super.loadClass(name, resolve);
		}
		try {
			// System.out.println("Loading " + name);
			CtClass ctClass = pool.get(name);
			StringBuilder b = new StringBuilder();
			b.append("{hiro.Injector injector = hiro.internal.InjectorManager.getInjector();");

			for (CtField field : ctClass.getDeclaredFields()) {
				if (field.hasAnnotation(Inject.class)) {
					if (field.getType().getName().equals("hiro.Injector")) {
						b.append("this." + field.getName() + " = injector;");
					} else {

						b.append("this." + field.getName() + " = (" + field.getType().getName() + ")injector.get("
								+ field.getType().getName() + ".class);");
					}
				}
				if (field.hasAnnotation(Resource.class)) {
					if (field.getType().getName().equals(InputStream.class.getName())) {
						b.append("this." + field.getName() + " = " + ctClass.getName() + ".class.getResourceAsStream(\""
								+ ((Resource) field.getAnnotation(Resource.class)).value() + "\");");
					}
					else if(field.getType().getName().equals(String.class.getName())) {
						
						b.append("this." + field.getName() + " = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(" + ctClass.getName() + ".class.getResource(\""
								+ ((Resource) field.getAnnotation(Resource.class)).value() + "\").toURI())));");
					}
				}

			}
			b.append("}");

			//System.out.println(b.toString());

			ctClass.getConstructors()[0].insertBefore(b.toString());
			byte[] bytecode = ctClass.toBytecode();
			// System.out.println("Finished " + name);
			return defineClass(name, bytecode, 0, bytecode.length);
		} catch (Exception e) {
			throw new ClassNotFoundException("Could not transform class", e);
		}
	}
}
