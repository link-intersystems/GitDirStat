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
 * This interface represents the average of a certain amount of values.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @param <N>
 *            the precision of the average.
 * @since 1.0.0.0
 */
public interface Average<N extends Number> extends Aggregate<N> {

	/**
	 * The average value of all values that have been added to this
	 * {@link Average}.
	 * 
	 * @return the average of all values that have been added.
	 * @since 1.0.0.0
	 */
	public N getValue();

	/**
	 * Add another value to this average.
	 * 
	 * @param value
	 *            the value to add.
	 * @throws IllegalArgumentException
	 *             if value is null.
	 * @since 1.0.0.1
	 */
	public boolean addValue(Number value);

}
