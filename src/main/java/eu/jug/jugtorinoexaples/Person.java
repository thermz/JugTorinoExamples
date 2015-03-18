/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples;

import java.util.Optional;

/**
 *
 * @author RMuzzi
 */
public class Person {
	Optional<Car> car;

	public void setCar(Optional<Car> car) {
		this.car = car;
	}
	public Optional<Car> getCar() {
		return car;
	}
}
