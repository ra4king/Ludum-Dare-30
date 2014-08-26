package com.ra4king.ludumdare.factionwars.arena;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.ra4king.gameutils.Entity;
import com.ra4king.gameutils.Game;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.ludumdare.factionwars.Background;
import com.ra4king.ludumdare.factionwars.controller.AIController;
import com.ra4king.ludumdare.factionwars.controller.Controller;
import com.ra4king.ludumdare.factionwars.controller.UserController;
import com.ra4king.ludumdare.factionwars.ui.SidePanel;

/**
 * @author Roi Atalla
 */
public class Arena extends GameWorld {
	private static final int MAX_PLAYERS = 4;
	
	public static final int PLANET_COUNT = 30;
	
	public static final int CONTROLLER_Z = 1, PLANET_Z = 2, SHIP_Z = 3, SIDE_PANEL_Z = 4;
	
	private double arenaWidth, arenaHeight;
	
	private int currentTurn;
	private Controller[] controllers = new Controller[MAX_PLAYERS];
	
	private SidePanel sidePanel;
	
	public Arena() {}
	
	public Controller getCurrentController() {
		return controllers[currentTurn];
	}
	
	@Override
	public void init(Game game) {
		super.init(game);
		
		arenaWidth = getWidth() - 200;
		arenaHeight = getHeight();
		
		setBackground(new Color(0.1f, 0.1f, 0.1f));
		
		add(0, new Background(0, 0, getWidth(), arenaHeight));
		
		controllers[0] = new UserController(game, this, new Player(1));
		controllers[1] = new AIController(game, this, new Player(2));
		controllers[2] = new AIController(game, this, new Player(3));
		controllers[3] = new AIController(game, this, new Player(4));
		
		int[] controllersPlanet = new int[4];
		for(int a = 0; a < controllersPlanet.length; a++) {
			do {
				controllersPlanet[a] = (int)(Math.random() * PLANET_COUNT);
				
				for(int b = a - 1; b >= 0; b--)
					if(controllersPlanet[a] == controllersPlanet[b]) {
						controllersPlanet[a] = -1;
						break;
					}
			} while(controllersPlanet[a] == -1);
		}
		
		final double diameter = 25, spacing = 60;
		
		for(int a = 0; a < PLANET_COUNT; a++) {
			Planet planet = new Planet();
			
			boolean intersecting;
			do {
				planet.setBounds(Math.random() * (arenaWidth - diameter - spacing) + spacing * 0.5, Math.random() * (arenaHeight - diameter - spacing) + spacing * 0.5, diameter, diameter);
				
				intersecting = getEntities().stream().anyMatch(
						(Entity e) -> e instanceof Planet && e.intersects(planet.getX() - spacing * 0.5, planet.getY() - spacing * 0.5, planet.getWidth() + spacing, planet.getHeight() + spacing));
			} while(intersecting);
			
			for(int b = 0; b < controllersPlanet.length; b++) {
				if(controllersPlanet[b] == a) {
					planet.setOwner(controllers[b].getPlayer());
					controllers[b].addExploredPlanet(planet);
				}
			}
			
			add(PLANET_Z, planet);
		}
		
		sidePanel = new SidePanel((UserController)controllers[0], arenaWidth, 0, 300, arenaHeight);
		add(SIDE_PANEL_Z, sidePanel);
	}
	
	@Override
	public void update(long deltaTime) {
		if(controllers[currentTurn].doTurn()) {
			getEntitiesAt(PLANET_Z).stream().filter(e -> ((Planet)e).getOwner() == controllers[currentTurn].getPlayer()).forEach(e -> ((Planet)e).step());
			getEntitiesAt(CONTROLLER_Z).stream()
					.filter(e -> e instanceof Connection && ((Connection)e).getFrom().getOwner() == controllers[currentTurn].getPlayer()).forEach(e -> ((Connection)e).step());
			
			currentTurn = (currentTurn + 1) % MAX_PLAYERS;
			
			sidePanel.updateUI();
		}
		
		super.update(deltaTime);
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		
		controllers[currentTurn].draw(g);
	}
	
	@Override
	public void mousePressed(MouseEvent me) {
		Controller currentController;
		if(!((currentController = controllers[currentTurn]) instanceof UserController))
			return;
		
		for(Entity e : getEntitiesAt(PLANET_Z)) {
			Planet p = (Planet)e;
			
			if(p.contains(me.getX(), me.getY())) {
				currentController.selectedPlanet(p, me);
				sidePanel.updateUI();
				break;
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		Controller currentController;
		if((currentController = controllers[currentTurn]) instanceof UserController)
			currentController.keyPressed(key);
		
		sidePanel.updateUI();
	}
}
