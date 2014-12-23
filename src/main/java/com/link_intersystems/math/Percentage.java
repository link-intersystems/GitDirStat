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

import com.link_intersystems.lang.Assert;

/**
 * {@link Percentage} calculates the ratio of units in all.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public class Percentage {

	private static final double PER_CENT_UNIT = 1.0;

	/**
	 * 1 part in 100.
	 */
	public static final double PER_CENT = 100.0;

	/**
	 * 1 part in 1.000.
	 */
	public static final double PER_MIL = PER_CENT_UNIT * 10;

	/**
	 * 1 part in 10.000.
	 */
	public static final double BASE_POINT = PER_MIL * 10;

	/**
	 * 1 part in 100.000.
	 */
	public static final double PER_CENT_MIL = BASE_POINT * 10;

	private LinearEquation unitsToPercentageEquation;

	/**
	 * Constructs a percentage .
	 *
	 * @param maxUnits
	 *            the maximum of elements this {@link Percentage} should
	 *            calculate the {@link #ratio()} of.
	 * @since 1.2.0.0
	 */
	public Percentage(int maxUnits) {
		Assert.greaterOrEqual("maxUnits", 1.0, maxUnits);
		unitsToPercentageEquation = new TwoPointLinearEquation(0.0, 0.0,
				maxUnits, 1.0);
	}

	/**
	 * Sets the maximum of units.
	 *
	 * @param maxUnits
	 */
	public final void setMaxUnits(int maxUnits) {
		unitsToPercentageEquation = new TwoPointLinearEquation(0.0, 0.0,
				maxUnits, PER_CENT_UNIT);
	}

	/**
	 * The ratio of the current {@link #setValue(int)} in all units.
	 *
	 * @return the ratio of the current {@link #setValue(int)} in all units.
	 * @since 1.2.0.0
	 */
	public double ratio(int units) {
		return unitsToPercentageEquation.fX(units);
	}

}
