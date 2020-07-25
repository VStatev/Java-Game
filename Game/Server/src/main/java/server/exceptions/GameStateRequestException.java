package server.exceptions;

public class GameStateRequestException extends BaseServerException {
	public GameStateRequestException(String errorMessage) {
		super("Name: GameState Request failed", errorMessage);
	}
}
