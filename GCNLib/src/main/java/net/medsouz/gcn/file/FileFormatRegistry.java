package net.medsouz.gcn.file;

import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;
import net.medsouz.gcn.file.filesystem.rarc.RARCArchive;

import java.util.HashMap;

public class FileFormatRegistry<T extends FileFormat> {

	private static HashMap<String, FileFormatRegistry> map = new HashMap<>();
	static {
		map.put("?", new FileFormatRegistry<>("Unknown Format", FileFormat.class));
		map.put(".arc", new FileFormatRegistry<>("RARC Archive", RARCArchive.class));
		//*.gcm files may have the *.iso extension
		FileFormatRegistry gcm = new FileFormatRegistry<>("Gamecube Disk", GCMArchive.class);
		map.put(".gcm", gcm);
		map.put(".iso", gcm);
	}

	public static FileFormatRegistry lookup(String key) {
		if(map.containsKey(key))
			return map.get(key);
		else
			return map.get("?");
	}

	public static HashMap<String, FileFormatRegistry> getFormats() {
		return map;
	}

	private String name;
	private Class<T> format;

	public FileFormatRegistry(String name, Class<T> format) {
		this.name = name;
		this.format = format;
	}

	public String getName() {
		return name;
	}

	public T getInstance() {
		try {
			return format.newInstance();
		} catch(Exception err) {
			err.printStackTrace();
		}
		return null;
	}

	public boolean isExtending(Class<Object> clazz) {
		return clazz.isAssignableFrom(format);
	}
}
