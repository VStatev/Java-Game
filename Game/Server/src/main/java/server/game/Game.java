package server.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import MessagesBase.HalfMap;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.FullMap;
import server.convertors.MapConverter;
import server.generators.GameStateIDGenerator;

public class Game {


	private String gameID;
	private String gameStateID;
	private Set<Player> players;
	private ArrayList<HalfMap> halfMaps;
	private HashMap<UniquePlayerIdentifier, UniquePlayerIdentifier> fakePlayerIDs;
	private HashMap<String, FullMap> fullMap;
	private boolean gameOver = false;

	/**
	 * Constructor for Game object, that stores the gameID, gameStateID, the
	 * players, halfMaps, the fake ids for every player, the Map and whether the
	 * game is over or not
	 * 
	 * @param gameID The UniqueGameIdentifier generated for this game
	 */
	public Game(String gameID) {
		this.gameID = gameID;
		this.players = new HashSet<>();
		this.halfMaps = new ArrayList<>();
		this.fakePlayerIDs = new HashMap<UniquePlayerIdentifier, UniquePlayerIdentifier>();
		this.fullMap = new HashMap<String, FullMap>();
		this.updateGameStateID();
	}

	private void updateGameStateID() {
		GameStateIDGenerator generator = new GameStateIDGenerator();
		this.gameStateID = generator.generate();
	}

	/**
	 * Method to get the current gameStateID
	 * 
	 * @return the gameStateID of the game
	 */
	public String getGameStateID() {
		return gameStateID;
	}

	/**
	 * Method to get the gameID of the game
	 * 
	 * @return the gameID of the game
	 */
	public String getGameID() {
		return gameID;
	}

	/**
	 * Method to get the Set of Players that registered for the game
	 * 
	 * @return the Set of Players registered for the game
	 */
	public Set<Player> getPlayers() {
		return players;
	}

	/**
	 * Method to get the FullMap of the game
	 * 
	 * @return the FullMap
	 */
	public FullMap getFullMapForPlayer(String playerID) {
		return fullMap.get(playerID);
	}

	/**
	 * Method to get the List of HalfMaps generated by the players
	 * 
	 * @return List of the generated HalfMaps
	 */
	public ArrayList<HalfMap> getHalfMaps() {
		return halfMaps;
	}

	/**
	 * Method to get the HashMap containing the fake player ids for each player
	 * 
	 * @return HashMap containing the fake player ids
	 */
	public HashMap<UniquePlayerIdentifier, UniquePlayerIdentifier> getFakeIDs() {
		return fakePlayerIDs;
	}

	/**
	 * Method that adds a Player to the current game. The method first checks if the
	 * game is over or not, if not an exception is thrown. Then checks if 2 players
	 * have already registered for the game, if the player is not already
	 * registered, he is added to the Set of players, after the registration of the
	 * Player a fake ID is generated for them and put in the HashMap, The last
	 * functionality of the method is to set the states of the player. If the player
	 * passed as an argument is the only registered player, their state is set to
	 * ShouldWait, if not the method chooses randomly who should wait and who should
	 * act next.
	 * 
	 * @param player The player that should be registered for the game.
	 * @throws Exception An Exception that holds in its message which business rule
	 *                   is violated.
	 */
	public void addPlayer(Player player) throws Exception {
		if (this.gameOver)
			throw new Exception("This game is already over!");

		if (this.players.size() == 2)
			throw new Exception("Too many players try to register!");
		if (!this.players.add(player))
			throw new Exception("Player already registered!");

		this.fakePlayerIDs.put(player.getPlayerID(), UniquePlayerIdentifier.random());
		this.updateGameStateID();

		if (this.players.size() != 2) {
			for (Player currentPlayer : players)
				currentPlayer.setPlayerGameState(EPlayerGameState.ShouldWait);
		} else {
			double random = Math.random();
			for (Player current : this.players) {
				if (current.getPlayerID().equals(player.getPlayerID())) {
					if (random < 0.8)
						current.setPlayerGameState(EPlayerGameState.ShouldActNext);
					else
						current.setPlayerGameState(EPlayerGameState.ShouldWait);
				} else {
					if (random < 0.8)
						current.setPlayerGameState(EPlayerGameState.ShouldWait);
					else
						current.setPlayerGameState(EPlayerGameState.ShouldActNext);
				}
			}

		}
	}

	/**
	 * Method that checks if a Player with a given ID is registered for this game.
	 * If the player is not registered for the game, an exception is thrown.
	 * 
	 * @param playerID playerID of the player we are looking for.
	 * @throws Exception thrown if the Player is not registered for this game.
	 */
	/**
	 * Method that checks if a Player with a given ID is registered for this game,
	 * if it is registered it will be returned. If the player is not registered for
	 * the game, an exception is thrown.
	 * 
	 * @param playerID
	 * @return Player object with the given playerID
	 * @throws Exception
	 */
	public Player getPlayerByID(String playerID) throws Exception {
		for (Player p : this.players) {
			if (p.getPlayerID().getUniquePlayerID().equals(playerID)) {
				return p;
			}
		}
		throw new Exception("Player " + playerID + " isn't registered for this game!");
	}

	/**
	 * Method that adds a HalfMap to the List of HalfMaps. The method first checks
	 * if the game is over or not. Then it uses the static method
	 * MapValidator.verify class to check if the HalfMap violates any of the
	 * business rules. If it catches an Exception it sets the state of the Player
	 * that sent the map to Lost and the ones of his opponent to Won. After the
	 * HalfMap is added to the List the gameStateID is updated via
	 * updateGameSateID(). After the storing of the HalfMap the state of the Player
	 * that generated the map is set to ShouldWait and the one of his opponent to
	 * ShouldActNext. The last thing the method checks if the FullMap is already
	 * generated or not and if there are 2 HalfMaps stored. If this condition is
	 * fulfilled it calls the combineHalfMaps() method.
	 * 
	 * @param newHalfMap The HalfMap that needs to be added to the game.
	 * @throws Exception Exception showing that the game is over, or that a player
	 *                   has lost and the reason for the loss.
	 */
	public void addHalfMap(HalfMap newHalfMap) throws Exception {
		if (this.gameOver)
			throw new Exception("This game is already over!");
		try {
			MapValidator.verify(newHalfMap, this);
		} catch (Exception e) {
			this.lostGame(newHalfMap.getUniquePlayerID());
			this.updateGameStateID();
			System.out.println("You lost " + e.getMessage());
			throw new Exception("You lost " + e.getMessage());
		}
		this.halfMaps.add(newHalfMap);
		this.updateGameStateID();
		for (Player player : this.players) {
			if (player.getPlayerID().getUniquePlayerID().equals(newHalfMap.getUniquePlayerID()))
				player.setPlayerGameState(EPlayerGameState.ShouldWait);
			else
				player.setPlayerGameState(EPlayerGameState.ShouldActNext);
		}
		if (fullMap.isEmpty() && this.halfMaps.size() == 2) {
			this.combineHalfMaps();
		}
	}

	/**
	 * This method combines the two halfMaps into a FullMap with the help of
	 * MapConverter object and its combineHalfMaps method.
	 */
	private void combineHalfMaps() {
		MapConverter converter = new MapConverter();
		fullMap.put(halfMaps.get(0).getUniquePlayerID(),
				converter.combineHalfMaps(halfMaps, halfMaps.get(0).getUniquePlayerID()));
		fullMap.put(halfMaps.get(1).getUniquePlayerID(),
				converter.combineHalfMaps(halfMaps, halfMaps.get(1).getUniquePlayerID()));
	}
	
	public void lostGame(String playerID) {
		this.gameOver = true;
		for(Player player : this.players) {
			if (player.getPlayerID().getUniquePlayerID().equals(playerID))
				player.setPlayerGameState(EPlayerGameState.Lost);
			else
				player.setPlayerGameState(EPlayerGameState.Won);
		}
	}
	/**
	 * Debug
	 */
	@Override
	public String toString() {
		return "Game [gameID=" + gameID + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameID == null) ? 0 : gameID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (gameID == null) {
			if (other.gameID != null)
				return false;
		} else if (!gameID.equals(other.gameID))
			return false;
		return true;
	}
}