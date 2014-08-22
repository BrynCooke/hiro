package hiro.internal;

import hiro.Injector;
import hiro.Scope;
import hiro.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InjectorImpl implements Injector {

	private TransformingClassLoader cl = new TransformingClassLoader();

	private ConcurrentMap<Scope, ScopeDefinition> activeScopes = new ConcurrentHashMap<>();

	@Override
	public <T> T get(TypeLiteral<T> type) {

		return null;
	}

	@Override
	public <T> T get(Class<T> type) {
		try {
			Class<T> loadedClass = (Class<T>) cl.loadClass(type.getName());
			T instance = loadedClass.newInstance();
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T, S extends Scope> T get(TypeLiteral<T> type, Class<S> scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, S extends Scope> T get(Class<T> type, Class<S> scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Annotation> void enterScope(Scope scope) {

		synchronized (activeScopes) {

			if (activeScopes.containsKey(scope)) {
				throw new RuntimeException("Scope already active");
			}
			try {
				activeScopes.put(scope, new ScopeDefinition(scope.getClass()));
				scope.run();
			} finally {
				activeScopes.remove(scope.getClass());
			}
		}

	}
}
