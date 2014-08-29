package com.ra4king.ludumdare.factionwars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ra4king.gameutils.BasicScreen;
import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.Screen;

/**
 * @author Roi Atalla
 */
public class GameOverScreen extends BasicScreen {
	private Screen backgroundScreen;
	private boolean isPlayerWon;
	
	public GameOverScreen(Screen backgroundScreen, boolean isPlayerWon) {
		this.backgroundScreen = backgroundScreen;
		this.isPlayerWon = isPlayerWon;
	}
	
	@Override
	public void init(Game game) {
		super.init(game);
	}
	
	@Override
	public void update(long deltaTime) {
		backgroundScreen.update(deltaTime);
	}
	
	@Override
	public void draw(Graphics2D g) {
		backgroundScreen.draw(g);
		
		g.setColor(new Color(0, 0, 0, 0.6f));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		g.setColor(Color.ORANGE);
		g.drawString(isPlayerWon ? "You won!" : "You lost! :(", getWidth() / 2 - 100, getHeight() / 2 + 10);
	}
}
