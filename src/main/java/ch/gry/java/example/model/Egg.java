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
	
	protected int id;
	private static int idCounter = 0;
	
	public Egg(LocalDate layingDate, double weight) {
		super();
		this.layingDate = layingDate;
		this.weight = weight;
		this.id = idCounter++;
	}
	
	public int getId() {
		return id;
	}
	
	public LocalDate getLayingDate() {
		return layingDate;
	}
	
	public double getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return "Egg[" + this.id + "]";
	}
	
}
