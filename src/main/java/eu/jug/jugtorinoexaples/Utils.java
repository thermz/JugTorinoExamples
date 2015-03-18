/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples;

/**
 *
 * @author RMuzzi
 */
public class Utils {

	public static <T> T unchecked(Uncheck<T> uncheck ){
		T result = null;
		try{
			result = uncheck.uncheck();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
}
