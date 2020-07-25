package client.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

public class Dijkstra {
	private ArrayList<Point> points = new ArrayList<>();
	private HashMap<Point, Integer> distances = new HashMap<>();
	private HashMap<Point, Point> nearest = new HashMap<>();

	/**
	 * Constructor of the Dijkstra class
	 */
	public Dijkstra() {}

	/**
	 * Resets the values stored in points, distances and nearest in order to execute
	 * dijkstra again if needed
	 */
	public void clear() {
		this.points.clear();
		this.distances.clear();
		this.nearest.clear();
	}

	/**
	 * Method that searches and returns the Point with shortest distance
	 * 
	 * @return Point with shortest distance
	 */
	private Point shortestDistance() {
		Point shortest = this.points.get(0);
		for (Point p : this.points) {
			if (this.distances.get(p) < this.distances.get(shortest))
				shortest = p;
		}
		return shortest;
	}

	/**
	 * Implementation of the Dijkstra algorithm giving us the shortest distances and
	 * paths from and to every point in the map. First put every Point into the List
	 * of Points, Hashmap nearest with a null as a nearest point and distances with
	 * Max_Value of integer, Instantiate that the distance to the current point is
	 * 0, the algorithms starts with the point with shortest distance and goes over
	 * all of its valid neighbors and checks the required steps needed to enter
	 * them, if the distance is shorter than the one stored in the distances Hashmap
	 * replace it and put the nearest point to it as p
	 * 
	 * @param map             the current Map that the game is played on
	 * @param currentPosition the currentPosition of the player and starting
	 *                        position of the algorithm
	 */
	public void runDijkstra(Map map, Point currentPosition) {
		for (Entry<Point, Field> e : map.getMap().entrySet()) {
			this.distances.put(e.getKey(), Integer.MAX_VALUE);
			this.nearest.put(e.getKey(), null);
			this.points.add(e.getKey());
		}
		this.distances.replace(currentPosition, 0);
		this.nearest.replace(currentPosition, currentPosition);
		while (points.size() > 0) {
			Point p = this.shortestDistance();
			this.points.remove(p);
			ArrayList<Point> neighbors = p.getNeighbors();
			for (Point neighbor : neighbors) {
				if (this.points.contains(neighbor)) {
					int alt = this.distances.get(p) + map.getMap().get(p).getTerrain().getRequiredToLeave()
							+ map.getMap().get(neighbor).getTerrain().getRequiredToEnter();
					if (alt < this.distances.get(neighbor)) {
						this.distances.replace(neighbor, alt);
						this.nearest.replace(neighbor, p);
					}
				}
			}

		}
	}

	/**
	 * Method that returns the shortest distance between two fields iterate through
	 * the nearest point starting with the goal and accumulate their distances
	 * 
	 * @param currentPosition Point where the player finds himself
	 * @param goal            Point where the player wants to get to
	 * @return the shortest distance between currentPosition and goal
	 */
	public int getShortestDistanceTo(Point currentPosition, Point goal) {
		Point u = new Point(goal.getX(), goal.getY());
		if (distances.get(u) == null)
			return Integer.MAX_VALUE;
		int dist = 0;
		while (true) {
			dist += distances.get(u);
			u = nearest.get(u);
			if (u == currentPosition)
				break;
		}
		return dist;
	}

	/**
	 * Method that returns a Stack of Points giving the path from the
	 * currentPosition to the goal, iterate through the nearest point starting with
	 * the goal and add them to the stack until the currentPosition is reached
	 * 
	 * @param currentPosition Point where the player finds himself
	 * @param goal            Point where the players want to get to
	 * @return the path from currentPosition to goal
	 */
	public Stack<Point> pathTo(Point currentPosition, Point goal) {
		Point u = goal;
		Stack<Point> path = new Stack<>();
		while (true) {
			path.add(u);
			u = nearest.get(u);
			if (u == currentPosition)
				break;
		}
		return path;
	}

	/**
	 * Method that returns the closest Field made of Mountain to the starting point
	 * initiate dist and temp as MAX_VALUE in order to search for the closest field
	 * iterate over the points of mountain fields and find the one closest to
	 * currentPosition
	 * 
	 * @param currentPosition Point where the player finds himself
	 * @param mountains       List of Point of mountain fields
	 * @return the Point where the closest mountain is
	 */
	public Point closestMountain(Point currentPosition, ArrayList<Point> mountains) {
		int dist = Integer.MAX_VALUE;
		int temp = Integer.MAX_VALUE;
		Point closest = null;
		for (Point mountain : mountains) {
			temp = this.getShortestDistanceTo(currentPosition, mountain);
			if (temp < dist) {
				dist = temp;
				closest = mountain;
			}
		}
		return closest;
	}
}
