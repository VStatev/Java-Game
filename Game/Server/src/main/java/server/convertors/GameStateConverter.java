package server.convertors;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.FullMap;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import server.game.Game;
import server.game.Player;

public class GameStateConverter {
	/**
	 * Static method that converts a Game object to a GameState object in order to be sent to the Player.
	 * The method converts the Player objects stored in the game to PlayerState objects using the PlayerStateConverter.convert static method.
	 * If the current player being converted is not the same that requested the GameState it passes the fakeID that has been generated for them.
	 * 
	 * @param game Game for which the state is asked for
	 * @param playerThatRequests the Player that requested the GameState, used in order to hide the oponent's id.
	 * @return the GameState of the game
	 */
	public static GameState convert(Game game, Player playerThatRequests) {
		Set<PlayerState> players = new HashSet<>();
		for (Player player : game.getPlayers()) {
			if (player.equals(playerThatRequests))
				players.add(PlayerStateConverter.convert(player, player.getPlayerID()));
			else {
				UniquePlayerIdentifier fakeID = game.getFakeIDs().get(player.getPlayerID());
				players.add(PlayerStateConverter.convert(player, fakeID));
			}
		}
		Optional<FullMap> fullMap = Optional.ofNullable(game.getFullMapForPlayer(playerThatRequests.getPlayerID().getUniquePlayerID()));

		return new GameState(fullMap, players, game.getGameStateID());
	}
}
