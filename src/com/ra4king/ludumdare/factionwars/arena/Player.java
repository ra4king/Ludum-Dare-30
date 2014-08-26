package com.ra4king.ludumdare.factionwars.arena;

import com.ra4king.ludumdare.factionwars.Stats;

/**
 * @author Roi Atalla
 */
public class Player {
	private int id;
	
	private int money = 200;
	private Stats stats = new Stats();
	
	public Player(int id) {
		this.id = id;
	}
	
	public Stats getStats() {
		return stats;
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
