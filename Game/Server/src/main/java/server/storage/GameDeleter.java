package server.storage;

import server.game.Game;

public class GameDeleter implements Runnable{
	private Game gameToBeDeleted;
	
	public GameDeleter(Game game) {
		gameToBeDeleted = game;
	}
	
	@Override
	public void run() {
		GameStorage.getStorage().deleteGame(gameToBeDeleted);
	}

}
