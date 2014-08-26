package com.ra4king.ludumdare.factionwars.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ra4king.gameutils.gameworld.GameComponent;
import com.ra4king.gameutils.gameworld.GameWorld;
import com.ra4king.gameutils.gui.Button;
import com.ra4king.ludumdare.factionwars.arena.Arena;
import com.ra4king.ludumdare.factionwars.arena.Planet;
import com.ra4king.ludumdare.factionwars.arena.Player;
import com.ra4king.ludumdare.factionwars.controller.Controller.Action;
import com.ra4king.ludumdare.factionwars.controller.UserController;

/**
 * @author Roi Atalla
 */
public class SidePanel extends GameComponent {
	private UserController userController;
	private final int UI_Z = Arena.SIDE_PANEL_Z + 1;
	
	private static final String EXPLORE_TEXT = "Explore (F)";
	private static final String TAX_TEXT = "Tax (T)";
	private static final String BUY_TEXT = "Buy Ship (R)";
	private static final String SEND_ONE_TEXT = "Send one ship (Q)";
	private static final String SEND_HALF_TEXT = "Send half of ships (W)";
	private static final String SEND_ALL_TEXT = "Send all ships (E)";
	private static final String UPGRADE_FUEL_RANGE = "Upgrade Fuel Range";
	private static final String UPGRADE_WEAPONS_TEXT = "Upgrade Weapons";
	private static final String UPGRADE_DEFENSE_TEXT = "Upgrade Defense";
	private static final String BUY_CONNECTION = "Buy Connection";
	
	private Button explore, tax, buy, sendOne, sendHalf, sendAll;
	private Button upgradeFuelRange, upgradeWeapons, upgradeDefense, buyConnection;
	
	public SidePanel(UserController userController, double x, double y, double width, double height) {
		super(x, y, width, height);
		this.userController = userController;
		
		explore = new Button(EXPLORE_TEXT, 15, getX() + getWidth() / 3, 60, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.EXPLORE);
		});
		explore.setWidth(170);
		
		tax = new Button(TAX_TEXT, 15, getX() + getWidth() / 3, 80, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.TAX);
		});
		tax.setWidth(170);
		
		buy = new Button(BUY_TEXT, 15, getX() + getWidth() / 3, 120, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.BUY_SHIP);
		});
		buy.setWidth(170);
		
		sendOne = new Button(SEND_ONE_TEXT, 13, getX() + getWidth() / 3, 160, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.SEND_ONE_SHIP);
		});
		sendOne.setWidth(170);
		
		sendHalf = new Button(SEND_HALF_TEXT, 13, getX() + getWidth() / 3, 200, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.SEND_HALF_SHIPS);
		});
		sendHalf.setWidth(170);
		
		sendAll = new Button(SEND_ALL_TEXT, 13, getX() + getWidth() / 3, 240, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.SEND_ALL_SHIPS);
		});
		sendAll.setWidth(170);
		
		upgradeFuelRange = new Button(UPGRADE_FUEL_RANGE, 12, getX() + getWidth() / 3, 280, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.UPGRADE_FUEL_RANGE);
		});
		upgradeFuelRange.setWidth(170);
		
		upgradeWeapons = new Button(UPGRADE_WEAPONS_TEXT, 12, getX() + getWidth() / 3, 320, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.UPGRADE_WEAPONS);
		});
		upgradeWeapons.setWidth(170);
		
		upgradeDefense = new Button(UPGRADE_DEFENSE_TEXT, 12, getX() + getWidth() / 3, 360, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.UPGRADE_DEFENSE);
		});
		upgradeDefense.setWidth(170);
		
		buyConnection = new Button(BUY_CONNECTION, 13, getX() + getWidth() / 3, 400, 10, 10, true, (Button b) -> {
			Arena arena = (Arena)getParent();
			if(arena.getCurrentController() instanceof UserController)
				((UserController)arena.getCurrentController()).setAction(Action.BUY_CONNECTION);
		});
		buyConnection.setWidth(170);
	}
	
	@Override
	public void init(GameWorld world) {
		// BufferedImage[] images = {
		// (BufferedImage)getParent().getGame().getArt().get("button1"),
		// (BufferedImage)getParent().getGame().getArt().get("button2"),
		// (BufferedImage)getParent().getGame().getArt().get("button3")
		// };
		//
		// int r;
		// explore.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth() * 4, images[r].getHeight() * 4)));
		// tax.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth() * 4, images[r].getHeight() * 4)));
		// buy.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		// sendOne.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		// sendHalf.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		// sendAll.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		// upgradeDefense.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		// upgradeWeapons.setBackground(new TexturePaint(images[(r = (int)(Math.random() * 3))], new Rectangle2D.Double(0, 0, images[r].getWidth(), images[r].getHeight())));
		
		updateUI();
	}
	
	public void updateUI() {
		Arena arena = (Arena)getParent();
		
		arena.remove(explore);
		arena.remove(tax);
		arena.remove(buy);
		arena.remove(sendOne);
		arena.remove(sendHalf);
		arena.remove(sendAll);
		arena.remove(upgradeFuelRange);
		arena.remove(upgradeDefense);
		arena.remove(upgradeWeapons);
		arena.remove(buyConnection);
		
		Player player = userController.getPlayer();
		Planet fromPlanet = userController.getSelectedFromPlanet();
		Planet toPlanet = userController.getSelectedToPlanet();
		
		if(fromPlanet != null && fromPlanet == toPlanet)
			throw new IllegalStateException("What in the fuck?!");
		
		if(Action.TAX.canAct(userController, arena, fromPlanet, toPlanet)) {
			int taxAmount = fromPlanet.getTaxAmount();
			tax.setEnabled(taxAmount > 0);
			tax.setText(TAX_TEXT + " +$" + taxAmount);
			arena.add(UI_Z, tax);
		}
		
		if(Action.BUY_SHIP.canAct(userController, arena, fromPlanet, toPlanet)) {
			int shipPrice = player.getStats().getShipPrice();
			buy.setEnabled(player.getMoney() >= shipPrice);
			buy.setText(BUY_TEXT + " -$" + shipPrice);
			arena.add(UI_Z, buy);
		}
		
		if(Action.UPGRADE_DEFENSE.canAct(userController, arena, fromPlanet, toPlanet)) {
			int defenseUpgradePrice = player.getStats().getDefenseUpgradePrice();
			upgradeDefense.setEnabled(player.getMoney() >= defenseUpgradePrice);
			upgradeDefense.setText(UPGRADE_DEFENSE_TEXT + " -$" + defenseUpgradePrice);
			arena.add(UI_Z, upgradeDefense);
		}
		
		if(Action.SEND_ONE_SHIP.canAct(userController, arena, fromPlanet, toPlanet)) {
			arena.add(UI_Z, sendOne);
			arena.add(UI_Z, sendHalf);
			arena.add(UI_Z, sendAll);
		}
		
		if(Action.BUY_CONNECTION.canAct(userController, arena, fromPlanet, toPlanet)) {
			int connectionPrice = player.getStats().getConnectionPrice();
			buyConnection.setEnabled(player.getMoney() >= connectionPrice);
			buyConnection.setText(BUY_CONNECTION + " -$" + connectionPrice);
			arena.add(UI_Z, buyConnection);
		}
		
		if(Action.EXPLORE.canAct(userController, arena, fromPlanet, toPlanet)) {
			int explorePrice = player.getStats().getExplorePrice();
			explore.setEnabled(player.getMoney() >= explorePrice);
			explore.setText(EXPLORE_TEXT + " -$" + explorePrice);
			arena.add(UI_Z, explore);
		}
		
		if(Action.UPGRADE_FUEL_RANGE.canAct(userController, arena, fromPlanet, toPlanet)) {
			int upgradeFuelRangePrice = player.getStats().getFuelRangePrice();
			upgradeFuelRange.setText(UPGRADE_FUEL_RANGE + " -$" + upgradeFuelRangePrice);
			upgradeFuelRange.setEnabled(player.getMoney() >= upgradeFuelRangePrice);
			arena.add(UI_Z, upgradeFuelRange);
		}
		
		if(Action.UPGRADE_WEAPONS.canAct(userController, arena, fromPlanet, toPlanet)) {
			int upgradeWeaponsPrice = player.getStats().getWeaponUpgradePrice();
			upgradeWeapons.setText(UPGRADE_WEAPONS_TEXT + " -$" + upgradeWeaponsPrice);
			upgradeWeapons.setEnabled(player.getMoney() >= upgradeWeaponsPrice);
			arena.add(UI_Z, upgradeWeapons);
		}
	}
	
	@Override
	public void update(long deltaTime) {}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(getParent().getGame().getArt().get("sidebar"), getIntX(), getIntY(), getIntWidth(), getIntHeight(), null);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		g.drawString("Money : $" + userController.getPlayer().getMoney(), (float)(getX() + 30), 15);
		g.drawString("Damage: " + userController.getPlayer().getStats().getWeaponDamage(), (float)(getX() + 30), 30);
		
		Planet fromPlanet = userController.getSelectedFromPlanet();
		if(fromPlanet != null && (userController.isExplored(fromPlanet) || fromPlanet.getOwner() == userController.getPlayer())) {
			g.drawString("Selected planet:", (float)(getX() + 30), (float)(getHeight() - 100));
			g.drawString("Defense: " + String.format("%.1f", fromPlanet.getDefense()), (float)(getX() + 30), (float)(getHeight() - 85));
			g.drawString("Ships: " + fromPlanet.getShips().size(), (float)(getX() + 30), (float)(getHeight() - 70));
		}
	}
}
