package hiro.internal;

import hiro.Injector;
import hiro.TypeLiteral;

public interface ScopeDefinition {

	<T> T get(Injector injector, TypeLiteral<T> type);

}
