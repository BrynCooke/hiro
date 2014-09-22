package hiro;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeLiteral<T> {

	private Class<T> rawType;
	
	public TypeLiteral() {
	}
	
	public TypeLiteral(Class<T> rawType) {
		this.rawType = rawType;
	}
	
	
	public String getTypeName() {
		Type t = getClass().getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) t)
					.getActualTypeArguments();
			return actualTypeArguments[0].getTypeName();
		}
		return rawType.getTypeName();
	}
	//Need to think about hash code and equals....
	
}
