package server.convertors;

import MessagesBase.HalfMapNode;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerPositionState;
import MessagesGameState.ETreasureState;
import MessagesGameState.FullMapNode;

public class NodeConverter {
	public NodeConverter() {}
	/**
	 * Method that converts HalfMapNodes into FullMapNodes. If the fort is present
	 * and a node and the information shouldn't be hidden, the fortress and player
	 * are placed on the tile. If the opponent Player needs to be placed and
	 * information should be hidden, the opponent is placed on the tile.
	 * 
	 * @param halfMapNode HalfMapNode that needs to be converted
	 * @param xModifier   Modifier for Coordinate X
	 * @param yModifier   Modifier for Coordinate X
	 * @param hide        whether the information should be hidden or not
	 * @param placePlayer whether the Opponent should be placed on the tile or not
	 * @return converted FullMapNode
	 */
	public FullMapNode convertHalfMapNode(HalfMapNode halfMapNode, int xModifier, int yModifier, boolean hide,
			boolean placePlayer) {
		EPlayerPositionState player = EPlayerPositionState.NoPlayerPresent;
		EFortState fort = EFortState.NoOrUnknownFortState;
		if (halfMapNode.isFortPresent() && !hide) {
			player = EPlayerPositionState.MyPosition;
			fort = EFortState.MyFortPresent;
		}
		if (placePlayer) {
			player = EPlayerPositionState.EnemyPlayerPosition;
			System.out.println("placed enemy ");
		}
		return new FullMapNode(halfMapNode.getTerrain(), player, ETreasureState.NoOrUnknownTreasureState, fort,
				halfMapNode.getX() + xModifier, halfMapNode.getY() + yModifier);
	}
}
