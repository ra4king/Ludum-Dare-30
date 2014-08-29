package com.ra4king.ludumdare.factionwars.controller;

import java.util.ArrayList;
import java.util.List;

import com.ra4king.gameutils.Game;
import com.ra4king.ludumdare.factionwars.arena.Arena;
import com.ra4king.ludumdare.factionwars.arena.Planet;
import com.ra4king.ludumdare.factionwars.arena.Player;

/**
 * @author Roi Atalla
 */
public class AIController extends Controller {
	private List<Planet> lastKnownOwnedPlanets;
	
	public AIController(Game game, Arena arena, Player player) {
		super(game, arena, player);
	}
	
	@Override
	public boolean doTurn() {
		List<Planet> knownOwnedPlanets = new ArrayList<>();
		arena.getEntitiesAt(Arena.PLANET_Z).stream().filter(e -> ((Planet)e).getOwner() == player).forEach(e -> knownOwnedPlanets.add((Planet)e));
		
		if(knownOwnedPlanets.size() == 0)
			return true;

		ArrayList<PossibleAction> possibleActions = new ArrayList<>();
		
		for(Action action : Action.values()) {
			for(Planet from : chooseFrom(action, knownOwnedPlanets)) {
				for(Planet to : chooseTo(action, from, knownOwnedPlanets)) {
					if(from == null && to != null)
						continue;
					
					if(action.canAct(this, arena, from, to))
						possibleActions.add(new PossibleAction(action, from, to));
				}
			}
		}
		
		List<PossibleActionYieldPair> maxYieldActions = new ArrayList<>();
		for(PossibleAction pa : possibleActions) {
			double yield = netYield(pa, knownOwnedPlanets);
			
			System.out.println(pa + " - " + yield);
			
			if(maxYieldActions.size() == 0 || yield >= maxYieldActions.get(0).yield) {
				if(maxYieldActions.size() > 0 && yield > maxYieldActions.get(0).yield)
					maxYieldActions.clear();
				
				maxYieldActions.add(new PossibleActionYieldPair(pa, yield));
	}
		}
		
		if(maxYieldActions.size() == 0) {
			// try again....
			return doTurn();
		}
		
		PossibleActionYieldPair maxYieldActionYieldPair = maxYieldActions.get((int)(Math.random() * maxYieldActions.size()));
		PossibleAction maxYieldAction = maxYieldActionYieldPair.possibleAction;
		double maxYield = maxYieldActionYieldPair.yield;
		
		System.out.println("Final selection: " + maxYieldAction + " - " + maxYield);
		System.out.println("----------------------------------------\n");
		
		act(maxYieldAction.action, maxYieldAction.from, maxYieldAction.to);
		
		lastKnownOwnedPlanets = knownOwnedPlanets;
		
		return true;
	}
	
	private double netYield(PossibleAction possibleAction, List<Planet> knownOwnedPlanets) {
		switch(possibleAction.action) {
			case EXPLORE:
				return 400 - 5 * knownOwnedPlanets.size();
			case TAX:
				return 10 * possibleAction.from.getTaxAmount(); // +tax
			case BUY_SHIP:
				int buyShipPrice = player.getStats().getShipPrice();
				return 2 * buyShipPrice / (possibleAction.from.getShips().size() + 1);
			case SEND_ONE_SHIP:
				return 10 * (Arena.PLANET_COUNT - knownOwnedPlanets.size()) + 2 * (possibleAction.to.getOwner() != player ?
						player.getStats().getWeaponDamage() * possibleAction.from.getShips().size() - (possibleAction.to.getDefense() + possibleAction.to.getShips().size()) : 0);
			case SEND_HALF_SHIPS:
				return 17 * (Arena.PLANET_COUNT - knownOwnedPlanets.size()) + 3 * (possibleAction.to.getOwner() != player ?
						player.getStats().getWeaponDamage() * possibleAction.from.getShips().size() - (possibleAction.to.getDefense() + possibleAction.to.getShips().size()) : 0);
			case SEND_ALL_SHIPS:
				return !lastKnownOwnedPlanets.contains(possibleAction.to) ? 1000 : -1000;
			case UPGRADE_DEFENSE:
				int upgradeDefensePrice = player.getStats().getDefenseUpgradePrice();
				return -upgradeDefensePrice + 10 * (72 - possibleAction.from.getDefense());
			case UPGRADE_FUEL_RANGE:
				int fuelRangePrice = player.getStats().getFuelRangePrice();
				return -fuelRangePrice + (Arena.PLANET_COUNT - knownOwnedPlanets.size()) * (fuelRangePrice / 7.0);
			case UPGRADE_WEAPONS:
				int upgradeWeaponsPrice = player.getStats().getWeaponUpgradePrice();
				return -upgradeWeaponsPrice + (knownOwnedPlanets.stream().mapToInt(p -> p.getShips().size()).sum() * 25) / upgradeWeaponsPrice;
			case BUY_CONNECTION:
				int connectionPrice = player.getStats().getConnectionPrice();
				return -connectionPrice + 10 * (possibleAction.from.getDefense() + possibleAction.from.getShips().size())
						+ 10 * (possibleAction.to.getDefense() + possibleAction.to.getShips().size());
			default:
				throw new IllegalArgumentException("WUT");
		}
	}
	
	private Planet[] chooseFrom(Action action, List<Planet> knownOwnedPlanets) {
		switch(action) {
			case EXPLORE:
			case BUY_SHIP:
				return knownOwnedPlanets.toArray(new Planet[knownOwnedPlanets.size()]);
			case UPGRADE_FUEL_RANGE:
			case UPGRADE_WEAPONS:
				return new Planet[] { null };
			case TAX:
				int maxTax = arena.getEntitiesAt(Arena.PLANET_Z).stream().filter(e -> ((Planet)e).getOwner() == player)
						.mapToInt(e -> ((Planet)e).getTaxAmount()).max().orElse(-1);
				
				if(maxTax == -1)
					return new Planet[] { null };
				
				return convert(arena.getEntitiesAt(Arena.PLANET_Z).stream().map(e -> (Planet)e).filter(p -> (p.getOwner() == player && p.getTaxAmount() == maxTax)).toArray());
			case SEND_ONE_SHIP:
				return convert(knownOwnedPlanets.stream().filter((Planet p) -> p.getShips().size() > 0).toArray());
			case SEND_HALF_SHIPS:
				return convert(knownOwnedPlanets.stream().filter((Planet p) -> p.getShips().size() > 1).toArray());
			case SEND_ALL_SHIPS:
				return convert(knownOwnedPlanets.stream().filter((Planet p) -> p.getShips().size() > 2 && p.getDefense() > 2).toArray());
			case UPGRADE_DEFENSE:
				double avgDefense = knownOwnedPlanets.stream().mapToDouble(Planet::getDefense).average().orElse(0.0);
				return convert(knownOwnedPlanets.stream().filter((Planet p) -> p.getDefense() <= avgDefense).toArray());
			case BUY_CONNECTION:
				return knownOwnedPlanets.toArray(new Planet[knownOwnedPlanets.size()]);
			default:
				throw new IllegalArgumentException("WUT");
		}
	}
	
	private Planet[] chooseTo(Action action, Planet from, List<Planet> knownOwnedPlanets) {
		switch(action) {
			case EXPLORE:
				return convert(arena.getEntitiesAt(Arena.PLANET_Z).stream().map(e -> (Planet)e).filter(p -> !isExplored(p) && withinRange(from, p)).toArray());
			case TAX:
			case BUY_SHIP:
			case UPGRADE_FUEL_RANGE:
			case UPGRADE_WEAPONS:
			case UPGRADE_DEFENSE:
				return new Planet[] { null };
			case SEND_ONE_SHIP:
			case SEND_HALF_SHIPS:
				double avgShips = exploredPlanets.stream().mapToDouble((Planet p) -> p.getShips().size()).average().orElse(0.0);
				return convert(exploredPlanets.stream().filter((Planet p) -> p != from &&
						((p.getOwner() == player && p.getShips().size() <= avgShips) || (p.getOwner() != player)) && withinRange(from, p)).toArray());
			case SEND_ALL_SHIPS:
				return convert(exploredPlanets.stream().filter((Planet p) -> p != from && !lastKnownOwnedPlanets.contains(p) && withinRange(from, p)).toArray());
			case BUY_CONNECTION:
				return convert(knownOwnedPlanets.stream().filter(p -> p != from).toArray());
			default:
				throw new IllegalArgumentException("WUT");
		}
	}
	
	private Planet[] convert(Object[] o) {
		Planet[] p = new Planet[o.length];
		for(int a = 0; a < o.length; a++)
			p[a] = (Planet)o[a];
		return p;
	}
	
	private static class PossibleAction {
		private Action action;
		private Planet from, to;
		
		public PossibleAction(Action action, Planet from, Planet to) {
			this.action = action;
			this.from = from;
			this.to = to;
		}
		
		@Override
		public String toString() {
			return "Possible Action: " + action + " from " + from + " to " + to;
		}
	}
	
	private static class PossibleActionYieldPair {
		private PossibleAction possibleAction;
		private double yield;
		
		public PossibleActionYieldPair(PossibleAction possibleAction, double yield) {
			this.possibleAction = possibleAction;
			this.yield = yield;
		}
	}
}
