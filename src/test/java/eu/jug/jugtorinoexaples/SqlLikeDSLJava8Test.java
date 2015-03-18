/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import eu.jug.jugtorinoexaples.sampledsl.MongoCollectionName;
import eu.jug.jugtorinoexaples.sampledsl.MongoFieldName;
import eu.jug.jugtorinoexaples.sampledsl.SQLBuilder;
import static eu.jug.jugtorinoexaples.sampledsl.SQLBuilder.selectFrom;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class SqlLikeDSLJava8Test {
	
	@Test
	public void testSQLBuilder(){
		System.out.println( selectFrom(Person.class).getPropertyName(Person::getAge ) );
		
		System.out.println( "resulting and query BSON object:\n"+
			
			selectFrom(  Person.class)
				.where(	 Person::getAge ).eq(2)
					.and( Person::getName ).eq("asd")
					.and( Person::getTimestamp ).lte(new Date())
					.and( Person::getTimestamp ).gte(new Date())
				
					.createMongoQuery() );
		
//		System.out.println("There are "+dbCol.count()+" in collection "+dbCol.getName());
	}
	
//	@Test
	public void testLT(){
		assertEquals(2, selectFrom( Person.class )
							.where( Person::getAge ).lt( 5 )
							.get().size() );
	}
	
//	@Test
	public void testGTE(){
		assertEquals(3, selectFrom( Person.class )
							.where( Person::getAge ).gte( 5 )
							.get().size() );
	}
	
//	@Test
	public void testEQ(){
		assertEquals(2, selectFrom( Person.class )
							.where( Person::getName ).eq( "Lambda" )
							.get().size() );
	}
	
//	@Test
	public void testEQAND(){
		assertEquals(2, selectFrom( Person.class )
							.where( Person::getName ).eq( "Lambda" )
							.and(	Person::getAge ).lte(10)
							.get().size() );
	}
	
//	@Test
	public void testOR(){
		System.out.println("Testing OR condition: all 5 results should be in the result set");
		SQLBuilder<Person> orQueryTest =
								selectFrom( Person.class )
									.where( Person::getName ).eq( "Lambda" )
									.or(	Person::getAge ).lte(10);
		System.out.println("OR Mongo resulting query = "+orQueryTest.createMongoQuery());
		assertEquals(5, orQueryTest.get().size() );
	}
	
//	@Test
	public void testAnnotation(){
		SQLBuilder<Event> sql = selectFrom(Event.class).where( Event::getName ).eq("anevent");
		DBObject mongoQuery = sql.createMongoQuery();
		System.out.println("Resulting annotation query: "+mongoQuery);
		System.out.println("Collection Name: "+sql.getCollectionName());
		assertEquals( "ev_name", ((DBObject)((BasicDBList)mongoQuery.get("$or")).get(0)).keySet().iterator().next());
		assertEquals( 1, sql.get().size() );
	}
	
	private static Person pojoHigh(){
		Person result = new Person();
//		result.setAge(randInt(5, 10));
//		result.setName(randomString(10));
//		result.setTimestamp(dateOf(1).hours().ago());
		return result;
	}
	private static Person pojoLow(){
		Person result = new Person();
//		result.setAge(randInt(0, 4));
//		result.setName("Lambda");
//		result.setTimestamp(dateOf(5).hours().ago());
		return result;
	}
	private static Event event(){
		return new Event("anevent", 15);
	}
	
	public static class Person {
		private Integer age;
		private String name;
		private Date timestamp;

		public Integer getAge() {
			return age;
		}
		public String getName() {
			return name;
		}
		public Date getTimestamp() {
			return timestamp;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}
		@Override
		public String toString() {
			return "MyPOJO{" + "number=" + age + ", string=" + name + ", timestamp=" + timestamp + '}';
		}
	}

	@MongoCollectionName("eventi")
	public static class Event {
		String name;
		Integer participants;

		public Event() {
		}
		
		public Event(String name, Integer participants) {
			this.name = name;
			this.participants = participants;
		}
		
		@MongoFieldName("ev_name")
		public String getName() {
			return name;
		}

		public Integer getParticipants() {
			return participants;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setParticipants(Integer participants) {
			this.participants = participants;
		}

		@Override
		public String toString() {
			return "Event{" + "name=" + name + ", participants=" + participants + '}';
		}
		
	}
	
}
