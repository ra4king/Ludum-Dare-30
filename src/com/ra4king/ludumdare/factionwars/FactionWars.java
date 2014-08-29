package com.ra4king.ludumdare.factionwars;

import java.awt.Graphics2D;

import com.ra4king.gameutils.Game;

/**
 * @author Roi Atalla
 */
public class FactionWars extends Game {
	public static void main(String[] args) {
		FactionWars game = new FactionWars();
		game.setupFrame("LD30 - Faction Wars", false);
		game.start();
	}
	
	public FactionWars() {
		super(1000, 600);
	}
	
	@Override
	protected void initGame() {
		showFPS(false);
		
		for(int a = 1; a <= 5; a++) {
			try {
				getArt().add("planet" + a + ".png");
			} catch(Exception exc) {
				System.err.println("Loading 'planet" + a + ".png' failed.");
				exc.printStackTrace();
			}
		}
		
		for(int a = 1; a <= 3; a++) {
			try {
				getArt().add("button" + a + ".png");
			} catch(Exception exc) {
				System.err.println("Loading 'button" + a + ".png' failed.");
				exc.printStackTrace();
			}
		}
		
		try {
			getArt().add("Splash.png");
			getArt().add("sidebar.png");
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		
		setScreen("Splash", new SplashScreen());
	}
	
	@Override
	protected void update(long deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	protected void paint(Graphics2D g) {
		super.paint(g);
	}
}
