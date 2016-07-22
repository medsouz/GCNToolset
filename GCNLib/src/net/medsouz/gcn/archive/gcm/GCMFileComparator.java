package net.medsouz.gcn.archive.gcm;

import java.io.File;
import java.util.Comparator;

public class GCMFileComparator implements Comparator<File> {
	@Override
	public int compare(File o1, File o2) {
		String s1 = o1.getName().toUpperCase();
		String s2 = o2.getName().toUpperCase();
		int n1 = s1.length(), n2 = s2.length();
		int n = n1 < n2 ? n1 : n2;
		for (int i = 0; i < n; i++) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);
			if (c1 != c2) {
				if (c1 != c2) {
					return c1 - c2;
				}
			}
		}
		return n1 - n2;
	}
}
