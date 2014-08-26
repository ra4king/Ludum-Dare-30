package com.ra4king.ludumdare.factionwars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.ludumdare.factionwars.arena.Arena;

/**
 * @author Roi Atalla
 */
public class Splash extends BasicScreen {
	@Override
	public void update(long deltaTime) {
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.setBackground(new Color(0.4f, 0.4f, 0.6f, 1));
		g.clearRect(0, 0, getWidth(), getHeight());
		
		Image splash = getGame().getArt().get("Splash");
		g.drawImage(splash, Math.round((getWidth() - splash.getWidth(null)) / 2), Math.round((getHeight() - splash.getHeight(null)) / 2), null);
		
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		g.setColor(Color.BLACK);
		g.drawString("Click anywhere to play!", getWidth() / 2 - 120, getHeight() - 150);
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		getGame().setScreen("Arena", new Arena());
	}
}
