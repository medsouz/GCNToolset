package net.medsouz.gcn.toolset;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.medsouz.gcn.file.BufferFile;
import net.medsouz.gcn.file.ChannelFile;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;
import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;
import net.medsouz.gcn.file.filesystem.rarc.RARCArchive;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	@FXML
	private TreeView<String> fileTree;

	@Override
	public void initialize(URL location, ResourceBundle resources) {


		GCMArchive archive = new GCMArchive();
		try {
			//TODO: Don't hardcode
			archive.read(new ChannelFile(new File("X:\\working_dir\\Mario Kart - Double Dash!!.iso")));
			fileTree.setRoot(getFileTree(archive));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
