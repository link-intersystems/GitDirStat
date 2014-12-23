package com.link_intersystems.gitdirstat.cli;

import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.gitdirstat.domain.TreeObject;

public class PathListFormatter {
	private List<TreeObject> treeObjects;

	public PathListFormatter(List<TreeObject> treeObjects) {
		this.treeObjects = treeObjects;
	}

	public void format(OutputStream outputStream) {

		final BigInteger maxSize = getMaxSize();
		int maxLength = maxSize.toString().length();

		for (TreeObject treeObject : treeObjects) {
			BigInteger size = treeObject.getSize();
			String sizeValue = size.toString();
			String paddedSize = StringUtils.rightPad(sizeValue, maxLength);
			String path = treeObject.getPath().getPathname();
			String pathSizeLine = MessageFormat.format("{0} {1}", paddedSize,
					path);
			System.out.println(pathSizeLine);
		}
	}

	private BigInteger getMaxSize() {
		TreeObject max = null;
		for (TreeObject treeObject : treeObjects) {
			if (max == null) {
				max = treeObject;
			} else {
				max = treeObject.compareTo(max) > 0 ? treeObject : max;
			}

		}
		final BigInteger maxSize = max.getSize();
		return maxSize;
	}

}