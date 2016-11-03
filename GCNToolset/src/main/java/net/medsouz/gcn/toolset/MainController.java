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
import net.medsouz.gcn.file.FileFormatRegistry;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private Stage stage;

	@FXML
	private TreeView<String> fileTree;
	@FXML
	private MenuItem menuOpen;
	@FXML
	private MenuItem menuClose;

	public MainController(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent actionEvent) {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open File");
					File selected = fileChooser.showOpenDialog(stage);

					String ext = selected.getName().substring(selected.getName().lastIndexOf(".")).toLowerCase();
					FileFormatRegistry format = FileFormatRegistry.lookup(ext);
					if(format.isExtending(Archive.class)) {
						Archive archive = (Archive)format.getInstance();
						archive.read(new ChannelFile(selected));
						fileTree.setRoot(getFileTree(archive, ext));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		menuClose.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent actionEvent) {
				fileTree.setRoot(null);
			}
		});
	}


	public TreeItem<String> getFileTree(Archive archive, String ext) {
		TreeItem<String> rootItem = new TreeItem<>(archive.getName(), new ImageView(FileIconRegistry.lookup(ext).getImage()));
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
			int extOffset = root.getName().lastIndexOf(".");
			String ext = (extOffset != -1) ? root.getName().substring(extOffset).toLowerCase() : "?";
			item.setGraphic(new ImageView(FileIconRegistry.lookup(ext).getImage()));
			FileFormatRegistry format = FileFormatRegistry.lookup(ext);
			if(format.isExtending(Archive.class)) {
				Archive nestedArchive = (Archive)format.getInstance();
				if(nestedArchive.read(new BufferFile(archive.getFile(root)))) {
					for(FileEntry fe : nestedArchive.getFilesystem().getChildren())
						setFileTree(fe, item, nestedArchive);
				}
			}
		}
		parent.getChildren().add(item);
	}
}
