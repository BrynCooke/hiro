package hiro;

import java.lang.annotation.Annotation;

public interface Injector {

	
	
	<T> T get(TypeLiteral<T> type);
	<T> T get(Class<T> type);
	
	<T, S extends Scope> T get(TypeLiteral<T> type, Class<S> scope);
	<T, S extends Scope> T get(Class<T> type, Class<S> scope);
	
	
	public <T extends Annotation> void enterScope(Scope test);
	

}
