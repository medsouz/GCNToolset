package net.medsouz.gcn.toolset;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class FileRegistry {

	private static Map<String, FileRegistry> map = new HashMap<>();
	static {
		map.put("?",  new FileRegistry("Unknown Filetype", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/page_white.png"))));
		map.put(".arc", new FileRegistry("RARC Archive", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/package.png"))));
		map.put(".ast", new FileRegistry("Audio Stream", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/sound.png"))));
		map.put(".aw", new FileRegistry("Audio Wave", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/sound.png"))));
		map.put(".bck", new FileRegistry("Model Animation", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/vector.png"))));
		map.put(".bdl", new FileRegistry("Model", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/brick.png"))));
		map.put(".brk", new FileRegistry("Model Animation", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/vector.png"))));
		map.put(".blo", new FileRegistry("HUD", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/layout.png"))));
		map.put(".bmd", new FileRegistry("Model", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/brick.png"))));
		map.put(".bnr", new FileRegistry("Banner", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/tag_green.png"))));
		map.put(".bti", new FileRegistry("Texture", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/picture.png"))));
		map.put(".btp", new FileRegistry("Texture Animation", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/picture_go.png"))));
		map.put(".gcm", new FileRegistry("Gamecube Disk", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/cd.png"))));
		map.put(".map", new FileRegistry("Debug Symbols", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/page_white_code.png"))));
		map.put(".thp", new FileRegistry("Movie", new Image(FileRegistry.class.getClass().getResourceAsStream("/silk_icons/film.png"))));
	}

	public static FileRegistry lookup(String key) {
		if(map.containsKey(key))
			return map.get(key);
		else
			return map.get("?");
	}

	private Image image;
	private String name;

	private FileRegistry(String name, Image image) {
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
