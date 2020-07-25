package server.exceptions;

public class CreateGameException extends BaseServerException{

	public CreateGameException(String errorMessage) {
		super("Name: Game Creation failed", errorMessage);
	}

}
