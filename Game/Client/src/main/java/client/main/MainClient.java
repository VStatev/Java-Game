package client.main;

public class MainClient {

	public static void main(String[] args) {
		String serverBaseUrl = args[1];
		String gameId = args[2];

		GameStatus model = new GameStatus();

		Network network = new Network(serverBaseUrl, gameId);
		Controller controller = new Controller(model, network);
		controller.registerPlayer("Vasil", "Statev", "11709076");
		controller.play();

	}
}
