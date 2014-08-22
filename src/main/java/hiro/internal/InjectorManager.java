package hiro.internal;

import hiro.Injector;

public class InjectorManager {

	private static ThreadLocal<Injector> injector = new ThreadLocal<Injector>() {
		protected Injector initialValue() {
			return new InjectorImpl();
		};
	}; 
	
	public static Injector getInjector() {
		return injector.get();
	}
}
