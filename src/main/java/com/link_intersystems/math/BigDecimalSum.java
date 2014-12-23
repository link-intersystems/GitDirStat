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
 * A {@link Sum} with a {@link BigDecimal} precision.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 * 
 */
public class BigDecimalSum extends AbstractBigDecimalAggregate implements
		Sum<BigDecimal> {

	private BigDecimal sum = BigDecimal.ZERO;

	/**
	 * 
	 * @inherited
	 * 
	 * @since 1.2.0.0.
	 */
	public BigDecimal getValue() {
		return sum;
	}

	@Override
	protected boolean doAddValue(BigDecimal valueToAdd) {
		if (valueToAdd.compareTo(BigDecimal.ZERO) == 0) {
			return false;
		}
		sum = sum.add(valueToAdd);
		return true;
	}
}
