/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples;

import static eu.jug.jugtorinoexaples.Example.getCarInsuranceName;
import static eu.jug.jugtorinoexaples.Example.in;
import static eu.jug.jugtorinoexaples.Utils.unchecked;
import java.util.Optional;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author RMuzzi
 */
public class ExampleTest {
	
	@Test
	public void testOptionalBase(){
		System.out.println("Testing car insurance Optional");
		assertEquals("N/A", getCarInsuranceName(null) );
		assertEquals("AXA", getCarInsuranceName( personFactory() ));
	}
	
	@Test
	public void testSyntaxSugar(){
		System.out.println("Testing Syntax Sugar");
		
		Person person = null;
			
		assertEquals( 
			"N/A",
			
			in(person)
			.on(Person::getCar)
			.in(Car::getInsurance)
			.in(Insurance::getName)
			.orElse("N/A")
		);
		
		assertEquals( 
			"AXA",
			
			in( personFactory() )
			.on(Person::getCar)
			.in(Car::getInsurance)
			.in(Insurance::getName)
			.orElse("N/A")
		);
			
	}
	
	@Test(expected = RuntimeException.class)
	public void testUncheckedFail(){
		System.out.println("Running a piece of code that throws checked exception");
		unchecked( ()->{
			if( true )
				throw new MyCheckedException();
			return 1;
		});
	}
	
	@Test()
	public void testUnchecked(){
		System.out.println("Running a piece of code that doesn't throws checked exception");
		String toBeAssigned = unchecked( ()-> "A String");
		assert "A String".equals(toBeAssigned);
	}
	
	public static class MyCheckedException extends Exception {
		public MyCheckedException() {}
	}
	
	Person personFactory(){
		Insurance i = new Insurance();
		i.setName("AXA");
		Car car = new Car();
		car.setInsurance(i);
		Person p = new Person();
		p.setCar(of(car));
		return p;
	}
	
}
