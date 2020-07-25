package server.exceptions;

public class HalfMapRegistrationException extends BaseServerException{
	public HalfMapRegistrationException(String message) {
		super("Name: Half Map Registration failed", message);
	}
}
