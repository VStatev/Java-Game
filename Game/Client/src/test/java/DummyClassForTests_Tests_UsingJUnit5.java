
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerPositionState;
import MessagesGameState.ETreasureState;
import MessagesGameState.FullMapNode;
import client.exceptions.InvalidArgumentsException;
import client.main.Controller;
import client.main.Direction;
import client.main.Field;
import client.main.Fortress;
import client.main.GameStatus;
import client.main.GenerateMap;
import client.main.Map;
import client.main.MapConverter;
import client.main.Movement;
import client.main.Network;
import client.main.Player;
import client.main.PlayerOnPosition;
import client.main.Point;
import client.main.StateOfPlayer;
import client.main.Terrain;
import client.main.Treasure;

//here we use the newer jUnit5 style to defined tests, see the other package for details on jUnit 4
//the most important difference between 4 and 5 is that the annotations have different names for the same functionality
//and that data driven tests can more easily be defined
//we show comparable functionality for jUnit 4 and 5
public class DummyClassForTests_Tests_UsingJUnit5 {

	// ADDITONAL TIPS ON THIS MATTER ARE GIVEN THROUGHOUT THE TUTORIAL SESSION!

	/*
	 * Note: You can run tests normally, but you can also start tests using the
	 * debug feature of eclipse to step through them. This is quite helpful when
	 * debugging tests so see why they fail.
	 * 
	 * In the following you see: 1) how to set up and prepare tests and also clean
	 * them up after their execution 2) how to test for the correct handling of
	 * failures 3) how to ignore tests 4) how to create normal tests for good cases
	 * 5) how to write data driven tests.
	 */

	/*
	 * Test organization and Test naming When creating unit tests one should also
	 * respect the separation of responsibility, hence: 1) For each class which you
	 * test create an individual unit test class which only holds units test for the
	 * methods of such class under test 2) Each unit test should only test a small
	 * portion, i.e., small amount of asserts so that it becomes easy to spot which
	 * behavior is broken when the test fails. 3) Naming tests correctly is as
	 * important as naming methods, classes, and variables correctly and in a
	 * readable manner I would recommend to apply following style:
	 * CurrentState_ExecutedAction_ExpectedState, e.g.
	 * NoUserRegistered_RegisterNewUser_NewUserIsPermanentlyStoredInTheDB However, a
	 * number of alternative naming conventions exist, examples are given here:
	 * https://dzone.com/articles/7-popular-unit-test-naming Try to follow them and,
	 * most importantly, stay consistent. 4) Organize your tests in multiple
	 * packages, just as your organize your logic classes in multiple to group
	 * together tests and related helper classes
	 * 
	 * NOTE: Take the aspects in mind which were discussed during the tutorial
	 * sessions on good unit testing.
	 */

	// see how this is set up by the @BeforeEach method
	private DummyClassForTests dummy = null;

	/**
	 * Parameterized Test to test the validPoint method in Point, test 1) 0,0 should
	 * be valid test 2) 8,8 should be false
	 * 
	 * @param x        x coordinate of Point
	 * @param y        y coordinate of Point
	 * @param expected expected result
	 */
	@ParameterizedTest
	@CsvSource({ "0,0,true", "8,8,false" })
	public void testValidityOfPoint(int x, int y, Boolean expected) {
		Point testedPointForValidity = new Point(x, y);
		assertEquals(expected, testedPointForValidity.isValid());

	}

	/**
	 * Test the getNeighbors() method in Point, method should return the 4 neighbors
	 * of the point if they are valid Neighbors of Point 1,1 should be: (0,1),
	 * (2,1), (1,0), (1,2)
	 */
	@Test
	public void testGetNeighborsOfPoint() {
		Point rootPoint = new Point(1, 1);
		ArrayList<Point> neighborsRecieved = rootPoint.getNeighbors();
		ArrayList<Point> neighborsExpected = new ArrayList<>();
		neighborsExpected.add(new Point(0, 1));
		neighborsExpected.add(new Point(2, 1));
		neighborsExpected.add(new Point(1, 0));
		neighborsExpected.add(new Point(1, 2));
		assertEquals(neighborsRecieved, neighborsExpected);
	}

	/**
	 * Test the generateMap() method in GenerateMap, the execution of this method
	 * should return without exceptions
	 */
	@Test
	public void testGenerateMap() {
		GenerateMap testGenerator;
		testGenerator = new GenerateMap();
		Map generatedMapFromTestGenerator = testGenerator.getMap();
	}

	/**
	 * Test the convertToHalfMap() method in MapConverter, test Map is generated:
	 * (0,0,Grass,MyFortress,None,None); (0,1,Water,None,None,None);
	 * (0,2,Mountain,None,None) a Set of HalfMapNodes is expected such as:
	 * (0,0,true,Grass); (0,1,false,Water); (0,2,false,Mountain);
	 */
	@Test
	public void testConvertMapToHalfMap() {
		HashMap<Point, Field> hashMapUsedToCreateMap;
		UniquePlayerIdentifier dummyPlayerID = new UniquePlayerIdentifier("test");

		Field firstField = new Field(Terrain.Grass, Fortress.MyFortress, Treasure.None, PlayerOnPosition.None);
		Field secondField = new Field(Terrain.Water, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field thirdField = new Field(Terrain.Mountain, Fortress.None, Treasure.None, PlayerOnPosition.None);
		hashMapUsedToCreateMap = new HashMap<>();
		hashMapUsedToCreateMap.put(new Point(0, 0), firstField);
		hashMapUsedToCreateMap.put(new Point(0, 1), secondField);
		hashMapUsedToCreateMap.put(new Point(0, 2), thirdField);
		Map mapToBeConverted = new Map(hashMapUsedToCreateMap);

		Set<HalfMapNode> halfMapNodesToCreateHalfMap;
		HalfMapNode firstHalfMapNode = new HalfMapNode(0, 0, true, ETerrain.Grass);
		HalfMapNode secondHalfMapNode = new HalfMapNode(0, 1, false, ETerrain.Water);
		HalfMapNode thirdHalfMapNode = new HalfMapNode(0, 2, false, ETerrain.Mountain);
		halfMapNodesToCreateHalfMap = new HashSet<>();
		halfMapNodesToCreateHalfMap.add(firstHalfMapNode);
		halfMapNodesToCreateHalfMap.add(secondHalfMapNode);
		halfMapNodesToCreateHalfMap.add(thirdHalfMapNode);
		HalfMap halfMapExpectedAsResult = new HalfMap(dummyPlayerID, halfMapNodesToCreateHalfMap);

		MapConverter convertBeingTested = new MapConverter();
		assertEquals(halfMapExpectedAsResult, convertBeingTested.convertToHalfMap(dummyPlayerID, mapToBeConverted));
	}

	/**
	 * Test the convertToMap() method in MapConverter, test Set of FullMapNodes is
	 * generated as such: (0,0,MyPosition,MyTreasurePresent,None,None);
	 * (0,1,Water,None,None,None); (0,2,Mountain,None,None) a Set of HalfMapNodes is
	 * expected as such: (0,0,true,Grass); (0,1,false,Water); (0,2,false,Mountain);
	 */
	@Test
	public void testConvertFullMapToMap() {
		Set<FullMapNode> fullMapNodesBeingConvertedToMap;
		FullMapNode firstNode = new FullMapNode(ETerrain.Grass, EPlayerPositionState.MyPosition,
				ETreasureState.MyTreasureIsPresent, EFortState.EnemyFortPresent, 0, 0);
		FullMapNode secondNode = new FullMapNode(ETerrain.Water, EPlayerPositionState.EnemyPlayerPosition,
				ETreasureState.NoOrUnknownTreasureState, EFortState.MyFortPresent, 0, 1);
		FullMapNode thirdNode = new FullMapNode(ETerrain.Mountain, EPlayerPositionState.BothPlayerPosition,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 0, 2);

		fullMapNodesBeingConvertedToMap = new HashSet<>();
		fullMapNodesBeingConvertedToMap.add(firstNode);
		fullMapNodesBeingConvertedToMap.add(secondNode);
		fullMapNodesBeingConvertedToMap.add(thirdNode);

		HashMap<Point, Field> hashMapCreatingExpectedMap = new HashMap<>();

		Field firstField = new Field(Terrain.Grass, Fortress.EnemyFortress, Treasure.MyTreasure,
				PlayerOnPosition.Player1);
		Field secondField = new Field(Terrain.Water, Fortress.MyFortress, Treasure.None, PlayerOnPosition.Player2);
		Field thirdField = new Field(Terrain.Mountain, Fortress.None, Treasure.None, PlayerOnPosition.Both);
		hashMapCreatingExpectedMap.put(new Point(0, 0), firstField);
		hashMapCreatingExpectedMap.put(new Point(0, 1), secondField);
		hashMapCreatingExpectedMap.put(new Point(0, 2), thirdField);
		Map mapExpectedAsResult = new Map(hashMapCreatingExpectedMap);

		MapConverter converterBeingTested = new MapConverter();
		assertEquals(mapExpectedAsResult.getMap(),
				converterBeingTested.convertToMap(fullMapNodesBeingConvertedToMap).getMap());
	}

	/**
	 * Test of findNearestMountain() method of the Movement class Map is generated
	 * as such that the Player is on Point (0,1) and the closest Mountain is on
	 * Point(1,0), in this test all directions will be used.
	 */
	@Test
	public void testMovementFindClosestMountain() {
		Field firstField = new Field(Terrain.Water, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field secondField = new Field(Terrain.Mountain, Fortress.MyFortress, Treasure.None, PlayerOnPosition.None);
		Field thirdField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field fourthField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.Both);
		Field fifthField = new Field(Terrain.Water, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field sixthField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field seventhField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field eigthField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.None);
		Field ninthField = new Field(Terrain.Grass, Fortress.None, Treasure.None, PlayerOnPosition.None);

		HashMap<Point, Field> hashMapToCreateMap = new HashMap<>();
		hashMapToCreateMap.put(new Point(0, 0), firstField);
		hashMapToCreateMap.put(new Point(1, 0), secondField);
		hashMapToCreateMap.put(new Point(2, 0), thirdField);
		hashMapToCreateMap.put(new Point(0, 1), fourthField);
		hashMapToCreateMap.put(new Point(1, 1), fifthField);
		hashMapToCreateMap.put(new Point(2, 1), sixthField);
		hashMapToCreateMap.put(new Point(0, 2), seventhField);
		hashMapToCreateMap.put(new Point(1, 2), eigthField);
		hashMapToCreateMap.put(new Point(2, 2), ninthField);

		Map mapOnWhichMovementIsTested = new Map(hashMapToCreateMap);

		Movement movementBeingTested = new Movement(mapOnWhichMovementIsTested,
				mapOnWhichMovementIsTested.getCurrentPosition());

		ArrayList<Direction> expectedDirectionsToGetToClosestMountain = new ArrayList<>();
		for (int i = 0; i < 2; i++)
			expectedDirectionsToGetToClosestMountain.add(Direction.Down);
		for (int i = 0; i < 4; i++)
			expectedDirectionsToGetToClosestMountain.add(Direction.Right);
		for (int i = 0; i < 4; i++)
			expectedDirectionsToGetToClosestMountain.add(Direction.Up);
		for (int i = 0; i < 3; i++)
			expectedDirectionsToGetToClosestMountain.add(Direction.Left);
		assertEquals(expectedDirectionsToGetToClosestMountain, movementBeingTested.findNearestMountain());
	}

	/**
	 * Test the GameStatus class that acts as Model and the View class using Mockito
	 * to mock the Network class, on request of registerPlayer() the String
	 * "Successfully called Registration" is returned and on updatePlayerStatus()
	 * StateOfPlayer.ShouldActNext is returned, the view should print a successful
	 * registration and the state returned.
	 */
	@Test
	public void MVCtestUsingMockitoToMockNetwork() {
		Network mocked = Mockito.mock(Network.class);
		Mockito.when(mocked.registerPlayer(any())).thenReturn("Succesfully called Registration");

		Mockito.when(mocked.getGameState(any())).thenReturn(StateOfPlayer.ShouldActNext);

		GameStatus testModel = new GameStatus();
		Controller testController = new Controller(testModel, mocked);

		testController.registerPlayer("test", "test", "test");
		testController.updatePlayerStatus();
	}

	/**
	 * This method tests if the exception thrown by registerPlayer are correctly
	 * handled. On attempt to call registerPlayer() mockito throws an
	 * InvalidArgumentsException, if the controller responses right, on entry of a
	 * valid baseURL and gameID the registration would run without a problem.
	 */
	@Test
	public void MVCtestUsingMockitoThrowingExceptions() {
		Network mocked = Mockito.mock(Network.class);
		Mockito.when(mocked.registerPlayer(any()))
				.thenThrow(new InvalidArgumentsException("Enter valid Server URL and GameID to complete test"));

		GameStatus testModel = new GameStatus();
		Controller testController = new Controller(testModel, mocked);
		testController.registerPlayer("test", "test", "test");
	}

	/**
	 * Test that check if the registerPlayer() method in networks throws an
	 * InvalidArgumentsException when its object is created with invalid parameters.
	 */
	@Test
	public void registerPlayer_shouldThrowInvalidArgumentsException_whenConstructorParametersAreWrong() {
		Network networkWithInvalidParameters = new Network("InvalidURL", "InvalidGameID");

		assertThrows(InvalidArgumentsException.class, () -> networkWithInvalidParameters
				.registerPlayer(new Player("testFirstName", "testLastName", "testMatrikel")));
	}
}
