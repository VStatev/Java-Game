package client.main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameStatus {
	private Map map;
	private Boolean treasurePresent;
	private Boolean enemyFortressPresent;
	private StateOfPlayer state = StateOfPlayer.ShouldWait;
	private Point currentPosition;
	private Point treasurePosition;
	private Point enemyFortressPosition;
	private Player player;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	/**
	 * Constructor for GameStatus class
	 */
	public GameStatus() {}

	/**
	 * Method to add listeners to the PropertyChangeSupport in order of an automatic
	 * view to work
	 * 
	 * @param l PropertyChangeListener that notifies the view for changes
	 */
	public void addListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * @return the Player attribute holding the playerID needed for server
	 *         communication
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * setting player attribute
	 * 
	 * @param p
	 */
	public void setPlayer(Player p) {
		this.player = p;
	}

	/**
	 * @return Map object on which the game is being played
	 */
	public Map getMap() {
		return this.map;
	}

	/**
	 * @return The Point on which the player is sitting
	 */
	public Point getCurrentPosition() {
		return this.currentPosition;
	}

	/**
	 * Method that updates the currentPosition attribute when the currentPosition is
	 * changed the PropertyChangeSupport attributes fires property change to notify
	 * the view that a change has occurred in the model
	 */
	public void updatePosition() {
		if (this.currentPosition != null) {
			Point oldPosition = new Point(this.currentPosition.getX(), this.currentPosition.getY());
			this.currentPosition = this.map.getCurrentPosition();
			propertyChangeSupport.firePropertyChange("PlayerPosition", oldPosition, this.currentPosition);
		} else {
			this.currentPosition = this.map.getCurrentPosition();
		}
	}

	/**
	 * Method to set the map attribute initially when the map is set the method
	 * calls setTreasurePresent and setEnemyFortressPresent in order to initialize
	 * the attributes treasurePresent and enemyFortressPresent
	 * 
	 * @param map Map object to be stored in the model
	 */
	public void setMap(Map map) {
		this.map = map;
		this.updatePosition();
		this.setTreasurePresent(this.map.isTreasurePresent());
		this.setEnemyFortressPresent(this.map.isEnemyFortressPresent());
	}

	/**
	 * Method that updates the already existing map when the map is updated the
	 * method calls setTreasurePresent and setEnemyFortressPresent in order to
	 * update if needed the attributes treasurePresent and enemyFortressPresent
	 * 
	 * @param map an updated Map object
	 */
	public void updateMap(Map map) {
		Map oldMap = new Map(this.map.getMap());
		this.map.updateMap(map);
		this.updatePosition();
		this.setTreasurePresent(this.map.isTreasurePresent());
		this.setEnemyFortressPresent(this.map.isEnemyFortressPresent());
		propertyChangeSupport.firePropertyChange("MapUpdate", oldMap, this.map);

	}

	/**
	 * @return the Point on which the Treasure is
	 */
	public Point getTreasurePosition() {
		return this.treasurePosition;
	}

	/**
	 * Method that sets the treasurePresent attribute
	 * 
	 * @param b set if the treasure is present on map or not
	 */
	public void setTreasurePresent(Boolean b) {
		if (b == true) {
			Point temp = this.map.getTreasurePosition();
			this.treasurePosition = new Point(temp.getX(), temp.getY());
		}
		this.treasurePresent = b;
	}

	/**
	 * Set the StateOfPlayer attribute with the one received from the server on
	 * change the PropertyChangeSupport fires a property change to notify the view
	 * 
	 * @param state the StateOfPlayer object received from server
	 */
	public void setState(StateOfPlayer state) {
		StateOfPlayer old = this.state;
		this.state = state;
		propertyChangeSupport.firePropertyChange("PlayerState", old, this.state);
		if (this.state == StateOfPlayer.Lost || this.state == StateOfPlayer.Won)
			System.exit(0);
	}

	/**
	 * @return StateOfPlayer attribute (Won, Lost, ShouldActNext, ShouldWait)
	 */
	public StateOfPlayer getState() {
		return this.state;
	}

	/**
	 * Method that sets the enemyFortressPresent attribute
	 * 
	 * @param b set if the enemy fortress is present on map or not
	 */
	public void setEnemyFortressPresent(Boolean b) {
		if (b == true) {
			Point temp = this.map.getEnemyFortressPosition();
			this.enemyFortressPosition = new Point(temp.getX(), temp.getY());
		}
		this.enemyFortressPresent = b;
	}

	/**
	 * @return whether the treasure is present or not
	 */
	public Boolean getTreasurePresent() {
		return this.treasurePresent;
	}

	/**
	 * @return whether the enemy fortress is present or not
	 */
	public Boolean getEnemyFortressPresent() {
		return this.enemyFortressPresent;
	}

	/**
	 * @return the Point where the enemy fortress is positioned
	 */
	public Point getEnemyFortressPosition() {
		return enemyFortressPosition;
	}

}
