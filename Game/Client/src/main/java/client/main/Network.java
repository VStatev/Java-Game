package client.main;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import MessagesBase.EMove;
import MessagesBase.ERequestState;
import MessagesBase.HalfMap;
import MessagesBase.PlayerMove;
import MessagesBase.PlayerRegistration;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.FullMap;
import MessagesGameState.FullMapNode;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import client.exceptions.InvalidArgumentsException;
import client.exceptions.InvalidMapException;
import client.exceptions.InvalidMoveException;
import reactor.core.publisher.Mono;

public class Network {
	private WebClient baseWebClient;
	private String gameId;
	private UniquePlayerIdentifier playerid;
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(Network.class);

	/**
	 * Constructor for Network class taking the baseURL of the server and the gameId
	 * both provided in the arguments of the client the constructor creates a
	 * WebClient object that communicates with the server whose address is :
	 * <baseURL>/games/<gameId>
	 * 
	 * @param baseURL the base URL of the server
	 * @param gameId  the generated gameID
	 */
	public Network(String baseURL, String gameId) {
		this.baseWebClient = WebClient.builder().baseUrl(baseURL + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) // the network protocol uses
																							// XML
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
		this.gameId = gameId;
	}

	public void setGameID(String gameId) {
		this.gameId = gameId;
	}

	/**
	 * Method that sends a Player Object to the server in order to register the
	 * player and start a game A POST request is sent to the server, containing a
	 * Player object(First Name, Last Name and Matrikelnummer) as a response the
	 * server sends back status (Okay, Error) and playerID or error message. If the
	 * response holds an error state an InvalidArgumentsException is thrown
	 * notifying the user that the arguments he inputed are invalid and they should
	 * be reentered.
	 * 
	 * @param p player object that needs to be registered
	 * 
	 * @return uniquePlayeriD generated from server
	 * @throws InvalidArgumentsException Exception notifying the user that the given
	 *                                   arguments on program start are invalid
	 */
	public String registerPlayer(Player p) throws InvalidArgumentsException {

		PlayerRegistration playerReg = new PlayerRegistration(p.getFirstName(), p.getLastName(), p.getMatrNumber());
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/players")
				.body(BodyInserters.fromObject(playerReg)) // specify the data which is set to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server
		try {
			ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();
			if (resultReg.getState() == ERequestState.Error) {
				System.out.println("Client error, errormessage: " + resultReg.getExceptionMessage());
				throw new InvalidArgumentsException("Arguments are invalid, please enter valid arguments");
			} else {
				UniquePlayerIdentifier uniqueID = resultReg.getData().get();
				this.playerid = uniqueID;
				return uniqueID.getUniquePlayerID();
			}
		} catch(Exception e) {
			System.out.println("Client error, errormessage: " + e.getMessage());
			throw new InvalidArgumentsException("Arguments are invalid, please enter valid arguments");
		}
		
	}

	/**
	 * Method that sends a generated halfmap from the face of the player to the
	 * server At first check if both players have registered of a game, the check
	 * repeats until the constraint is fulfilled A POST request is sent to
	 * <serverURL>/games/<gameId>/halfmaps that holds a HalfMap and the Player that
	 * generated it as a response the server sends back status (Okay, Error) if an
	 * error has accrued the methods throws an InvalidMapException that gives the
	 * user information why the generated map is invalid.
	 * 
	 * @param p Player that generated the halfmap
	 * @param m generated halfmap
	 * @throws InvalidMapException
	 */
	public void sendMap(Player p, Map m) throws InvalidMapException {

		Boolean shouldMove = false;
		do {
			Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
					.uri("/" + gameId + "/states/" + p.getPlayerID()).retrieve().bodyToMono(ResponseEnvelope.class);

			ResponseEnvelope<GameState> requestResult = webAccess.block();

			if (requestResult.getState() == ERequestState.Error) {
				System.out.println(requestResult.getExceptionMessage());
			} else {
				Set<PlayerState> players = requestResult.getData().get().getPlayers();
				for (PlayerState player : players) {
					if (player.getUniquePlayerID().equals(this.playerid.getUniquePlayerID())
							&& player.getState() == EPlayerGameState.ShouldActNext)
						shouldMove = true;
				}
			}
		} while (shouldMove == false);

		MapConverter convert = new MapConverter();
		HalfMap map = convert.convertToHalfMap(this.playerid, m);

		Mono<ResponseEnvelope> webAccess2 = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/halfmaps")
				.body(BodyInserters.fromObject(map)) // specify the data which is set to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<HalfMap> resultReg2 = webAccess2.block();
		if (resultReg2.getState() == ERequestState.Error) {
			throw new InvalidMapException(
					"You lost the game, because you sent an invalid map! \n Info :" + resultReg2.getExceptionMessage());
		} else {
			logger.info("Player " + p.getPlayerID() + " sent a valid halfmap.");
		}
	}

	/**
	 * Method that get the State of the Game from the server as a GameState object,
	 * goal of which is to get information if the player should move next, wait, has
	 * won or lost. A GET request is sent to
	 * <serverURL>/games/<gameId>/states/<playerID> that asks for the current status
	 * of the game the GameState object is converted to StateOfPlayer with the help
	 * of the convert method in StateOfPlayer
	 * 
	 * @param p player whose information we want to recieve
	 * @return StateOfPlayer enum that gives information if the player should move,
	 *         wait or has won or lost.
	 */
	public StateOfPlayer getGameState(Player p) {
		StateOfPlayer state = null;
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameId + "/states/" + p.getPlayerID()).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<GameState> requestResult = webAccess.block();

		if (requestResult.getState() == ERequestState.Error) {
			System.out.println("Client error, errormessage: " + requestResult.getExceptionMessage());
		} else {
			Set<PlayerState> players = requestResult.getData().get().getPlayers();
			for (PlayerState player : players) {
				if (player.getUniquePlayerID().equals(this.playerid.getUniquePlayerID()))
					state = StateOfPlayer.convert(player.getState());
			}
		}
		return state;
	}

	/**
	 * Method that get the Map that the game is currently being played on from the
	 * server A GET request is sent to <serverURL>/games/<gameId>/states/<playerID>
	 * that asks for the current status of the game from which we can extract the
	 * map, the FullMap object is converted to a set of FullMapNodes, while the map
	 * is converted the maxX and maxY coordinates are set in the Global class, as a
	 * response the server sends back status (Okay, Error) if an error has accrued
	 * the methods throws an InvalidMapException that gives the user information why
	 * the generated map is invalid.
	 * 
	 * @param p player who asks for the map
	 * @return Map object
	 */
	public Map getFullMap(Player p) {
		EPlayerGameState help = EPlayerGameState.ShouldWait;
		Map map = new Map(new HashMap<Point, Field>());
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameId + "/states/" + p.getPlayerID()).retrieve().bodyToMono(ResponseEnvelope.class);
		ResponseEnvelope<GameState> result = webAccess.block();
		if (result.getState() == ERequestState.Error) {
			System.out.println("Client error, errormessage: " + result.getExceptionMessage());
		} else {
			Optional<FullMap> fullmap = result.getData().get().getMap();
			Set<FullMapNode> nodes = fullmap.get().getMapNodes();
			int maxX = 0;
			int maxY = 0;
			for (FullMapNode node : nodes) {
				if (node.getY() > maxY)
					maxY = node.getY();
				if (node.getX() > maxX)
					maxX = node.getX();
			}
			Global.setMaxX(maxX + 1);
			Global.setMaxY(maxY + 1);
			MapConverter convert = new MapConverter();
			map = convert.convertToMap(nodes);
		}
		return map;
	}

	/**
	 * Method that sends a movement of a player to the server. A POST request is
	 * sent to <serverURL>/games/<gameId>/moves containing the Player who moves and
	 * the Direction that he wants to move to, the Direction is converted to EMove
	 * object, and later is put into a PlayerMove object that also contains the
	 * playerID, as a response the server sends back status (Okay, Error) if an
	 * error has accrued the methods throws an InvalidMoveException that gives the
	 * user information why the movement is invalid.
	 * 
	 * @param p player who executes the movement
	 * @param d direction in which the player wants to move
	 * @exception InvalidMoveException exception holding information on current
	 *                                 position, desired position and why the
	 *                                 movement is invalid
	 */
	public void sendDirection(Player p, Direction d) throws InvalidMoveException {
		EMove direction = Direction.convert(d);
		PlayerMove move = PlayerMove.of(this.playerid, direction);
		Mono<ResponseEnvelope> webAccess2 = baseWebClient.method(HttpMethod.POST).uri("/" + gameId + "/moves")
				.body(BodyInserters.fromObject(move)) // specify the data which is set to the server
				.retrieve().bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<HalfMap> resultReg2 = webAccess2.block();
		if (resultReg2.getState() == ERequestState.Error) {
			System.out.println("Client error, errormessage: " + resultReg2.getExceptionMessage());
		} else {
			logger.info("Player " + p.getPlayerID() + " moved " + direction);
		}
	}
}
