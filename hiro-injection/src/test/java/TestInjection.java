import org.jglue.hiro.Produces;
import org.jglue.hiro.Scope;
import org.junit.Assert;
import org.junit.Test;

public class TestInjection {
	B b;

	@Test
	public void test() {
		Scope scope = new Scope() {
			

			@Produces
			A a = new A();
		};

		scope.execute(() -> {
			B b = new B();
			B c = new B();
			B d = new B();
			Assert.assertNotNull(Scope.currentInjector().get(A.class));
			//Assert.assertNotNull("Should have injected", b.a);
		});

	}
}
