package com.link_intersystems.lang.math;

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
 * <b>Slope–intercept</b> based {@link LinearEquation}.
 *
 * <pre>
 * y = m * x + b
 * </pre>
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public class PointSlopeLinearEquation implements LinearEquation {

	private Slope slope;

	private CartesianPoint cartesianPoint;

	/**
	 * <b>Slope–intercept</b> based {@link LinearEquation}.
	 *
	 * <pre>
	 * y = m * x + b
	 * </pre>
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param x1
	 * @param y1
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(Slope slope) {
		this(slope, 0.0, 0.0);
	}

	/**
	 * <b>Slope–intercept</b> based {@link LinearEquation}.
	 *
	 * <pre>
	 * y = m * x + b
	 * </pre>
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param x1
	 * @param y1
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(double slope) {
		this(new Slope(slope), 0.0, 0.0);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * <pre>
	 * y = m * x + b
	 * </pre>
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param b
	 *            the intersection point with the y axis.
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(Slope slope, double b) {
		this(slope, 0.0, b);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * <pre>
	 * y = m * x + b
	 * </pre>
	 *
	 * @param m
	 *            the slope of the line.
	 * @param x1
	 * @param y1
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(double slope, double b) {
		this(new Slope(slope), 0.0, b);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param x
	 *            x coordinate of a point on the linear equation.
	 * @param y
	 *            y coordinate of a point on the linear equation.
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(double slope, double x, double y) {
		this(new Slope(slope), new CartesianPoint(x, y));
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param x
	 *            x coordinate of a point on the linear equation.
	 * @param y
	 *            y coordinate of a point on the linear equation.
	 *
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(Slope slope, double x, double y) {
		this(slope, new CartesianPoint(x, y));
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param cartesianPoint
	 *            the {@link CartesianPoint} that describes the x and y
	 *            coordinates. x coordinate of a point on the linear equation.
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(double slope, CartesianPoint cartesianPoint) {
		this(new Slope(slope), cartesianPoint);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param slope
	 *            the slope of the line.
	 * @param cartesianPoint
	 *            the {@link CartesianPoint} that describes the x and y
	 *            coordinates. x coordinate of a point on the linear equation.
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(Slope slope, CartesianPoint cartesianPoint) {
		Assert.notNull("slope", slope);
		Assert.notNull("cartesianPoint", cartesianPoint);
		init(slope, cartesianPoint);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param slope
	 *            the slope of the line.
	 *
	 * @param polarPoint
	 *            the {@link PolarPoint} that describes the x and y coordinates.
	 *            x coordinate of a point on the linear equation.
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(double slope, PolarPoint polarPoint) {
		this(new Slope(slope), polarPoint);
	}

	/**
	 * <b>Slope–intercept</b> based {@link TwoPointLinearEquation}.
	 *
	 * @param m
	 *            the slope of the line.
	 *
	 * @param polarPoint
	 *            the {@link PolarPoint} that describes the x and y coordinates.
	 *            x coordinate of a point on the linear equation.
	 * @since 1.2.0.0
	 */
	public PointSlopeLinearEquation(Slope slope, PolarPoint polarPoint) {
		Assert.notNull("polarPoint", polarPoint);
		CartesianPoint cartesianPoint = polarPoint.toCartesianPoint();
		init(slope, cartesianPoint);
	}

	private void init(Slope slope, CartesianPoint cartesianPoint) {
		this.slope = slope;
		this.cartesianPoint = cartesianPoint;
	}

	/**
	 * The {@link CartesianPoint} that describes the x and y coordinates of this
	 * {@link PointSlopeLinearEquation}.
	 *
	 * @return the {@link CartesianPoint} that describes the x and y coordinates
	 *         of this {@link PointSlopeLinearEquation}.
	 * @since 1.2.0.0
	 */
	protected CartesianPoint getCartesianPoint() {
		return cartesianPoint;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.2.0.0
	 */
	public double fX(double x) {
		CartesianPoint cartesianPoint = getCartesianPoint();
		double px = cartesianPoint.getX();
		double py = cartesianPoint.getY();
		double m = slope.getValue();
		double y = (m * (x - px)) + py;
		return y;
	}

	/**
	 * {@inheritDoc}
	 */
	public double fY(double y) {
		CartesianPoint cartesianPoint = getCartesianPoint();
		double px = cartesianPoint.getX();
		double py = cartesianPoint.getY();
		double m = slope.getValue();
		double x = ((y - py) / m) + px;
		return x;
	}

}
