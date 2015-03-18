/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.jug.jugtorinoexaples.sampledsl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static java.util.stream.Stream.concat;

/**
 *
 * @author RMuzzi
 * @param <T> the type of the POJO used to set AND conditions
 */
public class AndStruct<T> {

	Map<String, List<
					SQLBuilder.ComparisonBuilder<T, ?>>> andConditions = new HashMap<>();

	public Map<String, List<SQLBuilder.ComparisonBuilder<T, ?>>> getAndConditions() {
		return andConditions;
	}

	public void put(String propertyName, SQLBuilder.ComparisonBuilder<T, ?> cond) {
		andConditions.put(
			propertyName,
			concat(
				get(propertyName).stream(),
				asList(cond).stream())
			.collect(toList())
		);
	}

	public List<SQLBuilder.ComparisonBuilder<T, ?>> get(String propName) {
		return andConditions.getOrDefault(propName, emptyList());
	}

	public Stream< Map.Entry<String, List<SQLBuilder.ComparisonBuilder<T, ?>>>> stream() {
		return andConditions.entrySet().stream();
	}

	public int size() {
		return andConditions.size();
	}
}
