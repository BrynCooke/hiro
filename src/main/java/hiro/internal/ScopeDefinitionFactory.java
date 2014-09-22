package hiro.internal;

import hiro.Injector;
import hiro.Produces;
import hiro.Scope;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class ScopeDefinitionFactory {
	ClassPool pool = ClassPool.getDefault();

	ScopeDefinition create(Scope scope, Injector injector) {
		try {

			// Look for all scope classes that are annotated with the scope
			// annotation matching the scope. Get them from the injector.
			List<Object> scopeObjects = Arrays.asList(scope);

			// Check for ambiguous providers

			// Create the scope definition

			CtClass cc = pool.makeClass(scope.getClass().getName() + "$Definition");
			cc.addInterface(pool.getCtClass(ScopeDefinition.class.getName()));

			StringBuilder builder = new StringBuilder("public void get(Injector injector, TypeLiteral type, "
					+ scopeObjects.stream().map(o -> {
						return o.getClass().getName() + " " + paramName(o);
					}).collect(Collectors.joining(",")) + ") {");

			for (Object o : scopeObjects) {
				Method[] methods = o.getClass().getDeclaredMethods();

				for (Method method : methods) {
					if (method.isAnnotationPresent(Produces.class)) {

						builder.append("if(type.equals(new TypeLiteral<" + method.getGenericReturnType().getTypeName()
								+ ">())){return " + paramName(o) + "()}");
					}
				}
			}

			builder.append("}");
			CtMethod method = CtNewMethod.make(builder.toString(), cc);

			
			
		} catch (Exception e) {

		}
	}

	private String paramName(Object o) {
		return o.getClass().getName().replace(".", "_");
	}
}
