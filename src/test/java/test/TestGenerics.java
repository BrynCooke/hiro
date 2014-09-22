package test;

import hiro.TypeLiteral;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

public class TestGenerics {
	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		TypeLiteral<?> t = new TypeLiteral<List<A>>() {
		};
		System.out.println(t.getTypeName());
		
		TypeLiteral<?> t2 = new TypeLiteral<A>() {
		};
		System.out.println(t2.getTypeName());
		
		Method method = this.getClass().getMethod("test2");
		Type genericReturnType = method.getGenericReturnType();
		System.out.println(genericReturnType.getTypeName());
	}
	
	public List<A> test2() {
		return null;
	}
}
