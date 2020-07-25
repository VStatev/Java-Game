package rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import server.convertors.GameStateConverter;
import server.game.Game;
import server.game.Player;
import server.generators.GameIDGenerator;

public class TestBusinessRules {
	/**
	 * Method to test if the business rule of only two players registering per game
	 * is fulfilled. The method takes three arguments from the source, which are
	 * just for constructor purposes. The method registers the player two times to
	 * the game, and then asserts that the registration of the third player to the
	 * game would case an exception being thrown.
	 * 
	 * @param firstName firstName of test Player
	 * @param lastName  lastName of test Player
	 * @param studentID studentID of test Player
	 */
	@ParameterizedTest
	@CsvSource({ "Test first Name, Test Last Name, Test StudentID" })
	public void testIfOnlyTwoPlayersCanRegistrate(String firstName, String lastName, String studentID) {
		PlayerRegistration reg = new PlayerRegistration(firstName, lastName, studentID);
		GameIDGenerator generatorForGameID = new GameIDGenerator();
		Game gameToBeRegisteredTo = new Game(generatorForGameID.generate());
		for (int i = 0; i < 2; i++) {
			try {
				gameToBeRegisteredTo.addPlayer(new Player(reg));
			} catch (Exception e) {
				System.out.println("Failed");
				return;
			}
		}
		assertThrows(Exception.class, () -> gameToBeRegisteredTo.addPlayer(new Player(reg)));
	}

	/**
	 * Method to test if the business rule of gameID being 5 characters long is
	 * fulfilled. The method crates a new GameIDGenerator, which on its hand
	 * generates a gameID. Then an assertion is made that the length of the string
	 * would be equal to 5.
	 */
	@Test
	public void testGeneratedGameIDLength() {
		GameIDGenerator generatorForGameIDThatWillBeTested = new GameIDGenerator();
		String gameIDThatWillBeTested = generatorForGameIDThatWillBeTested.generate();
		assertEquals(5, gameIDThatWillBeTested.length());
	}

	/**
	 * Method to test if the business rule of gameID being constructed from only
	 * alphanumeric characters is fulfilled. The method crates a new
	 * GameIDGenerator, which on its hand generates a gameID. Then an assertion is
	 * made that the string will match an alphanumeric Pattern.
	 */
	@Test
	public void testIfGameIDContainsOnlyAlphaNumericCharacters() {
		GameIDGenerator generatorForGameIDThatWillBeTested = new GameIDGenerator();
		String gameIDThatWillBeTested = generatorForGameIDThatWillBeTested.generate();
		assertTrue(gameIDThatWillBeTested.matches("[A-Za-z0-9]+"));
	}

	/**
	 * Method to test if the business rule of Opponent's gameID being hidden is
	 * fulfilled. The method creates a new Game and registers two player to it. Then
	 * a GameState is requested by player1. From the GameState the Players'
	 * information is being stored in a set and their IDs in an ArrayList. Then an
	 * Assertion is made that the opponent's playerID is not contained in the List
	 * of received IDs.
	 */
	@Test
	public void testIfOponentsGameIDIsHidden() {
		GameIDGenerator generator = new GameIDGenerator();
		Game game = new Game(generator.generate());
		Player player1 = new Player(new PlayerRegistration("FName1", "LName1", "StudentID1"));
		Player player2 = new Player(new PlayerRegistration("FName2", "LName2", "StudentID2"));
		try {
			game.addPlayer(player1);
			game.addPlayer(player2);
		} catch (Exception e) {
		}
		GameState gameStateInWhichPlayer2sIDShouldBeHidden = GameStateConverter.convert(game, player1);
		Set<PlayerState> playerStates = gameStateInWhichPlayer2sIDShouldBeHidden.getPlayers();
		ArrayList<String> receivedPlayerIDs = new ArrayList<>();
		for (PlayerState player : playerStates) {
			receivedPlayerIDs.add(player.getUniquePlayerID());
		}
		assertFalse(receivedPlayerIDs.contains(player2.getPlayerID().getUniquePlayerID()));
	}

	/**
	 * Method to test if the business rule that when a player sends a HalfMap when
	 * he isn't supposed to, he will lose. The method creates a new Game and
	 * registers two players to it. Then the player who shouldn't act next tries to
	 * register a HalfMap. If an exception isn't thrown during the HalfMap
	 * registration the test will fail, if not an assertion is made that the
	 * EPlayerGameState of the player that send the map is equal to Lost.
	 */
	@Test
	public void testIfAPlayerActsWhenHeShouldntWillLoseHimTheGame() {
		GameIDGenerator generator = new GameIDGenerator();
		Game game = new Game(generator.generate());
		Player player1 = new Player(new PlayerRegistration("FName1", "LName1", "StudentID1"));
		Player player2 = new Player(new PlayerRegistration("FName2", "LName2", "StudentID2"));
		try {
			game.addPlayer(player1);
			game.addPlayer(player2);
		} catch (Exception e) {
		}
		Player playerThatShouldntActNext = null;
		if (player1.getPlayerGameState() == EPlayerGameState.ShouldActNext)
			playerThatShouldntActNext = player2;
		else
			playerThatShouldntActNext = player1;
		HalfMap halfMap = new HalfMap(playerThatShouldntActNext.getPlayerID(), null);
		try {
			game.addHalfMap(halfMap);
			fail();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(EPlayerGameState.Lost, playerThatShouldntActNext.getPlayerGameState());
	}
}
