package client.main;

public class Field {
	private Terrain terrain;
	private Fortress fortress;
	private Treasure treasure;
	private PlayerOnPosition player;
	private Boolean visited;

	/**
	 * Constructor for the Field class holding Terrain enum, Fortress Enum, Treasure
	 * Enum and PlayerOnPosition enum
	 * 
	 * @param terrain  the Terrain of the field
	 * @param fortress is a Fortress present on the field and whose
	 * @param treasure is a Treasure present on the field
	 * @param player   is a Player on the field
	 */
	public Field(Terrain terrain, Fortress fortress, Treasure treasure, PlayerOnPosition player) {
		this.terrain = terrain;
		this.fortress = fortress;
		this.player = player;
		this.visited = false;
		this.treasure = treasure;
	}

	/**
	 * @return the terrain on the field (Grass,Mountain, Water)
	 */
	public Terrain getTerrain() {
		return this.terrain;
	}

	/**
	 * @return the fortress on that field (MyFortress, EnemyFortress, None)
	 */
	public Fortress getFortress() {
		return this.fortress;
	}

	/**
	 * @return the treasure on that field (MyTreasure, None)
	 */
	public Treasure getTreasure() {
		return this.treasure;
	}

	/**
	 * @return the players who are on that field (Player1, Player2, Both0
	 */
	public PlayerOnPosition getPlayer() {
		return this.player;
	}

	/**
	 * @param b set if a given field has been visited or not ( used in flood-fill
	 *          and movement)
	 */
	public void setVisited(Boolean b) {
		this.visited = b;
	}

	/**
	 * @return is the current field visited or not
	 */
	public Boolean getVisited() {
		return this.visited;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fortress == null) ? 0 : fortress.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + ((terrain == null) ? 0 : terrain.hashCode());
		result = prime * result + ((treasure == null) ? 0 : treasure.hashCode());
		result = prime * result + ((visited == null) ? 0 : visited.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (fortress != other.fortress)
			return false;
		if (player != other.player)
			return false;
		if (terrain != other.terrain)
			return false;
		if (treasure != other.treasure)
			return false;
		if (visited == null) {
			if (other.visited != null)
				return false;
		} else if (!visited.equals(other.visited))
			return false;
		return true;
	}
	

}
