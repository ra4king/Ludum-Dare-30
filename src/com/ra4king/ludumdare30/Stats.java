package com.ra4king.ludumdare30;

/**
 * @author Roi Atalla
 */
public class Stats {
	private double fuelRange = 150; // in pixels
	private double fuelRangeIncrease = 30;
	
	private int fuelRangePrice = 400;
	private int fuelRangePriceIncrease = 50;
	
	private double weaponDamage = 1; // defense value
	
	private int explorePrice = 20;
	
	private int shipPrice = 60;
	private final int shipPriceIncrease = 40;
	
	private int weaponUpgradePrice = 100;
	private int weaponUpgradePriceIncrease = 50;
	
	private double defenseUpgradeValue = 3;
	private int defenseUpgradePrice = 30;
	private int defenseUpgradePriceIncrease = 30;
	
	private int connectionPrice = 1000;
	private int connectionPriceIncrease = 500;
	
	public double getFuelRange() {
		return fuelRange;
	}
	
	public double increaseFuelRange() {
		return fuelRange += fuelRangeIncrease;
	}
	
	public int getFuelRangePrice() {
		return fuelRangePrice;
	}
	
	public int getAndIncreaseFuelRangePrice() {
		int price = fuelRangePrice;
		fuelRangePrice += fuelRangePriceIncrease;
		return price;
	}
	
	public double getWeaponDamage() {
		return weaponDamage;
	}
	
	public double increaseWeaponDamage(double delta) {
		return weaponDamage += delta;
	}
	
	public int getExplorePrice() {
		return explorePrice;
	}
	
	public int getShipPrice() {
		return shipPrice;
	}
	
	public int getAndIncreaseShipPrice() {
		int price = shipPrice;
		shipPrice += shipPriceIncrease;
		return price;
	}
	
	public int getWeaponUpgradePrice() {
		return weaponUpgradePrice;
	}
	
	public int getAndIncreaseWeaponUpgradePrice() {
		int price = weaponUpgradePrice;
		weaponUpgradePrice += weaponUpgradePriceIncrease;
		return price;
	}
	
	public double getDefenseUpgradeValue() {
		return defenseUpgradeValue;
	}
	
	public int getDefenseUpgradePrice() {
		return defenseUpgradePrice;
	}
	
	public int getAndIncreaseDefenseUpgradePrice() {
		int price = defenseUpgradePrice;
		defenseUpgradePrice += defenseUpgradePriceIncrease;
		return price;
	}
	
	public int getConnectionPrice() {
		return connectionPrice;
	}
	
	public int getAndIncreaseConnectionPrice() {
		int price = connectionPrice;
		connectionPrice += connectionPriceIncrease;
		return price;
	}
}
