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
package com.link_intersystems.lang.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.link_intersystems.lang.Assert;

/**
 * Implementation of an incremental average calculation based on double
 * precision. Because of the double precision restriction this implementation is
 * very fast. If you need more precision and high performance is not an issue,
 * take a look at {@link BigIncrementalAverage}.
 * 
 * <p>
 * <h3>Incremental average calculation <br/>
 * <font size="-1">&copy;
 * http://jvminside.blogspot.com/2010/01/incremental-average
 * -calculation.html</font></h3>
 * <br/>
 * <cite> <img src="doc-files/average_formula.jpg"/> <br/>
 * <br/>
 * Fortunately we can easily transform the formula to an "incremental" form: <br/>
 * <br/>
 * <img src="doc-files/incremental_average_formula.jpg"/>
 * 
 * </cite> <br/>
 * 
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.0.0.0
 */
public class IncrementalAverage implements Average<Double> {

	private long averageValueCount = 0;

	private Double average = Double.valueOf(0);

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if you try to add more than {@link Long#MAX_VALUE} values,
	 *             because of data types that are used internally.
	 * @throws IllegalStateException
	 *             if value is a {@link BigDecimal} or {@link BigInteger},
	 *             because this implementation can only guarantee double
	 *             precision.
	 * @see BigIncrementalAverage
	 * 
	 * @since 1.0.0.0
	 */
	public boolean addValue(Number value) {
		Assert.notNull("value", value);
		if (value instanceof BigDecimal) {
			throw new IllegalArgumentException(
					"value must not be a big decimal, because only double"
							+ " precision can be guranteed by this average implementation.");
		}
		if (value instanceof BigInteger) {
			throw new IllegalArgumentException(
					"value must not be a big integer, because only double"
							+ " precision can be guranteed by this average implementation.");
		}
		if (getAverageValueCount() == Long.MAX_VALUE) {
			throw new IllegalStateException(
					"This average implementation can only handle a maximum of "
							+ Long.MAX_VALUE + " values.");
		}

		Double oldAverage = average;

		averageValueCount++;
		average = average
				+ ((value.doubleValue() - average) / averageValueCount);
		return oldAverage.compareTo(average) != 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0.0.0
	 */
	public Double getValue() {
		return average;
	}

	/**
	 * 
	 * @return the count of values that have been added to this {@link Average}.
	 */
	protected long getAverageValueCount() {
		return averageValueCount;
	}
}
