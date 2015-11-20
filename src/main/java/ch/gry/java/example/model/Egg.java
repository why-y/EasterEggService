package ch.gry.java.example.model;

import java.time.LocalDate;

public class Egg {
	
	LocalDate layingDate;
	double weight;
	
	public Egg(LocalDate layingDate, double weight) {
		super();
		this.layingDate = layingDate;
		this.weight = weight;
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
		return "Egg [layingDate=" + layingDate + ", weight=" + weight + "]";
	}
	
}
