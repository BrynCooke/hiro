package org.jglue.hiro;


public interface Injector {

	<T> T get(TypeLiteral<T> type);

	<T> T get(Class<T> type);


}
