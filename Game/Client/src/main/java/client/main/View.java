package client.main;

import java.util.Map.Entry;

public class View {
	/**
	 * Constructor of the View class, taking as parameter the GameStatus object to
	 * which it adds listeners, the listeners activate on property change of three
	 * attributes, 1) currentPosition -> printMovement(oldPoint, newPoint) 2) Map ->
	 * display(Map) 3) state -> printState(StateOfPlayer)
	 * 
	 * @param model GameStatus object acting as Model from MVC Pattern
	 */
	public View(GameStatus model) {

		model.addListener(event -> {
			switch (event.getPropertyName()) {
			case "PlayerPosition":
				printMovement((Point) event.getNewValue(), (Point) event.getOldValue());
				break;
			case "MapUpdate":
				display((Map) event.getNewValue());
				break;
			case "PlayerState":
				printState((StateOfPlayer) event.getNewValue());
				break;
			}
		});

	}

	private void printMovement(Point newPoint, Point oldPoint) {
		if (newPoint.getX() == oldPoint.getX() - 1)
			System.out.println("You moved Left!");
		if (newPoint.getX() == oldPoint.getX() + 1)
			System.out.println("You moved Right!");
		if (newPoint.getY() == oldPoint.getY() - 1)
			System.out.println("You moved Up!");
		if (newPoint.getY() == oldPoint.getY() + 1)
			System.out.println("You moved Down!");
	}

	private void printState(StateOfPlayer s) {
		System.out.println("You " + s);
	}

	private void display(Map m) {
		System.out.println();
		System.out.println();
		System.out.println();
		Field[][] temp = new Field[Global.maxX][Global.maxY];
		for (Entry<Point, Field> entry : m.getMap().entrySet()) {
			temp[entry.getKey().getX()][entry.getKey().getY()] = entry.getValue();
		}
		for (int i = 0; i < Global.maxX; i++) {
			System.out.print("|" + i);
		}
		System.out.println();
		for (int y = 0; y < Global.maxY; y++) {
			System.out.println("_________________");
			for (int x = 0; x < Global.maxX; x++) {
				System.out.print('|');
				switch (temp[x][y].getTerrain()) {
				case Mountain:
					if (temp[x][y].getPlayer() == PlayerOnPosition.Player1) {
						System.out.print("P1");
					} else if (temp[x][y].getPlayer() == PlayerOnPosition.Player2) {
						System.out.print("P2");
					} else if (temp[x][y].getPlayer() == PlayerOnPosition.Both) {
						System.out.print("P12");
					}
					System.out.print("M");
					break;
				case Water:
					System.out.print("W");
					break;

				case Grass:
					if (temp[x][y].getFortress() == Fortress.MyFortress) {
						System.out.print("F1");
					} else if (temp[x][y].getFortress() == Fortress.EnemyFortress) {
						System.out.print("F2");
					} else if (temp[x][y].getTreasure() == Treasure.MyTreasure) {
						System.out.print("T");
					}
					if (temp[x][y].getPlayer() == PlayerOnPosition.Player1) {
						System.out.print("P1");
					} else if (temp[x][y].getPlayer() == PlayerOnPosition.Player2) {
						System.out.print("P2");
					} else if (temp[x][y].getPlayer() == PlayerOnPosition.Both) {
						System.out.print("P12");
					}
					System.out.print("G");
					break;
				}
			}
			System.out.print("   |" + y + '\n');
		}
	}
}
