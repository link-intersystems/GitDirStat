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
import java.math.BigInteger;

import com.link_intersystems.lang.Assert;

/**
 * Base class for {@link Aggregate} funtions that handle {@link BigDecimal}
 * precision values. The {@link #addValue(Number)} method ensures that the given
 * {@link Number} is transformed into a {@link BigDecimal} before calling the
 * {@link #doAddValue(BigDecimal)}.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * 
 */
public abstract class AbstractBigDecimalAggregate implements
		Aggregate<BigDecimal> {

	/**
	 * Ensures that the {@link Number} is transformed to a {@link BigDecimal}
	 * and before {@link #doAddValue(BigDecimal)} is called. The transformation
	 * logic is:
	 * <ul>
	 * <li>
	 * If the {@link Number} is an instance of {@link BigDecimal} the casted
	 * instance will be passed to {@link #doAddValue(BigDecimal)}.</li>
	 * <li>
	 * If the {@link Number} is an instance of {@link BigInteger} a new
	 * {@link BigDecimal} is created and the {@link BigInteger} is passed to the
	 * {@link BigDecimal#BigDecimal(BigInteger)}'s constructor
	 * </ul>
	 * <li>
	 * In any other case the {@link Number} is interpreted as a double value and
	 * a new {@link BigDecimal} is created with that double value. </ul>
	 * 
	 * @inherited
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value) {
		Assert.notNull("value", value);
		BigDecimal bigDecimalToAdd = null;
		if (value instanceof BigDecimal) {
			bigDecimalToAdd = (BigDecimal) value;
		} else if (value instanceof BigInteger) {
			bigDecimalToAdd = new BigDecimal((BigInteger) value);
		} else {
			bigDecimalToAdd = new BigDecimal(value.doubleValue());
		}
		boolean valueChanged = doAddValue(bigDecimalToAdd);
		return valueChanged;
	}

	/**
	 * Called by {@link #addValue(Number)} after the {@link Number} has been
	 * transformed to a {@link BigDecimal}.
	 * 
	 * @param valueToAdd
	 * @return true if the value of this {@link Aggregate} changed after the
	 *         valueToAdd was added.
	 */
	protected abstract boolean doAddValue(BigDecimal valueToAdd);

}