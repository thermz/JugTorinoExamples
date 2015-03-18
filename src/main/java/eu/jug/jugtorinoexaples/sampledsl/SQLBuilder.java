/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples.sampledsl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import static java.util.Optional.ofNullable;
import java.util.function.Function;

/**
 *
 * @author RMuzzi
 */
public class SQLBuilder<T> {
	
	static final String GT  = "$gt";
	static final String LT  = "$lt";
	static final String NE  = "$ne";
	static final String EQ  = "eq";
	static final String GTE = "$gte";
	static final String LTE = "$lte";
	static final List<String> greaterThanRangeComparators = asList(GT,GTE);
	static final List<String> lessThanRangeComparators = asList(LT,LTE);
	
	Recorder<T> recorder;
	AndStruct<T> ands = new AndStruct<>();
	List<ComparisonBuilder<T,?>> orComparison = new ArrayList<>();
	ComparisonBuilder<T,?> initialComparison;
	Class<T> clazz;
	
	Date upperBound;
	Date lowerBound;
	
	public SQLBuilder( Class<T> clazz ) {
		this.clazz = clazz;
		recorder = RecordingObject.create(clazz);
	}

	List<ComparisonBuilder<T, ?>> getOrComparisons()  { return orComparison;  }
	
	public static <S> SQLBuilder<S> selectFrom(Class<S> clazz){
		return new SQLBuilder<>(clazz);
	}
	
	public <R> ComparisonBuilder<T,R> where(Function<T,R> getter){
		ComparisonBuilder<T,R> cb = new ComparisonBuilder<>(getter,this);
		initialComparison = cb;
		return cb;
	}
	public <R> ComparisonBuilder<T,R> and(Function<T,R> getter){
		ComparisonBuilder<T,R> cb = new ComparisonBuilder<>(getter,this);
		ands.put(getPropertyName(getter), cb);
		return cb;
	}
	public <R> ComparisonBuilder<T,R> or(Function<T,R> getter){
		ComparisonBuilder<T,R> cb = new ComparisonBuilder<>(getter,this);
		orComparison.add(cb);
		return cb;
	}
	
	public String getCollectionName(){
		return ofNullable(clazz.getAnnotation(MongoCollectionName.class))
					.map(MongoCollectionName::value)
					.orElse(clazz.getSimpleName().toLowerCase());
	}
	
	public List<T> get(){
		return null; //TODO
	}
	
	public DBObject createMongoQuery(){
		DBObject result = new BasicDBObject();
		DBObject andQuery = createAndConditionsMongoQuery();
		
		if(andQuery.toMap().isEmpty())
			orComparison.add(initialComparison);
		else
			result.putAll(getSingleCondition(initialComparison));
		DBObject orQuery = createOrConditionsMongoQuery();
		
		result.putAll( reduceDBObject(andQuery, orQuery) );
		return result;
	}
	
	private DBObject createAndConditionsMongoQuery(){
		return ands.stream()
				.map( this::mapAndToMongoDBO )
				.reduce( SQLBuilder::reduceDBObject )
				.orElse(new BasicDBObject());
	}
	private BasicDBObject createFieldAndConditionsMongoQuery(List<ComparisonBuilder<T,?>> andCond){
		return new BasicDBObject("$and", andCond.stream().collect(BasicDBList::new, 
									(dbl,cond)->dbl.add( getSingleCondition(cond) ), 
									(x,y)->x.addAll(y)) );
	}
	private BasicDBObject createOrConditionsMongoQuery(){
		return createOrConditionsMongoQuery(getOrComparisons());
	}
	private BasicDBObject createOrConditionsMongoQuery(List<ComparisonBuilder<T,?>> orCond){
		if(orCond.isEmpty())
			return new BasicDBObject();
		else
			return new BasicDBObject("$or", orCond.stream().collect(BasicDBList::new, 
									(dbl,cond)->dbl.add( getSingleCondition(cond) ), 
									(x,y)->x.addAll(y)) );
	}
	private DBObject getSingleCondition(ComparisonBuilder<T,?> comparison){
		DBObject result = new BasicDBObject();
		
		switch(comparison.comparisonType){
			case EQ: 
				result = new BasicDBObject(getPropertyName(comparison.getter),comparison.comparison); break;
			case GT:
			case GTE:
			case LT:
			case LTE:
			case NE:
				result = new BasicDBObject(
								getPropertyName(comparison.getter),
								new BasicDBObject(
									comparison.comparisonType,
									comparison.comparison
								)
							);
				break;
		}
		return result;
	}
	
	public String getPropertyName(Function<T,?> getter ){
		getter.apply( recorder.getObject() );
		return recorder.getCurrentPropertyName();
	}
	
	public static class ComparisonBuilder<T,R> {
		Function<T,R> getter;
		R comparison;
		SQLBuilder<T> sqlBuilderReference;
		String comparisonType = "";
		
		public ComparisonBuilder( Function<T,R> getter, SQLBuilder<T> sqlBuilder ) {
			this.getter = getter;
			this.sqlBuilderReference = sqlBuilder;
		}
		public SQLBuilder<T> eq(R comparison){
			return createComparison(comparison, EQ);
		}
		public SQLBuilder<T> ne(R comparison){
			return createComparison(comparison, NE);
		}
		public SQLBuilder<T> gte(R comparison){
			return createComparison(comparison, GTE);
		}
		public SQLBuilder<T> gt(R comparison){
			return createComparison(comparison, GT);
		}
		public SQLBuilder<T> lte(R comparison){
			return createComparison(comparison, LTE);
		}
		public SQLBuilder<T> lt(R comparison){
			return createComparison(comparison, LT);
		}
		
		private SQLBuilder<T> createComparison(R comparison, String comparisonType){
			this.comparisonType = comparisonType;
			this.comparison = comparison;
			return sqlBuilderReference;
		}
		
	}
	
	//If a field has already an AND condition it will became $and:[ cond1, cond2 ...]
	private DBObject mapAndToMongoDBO(Entry<String,List<ComparisonBuilder<T, ?>>> andEntry){
		if(andEntry.getValue().size()==1)
			return getSingleCondition(andEntry.getValue().get(0));
		else
			return createFieldAndConditionsMongoQuery(andEntry.getValue());
	}
	
	public static DBObject reduceDBObject(DBObject o1, DBObject o2){
		o1.putAll(o2);
		return o1;
	}
	
}
