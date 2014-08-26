package com.ra4king.ludumdare.factionwars.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Game;
import com.ra4king.ludumdare.factionwars.arena.Arena;
import com.ra4king.ludumdare.factionwars.arena.Connection;
import com.ra4king.ludumdare.factionwars.arena.Planet;
import com.ra4king.ludumdare.factionwars.arena.Player;
import com.ra4king.ludumdare.factionwars.arena.Ship;

/**
 * @author Roi Atalla
 */
public abstract class Controller {
	protected Game game;
	protected Arena arena;
	protected Player player;
	
	protected Set<Planet> exploredPlanets = new HashSet<>();
	
	public Controller(Game game, Arena arena, Player player) {
		this.game = game;
		this.arena = arena;
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isExplored(Planet planet) {
		return exploredPlanets.contains(planet);
	}
	
	public void addExploredPlanet(Planet planet) {
		exploredPlanets.add(planet);
	}
	
	/**
	 * @return true = completed turn, false = still waiting on input
	 */
	public abstract boolean doTurn();
	
	public void draw(Graphics2D g) {
		arena.getEntitiesAt(Arena.PLANET_Z).stream().forEach((Entity e) -> {
			Planet p = (Planet)e;
			if(!(this instanceof UserController) || !isExplored(p)) {
				g.setColor(new Color(0.2f, 0.2f, 0.2f, 1));
				
				Ellipse2D.Double bounds = p.getBoundsEllipse();
				g.fill(new Ellipse2D.Double(bounds.getX() - 7, bounds.getY() - 7, bounds.getWidth() + 14, bounds.getHeight() + 14));
				
				g.setColor(Color.BLACK);
				g.draw(bounds);
			}
		});
	}
	
	protected void act(Action action, Planet fromTarget, Planet toTarget) {
		action.act(this, arena, fromTarget, toTarget);
	}
	
	// private boolean withinEntireRange(Planet target) {
	// return player.getPlanetsFuelRangeArea().intersects(target.getBounds());
	// }
	
	public boolean withinRange(Planet fromTarget, Planet toTarget) {
		double dx = toTarget.getX() - fromTarget.getX();
		double dy = toTarget.getY() - fromTarget.getY();
		
		double maxDist = player.getStats().getFuelRange() + fromTarget.getWidth() * 0.5 + toTarget.getWidth() * 0.5;
		return dx * dx + dy * dy <= maxDist * maxDist;
	}
	
	public void selectedPlanet(Planet planet, MouseEvent me) {}
	
	public void keyPressed(KeyEvent key) {}
	
	public static enum Action {
		EXPLORE {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getExplorePrice());
				controller.exploredPlanets.add(toTarget);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null && toTarget != null && fromTarget != toTarget &&
						fromTarget.getOwner() == controller.getPlayer() &&
						!controller.exploredPlanets.contains(toTarget) &&
						controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getExplorePrice() &&
						controller.withinRange(fromTarget, toTarget);
			}
		},
		TAX {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().increaseMoney(fromTarget.doTax());
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null && toTarget == null && fromTarget.getOwner() == controller.getPlayer();
			}
		},
		BUY_SHIP {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getAndIncreaseShipPrice());
				fromTarget.addShip();
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null && toTarget == null && fromTarget.getOwner() == controller.getPlayer() &&
						controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getShipPrice();
			}
		},
		SEND_ONE_SHIP {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				fromTarget.removeShip().setToPlanet(toTarget, 0);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return toTarget != null && fromTarget != null && fromTarget != toTarget &&
						fromTarget.getOwner() == controller.getPlayer() && controller.isExplored(toTarget) &&
						fromTarget.getShips().size() > 0 && controller.withinRange(fromTarget, toTarget);
			}
		},
		SEND_HALF_SHIPS {
			private int count;
			
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				ArrayList<Ship> ships = fromTarget.getShips();
				
				count = 0;
				ships.stream().skip(ships.size() / 2).forEach((Ship s) -> s.setToPlanet(toTarget, count++));
				
				fromTarget.removeShips(count);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return toTarget != null && fromTarget != null && fromTarget != toTarget &&
						fromTarget.getOwner() == controller.getPlayer() && controller.isExplored(toTarget) &&
						fromTarget.getShips().size() > 0 && controller.withinRange(fromTarget, toTarget);
			}
		},
		SEND_ALL_SHIPS {
			private int count;
			
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				ArrayList<Ship> ships = fromTarget.getShips();
				
				count = 0;
				ships.stream().forEach((Ship s) -> s.setToPlanet(toTarget, count++));
				
				fromTarget.removeShips(count);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return toTarget != null && fromTarget != null && fromTarget != toTarget &&
						fromTarget.getOwner() == controller.getPlayer() && controller.isExplored(toTarget) &&
						fromTarget.getShips().size() > 0 && controller.withinRange(fromTarget, toTarget);
			}
		},
		UPGRADE_FUEL_RANGE {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getAndIncreaseFuelRangePrice());
				controller.getPlayer().getStats().increaseFuelRange();
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getFuelRangePrice();
			}
		},
		UPGRADE_WEAPONS {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getAndIncreaseWeaponUpgradePrice());
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getWeaponUpgradePrice();
			}
		},
		UPGRADE_DEFENSE {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getAndIncreaseDefenseUpgradePrice());
				fromTarget.increaseDefenseLevel(controller.getPlayer().getStats().getDefenseUpgradeValue());
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null && toTarget == null && fromTarget.getOwner() == controller.getPlayer() &&
						controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getDefenseUpgradePrice();
			}
		},
		BUY_CONNECTION {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getAndIncreaseConnectionPrice());
				
				Connection connection = new Connection(fromTarget, toTarget);
				fromTarget.addConnection(connection);
				toTarget.addConnection(connection);
				arena.add(Arena.CONTROLLER_Z, connection);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null && toTarget != null && fromTarget != toTarget &&
						fromTarget.getOwner() == controller.getPlayer() && fromTarget.getOwner() == toTarget.getOwner() &&
						controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getConnectionPrice() &&
						controller.withinRange(fromTarget, toTarget);
			}
		};
		
		public void act(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
			if(!canAct(controller, arena, fromTarget, toTarget))
				throw new IllegalArgumentException("Invalid action " + this);
			
			doAct(controller, arena, fromTarget, toTarget);
		}
		
		abstract void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget);
		
		public abstract boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget);
	}
}
