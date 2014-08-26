package com.ra4king.ludumdare30.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.ludumdare30.arena.Arena;
import com.ra4king.ludumdare30.arena.Planet;
import com.ra4king.ludumdare30.arena.Player;

/**
 * @author Roi Atalla
 */
public class UserController extends Controller {
	private Planet selectedFromPlanet, selectedToPlanet;
	
	private Action selectedAction;
	
	public UserController(Game game, Arena arena, Player player) {
		super(game, arena, player);
		
		arena.add(Arena.CONTROLLER_Z, new GameComponent() {
			@Override
			public void update(long deltaTime) {}
			
			private Ellipse2D.Double ellipse = new Ellipse2D.Double();
			
			@Override
			public void draw(Graphics2D g) {
				if(selectedFromPlanet != null && selectedFromPlanet.getOwner() == player) {
					g.setColor(new Color(0.6f, 0.6f, 0.6f, 0.2f));
					
					double fuelRange = player.getStats().getFuelRange();
					ellipse.setFrame(selectedFromPlanet.getX() - fuelRange, selectedFromPlanet.getY() - fuelRange,
							fuelRange * 2, fuelRange * 2);
					g.fill(ellipse);
				}
				
				g.setColor(new Color(0.8f, 0.8f, 0.8f, 0.05f));
				g.fill(player.getPlanetsFuelRangeArea());
			}
		});
	}
	
	public Planet getSelectedFromPlanet() {
		return selectedFromPlanet;
	}
	
	public Planet getSelectedToPlanet() {
		return selectedToPlanet;
	}
	
	@Override
	public boolean doTurn() {
		if(selectedAction == null)
			return false;
		
		try {
			if(selectedAction.canAct(this, arena, selectedFromPlanet, selectedToPlanet)) {
				act(selectedAction, selectedFromPlanet, selectedToPlanet);
				return true;
			}
		} finally {
			selectedAction = null;
			selectedToPlanet = null;
		}
		
		return false;
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		FontMetrics fm = g.getFontMetrics();
		
		arena.getEntitiesAt(Arena.PLANET_Z).stream().filter((Entity e) -> ((Planet)e).getOwner() == player).forEach((Entity e) -> {
			Planet p = (Planet)e;
			
			String text = "$" + p.getTaxAmount();
			Rectangle2D rect = fm.getStringBounds(text, g);
			g.drawString(text, (float)(p.getCenterX() - rect.getWidth() * 0.5), (float)(p.getCenterY() + rect.getHeight() * 0.25));
		});
		
		if(selectedFromPlanet != null) {
			Stroke old = g.getStroke();
			
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.WHITE);
			
			if(isDiscovered(selectedFromPlanet) || selectedFromPlanet.getOwner() == player)
				g.drawOval(selectedFromPlanet.getIntX(), selectedFromPlanet.getIntY(), selectedFromPlanet.getIntWidth(), selectedFromPlanet.getIntHeight());
			else
				g.drawOval(selectedFromPlanet.getIntX() - 6, selectedFromPlanet.getIntY() - 6, selectedFromPlanet.getIntWidth() + 12, selectedFromPlanet.getIntHeight() + 12);
			
			g.setStroke(old);
		}
		
		if(selectedToPlanet != null) {
			Stroke old = g.getStroke();
			
			g.setStroke(new BasicStroke(2));
			g.setColor(Color.CYAN);
			
			if(isDiscovered(selectedToPlanet) || selectedToPlanet.getOwner() == player)
				g.drawOval(selectedToPlanet.getIntX(), selectedToPlanet.getIntY(), selectedToPlanet.getIntWidth(), selectedToPlanet.getIntHeight());
			else
				g.drawOval(selectedToPlanet.getIntX() - 6, selectedToPlanet.getIntY() - 6, selectedToPlanet.getIntWidth() + 12, selectedToPlanet.getIntHeight() + 12);
			
			g.setStroke(old);
		}
	}
	
	@Override
	public void selectedPlanet(Planet target, MouseEvent me) {
		if(me.getButton() == MouseEvent.BUTTON1) {
			selectedFromPlanet = target;
			selectedToPlanet = null;
		}
		else if(me.getButton() == MouseEvent.BUTTON3 && selectedFromPlanet != null && selectedFromPlanet.getOwner() == player && selectedFromPlanet != target) {
			selectedToPlanet = target;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		switch(key.getKeyCode()) {
			case KeyEvent.VK_Q:
				setAction(Action.SEND_ONE_SHIP);
				break;
			case KeyEvent.VK_W:
				setAction(Action.SEND_HALF_SHIPS);
				break;
			case KeyEvent.VK_E:
				setAction(Action.SEND_ALL_SHIPS);
				break;
			case KeyEvent.VK_F:
				setAction(Action.EXPLORE);
				break;
			case KeyEvent.VK_R:
				setAction(Action.BUY_SHIP);
				break;
			case KeyEvent.VK_T:
				setAction(Action.TAX);
				break;
		}
	}
	
	public void setAction(Action action) {
		selectedAction = action;
	}
}
