package com.ra4king.ludumdare.factionwars.arena;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.gameworld.GameComponent;

/**
 * @author Roi Atalla
 */
public class Ship extends GameComponent {
	private static final double STEP = 0.03;
	private static final HashMap<Planet,Long> offsets = new HashMap<Planet,Long>();
	
	private Player owner;
	private Planet fromPlanet, toPlanet;
	
	private long moveStartTime;
	private int moveStartCount;
	
	private long elapsedTime;
	private Point2D.Double lastLocation = new Point2D.Double();
	private double angle;
	
	private static double ORBIT_SPEED = 30, TRAVEL_SPEED = 300;
	
	public Ship(Player owner, Planet home, long totalElapsedTime) {
		this.owner = owner;
		
		setFromPlanet(home, totalElapsedTime);
		
		setSize(3, 1);
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public Planet getFromPlanet() {
		return fromPlanet;
	}
	
	public void setFromPlanet(Planet fromPlanet, long totalElapsedTime) {
		Planet oldFromPlanet = this.fromPlanet;
		
		this.fromPlanet = fromPlanet;
		
		double totalTime = 1e9 * (Math.PI * getOrbitDiameter()) / ORBIT_SPEED;
		
		long offset = 0;
		if(oldFromPlanet == null) {
			Long l;
			if((l = offsets.get(fromPlanet)) != null) {
				offset = l;
			} else {
				offset = (long)(Math.random() * totalTime);
				offsets.put(fromPlanet, offset);
			}
		} else if(fromPlanet.getShips().size() == 0 && toPlanet != null) {
			double incidentAngle = angle - Math.PI * 0.25;
			offset = (long)(totalTime * (incidentAngle / (2 * Math.PI)));
			offsets.put(fromPlanet, offset);
		} else {
			offset = offsets.get(fromPlanet);
		}
		
		this.toPlanet = null;
		
		elapsedTime = offset + totalElapsedTime - (long)(fromPlanet.getShips().size() * STEP * totalTime);
	}
	
	public Planet getToPlanet() {
		return toPlanet;
	}
	
	public void setToPlanet(Planet toPlanet, int count) {
		this.toPlanet = toPlanet;
		
		moveStartTime = elapsedTime;
		moveStartCount = count;
	}
	
	private double getOrbitDiameter() {
		return fromPlanet.getWidth() + 10;
	}
	
	public boolean intersects(Ship other) {
		if(!super.intersects(other) || owner == other.owner)
			return false;
		
		setAlive(false);
		getParent().remove(this);
		
		other.setAlive(false);
		getParent().remove(other);
		
		return true;
	}
	
	@Override
	public void update(long deltaTime) {
		elapsedTime += deltaTime;
		
		double orbitDiameter = getOrbitDiameter();
		double circumference = Math.PI * orbitDiameter;
		double radius = orbitDiameter * 0.5;
		
		if(toPlanet == null) {
			angle = 2 * Math.PI * (elapsedTime / 1e9) * (ORBIT_SPEED / circumference);
			
			lastLocation.setLocation(fromPlanet.getCenterX() + radius * Math.cos(angle + Math.PI * 0.5),
					fromPlanet.getCenterY() - radius * -Math.sin(angle + Math.PI * 0.5));
			setLocation(lastLocation);
		} else {
			long elapsedSinceMove = elapsedTime - moveStartTime;
			
			double distTraveled = radius + (moveStartCount % 5) * getWidth() * 1.3 + (elapsedSinceMove / 1e9) * TRAVEL_SPEED;
			angle = Math.atan2(toPlanet.getY() - fromPlanet.getY(), toPlanet.getX() - fromPlanet.getX());
			
			lastLocation.setLocation(fromPlanet.getCenterX() + distTraveled * Math.cos(angle),
					fromPlanet.getCenterY() + distTraveled * Math.sin(angle));
			setLocation(lastLocation);
			
			for(Entity e : getParent().getEntitiesAt(Arena.PLANET_Z)) {
				if(((Planet)e).intersects(this) && !isAlive())
					break;
			}
			
			if(isAlive()) {
				for(Entity e : getParent().getEntitiesAt(Arena.SHIP_Z)) {
					if(((Ship)e).intersects(this) && !isAlive())
						break;
				}
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		AffineTransform old = g.getTransform();
		AffineTransform transform = new AffineTransform();
		transform.rotate(angle, lastLocation.getX(), lastLocation.getY());
		g.setTransform(transform);
		    
		g.setColor(Color.WHITE);
		g.fill(getBounds());
		
		g.setTransform(old);
	}
}
