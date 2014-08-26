package com.ra4king.ludumdare30;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.gameworld.GameWorld;

public class Background extends GameComponent {
	private ArrayList<Star> stars;
	
	private final int STAR_COUNT = 600;
	private final Color STAR_COLOR = new Color(0.5f, 0.5f, 0.5f);
	
	public Background(double x, double y, double width, double height) {
		super(x, y, width, height);
		stars = new ArrayList<>(STAR_COUNT);
	}
	
	public void init(GameWorld space) {
		super.init(space);
		
		for(int a = 0; a < STAR_COUNT; a++)
			stars.add(new Star(Math.random() * getWidth(), Math.random() * getHeight(), 1));
	}
	
	@Override
	public void update(long deltaTime) {}
	
	@Override
	public void draw(Graphics2D g) {
		for(Star s : stars)
			s.draw(g);
	}
	
	private class Star {
		private double x, y;
		private int size;
		
		public Star(double x, double y, double size) {
			this.x = x;
			this.y = y;
			this.size = (int)Math.round(size);
		}
		
		public void draw(Graphics2D g) {
			g.setColor(STAR_COLOR);
			g.fillOval((int)Math.round(x), (int)Math.round(y), size, size);
		}
	}
}
