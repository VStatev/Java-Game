package server.game;

import java.util.ArrayList;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesGameState.EPlayerGameState;

public class MapValidator {

	public static void compareHalfMaps(HalfMap lhs, HalfMap rhs) throws Exception {
		int numberOfMatches = 0;
		
		ArrayList<HalfMapNode> lhsNodes = new ArrayList<>(lhs.getNodes());
		ArrayList<HalfMapNode> rhsNodes = new ArrayList<>(rhs.getNodes());
		
		for(int i = 0; i < 32; i++) {
			if(lhsNodes.get(i).equals(rhsNodes.get(i)))
					numberOfMatches++;
		}
		
		if(numberOfMatches > 10)
			throw new Exception("The two HalfMaps are too similiar!");
	}

	

	public static void verify(HalfMap halfMap, Game game) throws Exception {
		ArrayList<HalfMapNode> nodes = new ArrayList<>(halfMap.getNodes());
		try {
			senderCheck(halfMap, game);
			checkSize(nodes);
			checkBorders(nodes);
			checkQuantities(nodes);
		} catch (Exception e) {
			throw e;
		}
		MapNode[][] temp = new MapNode[8][4];
		for (HalfMapNode node : halfMap.getNodes()) {
			temp[node.getX()][node.getY()] = new MapNode(node);
		}
		traverse(temp, 0, 0);
		for(int x = 0; x < 7; x++) {
			for(int y = 0; y < 4; y++) {
				if(!temp[x][y].getTraversed() && temp[x][y].getNode().getTerrain() != ETerrain.Water)
					throw new Exception("The halfMap has islands inside it!");
			}
		}
	}

	private static void traverse(MapNode[][] map, int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 3)
			return;
		if (map[x][y].getNode().getTerrain() == ETerrain.Water || map[x][y].getTraversed() == true)
			return;
		map[x][y].setTraversed(true);

		traverse(map, x, y + 1);
		traverse(map, x, y - 1);
		traverse(map, x - 1, y);
		traverse(map, x + 1, y);

	}
	
	private static void senderCheck(HalfMap halfMap, Game game) throws Exception {
		int numberMissmatches = 0;
		if(game.getPlayers().size() != 2) {
			game.getPlayerByID(halfMap.getUniquePlayerID()).setPlayerGameState(EPlayerGameState.Lost);
			throw new Exception("Player tried to sent a HalfMap before all players registered! The culprit is "
					+ halfMap.getUniquePlayerID());
		}
		if(game.getHalfMaps().size() == 2) {
			game.getPlayerByID(halfMap.getUniquePlayerID()).setPlayerGameState(EPlayerGameState.Lost);
			throw new Exception("Two HalfMaps have already been sent! Culprit was: " + halfMap.getUniquePlayerID());
		}
		for(Player player : game.getPlayers()) {
			if(player.getPlayerID().getUniquePlayerID().equals(halfMap.getUniquePlayerID())) {
				if(player.getPlayerGameState() != EPlayerGameState.ShouldActNext)
					throw new Exception("Player tried to sent HalfMap, when he should wait! Culprit was: "
							+ halfMap.getUniquePlayerID());
				if(player.getSentHalfMap())
					throw new Exception("Player " + halfMap.getUniquePlayerID() + " has already sent a HalfMap");
				
			} else {
				numberMissmatches++;
			}
		}
		if(numberMissmatches == 2)
			throw new Exception("Player " + halfMap.getUniquePlayerID() + " isn't registered for this game");
	}
	private static void checkSize(ArrayList<HalfMapNode> nodes) throws Exception {
		if (nodes.size() != 32) {
			throw new Exception("Map size is invalid! Current size :" + nodes.size() + ", desired size is 32");
		}
	}

	private static void checkQuantities(ArrayList<HalfMapNode> nodes) throws Exception {
		int mountainCount = 0;
		int waterCount = 0;
		int grassCount = 0;
		String errorMessage = null;
		for (HalfMapNode node : nodes) {
			switch (node.getTerrain()) {
			case Water:
				waterCount++;
				break;
			case Grass:
				grassCount++;
				break;
			case Mountain:
				mountainCount++;
				break;
			}
		}
		if (mountainCount < 3) {
			if(errorMessage == null) {
				errorMessage = "Mountain Count insufficent, given are " + mountainCount + " with 3 desired";
			}
		}
		

		if (waterCount < 4) {
			if(errorMessage == null) {
				errorMessage = "Water Count insufficent, given are " + waterCount + " with 4 desired";
			}
			else
				errorMessage.concat("\n Water Count insufficent, given are " + waterCount + " with 4 desired");
		}

		if (grassCount < 15) {
			if(errorMessage == null) {
				errorMessage = "Grass Count insufficent, given are " + mountainCount + " with 15 desired";
			}
			else
				errorMessage.concat("\n Grass Count insufficent, given are " + mountainCount + " with 15 desired");
		}
		
		if (errorMessage != null) 
			throw new Exception(errorMessage);
		
	}

	private static void checkBorders(ArrayList<HalfMapNode> nodes) throws Exception {
		int widthBorderWater = 0;
		int heightBorderWater = 0;
		String errorMessage = "";
		Boolean fortressPresent = false;

		for (HalfMapNode node : nodes) {

			if (node.isFortPresent())
				fortressPresent = true;

			int y = node.getY();
			int x = node.getX();
			if (y == 0 || y == 3 || x == 0 || x == 7) {
				if (node.getTerrain().equals(ETerrain.Water)) {
					if (y == 0 || y == 3)
						widthBorderWater++;
					if (x == 0 || x == 7)
						heightBorderWater++;
				}
			}
		}
		if (!fortressPresent)
			errorMessage.concat("\n Fortress is not present");
		if (widthBorderWater > 3)
			errorMessage.concat(
					"\n Water field on the width of the border is " + widthBorderWater + " the maximal count is 3");
		if (heightBorderWater > 1)
			errorMessage.concat(
					"\n Water field on the height of the border is " + heightBorderWater + " the maximal count is 1");
		if (!errorMessage.isEmpty())
			throw new Exception(errorMessage);
	}

}
