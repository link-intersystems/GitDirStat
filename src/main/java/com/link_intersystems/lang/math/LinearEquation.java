package com.link_intersystems.lang.math;
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
 * Interface of a mathematical linear equation.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 *
 * @since 1.2.0.0
 *
 */
public interface LinearEquation {

	/**
	 * Returns the <code>y</code>(the function of x) coordinate for the given
	 * <code>x</code> coordinate.
	 *
	 * @param x
	 *            the <code>x</code> coordinate.
	 * @return the <code>y</code> coordinate for the given <code>x</code>
	 *         coordinate.
	 */
	public abstract double fX(double x);

	/**
	 * Returns the <code>x</code>(the function of y) coordinate for the given
	 * <code>y</code> coordinate.
	 *
	 * @param y
	 *            the <code>y</code> coordinate.
	 * @return the <code>x</code> coordinate for the given <code>y</code>
	 *         coordinate.
	 */
	public abstract double fY(double y);

}