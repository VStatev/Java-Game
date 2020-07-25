package server.generators;

import server.storage.GameStorage;

public class GameIDGenerator {
	private char[] alphabet;

	/**
	 * Constructor of GameIDGenerator, on call it creates the alphabet containing
	 * alphanumeric characters both low and capital
	 */
	public GameIDGenerator() {
		alphabet = new char[62];
		// insert numerics into alphabet
		for (int i = 0; i < 10; i++) {
			alphabet[i] = (char) (48 + i);
		}
		// insert capital letters
		for (int i = 0; i < 26; i++) {
			alphabet[10 + i] = (char) (65 + i);
		}
		//
		for (int i = 0; i < 26; i++) {
			alphabet[36 + i] = (char) (97 + i);
		}
	}

	/**
	 * Method that generates a valid gameID get an instance of the storage in order
	 * to check if generated gameID is used or not and to add the newly generated.
	 * In order to generate the method creates 5 indexes in the range of the
	 * alphabet and append the passing character to the string
	 * 
	 * @return randomly generated valid gameID
	 */
	public String generate() {
		StringBuilder gameID;
		GameStorage storage = GameStorage.getStorage();
		try {
			gameID = new StringBuilder();
			for (int i = 0; i < 5; i++) {
				int index = (int) (62 * Math.random());
				gameID.append(String.valueOf(alphabet[index]));
			}
			storage.checkGameID(gameID.toString());
			return gameID.toString();
		} catch (Exception e) {
			return generate();
		}
	}
}