package test;

import hiro.internal.TransformingClassLoader;

public class Test1 {

	public static void main(String[] args) throws Exception {
		TransformingClassLoader cl = new TransformingClassLoader();
		Class t = cl.loadClass("hiro.Test2");
		t.newInstance();
	}
	
	
}
