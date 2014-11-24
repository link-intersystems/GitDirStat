package com.link_intersystems.tools.git.service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class SizeMetrics {

	private Map<String, BigInteger> pathSizes;

	SizeMetrics(Map<String, BigInteger> pathSizes) {
		this.pathSizes = pathSizes;
	}

	public Map<String, BigInteger> getPathSizes() {
		return Collections.unmodifiableMap(pathSizes);
	}

}
