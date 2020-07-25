package server.convertors;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesGameState.FullMap;
import MessagesGameState.FullMapNode;

public class MapConverter {
	private NodeConverter converter;
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(MapConverter.class);

	public MapConverter() {
		this.converter = new NodeConverter();
	}
	/**
	 * Method that combines Two HalfMaps into a FullMap. The method holds the
	 * modifier for X and Y coordinates in order to create an 8x8 Map or 4x16. The
	 * integers firstMapIndex and secondMapIndex are used to choose which map is
	 * used first. OponentPlaced shows if the opponent player has already been
	 * placed on a random place on the map or not. The method chooses randomly the
	 * aspect of the map and the first halfMap being used. The method converts every
	 * node of the halfMaps into FullMapNodes and places them in a list. After every
	 * node is converted the FullMap is returned.
	 * 
	 * @param halfMaps List of HalfMaps that need to be combined
	 * @param playerID the ID of the Player that will see this map, used in order to
	 *                 hide the fortress and opponent position
	 * @return combined FullMap
	 */
	public FullMap combineHalfMaps(ArrayList<HalfMap> halfMaps, String playerID) {
		int xModifier = 0;
		int yModifier = 4;

		int firstMapIndex = 0;
		int secondMapIndex = 1;
		double threshold = 0.1;

		boolean oponentPlaced = false;
		
		int maxX = 0;
		int maxY = 0;
		if (Math.random() <= 0.5) { // Aspect of Map
			xModifier = 8;
			yModifier = 0;

		}
		if (Math.random() <= 0.5) { // Place of HalfMaps
			firstMapIndex = 1;
			secondMapIndex = 0;
		}

		ArrayList<FullMapNode> nodes = new ArrayList<>();
		boolean hide = !(halfMaps.get(firstMapIndex).getUniquePlayerID() == playerID);
		for (HalfMapNode node : halfMaps.get(firstMapIndex).getNodes()) {
			FullMapNode fullMapNode;
			if (Math.random() <= threshold && node.getTerrain() == ETerrain.Grass && !oponentPlaced) {
				oponentPlaced = true;
				fullMapNode = converter.convertHalfMapNode(node, 0, 0, hide, true);

			} else {
				threshold += 0.1; // assure that the enemy will be placed
				fullMapNode = converter.convertHalfMapNode(node, 0, 0, hide, false);
			}
			if(fullMapNode.getX() > maxX)
				maxX = fullMapNode.getX();
			if(fullMapNode.getY() > maxY)
				maxY = fullMapNode.getY();
			nodes.add(fullMapNode);
		}

		hide = !hide;
		for (HalfMapNode node : halfMaps.get(secondMapIndex).getNodes()) {
			FullMapNode fullMapNode;
			if (Math.random() <= threshold && node.getTerrain() == ETerrain.Grass && !oponentPlaced) {
				oponentPlaced = true;
				fullMapNode = converter.convertHalfMapNode(node, xModifier, yModifier, hide, true);

			} else {
				threshold += 0.1; // assure that the enemy will be placed
				fullMapNode = converter.convertHalfMapNode(node, xModifier, yModifier, hide, false);
			}

			if(fullMapNode.getX() > maxX)
				maxX = fullMapNode.getX();
			if(fullMapNode.getY() > maxY)
				maxY = fullMapNode.getY();
			nodes.add(fullMapNode);
		}
		logger.info("Sucesfully combined HalfMaps to a FullMap with aspect: " + ++maxX + 'x' + ++maxY);
		return new FullMap(nodes);
	}
}
