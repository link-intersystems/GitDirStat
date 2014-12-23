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
 * Base class for {@link Aggregate} funtions that handle {@link BigInteger}
 * precision values. The {@link #addValue(Number)} method ensures that the given
 * {@link Number} is transformed into a {@link BigInteger} before calling the
 * {@link #doAddValue(BigInteger)}.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 *
 */
public abstract class AbstractBigIntegerAggregate implements
		Aggregate<BigInteger> {

	/**
	 * Ensures that the {@link Number} is transformed to a {@link BigInteger}
	 * and before {@link #doAddValue(BigInteger)} is called. The transformation
	 * logic is:
	 * <ul>
	 * <li>
	 * If the {@link Number} is an instance of {@link BigInteger} the casted
	 * instance will be passed to {@link #doAddValue(BigInteger)}.</li>
	 * <li>
	 * If the {@link Number} is an instance of {@link BigDecimal} a new
	 * {@link BigInteger} is created using {@link BigDecimal#toBigInteger()}
	 * </ul>
	 * <li>
	 * In any other case the {@link Number}'s string value is passed to the
	 * {@link BigInteger#BigInteger(String)} constructor</ul>
	 *
	 * @inherited
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value) {
		Assert.notNull("value", value);
		BigInteger bigDecimalToAdd = null;
		if (value instanceof BigInteger) {
			bigDecimalToAdd = (BigInteger) value;
		} else if (value instanceof BigDecimal) {
			bigDecimalToAdd = ((BigDecimal) value).toBigInteger();
		} else {
			bigDecimalToAdd = new BigInteger(String.valueOf(value));
		}
		boolean valueChanged = doAddValue(bigDecimalToAdd);
		return valueChanged;
	}

	/**
	 * Called by {@link #addValue(Number)} after the {@link Number} has been
	 * transformed to a {@link BigInteger}.
	 *
	 * @param valueToAdd
	 * @return true if the value of this {@link Aggregate} changed after the
	 *         valueToAdd was added.
	 */
	protected abstract boolean doAddValue(BigInteger valueToAdd);

}