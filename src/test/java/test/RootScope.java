package test;

import hiro.Hiro;
import hiro.Injector;
import hiro.Produces;
import hiro.Scope;

import javax.inject.Inject;

public class RootScope implements Scope {
	public static void main(String[] args) {
		new Hiro().main(RootScope.class);
	}
	

	
	@Inject
	private Injector injector;
	
	@Inject
	private C c; //This is a request scoped item

	@Produces
	public B produce() {
		return new B();
	}

	public void run() {
		new A(6);
		injector.enterScope(() -> {

			test();

		});
		
		
	}

	public void test() {
		c.test();
	}

}
