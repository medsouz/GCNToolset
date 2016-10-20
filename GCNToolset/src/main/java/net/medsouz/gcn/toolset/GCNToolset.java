package net.medsouz.gcn.toolset;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GCNToolset extends Application {
	public static void main(String[] args) {
		GCNToolset.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		@SuppressWarnings("ConstantConditions")
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));
		Scene scene = new Scene(root, 800, 600);

		primaryStage.setTitle("GCNToolset");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
