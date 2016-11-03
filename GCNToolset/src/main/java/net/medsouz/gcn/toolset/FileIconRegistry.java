package net.medsouz.gcn.toolset;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class FileIconRegistry {

	private static Map<String, FileIconRegistry> map = new HashMap<>();
	static {
		map.put("?",  new FileIconRegistry("Unknown Filetype", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/page_white.png"))));
		map.put(".arc", new FileIconRegistry("RARC Archive", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/package.png"))));
		map.put(".ast", new FileIconRegistry("Audio Stream", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/sound.png"))));
		map.put(".aw", new FileIconRegistry("Audio Wave", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/sound.png"))));
		map.put(".bck", new FileIconRegistry("Model Animation", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/vector.png"))));
		map.put(".bdl", new FileIconRegistry("Model", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/brick.png"))));
		map.put(".brk", new FileIconRegistry("Model Animation", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/vector.png"))));
		map.put(".blo", new FileIconRegistry("HUD", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/layout.png"))));
		map.put(".bmd", new FileIconRegistry("Model", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/brick.png"))));
		map.put(".bnr", new FileIconRegistry("Banner", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/tag_green.png"))));
		map.put(".bti", new FileIconRegistry("Texture", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/picture.png"))));
		map.put(".btp", new FileIconRegistry("Texture Animation", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/picture_go.png"))));
		//*.gcm files may have the *.iso extension
		FileIconRegistry gcm = new FileIconRegistry("Gamecube Disk", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/cd.png")));
		map.put(".gcm", gcm);
		map.put(".iso", gcm);
		map.put(".map", new FileIconRegistry("Debug Symbols", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/page_white_code.png"))));
		map.put(".thp", new FileIconRegistry("Movie", new Image(FileIconRegistry.class.getClass().getResourceAsStream("/silk_icons/film.png"))));
	}

	public static FileIconRegistry lookup(String key) {
		if(map.containsKey(key))
			return map.get(key);
		else
			return map.get("?");
	}

	private Image image;
	private String name;

	private FileIconRegistry(String name, Image image) {
		this.name = name;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public Image getImage() {
		return image;
	}
}
