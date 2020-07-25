package server.convertors;

import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.PlayerState;
import server.game.Player;

public class PlayerStateConverter {
	/**
	 * Method that converts a Player object to a PlayerState in order to be used in
	 * the GameState message. The method creates a new PlayerState with the
	 * information that is held in the player object and the given playerID, the
	 * playerID is given, so that the playerID of the opponent stays hidden.
	 * 
	 * @param player   Player object that needs to be converted.
	 * @param playerID Given playerID stored in game object, that gives the real id
	 *                 or a fake one.
	 * @return converted PlayerState object
	 */
	public static PlayerState convert(Player player, UniquePlayerIdentifier playerID) {
		return new PlayerState(player.getFirstName(), player.getLastName(), player.getStudentID(),
				player.getPlayerGameState(), playerID, false);
	}
}
