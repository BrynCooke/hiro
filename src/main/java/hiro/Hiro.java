package hiro;

import hiro.internal.InjectorManager;
import hiro.internal.TransformingClassLoader;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class Hiro {



	private MethodHandler methodHandler = new MethodHandler() {
		public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
			System.out.println("Name: " + m.getName());
			return proceed.invoke(self, args); // execute the original method.
		}
	};

	private LoadingCache<Class<?>, Class<?>> proxyClasses = CacheBuilder.newBuilder().build(
			new CacheLoader<Class<?>, Class<?>>() {

				@Override
				public Class<?> load(Class<?> key) throws Exception {
					ProxyFactory f = new ProxyFactory();
					f.setSuperclass(key);
					Class<?> proxyClass = f.createClass();

					return proxyClass;
				}

			});

	public <T extends Runnable> void main(Class<T> clazz) {

		InjectorManager.getInjector().get(clazz).run();

	}

	private <T> T createProxy(Class<T> clazz) {
		Objenesis objenesis = new ObjenesisStd();
		try {
			Class<T> proxyClass = (Class<T>) proxyClasses.get(clazz);
			ObjectInstantiator<T> instantiatorOf = objenesis.getInstantiatorOf(proxyClass);
			T object = instantiatorOf.newInstance();

			((Proxy) object).setHandler(methodHandler);
			return object;
		} catch (Exception e) {
			throw new RuntimeException("Unable to create proxy", e);
		}
	}

}
