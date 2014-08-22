package test;

import hiro.RequestScope;

@RequestScope
public class C {

	public void test() {
		System.out.println("Called C");
	}

}
