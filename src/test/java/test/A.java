package test;

import hiro.Resource;

import javax.inject.Inject;


public class A {

	

	@Inject
	private B b;
	
	
	
	
	@Resource("testResource.txt")
	private String resource;


	
	public A(int d) {
		System.out.println(resource);
		b.test();
		
		
	}
	
	void test() {
		
	}
	
	
}
