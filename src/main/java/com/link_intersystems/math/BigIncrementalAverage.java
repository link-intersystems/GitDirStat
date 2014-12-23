/**
 * Copyright 2011 Link Intersystems GmbH <rene.link@link-intersystems.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.link_intersystems.math;

import java.math.BigDecimal;

/**
 * Implementation of an incremental average calculation with no precision
 * limits. You can add as many values as you wish. This {@link Average}
 * implementation is a bit slower than the {@link IncrementalAverage}, but has
 * no limits.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.0.0.0
 */
public class BigIncrementalAverage extends AbstractBigDecimalAggregate
		implements Average<BigDecimal> {

	private BigDecimal averageIndex;

	private BigDecimal average;

	/**
	 * Create a new {@link BigIncrementalAverage} instance.
	 * 
	 * @since 1.0.0.0
	 */
	public BigIncrementalAverage() {
		averageIndex = BigDecimal.ZERO;
		average = BigDecimal.ZERO;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0.0.0
	 */
	public BigDecimal getValue() {
		return average;
	}

	@Override
	protected boolean doAddValue(BigDecimal valueToAdd) {
		BigDecimal oldAverage = average;
		averageIndex = averageIndex.add(BigDecimal.ONE);
		average = average.add(valueToAdd.subtract(average).divide(averageIndex,
				BigDecimal.ROUND_HALF_EVEN));
		return oldAverage.compareTo(average) != 0;
	}

}
