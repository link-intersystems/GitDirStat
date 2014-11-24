package com.link_intersystems.tools.git.cli;

import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.tools.git.service.SizeMetrics;

public class SizesMetricsFormatter {
	private SizeMetrics sizeMetrics;

	public SizesMetricsFormatter(SizeMetrics sizeMetrics) {
		this.sizeMetrics = sizeMetrics;
	}

	public void format(OutputStream outputStream) {
		Map<String, BigInteger> pathSizes = sizeMetrics.getPathSizes();

		BigInteger max = Collections.max(pathSizes.values());
		int maxLength = max.toString().length();
		for (Entry<String, BigInteger> pathSize : pathSizes.entrySet()) {
			String sizeValue = pathSize.getValue().toString();
			String paddedSize = StringUtils.rightPad(sizeValue, maxLength);
			String path = pathSize.getKey();
			String pathSizeLine = MessageFormat.format("{0} {1}",
					paddedSize, path);
			System.out.println(pathSizeLine);
		}
	}
}