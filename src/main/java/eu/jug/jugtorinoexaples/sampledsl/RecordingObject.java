/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples.sampledsl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import static java.util.Optional.ofNullable;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class RecordingObject implements MethodInterceptor {

	private static final Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>(){{
		put(String.class, "");
        put(Integer.class,0);
        put(Float.class, 0f);
        put(Double.class, 0d);
        put(Long.class, 0L);
        put(Character.class, 'c');
        put(Byte.class, (byte)0);
        put(int.class, 0);
        put(float.class,0f);
        put(double.class,0d);
        put(long.class, 0L);
        put(char.class, 'c');
        put(byte.class, (byte)0);
	}};
	
    private String currentPropertyName = "";
    private Recorder<?> currentMock = null;

    @SuppressWarnings("unchecked")
    public static <T> Recorder<T> create(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        final RecordingObject recordingObject = new RecordingObject();
        enhancer.setCallback(recordingObject);
        return new Recorder((T) enhancer.create(), recordingObject);
    }

    public Object intercept(Object o, Method method, Object[] os, MethodProxy mp) throws Throwable {
        if (method.getName().equals("getCurrentPropertyName")) {
            return getCurrentPropertyName();
        }
        currentPropertyName = getCurrentPropertyName(method);
        try {
            currentMock = create(method.getReturnType());
            return currentMock.getObject();
        } catch (IllegalArgumentException e) {
            return defaultValues.get(method.getReturnType());
        }
    }
	
	private String getCurrentPropertyName(Method met){
		return ofNullable(  met.getAnnotation(MongoFieldName.class) )
					.flatMap( a -> ofNullable(a.value()))
					.orElse( getterToField( met.getName() ) );
	}

    public String getCurrentPropertyName() {
        return currentPropertyName;
    }
	
	public static String getterToField(String getter) {
		String field = getter.substring(3);
		field = field.substring(0, 1).toLowerCase() + field.substring(1);
		return field;
	}
	public static String fieldToGetter(String field) {
		return "get"+field.substring(0, 1).toUpperCase() + field.substring(1);
	}
}
