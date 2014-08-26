package com.ra4king.ludumdare30.arena;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.ludumdare30.ui.Events;

/**
 * @author Roi Atalla
 */
public class Planet extends GameComponent {
	private Player owner;
	private ArrayList<Ship> ships = new ArrayList<>();
	private Ellipse2D.Double bounds = new Ellipse2D.Double();
	private long elapsedTime;
	
	private Set<Connection> connections = new HashSet<>();
	
	private int taxProduced;
	private double defense;
	
	private int planetImage;
	
	private static final int taxStep = 10;
	
	public static final Color[] playerColors = {
			new Color(0.4f, 0.4f, 0.4f, 0.6f),
			new Color(0.1f, 0.6f, 1.0f, 0.6f),
			new Color(1.0f, 0.1f, 0.1f, 0.6f),
			new Color(0.1f, 1.0f, 0.1f, 0.6f),
			new Color(1.0f, 0.5f, 0.1f, 0.6f)
	};
	
	public Planet() {
		this(0, 0, 0, 0);
	}
	
	public Planet(double x, double y, double width, double height) {
		super(x, y, width, height);
		
		taxProduced = taxStep;
		
		planetImage = (int)(Math.random() * 5) + 1;
	}
	
	@Override
	public void init(GameWorld world) {}
	
	public boolean intersects(Ship ship) {
		if(!super.intersects(ship) || ship.getToPlanet() != this)
			return false;
		
		if(owner != ship.getOwner() && (ships.size() > 0 || defense > 0)) {
			if(ships.size() > 0) {
				Ship toRemove = removeShip();
				
				getParent().remove(toRemove);
				getParent().remove(ship);
				
				ship.setAlive(false);
				toRemove.setAlive(false);
			} else if(defense > 0) {
				defense = Math.max(0.0, defense - ship.getOwner().getStats().getWeaponDamage());
			}
		} else if(owner == ship.getOwner()) {
			ship.setFromPlanet(this, elapsedTime);
			ships.add(ship);
		}
		
		if(ships.size() == 0 && defense == 0.0) {
			if(owner == null) {
				Events.pushEvent(ship.getOwner() + " took over a planet.");
			}
			else if(owner != ship.getOwner()) {
				Events.pushEvent(ship.getOwner() + " took over " + owner + "' planet!");
			}

			connections.stream().forEach(getParent()::remove);
			connections.clear();
			
			setOwner(ship.getOwner());
			ship.setFromPlanet(this, elapsedTime);
			ships.add(ship);
		}
		
		return true;
	}
	
	public Ellipse2D.Double getBoundsEllipse() {
		bounds.setFrame(getX(), getY(), getWidth(), getHeight());
		return bounds;
	}
	
	@Override
	public boolean contains(double x, double y) {
		return getBoundsEllipse().contains(x, y);
	}
	
	@Override
	public boolean intersects(Rectangle2D.Double r) {
		return getBoundsEllipse().intersects(r);
	}
	
	@Override
	public boolean intersects(Entity other) {
		return intersects(other.getBounds());
	}
	
	@Override
	public boolean intersects(double x, double y, double width, double height) {
		return getBoundsEllipse().intersects(x, y, width, height);
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setOwner(Player owner) {
		if(this.owner != null)
			this.owner.removePlanet(this);
		
		this.owner = owner;
		
		if(owner != null)
			owner.addPlanet(this);
	}
	
	public Set<Connection> getConnections() {
		return connections;
	}
	
	public void addConnection(Connection c) {
		connections.add(c);
	}
	
	public int getTaxAmount() {
		return taxProduced;
	}
	
	public void increaseTaxAmount(int amount) {
		taxProduced += amount;
	}
	
	public int doTax() {
		int tax = taxProduced;
		taxProduced = 0;
		return tax;
	}
	
	public double getDefense() {
		return defense;
	}
	
	public void increaseDefenseLevel(double increase) {
		defense += increase;
	}
	
	public void addShip() {
		Ship s = new Ship(owner, this, elapsedTime);
		ships.add(s);
		getParent().add(Arena.SHIP_Z, s);
	}
	
	public Ship removeShip() {
		return ships.remove(ships.size() - 1);
	}
	
	public void removeShips(int count) {
		while(count-- > 0)
			removeShip();
	}
	
	public ArrayList<Ship> getShips() {
		return ships;
	}
	
	public void step() {
		taxProduced += taxStep;
	}
	
	@Override
	public void update(long deltaTime) {
		elapsedTime += deltaTime;
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(getParent().getGame().getArt().get("planet" + planetImage), getIntX(), getIntY(), getIntWidth(), getIntHeight(), null);
		
		g.setColor(playerColors[owner == null ? 0 : owner.getID()]);
		g.fill(new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight()));

		Stroke old = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.setColor(playerColors[owner == null ? 0 : owner.getID()]);
		g.drawArc((int)Math.round(getX() - 3), (int)Math.round(getY() - 3), (int)Math.round(getWidth() + 6), (int)Math.round(getHeight() + 6), 90, -1 * (int)Math.round(defense * 5));
		g.setStroke(old);
	}
}
