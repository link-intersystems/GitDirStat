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
package com.link_intersystems.lang.math;

/**
 * This interface represents an aggregate function that can be applied to
 * {@link Number}s.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @param <N>
 *            the precision of the average.
 * @since 1.0.0.0
 */
public interface Aggregate<N extends Number> {

	/**
	 * The current value of this {@link Aggregate}.
	 * 
	 * @return the result, according to the {@link Aggregate} specification, of
	 *         all values that have been added until now.
	 * @since 1.2.0.0
	 */
	public N getValue();

	/**
	 * Add another value to this {@link Aggregate}.
	 * 
	 * @param value
	 *            the value to add.
	 * @return true if the {@link #addValue(Number)} operation changed the value
	 *         of this {@link Aggregate}.
	 * @throws IllegalArgumentException
	 *             if value is null.
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value);
}
