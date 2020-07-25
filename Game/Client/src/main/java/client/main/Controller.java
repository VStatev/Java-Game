package client.main;

import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.LoggerFactory;

import client.exceptions.InvalidArgumentsException;
import client.exceptions.InvalidMapException;
import client.exceptions.InvalidMoveException;

public class Controller {
	private GameStatus model;
	private Network network;
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(Controller.class);

	/**
	 * Constructor for Controller class holding GameStatus object for storing data
	 * and Network object for communicating with the server
	 * 
	 * @param model   GameStatus object for storing data
	 * @param network Network object for communicating with the server
	 */
	public Controller(GameStatus model, Network network) {
		this.model = model;
		this.network = network;
	}

	/**
	 * Method that creates a Player object and registers it in the game and stores
	 * the player in the model, if an IllegalArgumentsException is caught the user
	 * must enter a valid serverURL and gameID in order to retry the call
	 * 
	 * @param firstName      First Name of the Player
	 * @param surname        Surname of the Player
	 * @param matrikelnumber Matrikel Number of the player
	 */
	public void registerPlayer(String firstName, String surname, String matrikelnumber) {
		Player p = new Player(firstName, surname, matrikelnumber);
		try {
			String playerID = this.network.registerPlayer(p);
			p.setPlayerID(playerID);
			this.model.setPlayer(p);
		} catch (InvalidArgumentsException e) {
			logger.error(e.getMessage());
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter server base URL");
			String url = sc.nextLine();
			System.out.println("Enter gameID");
			String gameID = sc.nextLine();
			this.network = new Network(url, gameID);
			this.registerPlayer(firstName, surname, matrikelnumber);
		}
	}

	/**
	 * Method that sets the StateOfPlayer attribute in the model to the one given
	 * from server
	 */
	public void updatePlayerStatus() {
		this.model.setState(this.network.getGameState(this.model.getPlayer()));
	}

	/**
	 * Method that starts the map generation and sends it to the server The method
	 * creates a GenerateMap object that automatically generates a map the map is
	 * sent to the server with the possibility of an InvalidMapException to occur,
	 * the exception shows how the map was invalid, the next step is to get the full
	 * map after both players have sent their map, that is checked with the size of
	 * the map
	 */
	public void generateMap() {
		GenerateMap generate = new GenerateMap();
		Map map = generate.getMap();
		while (this.model.getState() == StateOfPlayer.ShouldWait) {
			this.updatePlayerStatus();
		}
		try {
			network.sendMap(this.model.getPlayer(), map);
		} catch (InvalidMapException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		do {
			map = network.getFullMap(this.model.getPlayer());
		} while (map.getMap().size() != 64);
		this.model.setMap(map);
		this.updatePlayerStatus();
	}

	/**
	 * Method that updates the map stored in the model with the received map from
	 * the server
	 */
	public void updateMap() {
		this.model.updateMap(this.network.getFullMap(this.model.getPlayer()));
	}

	/**
	 * Method that traverses the closest mountains in the map until the treasure is
	 * in sight on the map a list with directions is generated from the
	 * findNearestMountain method in movement, the method iterates over the
	 * directions and tries to send them to the map if its their turn to move if a
	 * direction is invalid an IllegalMoveException would be received giving
	 * information on how the move is invalid after every move the model map is
	 * updated as well as the one stored in movement.
	 * 
	 * @param movement Object from movement class that stores the visited Points as
	 *                 well as the instance of Dijkstra storing the shortest paths
	 */
	public void searchTreasure(Movement movement) {
		while (!this.model.getTreasurePresent()) {
			ArrayList<Direction> directions = movement.findNearestMountain();
			for (Direction d : directions) {
				while (this.model.getState() != StateOfPlayer.ShouldActNext) {
					this.updatePlayerStatus();
				}
				try {
					network.sendDirection(this.model.getPlayer(), d);
					this.updatePlayerStatus();
				} catch (InvalidMoveException e) {
					logger.error(e.getMessage());
					System.exit(0);
				}
				this.updateMap();

				movement.setMap(this.model.getMap());
			}
		}
	}

	/**
	 * Method that moves the player to a given Point p, the method is called with
	 * either the Point where the treasure is or the enemy fortress. List of
	 * directions is generated from the shortestPathTo method in movement that with
	 * the help of Dijkstra's algorithm gives the shortest path to the Point p the
	 * method iterates over the directions and tries to send them to the map if its
	 * their turn to move if a direction is invalid an IllegalMoveException would be
	 * received giving information on how the move is invalid after every move the
	 * model map is updated as well as the one stored in movement.
	 * 
	 * @param p        the goal Point we are going for
	 * @param movement Object from movement class that stores the visited Points as
	 *                 well as the instance of Dijkstra storing the shortest paths
	 */
	public void goTo(Point p, Movement movement) {
		ArrayList<Direction> directions = movement.shortestPathTo(p);
		for (Direction d : directions) {
			while (this.model.getState() != StateOfPlayer.ShouldActNext) {
				this.updatePlayerStatus();
			}
			try {
				network.sendDirection(this.model.getPlayer(), d);
				this.updatePlayerStatus();
			} catch (InvalidMoveException e) {
				logger.error(e.getMessage());
				System.exit(0);
			}
			this.updateMap();
			movement.setMap(this.model.getMap());
		}
	}

	/**
	 * Method that traverses the closest mountains in the map until the enemy
	 * fortress is in sight on the map a list with directions is generated from the
	 * findNearestMountain method in movement, the method iterates over the
	 * directions and tries to send them to the map if its their turn to move if a
	 * direction is invalid an IllegalMoveException would be received giving
	 * information on how the move is invalid after every move the model map is
	 * updated as well as the one stored in movement.
	 * 
	 * @param movement Object from movement class that stores the visited Points as
	 *                 well as the instance of Dijkstra storing the shortest paths
	 */
	public void searchEnemyFortress(Movement movement) {
		while (!this.model.getEnemyFortressPresent()) {
			ArrayList<Direction> directions = movement.findNearestMountain();
			for (Direction d : directions) {
				while (this.model.getState() != StateOfPlayer.ShouldActNext) {
					this.updatePlayerStatus();
				}
				try {
					network.sendDirection(this.model.getPlayer(), d);
					this.updatePlayerStatus();
				} catch (InvalidMoveException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				this.updateMap();
				movement.setMap(this.model.getMap());
			}
		}
	}

	/**
	 * This method executes the playing of the game. The first thing is to generate
	 * and send a map with the help of generateMap() method a movement object is
	 * initialized which will run the Dijkstra algorithm and give the shortest paths
	 * to given point or mountain, the searchTreasure method is called after this to
	 * traverse the mountains on the map until the treasure is in sight the method
	 * tries to get the position of the treasure and go to it. After the treasure is
	 * redeemed, the client will traverse the mountains again in order to find the
	 * position of the position of the enemy fortress when the treasure is located
	 * the method tries to get the position of the enemyFortress and go to it.
	 * 
	 */
	public void play() {
		this.generateMap();
		View view = new View(this.model);
		Movement movement = new Movement(this.model.getMap(), this.model.getMap().getCurrentPosition());

		this.searchTreasure(movement);
		Point treasureLocation = model.getTreasurePosition();
		this.goTo(treasureLocation, movement);

		this.searchEnemyFortress(movement);
		Point enemyFortressLocation = this.model.getEnemyFortressPosition();
		this.goTo(enemyFortressLocation, movement);

	}
}
