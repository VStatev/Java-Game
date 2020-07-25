package client.main;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Stack;

public class Movement {
	private Map map;
	private Point currentPosition;
	private ArrayList<Point> visited = new ArrayList<>();
	private Dijkstra dijkstra;

	/**
	 * Constructor for Movement classing taking Map object and Point object as
	 * parameters the currentPoint parameter is the current Point of the player on
	 * Initialization the point is set as visited and a Dijkstra object is created
	 * 
	 * @param map             the map on which the player will move
	 * @param currentPosition current position of layer
	 */
	public Movement(Map map, Point currentPosition) {
		this.map = map;
		this.currentPosition = this.map.getCurrentPosition();
		map.getMap().get(currentPosition).setVisited(true);
		this.dijkstra = new Dijkstra();
	}

	/**
	 * Method that sets the Map attribute and currentPosition of player on the map
	 * 
	 * @param map Map that needs to be Set
	 */
	public void setMap(Map map) {
		this.map = map;
		this.setCurrentPosition(this.map.getCurrentPosition());
	}

	/**
	 * @param newPosition Point that needs to be set as currentPosition
	 */
	public void setCurrentPosition(Point newPosition) {
		this.currentPosition.setX(newPosition.getX());
		this.currentPosition.setY(newPosition.getY());
	}

	/**
	 * A method that returns a List of directions that need to be sent to the server
	 * in order to reach a Point goal, the method uses the Dijkstra class in order
	 * to find the shortest path and uses the Stack returned from the Dijkstra
	 * pathTo function. Going through the stack the method checks the direction to
	 * the next Point the player needs to go to and adds it in the list of
	 * directions
	 * 
	 * @param goal goal Point that needs to be reached
	 * @return List of Directions to the goal Point
	 */
	public ArrayList<Direction> shortestPathTo(Point goal) {
		dijkstra.clear();
		dijkstra.runDijkstra(this.map, this.currentPosition);
		Stack<Point> path = this.dijkstra.pathTo(this.currentPosition, goal);
		ArrayList<Direction> directions = new ArrayList<>();
		Point tempCurrent = new Point(this.currentPosition.getX(), this.currentPosition.getY());
		while (!path.isEmpty()) {
			Point p = path.pop();
			System.out.println(this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
					+ this.map.getMap().get(p).getTerrain().getRequiredToEnter());
			if (p.getX() == tempCurrent.getX() - 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Left);
			}
			if (p.getX() == tempCurrent.getX() + 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Right);
			}
			if (p.getY() == tempCurrent.getY() - 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Up);
			}
			if (p.getY() == tempCurrent.getY() + 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Down);
			}
			tempCurrent.setX(p.getX());
			tempCurrent.setY(p.getY());
			if (map.getMap().get(p).getTerrain() == Terrain.Mountain)
				visited.add(p);
		}

		return directions;
	}

	/**
	 * Method that returns a list of directions to the nearest Mountain field to the
	 * player The method iterates over the map and insert every Point that has a
	 * mountain field associated to it in a list The dijkstra algorithm is called in
	 * oreder to find the shortest path to every point and finding the closest
	 * Mountain to the current position of the player Going through the stack the
	 * method checks the direction to the next Point the player needs to go to and
	 * adds it in the list of directions
	 * 
	 * @return List of directions to the closest mountain
	 */
	public ArrayList<Direction> findNearestMountain() {
		dijkstra.clear();
		ArrayList<Point> mountainLocations = new ArrayList<>();
		for (Entry<Point, Field> e : map.getMap().entrySet()) {
			if (e.getValue().getTerrain() == Terrain.Mountain && !e.getKey().equals(this.currentPosition)
					&& !visited.contains(e.getKey()))
				mountainLocations.add(e.getKey());
		}
		this.dijkstra.runDijkstra(this.map, this.currentPosition);
		Point closestMountain = dijkstra.closestMountain(currentPosition, mountainLocations);
		System.out.println("Closest Mountain: " + closestMountain);
		Stack<Point> path = dijkstra.pathTo(currentPosition, closestMountain);
		System.out.println("Path to " + closestMountain);
		for (Point p : path)
			System.out.println(p);
		ArrayList<Direction> directions = new ArrayList<>();
		Point tempCurrent = new Point(this.currentPosition.getX(), this.currentPosition.getY());
		while (!path.isEmpty()) {
			Point p = path.pop();
			System.out.println(this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
					+ this.map.getMap().get(p).getTerrain().getRequiredToEnter());
			if (p.getX() == tempCurrent.getX() - 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Left);
			}
			if (p.getX() == tempCurrent.getX() + 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Right);
			}
			if (p.getY() == tempCurrent.getY() - 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Up);
			}
			if (p.getY() == tempCurrent.getY() + 1) {
				for (int i = 0; i < this.map.getMap().get(tempCurrent).getTerrain().getRequiredToLeave()
						+ this.map.getMap().get(p).getTerrain().getRequiredToEnter(); i++)
					directions.add(Direction.Down);
			}
			tempCurrent.setX(p.getX());
			tempCurrent.setY(p.getY());
			if (map.getMap().get(p).getTerrain() == Terrain.Mountain)
				visited.add(p);
		}

		return directions;
	}

}
