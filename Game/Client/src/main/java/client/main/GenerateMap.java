package client.main;

import java.util.HashMap;
import java.util.Map.Entry;

public class GenerateMap {
	private Map map;

	/**
	 * Constructor for the GenereateMap class on initialization of an object the
	 * generate() method is automatically called
	 */
	public GenerateMap() {
		this.map = generate();
	}

	public Map getMap() {
		return this.map;
	}

	/**
	 * Method that generates a random HashMap<Point,Field> that is used to create a
	 * Map object, where the game will be played, Map is generated at 4x8 aspect
	 * with random terrains and a 30% chance to place a fortress on a given tile,
	 * after generating the map it is checked for legality.
	 * 
	 * @return HalfMap that needs to be sent to the network.
	 */
	private Map generate() {
		HashMap<Point, Field> map = new HashMap<>();
		do {
			map.clear();
			Boolean fortressPlaced = false;
			for (int y = 0; y < Global.maxY; y++) {
				for (int x = 0; x < Global.maxX; x++) {
					Point p = new Point(x, y);
					Fortress fort = Fortress.None;
					Terrain t = Terrain.getRandomTerrain();
					if (!fortressPlaced && t == Terrain.Grass) {
						if (Math.random() < 0.3) {
							fort = Fortress.MyFortress;
							fortressPlaced = true;
						}
					}
					Field f = new Field(t, fort, Treasure.None, PlayerOnPosition.None);
					map.put(p, f);
				}
			}
		} while (!check(map));
		Map m = new Map(map);
		return m;
	}

	/**
	 * Implementation of the floodfill algorithm, to check if every field of the map
	 * is reachable, when the algorithm reaches a given field its visited value its
	 * set to true. Every call of the algorithm calls finishes with a recursive call
	 * to its neighbor fields.
	 * 
	 * @param map the map thats needs to be traversed
	 * @param x   current position on the x-axis
	 * @param y   current position on the y-axis
	 */
	private void traverse(Field[][] map, int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 3)
			return;
		if (map[x][y].getTerrain() == Terrain.Water || map[x][y].getVisited() == true)
			return;
		map[x][y].setVisited(true);

		traverse(map, x, y + 1);
		traverse(map, x, y - 1);
		traverse(map, x - 1, y);
		traverse(map, x + 1, y);

	}

	/**
	 * Function that checks the validity of a map. Map needs to fulfill a couple of
	 * constraints: 1) Mountain fields need to be at least 3 2) Water fields need to
	 * be at least 4 3) Grass fields need to be at least 15 4) A fortress needs to
	 * be present 5) Every field must be reachable (no islands present) checked in
	 * the traverse function 6) Water fields on the width of the borders must be at
	 * most 3 7) Water fields on the height of the borders must be at most 1
	 * 
	 * @param m the map thats needs to be checked for its validity
	 * @return validity of map: true = valid, false = invalid
	 */
	private Boolean check(HashMap<Point, Field> m) {
		int mountainCount = 0;
		int waterCount = 0;
		int grassCount = 0;
		int widthBorderWater = 0;
		int heightBorderWater = 0;
		Boolean fortressPresent = false;
		for (Entry<Point, Field> entry : m.entrySet()) {
			switch (entry.getValue().getTerrain()) {
			case Mountain:
				mountainCount++;
				break;
			case Water:
				waterCount++;
				break;
			case Grass:
				grassCount++;
				break;
			}
			switch (entry.getValue().getFortress()) {
			case MyFortress:
				fortressPresent = true;
				break;
			default:
				break;
			}
		}

		Field[][] temp = new Field[8][4];
		for (Entry<Point, Field> entry : m.entrySet()) {
			temp[entry.getKey().getX()][entry.getKey().getY()] = entry.getValue();
		}
		traverse(temp, 0, 0);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 8; x++) {
				if (y == 0 || y == 3 || x == 0 || x == 7) {
					if (temp[x][y].getTerrain() == Terrain.Water) {
						if (y == 0 || y == 3)
							widthBorderWater++;
						if (x == 0 || x == 7)
							heightBorderWater++;
					}
				}
				if (temp[x][y].getTerrain() != Terrain.Water && temp[x][y].getVisited() == false)
					return false;
			}
		}
		for (Entry<Point, Field> e : m.entrySet()) {
			e.getValue().setVisited(false);
		}
		return mountainCount >= 3 && waterCount >= 4 && grassCount >= 15 && fortressPresent && widthBorderWater <= 3
				&& heightBorderWater <= 1;

	}
}
