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
 * A mathematical {@link CartesianPoint} that is based on a x and y coordinate..
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * 
 * @since 1.2.0.0
 * 
 */
public class CartesianPoint {

	private double x;
	private double y;

	/**
	 * Constructs a {@link CartesianPoint} for the coordinate.
	 * 
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @since 1.2.0.0
	 */
	public CartesianPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * The x coordinate.
	 * 
	 * @return the x coordinate.
	 * @since 1.2.0.0
	 */
	public double getX() {
		return x;
	}

	/**
	 * The y coordinate.
	 * 
	 * @return the y coordinate.
	 * @since 1.2.0.0
	 */
	public double getY() {
		return y;
	}

	/**
	 * Converts this {@link CartesianPoint} to a {@link PolarPoint}.
	 * 
	 * 
	 * @return the {@link PolarPoint} that is equal to this
	 *         {@link CartesianPoint}.
	 * @since 1.2.0.0
	 */
	public PolarPoint toPolarPoint() {
		double x2 = Math.pow(getX(), 2.0);
		double y2 = Math.pow(getY(), 2.0);
		double distance = Math.sqrt(x2 + y2);

		double polarAxis = Math.toDegrees(Math.atan2(getY(), getX()));

		PolarPoint polarPoint = new PolarPoint(distance, polarAxis);
		return polarPoint;
	}

}
