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

/**
 * This interface represents the maximum of a certain amount of values.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @param <N>
 *            the precision of the maximum.
 * @since 1.2.0.0
 */
public interface Max<N extends Number> extends Aggregate<N> {

	/**
	 * The maximum value of all values that have been added to this {@link Max}.
	 * 
	 * @return the maximum of all values that have been added, or
	 *         <code>null</code> if no value has been added yet.
	 * @since 1.2.0.0
	 */
	public N getValue();

	/**
	 * Add another value to this {@link Max}.
	 * 
	 * @param value
	 *            the value to add.
	 * @throws IllegalArgumentException
	 *             if value is null.
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value);
}
