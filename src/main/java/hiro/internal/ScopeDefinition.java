package hiro.internal;

import java.lang.reflect.Method;

import hiro.Produces;
import hiro.Scope;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class ScopeDefinition {
	ClassPool pool = ClassPool.getDefault();

	public ScopeDefinition(Class<? extends Scope> scopeClass) {
		CtClass cc = pool.makeClass(scopeClass.getName() + "$Injection");
		Method[] methods = scopeClass.getDeclaredMethods();
		for(Method method : methods) {
			if(method.isAnnotationPresent(Produces.class)) {
				Class<?> returnType = method.getReturnType();
				CtNewMethod.make("", cc);
				
			}
		}
		
	}

	public <T> T get(Class<T> a) {
		// TODO Auto-generated method stub
		return null;
	}

}
