package server.main;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

//Should already be configured correctly for all use cases, i.e., you will most likely not need to change this class
@SpringBootApplication
@Configuration
public class MainServer {

	/* Note, the server was already configured by us to run on port 18235, you can adapt this, e.g., in the application.properties when
	 * adding it to the build path or by using the code given below.
	 * Note, if you would like to run/debug the server multiple times in a row you will need to close all old running versions as 
	 * otherwise the port would already be recognized to be in use. You can do this in the debug screen, select the server instance and press the red
	 * stop button in the toolbar.*/
	// the test client assumes 18235 as its default port
	private static final int DEFAULT_PORT = 18235;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MainServer.class);
		
		// sets the port programmatically
        app.setDefaultProperties(Collections.singletonMap("server.port", DEFAULT_PORT));
        app.run(args);
	}
}
