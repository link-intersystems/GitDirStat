package com.link_intersystems.tools.git.cli;

import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.tools.git.domain.TreeObject;

public class PathMapFormatter {
	private Map<String, TreeObject> pathMap;

	public PathMapFormatter(Map<String, TreeObject> pathMap) {
		this.pathMap = pathMap;
	}

	public void format(OutputStream outputStream) {

		TreeObject max = Collections.max(pathMap.values());
		BigInteger maxSize = max.getSize();
		int maxLength = maxSize.toString().length();

		for (Entry<String, TreeObject> pathEntry : pathMap.entrySet()) {
			TreeObject treeObject = pathEntry.getValue();
			BigInteger size = treeObject.getSize();
			String sizeValue = size.toString();
			String paddedSize = StringUtils.rightPad(sizeValue, maxLength);
			String path = pathEntry.getKey();
			String pathSizeLine = MessageFormat.format("{0} {1}", paddedSize,
					path);
			System.out.println(pathSizeLine);
		}
	}

}