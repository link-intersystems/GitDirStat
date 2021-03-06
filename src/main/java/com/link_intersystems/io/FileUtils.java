package com.link_intersystems.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class FileUtils {

	private static final String PATH_ABBRIVIATION = "..";

	public static String abbreviatedPath(File file, int size) {
		String filePath = null;
		try {
			filePath = file.getCanonicalPath();
		} catch (IOException e) {
			filePath = file.getAbsolutePath();
		}

		StringBuilder abbreviatedPath = new StringBuilder();
		StringBuilderMarker abbreviationMarker = new StringBuilderMarker(
				abbreviatedPath);
		String[] pathSegments = StringUtils.split(filePath, File.separator);

		if (pathSegments.length > 0) {
			String firstSegment = pathSegments[0];

			abbreviatedPath.append(firstSegment);
			abbreviatedPath.append(File.separator);

			if (pathSegments.length > 1) {
				abbreviationMarker.begin();
				abbreviatedPath.append(PATH_ABBRIVIATION);
				abbreviatedPath.append(File.separator);
				abbreviationMarker.end();
			}

			if (abbreviatedPath.length() < size) {
				if (pathSegments.length > 1) {
					String lastSegment = pathSegments[pathSegments.length - 1];
					abbreviatedPath.append(lastSegment);
				}

				int i = 1;
				for (; i < pathSegments.length; i++) {
					String pathSegment = pathSegments[i];
					if (abbreviatedPath.length() + pathSegment.length() < size) {
						abbreviationMarker.appendBeforeMark(pathSegment);
						abbreviationMarker.appendBeforeMark(File.separator);
					} else {
						break;
					}
				}

				if (i >= pathSegments.length) {
					abbreviationMarker.delete();
				}
			}
		}

		return abbreviatedPath.toString();
	}
}
