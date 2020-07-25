package server.game;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EPlayerGameState;

public class Player {
	private String firstName;
	private String lastName;
	private String studentID;
	private UniquePlayerIdentifier playerID;
	private EPlayerGameState playerGameState;
	private boolean sentHalfMap;
	private long lastAction;

	/**
	 * Constructor for Player object that stores information of registered Player
	 * 
	 * @param playerRegistration PlayerRegistration got from POST Request at
	 *                           /players endpoint.
	 */
	public Player(PlayerRegistration playerRegistration) {
		this.firstName = playerRegistration.getStudentFirstName();
		this.lastName = playerRegistration.getStudentLastName();
		this.studentID = playerRegistration.getStudentID();
		this.playerID = UniquePlayerIdentifier.random();
		this.playerGameState = EPlayerGameState.ShouldWait;
		this.sentHalfMap = false;
		this.lastAction = System.currentTimeMillis();
	}

	/**
	 * Getter for the UniquePlayerIdentifier of the Player
	 * 
	 * @return UniquePlayerIdentifier of Player
	 */
	public UniquePlayerIdentifier getPlayerID() {
		return this.playerID;
	}
	public boolean checkTime(long time) {
		long elapsedTime = time-this.lastAction;
		System.out.println("Time since last action of Player " + elapsedTime/1000 + " s");
		if(elapsedTime > 5000) {
			return false;
		}
		this.lastAction = time;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((playerGameState == null) ? 0 : playerGameState.hashCode());
		result = prime * result + ((playerID == null) ? 0 : playerID.hashCode());
		result = prime * result + ((studentID == null) ? 0 : studentID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Player other = (Player) obj;
		return this.playerID.equals(other.getPlayerID());
	}

	/**
	 * Getter for First Name
	 * 
	 * @return First Name of Player
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Getter for Last Name
	 * 
	 * @return Last Name of Player
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Getter for Student ID
	 * 
	 * @return Student ID of Player
	 */
	public String getStudentID() {
		return studentID;
	}

	/**
	 * Getter for sentHalfMap
	 * 
	 * @return whether the Player has sent a HalfMap or not
	 */
	public boolean getSentHalfMap() {
		return this.sentHalfMap;
	}

	/**
	 * Getter for PlayerGameState
	 * 
	 * @return the EPlayerGameState of the Player
	 */
	public EPlayerGameState getPlayerGameState() {
		return playerGameState;
	}

	/**
	 * Setter for PlayerGameState
	 * 
	 * @param playerGameState new EPlayerGameState that needs to be set
	 */
	public void setPlayerGameState(EPlayerGameState playerGameState) {
		this.playerGameState = playerGameState;
	}

	/**
	 * Setter for the PlayerID
	 * 
	 * @param playerID UniquePlayerIdentifier that needs to be set
	 */
	public void setPlayerID(UniquePlayerIdentifier playerID) {
		this.playerID = playerID;
	}

	/**
	 * Setter for sentHalfMap
	 * 
	 * @param b whether the Player has sent a HalfMap or not
	 */
	public void setSentHalfMap(boolean b) {
		this.sentHalfMap = b;
	}

}
