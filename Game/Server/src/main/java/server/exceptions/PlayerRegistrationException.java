package server.exceptions;

public class PlayerRegistrationException extends BaseServerException{
	public PlayerRegistrationException(String message) {
		super("Name: Player Registration failed", message);
	}
}
