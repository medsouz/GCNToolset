package net.medsouz.gcn.toolset;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GCNToolset extends Application {
	public static void main(String[] args) {
		GCNToolset.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));

		MainController mainController = new MainController(primaryStage);
		loader.setController(mainController);
		Parent root = loader.load();
		Scene scene = new Scene(root, 800, 600);

		primaryStage.setTitle("Gamecube Toolset");
		primaryStage.getIcons().add(new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/wrench.png")));
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
