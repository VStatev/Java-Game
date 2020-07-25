package client.main;

public class Player {
	private String firstName;
	private String lastName;
	private String matrNumber;
	private String playerID;

	/**
	 * Constructor for the Player class
	 * @param firstName First Name of the Player
	 * @param lastName Last Name of the Player
	 * @param matrNumber Matrikel Number of Player
	 */
	public Player(String firstName, String lastName, String matrNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.matrNumber = matrNumber;
	}
	
	/**
	 * @return the first name of the player
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName given first name of the player
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return last name of the player
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName given last name of the player
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return matrikel number of the player
	 */
	public String getMatrNumber() {
		return matrNumber;
	}
	/**
	 * @param matrNumber given matrikel number of the player
	 */
	public void setMatrNumber(String matrNumber) {
		this.matrNumber = matrNumber;
	}
	/**
	 * @param playerID given playerID recieved from the server
	 */
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}
	/**
	 * @return playerID of the player
	 */
	public String getPlayerID() {
		return playerID;
	}
}
