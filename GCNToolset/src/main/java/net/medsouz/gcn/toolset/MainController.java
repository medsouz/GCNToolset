package net.medsouz.gcn.toolset;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.medsouz.gcn.file.BufferFile;
import net.medsouz.gcn.file.ChannelFile;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;
import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;
import net.medsouz.gcn.file.filesystem.rarc.RARCArchive;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private Stage stage;

	@FXML
	private TreeView<String> fileTree;
	@FXML
	private MenuItem menuOpen;

	public MainController(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent actionEvent) {
				GCMArchive archive = new GCMArchive();
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open Gamecube ROM File");
					archive.read(new ChannelFile(fileChooser.showOpenDialog(stage)));
					fileTree.setRoot(getFileTree(archive));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public TreeItem<String> getFileTree(Archive archive) {
		TreeItem<String> rootItem = new TreeItem<>(archive.getName(), new ImageView(FileRegistry.lookup(".gcm").getImage()));
		for(FileEntry item : archive.getFilesystem().getChildren())
			setFileTree(item, rootItem, archive);
		return rootItem;
	}

	public static void setFileTree(FileEntry root, TreeItem<String> parent, Archive archive) {
		TreeItem<String> item = new TreeItem<>(root.getName());
		if(root.isDirectory()) {
			item.setGraphic(new ImageView(new Image(MainController.class.getClass().getResourceAsStream("/silk_icons/folder.png"))));
			for(FileEntry fileEntry : root.getChildren())
				setFileTree(fileEntry, item, archive);
		} else {
			String ext = root.getName().substring(root.getName().lastIndexOf(".")).toLowerCase();
			item.setGraphic(new ImageView(FileRegistry.lookup(ext).getImage()));
			if(ext.equals(".arc")) {
				RARCArchive rarcArchive = new RARCArchive();
				if(rarcArchive.read(new BufferFile(archive.getFile(root)))) {
					for(FileEntry fe : rarcArchive.getFilesystem().getChildren())
						setFileTree(fe, item, rarcArchive);
				}
			}
		}
		parent.getChildren().add(item);
	}
}
