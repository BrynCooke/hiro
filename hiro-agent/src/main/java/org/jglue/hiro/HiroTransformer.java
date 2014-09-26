package org.jglue.hiro;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.NotFoundException;

import javax.inject.Inject;

public class HiroTransformer implements ClassFileTransformer {
	private ClassPool pool = ClassPool.getDefault();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {

			CtClass ctClass = pool.get(className);
			List<CtField> fieldsNeedingInjecting = new ArrayList<>();
			for (CtField field : ctClass.getDeclaredFields()) {
				if (field.hasAnnotation(Inject.class)) {
					fieldsNeedingInjecting.add(field);
				}
			}
			if (fieldsNeedingInjecting.size() > 0) {

				String injectionSource = "org.jglue.hiro.Injector injector = org.jglue.hiro.Scope.currentInjector();";
				for (CtField field : fieldsNeedingInjecting) {
					injectionSource += "this." + field.getName() + " = (" + field.getType().getName() + ") injector.get("
							+ field.getType().getName() + ".class);";
				}

				for (CtConstructor constructor : ctClass.getConstructors()) {
					if (constructor.callsSuper()) {
						constructor.insertBefore(injectionSource);

					}
				}
				return ctClass.toBytecode();
			}

		} catch (NotFoundException | CannotCompileException | IOException e) {
			throw new RuntimeException(e);
		}
		return classfileBuffer;
		//
		// try {
		//
		// // // Look for all scope classes that are annotated with the
		// // // scope
		// // // annotation matching the scope. Get them from the
		// // // injector.
		// //
		// // // Check for ambiguous providers
		// //
		// // // Create the scope definition
		// System.out.println(className);
		// pool.insertClassPath(new ByteArrayClassPath(className,
		// classfileBuffer));
		// CtClass cc = pool.get(className);
		//
		// CtClass scopeClass = pool.getCtClass("org.jglue.hiro.Scope");
		// if(!cc.subclassOf(scopeClass)) {
		// return classfileBuffer;
		// }
		//
		//
		//
		// // CtClass injectorInterface =
		// // pool.getCtClass("org.jglue.hiro.Injector");
		// // CtClass scopeClass = pool.getCtClass("org.jglue.hiro.Scope");
		// //
		// // CtClass injectorClass = scopeClass.makeNestedClass("Injector",
		// // true);
		// //
		// // injectorClass.addInterface(injectorInterface);
		// // injectorClass.addField(CtField.make("private " +
		// // Scope.this.getClass().getName() + " scope;", injectorClass));
		// //
		// //
		// injectorClass.addConstructor(CtNewConstructor.make("public Injector("
		// // + Scope.this.getClass().getName()
		// // +
		// //
		// " scope) {this.scope = scope;java.lang.System.out.println(scope.a);}",
		// // injectorClass));
		// //
		// // return injectorClass.toBytecode();
		//
		// System.out.println("Adding method");
		// cc.addMethod(CtMethod.make("Object get(java.lang.Class type) {return null;}",
		// cc));
		//
		// return cc.toBytecode();
		// } catch (NotFoundException | IOException | CannotCompileException e)
		// {
		// throw new RuntimeException(e);
		// }
	}
}
