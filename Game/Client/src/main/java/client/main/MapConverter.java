package client.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.FullMapNode;

public class MapConverter {
	/**
	 * Constructor for the MapConvertor class
	 */
	public MapConverter() {}
	/**
	 * Method that converts the generated Map to HalfMap in order to be sent to server,
	 * using stream over the entryset of the generated map and maping every entry to a HalfModeNode 
	 * and puting them into a List used to create a HalfMap Object
	 * @param playerid the id of the player that generated the map
	 * @param map map that needs to be converted to Halfmap
	 * @return the HalfMap ready to be sent to Server
	 */
	public HalfMap convertToHalfMap(UniquePlayerIdentifier playerid,Map map) {
		HashMap<Point, Field> myMap = map.getMap();
		
		Collection<HalfMapNode> nodes = myMap
				.entrySet()
				.stream()
				.map(e -> new HalfMapNode(e.getKey().getX(), e.getKey().getY(),
						e.getValue().getFortress() == Fortress.MyFortress ? true : false,
						Terrain.convert(e.getValue().getTerrain())))
				.collect(Collectors.toList());
		
		HalfMap halfMap = new HalfMap(playerid, nodes);
		return halfMap;
	}
	
	/**
	 * Method that converts a Set of FullMapNodes received from the server into a Map object that the game is played on.
	 * Going through every entry in the set and converting their coordinates into Point Objects and Field Objects that are later put in a HashMap,
	 * that is used to instantiate a Map object
	 * @param nodes Set of FullMapNodes received from the Server after a GET request
	 * @return Map object that is being used to play the game.
	 */
	public Map convertToMap(Set<FullMapNode> nodes) {
		HashMap<Point,Field> temp = new HashMap<>();
		for (FullMapNode node : nodes) {
			Point point = new Point(node.getX(),node.getY());
			Terrain terrain = Terrain.Grass;
			Treasure treasure = Treasure.None;
			switch(node.getTerrain()) {
			case Mountain : terrain = Terrain.Mountain; break;
			case Water : terrain = Terrain.Water; break;
			default : break;
			}
			Fortress fort = Fortress.None;
			switch(node.getFortState()) {
			case MyFortPresent: fort = Fortress.MyFortress; break;
			case EnemyFortPresent: fort = Fortress.EnemyFortress; break;
			default : fort = Fortress.None; break;
			}
			switch(node.getTreasureState()) {
			case MyTreasureIsPresent: treasure = Treasure.MyTreasure; break;
			default: break;
			}
			PlayerOnPosition player = PlayerOnPosition.None;
			switch(node.getPlayerPositionState()) {
			case MyPosition : player = PlayerOnPosition.Player1; break;
			case EnemyPlayerPosition : player = PlayerOnPosition.Player2; break;
			case BothPlayerPosition : player = PlayerOnPosition.Both; break;
			default: break;
			}
			Field field = new Field(terrain,fort,treasure,player);
			temp.put(point, field);
		}
		Map map = new Map(temp);
		return map;
	}
}
