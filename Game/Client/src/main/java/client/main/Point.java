package client.main;

import java.util.ArrayList;

public class Point {
	private int x;
	private int y;
	/**
	 * Constructor of Point class
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * @return x coordinate of point
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x given x coordinate of point
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return y coordinate of point
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y given y coordinate of point
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Checks validity of a given point,
	 * if the x or y coordinates are smaller than 0 or greater than the maximal coordinates (maxX = 7, maxY = 7 for 8x8 ratio, maxX = 15, maxY = 4 for 4x16 ratio)
	 * @return validity of a point
	 */
	public Boolean isValid() {

		return this.x >= 0 && this.x < Global.maxX && this.y >= 0 && this.y < Global.maxY;
	}
	/**
	 * Function returning an ArrayList<Point> containing all the valid neighbors of this Point,
	 * 
	 * @return List with valid Point objects that are defined as the up, down, left and right neighbor of this Point
	 */
	public ArrayList<Point> getNeighbors() {
		ArrayList<Point> neighbors = new ArrayList<>();
		Point left = new Point(this.x - 1, this.y);
		if (left.isValid())
			neighbors.add(left);
		Point right = new Point(this.x + 1, this.y);
		if (right.isValid())
			neighbors.add(right);
		Point up = new Point(this.x, this.y - 1);
		if (up.isValid())
			neighbors.add(up);
		Point down = new Point(this.x, this.y + 1);
		if (down.isValid())
			neighbors.add(down);
		return neighbors;
	}
	/**
	 * hashCode defined as the x coordinate added with the y coordinate times the maximal x coordinate for more organized hashmap.
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = this.x + y * Global.maxX;
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
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	/**
	 * Debug purposes, clearer view.
	 */
	@Override
	public String toString() {
		return "X:" + this.x + " Y:" + this.y;
	}

}
