package com.ra4king.ludumdare30.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Game;
import com.ra4king.ludumdare30.arena.Arena;
import com.ra4king.ludumdare30.arena.Connection;
import com.ra4king.ludumdare30.arena.Planet;
import com.ra4king.ludumdare30.arena.Player;
import com.ra4king.ludumdare30.arena.Ship;

/**
 * @author Roi Atalla
 */
public abstract class Controller {
	protected Game game;
	protected Arena arena;
	protected Player player;
	
	protected Set<Planet> discoveredPlanets = new HashSet<>();
	
	public Controller(Game game, Arena arena, Player player) {
		this.game = game;
		this.arena = arena;
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isDiscovered(Planet planet) {
		return discoveredPlanets.contains(planet);
	}
	
	/**
	 * @return true = completed turn, false = still waiting on input
	 */
	public abstract boolean doTurn();
	
	public void draw(Graphics2D g) {
		arena.getEntitiesAt(Arena.PLANET_Z).stream().forEach((Entity e) -> {
			Planet p = (Planet)e;
			if(!(this instanceof UserController) || (p.getOwner() != player && !isDiscovered(p))) {
				g.setColor(new Color(0.2f, 0.2f, 0.2f, 1));
				
				Ellipse2D.Double bounds = p.getBoundsEllipse();
				g.fill(new Ellipse2D.Double(bounds.getX() - 6, bounds.getY() - 6, bounds.getWidth() + 12, bounds.getHeight() + 12));
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
		
		return dx * dx + dy * dy <= player.getStats().getFuelRange() * player.getStats().getFuelRange();
	}
	
	public void selectedPlanet(Planet planet, MouseEvent me) {}
	
	public void keyPressed(KeyEvent key) {}
	
	public static enum Action {
		EXPLORE {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().decreaseMoney(controller.getPlayer().getStats().getExplorePrice());
				controller.discoveredPlanets.add(fromTarget);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				boolean initial = fromTarget != null && !controller.discoveredPlanets.contains(fromTarget)
						&& controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getExplorePrice();
				
				if(!initial)
					return false;
				
				Area test = new Area(controller.getPlayer().getPlanetsFuelRangeArea());
				test.intersect(new Area(fromTarget.getBoundsEllipse()));
				return !test.isEmpty();
			}
		},
		TAX {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				controller.getPlayer().increaseMoney(fromTarget.doTax());
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return fromTarget != null;
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
				return fromTarget != null && controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getShipPrice();
			}
		},
		SEND_ONE_SHIP {
			@Override
			void doAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				fromTarget.removeShip().setToPlanet(toTarget, 0);
			}
			
			@Override
			public boolean canAct(Controller controller, Arena arena, Planet fromTarget, Planet toTarget) {
				return toTarget != null && fromTarget != null && fromTarget.getOwner() == controller.getPlayer() &&
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
				return toTarget != null && fromTarget != null && fromTarget.getOwner() == controller.getPlayer() &&
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
				return toTarget != null && fromTarget != null && fromTarget.getOwner() == controller.getPlayer() &&
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
				return fromTarget != null && controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getDefenseUpgradePrice();
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
				return fromTarget != null && toTarget != null && fromTarget.getOwner() == controller.getPlayer() && fromTarget.getOwner() == toTarget.getOwner() &&
						controller.getPlayer().getMoney() >= controller.getPlayer().getStats().getConnectionPrice();
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
