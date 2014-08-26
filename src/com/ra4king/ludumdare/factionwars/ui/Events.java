package com.ra4king.ludumdare.factionwars.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Roi Atalla
 */
public class Events {
	private static Queue<String> events = new ConcurrentLinkedQueue<>();
	
	public static void pushEvent(String event) {
		events.add(event);
	}
	
	Queue<String> getEvents() {
		return events;
	}
}
