package client.main;

import java.util.HashMap;
import java.util.Map.Entry;

public class Map {
	private HashMap<Point, Field> map;

	/**
	 * Constructor for Map class consisting of a HashMap<Point, Field>
	 * 
	 * @param map HashMap<Point,Field> play the role of the map itself
	 */
	public Map(HashMap<Point, Field> map) {
		this.map = map;
	}

	/**
	 * @return HashMap<Point,Field> playing the role of the map
	 */
	public HashMap<Point, Field> getMap() {
		return this.map;
	}

	/**
	 * Method that updates the current Map object, in order the position of players,
	 * treasures and fortresses to be intact with the map on server, sets the
	 * HashMap attribute of the object to be equal to the HashMap attribute in the
	 * given as a parameter Map.
	 * 
	 * @param map Map gotten from the server that has all the information up to
	 *            date.
	 */
	public void updateMap(Map map) {
		this.map = map.getMap();
	}

	/**
	 * Method that finds where the player currently is on the map, check the field
	 * object stored in every entry in the hashmap attribute of the map if the
	 * PlayerOnPosition attribute is equal to PLAYER1 or BOTH
	 * 
	 * @return the current position of the player on the map
	 */
	public Point getCurrentPosition() {
		Point p = null;
		for (Entry<Point, Field> entry : this.map.entrySet()) {
			if (entry.getValue().getPlayer() == PlayerOnPosition.Player1
					|| entry.getValue().getPlayer() == PlayerOnPosition.Both) {
				p = entry.getKey();
				break;
			}
		}
		return p;
	}

	/**
	 * Method that checks if the treasure that the player seeks is present on the
	 * map, check every field object stored in every entry of the hashmap, if the
	 * Treasure object is equal to MyTreasure
	 * 
	 * @return whether or not the seeked treasure is present or not
	 */
	public Boolean isTreasurePresent() {
		for (Entry<Point, Field> entry : this.map.entrySet()) {
			if (entry.getValue().getTreasure() == Treasure.MyTreasure)
				return true;
		}
		return false;
	}

	/**
	 * Method that checks if the fortress of the enemy player is present on the map,
	 * check every field object stored in every entry of the hashmap, if the
	 * fortress object is equal to EnemyFortress
	 * 
	 * @return whether or not the enemy fortress is present or not
	 */
	public Boolean isEnemyFortressPresent() {
		for (Entry<Point, Field> entry : this.map.entrySet()) {
			if (entry.getValue().getFortress() == Fortress.EnemyFortress)
				return true;
		}
		return false;
	}

	/**
	 * Method that seeks and returns the Point where the treasure is located, checks
	 * every field object stored in every entry of the hashmap, and if the Treasure
	 * attribute is equal to MyTreasure returns the point associated to it.
	 * 
	 * @return Point object where Treasure is located

	 */
	public Point getTreasurePosition() {
		Point p = new Point(0,0);
		for (Entry<Point, Field> entry : this.map.entrySet()) {
			if (entry.getValue().getTreasure().equals(Treasure.MyTreasure)) {
				p.setX(entry.getKey().getX());
				p.setY(entry.getKey().getY());
			}
		}
		return p;
	}

	/**
	 * Method that seeks and returns the Point where the enemy fortress is located,
	 * checks every field object stored in every entry of the hashmap, and if the
	 * fortress attribute is equal to EnemyFortress and returns the point associated
	 * to it. 
	 * 
	 * @return Point object where EnemyFortress is located
	 */
	public Point getEnemyFortressPosition() {
		Point p = new Point(0, 0);
		for (Entry<Point, Field> entry : this.map.entrySet()) {
			if (entry.getValue().getFortress() == Fortress.EnemyFortress) {
				p.setX(entry.getKey().getX());
				p.setY(entry.getKey().getY());
			}
		}
		return p;
	}
}
