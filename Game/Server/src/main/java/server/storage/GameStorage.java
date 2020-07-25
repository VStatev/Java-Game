package server.storage;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.game.Game;

public class GameStorage {
	private static GameStorage instance = null;
	private ArrayList<Game> storage;

	private GameStorage() {
		this.storage = new ArrayList<>();
	}

	/**
	 * Get instance of the Storage
	 * 
	 * @return Instance of GameStorage that holds the games on the server.
	 */
	public static GameStorage getStorage() {
		if (instance == null)
			instance = new GameStorage();
		return instance;
	}

	/**
	 * Method that adds a Game to the storage. If the game is already in the storage
	 * an Exception is thrown. If the storage is full, the oldest game is removed
	 * from the storage.
	 * 
	 * @param newGame Game that needs to be stored.
	 * @throws Exception Is the game already in the storage or not
	 */
	public void storeGame(Game newGame) throws Exception {
		for (Game game : this.storage) {
			if (game.equals(newGame))
				throw new Exception("GameID in use");
		}
		if (this.storage.size() == 999)
			this.dropOldestGame();
		this.storage.add(newGame);
		System.out.println("Storage size " + storage.size());
		try {
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.schedule(new GameDeleter(newGame), 10, TimeUnit.MINUTES);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	/**
	 * Method that gets Game from the Storage by it's ID.
	 * If the game doesn't exist an Exception is thrown.
	 * @param gameID ID of the seeked Game
	 * @return Game with corresponding iD.
	 * @throws Exception Thrown if the seeked game is not in the storage 
	 */
	public Game getGameById(String gameID) throws Exception {
		for (Game game : this.storage) {
			if (game.getGameID().equals(gameID))
				return game;
		}
		throw new Exception("Game with this ID doesn't exist");
	}
	/**
	 * Method that deletes a given game from the Storage.
	 * @param game Game that needs to be deleted.
	 */
	public void deleteGame(Game game) {
		System.out.println("Dropping game " + game.getGameID());
		storage.remove(game);
	}
	/**
	 * Method that deletes the oldest Game in the storage.
	 */
	public void dropOldestGame() {
		System.out.println("Will try to delete");
		this.storage.remove(0);
		System.out.println("Droped  oldest game");
	}
	/**
	 * Method that checks if a Game with the given ID exists or not.
	 * If it exists an Exception is thrown
	 * @param gameID GameID that needs to be checked.
	 * @throws Exception Thrown when the gameID is already in use.
	 */
	public void checkGameID(String gameID) throws Exception {
		for (Game game : this.storage) {
			if (game.getGameID().contentEquals(gameID))
				throw new Exception("GameID in use");
		}
	}

}
