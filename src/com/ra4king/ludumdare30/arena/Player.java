package com.ra4king.ludumdare30.arena;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashSet;

import com.ra4king.ludumdare30.Stats;

/**
 * @author Roi Atalla
 */
public class Player {
	private int id;
	
	private int money = 200000;
	private Stats stats = new Stats();
	
	private Collection<Planet> planets = new HashSet<>();
	private Area planetsFuelRangeArea = new Area();
	
	public Player(int id) {
		this.id = id;
	}
	
	public Collection<Planet> getPlanets() {
		return planets;
	}
	
	public Stats getStats() {
		return stats;
	}
	
	private void rebuildArea() {
		planetsFuelRangeArea.reset();
		double fuelRange = stats.getFuelRange();
		planets.stream().forEach((Planet p) -> planetsFuelRangeArea.add(new Area(new Ellipse2D.Double(p.getX() - fuelRange, p.getY() - fuelRange, fuelRange * 2, fuelRange * 2))));
	}
	
	public Area getPlanetsFuelRangeArea() {
		return planetsFuelRangeArea;
	}
	
	public void addPlanet(Planet planet) {
		planets.add(planet);
		double fuelRange = stats.getFuelRange();
		planetsFuelRangeArea.add(new Area(new Ellipse2D.Double(planet.getX() - fuelRange, planet.getY() - fuelRange, fuelRange * 2, fuelRange * 2)));
	}
	
	public void removePlanet(Planet planet) {
		planets.remove(planet);
		rebuildArea();
	}
	
	public boolean ownsPlanet(Planet planet) {
		return planets.contains(planet);
	}
	
	public int getID() {
		return id;
	}
	
	public int getMoney() {
		return money;
	}
	
	public int increaseMoney(int amount) {
		return money += amount;
	}
	
	public int decreaseMoney(int amount) {
		if(money - amount < 0)
			throw new IllegalArgumentException("Negative money!");
		
		return money -= amount;
	}
	
	@Override
	public String toString() {
		return String.format("Player %d - $%d, range: %.2f, damage: %.2f", id, money, stats.getFuelRange(), stats.getWeaponDamage());
	}
}
