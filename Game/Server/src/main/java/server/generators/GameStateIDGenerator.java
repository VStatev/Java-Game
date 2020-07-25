package server.generators;

public class GameStateIDGenerator {
	private char[] alphabet;

	/**
	 * Constructor of GameStateIDGenerator, on call it creates the alphabet
	 * containing alphanumeric characters both low and capital
	 */
	public GameStateIDGenerator() {
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
	 * Method that generates a valid GameStateID In order to generate the method
	 * creates 15 indexes in the range of the alphabet and append the passing
	 * character to the string
	 * 
	 * @return randomly generated gameStateID
	 */
	public String generate() {
		StringBuilder uniqueGameStateID = new StringBuilder();
		for (int i = 0; i < 15; i++) {
			int index = (int) (62 * Math.random());
			uniqueGameStateID.append(String.valueOf(alphabet[index]));
		}
		return uniqueGameStateID.toString();
	}
}
