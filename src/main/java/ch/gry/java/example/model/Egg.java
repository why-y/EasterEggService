package ch.gry.java.example.model;

import java.time.LocalDate;

/**
 * A data bean representing an egg specified by
 * the attributes {@link #layingDate} and {@link #weight} in milligrams. 
 * @author yvesgross
 */
public class Egg {
	
	LocalDate layingDate;
	double weight;
	
	private int id;
	private static int idCounter = 0;
	
	
	public Egg(LocalDate layingDate, double weight) {
		super();
		this.layingDate = layingDate;
		this.weight = weight;
		this.id = idCounter++;
	}
	public LocalDate getLayingDate() {
		return layingDate;
	}
	public void setLayingDate(LocalDate layingDate) {
		this.layingDate = layingDate;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Egg[" + this.id + "]";
	}
	
}
