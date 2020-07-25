package server.game;

import MessagesBase.HalfMapNode;

public class MapNode {
	private HalfMapNode node;
	private boolean traversed;

	/**
	 * Constructor for MapNode object, this is used in the FloodFill algorithm in
	 * order to be able to store if a Node is traversable or not
	 * 
	 * @param node MapNode object.
	 */
	public MapNode(HalfMapNode node) {
		this.node = node;
		traversed = false;
	}

	/**
	 * Setter for the traversed attribute
	 * 
	 * @param b is the node traversed or not
	 */
	public void setTraversed(boolean b) {
		traversed = b;
	}

	/**
	 * Getter for the traverse attribute
	 * 
	 * @return has the node been traversed or not
	 */
	public boolean getTraversed() {
		return traversed;
	}

	/**
	 * Getter the HalfMapNode associated with the MapNode object
	 * 
	 * @return the HalfMapNode associated with the MapNode object
	 */
	public HalfMapNode getNode() {
		return this.node;
	}
}
