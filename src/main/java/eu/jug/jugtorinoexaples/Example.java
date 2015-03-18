/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RMuzzi
 */
public class Example {
	
	public static String getCarInsuranceName(Person person){
		return ofNullable(person)
				.flatMap(Person::getCar)
				.map(Car::getInsurance)
				.map(Insurance::getName)
				.orElse("N/A");
	}
	
	public static <T> InBuilder<T> in( T object ){
		return new InBuilder<>(ofNullable(object));
	}
	
	public static class InBuilder<T> {
		Optional<T> current;
		public InBuilder(Optional<T> object) {current = object;}
		public <R> InBuilder<R> in(Function<T,R> getter){
			return new InBuilder<>(current.map(getter));
		}
		public <R> InBuilder<R> on(Function<T,Optional<R>> getter){
			return new InBuilder<>(current.flatMap(getter));
		}
		public Optional<T> opt(){return current;}
		public T get(){ return current.get(); }
		public T orElse(T defVal){ return current.orElse(defVal); }
	}
	
}
