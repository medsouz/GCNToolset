package net.medsouz.gcn.file.filesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileEntry {
	private String name;
	private boolean isDirectory;
	private FileEntry parent;

	private List<FileEntry> children;

	public FileEntry(String name, FileEntry parent) {
		this(name, parent, false);
	}

	public FileEntry(String name, boolean isDirectory) {
		this(name, null, isDirectory);
	}

	public FileEntry(String name, FileEntry parent, boolean isDirectory) {
		this.name = name;
		this.isDirectory = isDirectory;
		if(isDirectory) //Only create the children list if it is a directory
			children = new ArrayList<>();
		if(parent != null) {
			if(parent.isDirectory()) {
				//Register the file under it's parent
				parent.addChild(this);
				this.parent = parent;
			} else {
				System.err.println("Tried to register " + name + " as the child of a file");
			}
		}

	}

	private void addChild(FileEntry child) {
		if(isDirectory)
			children.add(child);
	}

	public List<FileEntry> getChildren() {
		return (isDirectory) ? Collections.unmodifiableList(children) : null;
	}

	public String getName() {
		return name;
	}

	//Get the full name of the file entry
	public String getFullName() {
		return getFullName(true);
	}

	private String getFullName(boolean root) {
		String path = (root) ? "/" : "";
		if(parent != null)
			path += parent.getFullName(false);
		path += getName();
		if(isDirectory)
			path += "/";
		return path;
	}

	//Get the directory of the file entry
	public String getPath() {
		if(parent != null)
			return parent.getFullName(true);
		else
			return "/";
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public int getTotalChildCount() {
		int count = 0;
		if(isDirectory) {
			count += children.size();
			for(FileEntry child : children) {
				if(child.isDirectory())
					count += child.getTotalChildCount();
			}
		}
		return count;
	}
}
