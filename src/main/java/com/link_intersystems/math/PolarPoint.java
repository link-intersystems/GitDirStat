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
 * A mathematical {@link PolarPoint} that is based on an angle (the polar axis
 * in degree) and the distance from the center point.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public class PolarPoint {

	private double distance;

	private double polarAxis;

	/**
	 * Constructs a {@link PolarPoint} based on the angle (the polar axis in
	 * degree) and the distance from the center point
	 * 
	 * @param distance
	 *            the distance from the center point (radius) .
	 * @param polarAxis
	 *            the angle in degree (the polar axis).
	 * 
	 * @since 1.2.0.0
	 */
	public PolarPoint(double distance, double polarAxis) {
		this.distance = distance;
		this.polarAxis = polarAxis;
	}

	/**
	 * The polar axis in degrees.
	 * 
	 * @return the polar axis in degrees.
	 * @since 1.2.0.0
	 */
	public double getPolarAxis() {
		return polarAxis;
	}

	/**
	 * The distance from the center point (radius).
	 * 
	 * @return the distance.
	 * @since 1.2.0.0
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Converts this {@link PolarPoint} to a {@link CartesianPoint}.
	 * 
	 * 
	 * @return the {@link CartesianPoint} that is equal to this
	 *         {@link PolarPoint}.
	 * @since 1.2.0.0
	 */
	public CartesianPoint toCartesianPoint() {
		double radians = Math.toRadians(getPolarAxis());
		double x = getDistance() * Math.cos(radians);
		double y = getDistance() * Math.sin(radians);
		CartesianPoint cartesianPoint = new CartesianPoint(x, y);
		return cartesianPoint;
	}
}
