package com.link_intersystems.math;
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


/**
 * <b>Two-point<b> based {@link LinearEquation}.
 *
 * <pre>
 *           y2 - y1
 * y - y1 = --------- ( x - x1 )
 *           x2 - x1
 * </pre>
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 *
 * @since 1.2.0.0
 */
public class TwoPointLinearEquation extends PointSlopeLinearEquation {

	/**
	 * <b>Two-point<b> based {@link TwoPointLinearEquation}.
	 *
	 * <pre>
	 *           y2 - y1
	 * y - y1 = --------- ( x - x1 )
	 *           x2 - x1
	 * </pre>
	 *
	 * @param x1
	 *            x coordinate of point 1.
	 * @param y1
	 *            y coordinate of point 1.
	 * @param x2
	 *            x coordinate of point 2.
	 * @param y2
	 *            y coordinate of point 2.
	 *
	 * @since 1.2.0.0
	 */
	public TwoPointLinearEquation(double x1, double y1, double x2, double y2) {
		super(new Slope(x1, y1, x2, y2), x1, y1);
	}

	/**
	 * <b>Two-point<b> based {@link TwoPointLinearEquation}. Constructor for a
	 * {@link LinearEquation} that assumes that the {@link LinearEquation}'s
	 * intercept point with the x axis is (0,0).
	 *
	 * <pre>
	 *           y2 - y1
	 * y - y1 = --------- ( x - x1 )
	 *           x2 - x1
	 * </pre>
	 *
	 * @param x
	 *            x coordinate of point 1.
	 * @param y
	 *            y coordinate of point 1.
	 *
	 * @since 1.2.0.0
	 */
	public TwoPointLinearEquation(double x, double y) {
		this(x, y, 0.0, 0.0);
	}

}
