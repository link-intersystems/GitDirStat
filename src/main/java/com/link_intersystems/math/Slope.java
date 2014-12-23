package com.link_intersystems.math;

import com.link_intersystems.lang.Assert;
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
 * A {@link Slope} between two points.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public class Slope {

	private double slope;

	/**
	 * Constructs a slope based on the cartesian points represented by their x
	 * and y coordinates.
	 *
	 * @param x1
	 *            the first point's x coordinate.
	 * @param y1
	 *            the first point's y coordinate.
	 *
	 * @param x2
	 *            the second point's x coordinate.
	 * @param y2
	 *            the second point's y coordinate.
	 *
	 * @since 1.2.0.0
	 */
	public Slope(double slope) {
		this.slope = slope;
	}

	/**
	 * Constructs a slope based on the cartesian points represented by their x
	 * and y coordinates.
	 *
	 * @param x1
	 *            the first point's x coordinate.
	 * @param y1
	 *            the first point's y coordinate.
	 *
	 * @param x2
	 *            the second point's x coordinate.
	 * @param y2
	 *            the second point's y coordinate.
	 *
	 * @since 1.2.0.0
	 */
	public Slope(double x1, double y1, double x2, double y2) {
		init(new CartesianPoint(x1, y1), new CartesianPoint(x2, y2));
	}

	/**
	 * Constructs a slope based on the two cartesian points.
	 *
	 * @param p1
	 *            the first point.
	 * @param p2
	 *            the second point.
	 *
	 * @since 1.2.0.0
	 */
	public Slope(CartesianPoint p1, CartesianPoint p2) {
		Assert.notNull("p1", p1);
		Assert.notNull("p2", p2);
		init(p1, p2);
	}

	private void init(CartesianPoint p1, CartesianPoint p2) {
		double p1X = p1.getX();
		double p1Y = p1.getY();
		double p2X = p2.getX();
		double p2Y = p2.getY();
		slope = (p2Y - p1Y) / (p2X - p1X);
	}

	/**
	 * Constructs a slope based on the two polar points.
	 *
	 * @param p1
	 *            the first point.
	 * @param p2
	 *            the second point.
	 *
	 * @since 1.2.0.0
	 */
	public Slope(PolarPoint p1, PolarPoint p2) {
		Assert.notNull("p1", p1);
		Assert.notNull("p2", p2);
		CartesianPoint cartesianPoint1 = p1.toCartesianPoint();
		CartesianPoint cartesianPoint2 = p2.toCartesianPoint();
		init(cartesianPoint1, cartesianPoint2);
	}

	/**
	 * The slope determined by
	 *
	 * <pre>
	 *           p2.y - p1.y
	 * slope = ---------
	 *           p2.x - p1.x
	 * </pre>
	 *
	 * @return
	 */
	public double getValue() {
		return slope;
	}
}
