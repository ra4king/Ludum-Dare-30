package com.ra4king.ludumdare30.arena;

import java.awt.Color;
import java.awt.Graphics2D;

import com.ra4king.gameutils.gameworld.GameComponent;

/**
 * @author Roi Atalla
 */
public class Connection extends GameComponent {
	private Planet from, to;
	
	public Connection(Planet from, Planet to) {
		this.from = from;
		this.to = to;
	}
	
	public Planet getFrom() {
		return from;
	}
	
	public Planet getTo() {
		return to;
	}
	
	public void step() {
		from.increaseTaxAmount(to.doTax());
	}
	
	@Override
	public void update(long deltaTime) {}
	
	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawLine(from.getIntCenterX(), from.getIntCenterY(), to.getIntCenterX(), to.getIntCenterY());
	}
}
