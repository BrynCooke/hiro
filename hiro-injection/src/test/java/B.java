import javax.inject.Inject;

public class B {

	@Inject
	private A a;
	
	public B() {
		System.out.println(a);
	}
	

}
