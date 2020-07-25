package server.main;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.GameState;
import server.convertors.GameStateConverter;
import server.exceptions.BaseServerException;
import server.exceptions.CreateGameException;
import server.exceptions.GameStateRequestException;
import server.exceptions.HalfMapRegistrationException;
import server.exceptions.PlayerRegistrationException;
import server.game.Game;
import server.game.Player;
import server.generators.GameIDGenerator;
import server.storage.GameStorage;

@Controller
@RequestMapping(value = "/games")
public class ServerEndpoints {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ServerEndpoints.class);

	/**
	 * Method to handle GET Requests at the base Endpoint of the server. It creates
	 * a new GameIDGenerator and generates a new valid gameID
	 * 
	 * @return randomly generated valid GameID
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame() {

		GameIDGenerator generator = new GameIDGenerator();
		UniqueGameIdentifier gameID = new UniqueGameIdentifier(generator.generate());
		Game activeGame = new Game(gameID.getUniqueGameID());
		try {
			GameStorage.getStorage().storeGame(activeGame);
			logger.info("Generated Game: " + activeGame.getGameID());
		} catch (Exception e) {
			throw new CreateGameException(e.getMessage());
		}
		return gameID;

	}

	/**
	 * Method to handle POST request at the /<gameID>/players Endpoint of the
	 * server. The method creates a new Player object with the PlayerRegistration
	 * being passed, then it accesses the game with gameID used in the endpoint and
	 * it adds it to the game. If a game with this ID doesn't exist, the server
	 * tries a PlayerRegistrationException.
	 * 
	 * @param gameID             ID of the game that the player tries to register
	 *                           for
	 * @param playerRegistration Registration of a Player being sent with the
	 *                           request
	 * @return UniquePlayerIdentifier of the newly Registered Player
	 */
	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(@PathVariable String gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {

		try {
			Player newPlayer = new Player(playerRegistration);
			Game game = GameStorage.getStorage().getGameById(gameID);
			game.addPlayer(newPlayer);
			ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayer.getPlayerID());
			logger.info("Player: " + newPlayer.getPlayerID().getUniquePlayerID() +" registered for Game " + game.getGameID());
			return playerIDMessage;
		} catch (Exception e) {
			throw new PlayerRegistrationException("Message: " + e.getMessage());
		}
	}

	/**
	 * Method to handle POST requests at the /<gameID>/halfmaps endpoint of the
	 * server. The method gets the game with the given ID from the storage and the
	 * player that sent the halfMap. After that it tries to add the halfMap to the
	 * game. In the addHalfMap method in game the halfMap is being verified. If the
	 * halfMap violates any of the business rules, an error is caught and forwarded.
	 * 
	 * @param gameID  ID of the game that the player tries to send a HalfMap to
	 * @param halfMap the HalfMap that needs to be stored
	 * @return the current GameState of the game
	 */
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> recieveHalfMap(@PathVariable String gameID,
			@Validated @RequestBody HalfMap halfMap) {
		try {
			long timeNow = System.currentTimeMillis();
			Game game = GameStorage.getStorage().getGameById(gameID);
			Player p = game.getPlayerByID(halfMap.getUniquePlayerID());
			if(!p.checkTime(timeNow)) {
				game.lostGame(p.getPlayerID().getUniquePlayerID());
				throw new HalfMapRegistrationException("Message: Player " + p.getPlayerID().getUniquePlayerID() + " took too long to make an action!" );
			}
			game.addHalfMap(halfMap);
			logger.info("Player " + halfMap.getUniquePlayerID() + " sent a valid HalfMap to game " + gameID);
			return new ResponseEnvelope<>(
					GameStateConverter.convert(game, game.getPlayerByID(halfMap.getUniquePlayerID())));
		} catch (Exception e) {
			throw new HalfMapRegistrationException("Message: " + e.getMessage());

		}

	}
	/**
	 * Method that handles GET Requests at the /<gameID>/states/<playerID> endpoint of the server.
	 * The method fetches the game with the given gameID from the Storage, if the game doesn't exist it throws an error.
	 * Then it gets the Player object that asked for the state and returns the GameState of the game.
	 * @param gameID ID of the game that the player wants to get the state of
	 * @param playerID the ID of the player that tries to get the State
	 * @return GameState of game
	 */
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> getGameState(@PathVariable String gameID,
			@PathVariable String playerID) {
		try {
			long timeNow = System.currentTimeMillis();
			Game game = GameStorage.getStorage().getGameById(gameID);
			Player p = game.getPlayerByID(playerID);
			if(!p.checkTime(timeNow)) {
				game.lostGame(p.getPlayerID().getUniquePlayerID());
				throw new GameStateRequestException("Message: Player " + p.getPlayerID().getUniquePlayerID() + " took too long to make an action!" );
			}
			logger.info("Player " + playerID + " succesfully got GameState for Game " + gameID);
			return new ResponseEnvelope<>(GameStateConverter.convert(game, game.getPlayerByID(playerID)));
		} catch (Exception e) {
			throw new GameStateRequestException("Message: " + e.getMessage());
		}

	}

	@ExceptionHandler({ BaseServerException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(BaseServerException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());
		logger.error(ex.getErrorName() + " " + ex.getMessage());
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
