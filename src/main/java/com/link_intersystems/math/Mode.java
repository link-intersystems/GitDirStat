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

import java.util.List;

/**
 * This interface represents the mode of a certain amount of values - the mode
 * is the number that appears most often in a set of numbers.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @param <N>
 *            the precision of the mode.
 * @since 1.2.0.0
 */
public interface Mode<N extends Number> extends Aggregate<N> {

	/**
	 * The the value that was added most. If two or more values have the same
	 * most added count, the result is undefined. Any of those values might be
	 * returned. Use {@link #getValues()} if you want to get all values that
	 * were added most often.
	 * 
	 * @return the the value that was added most. If two or more values have
	 *         been added the same times, the result is undefined. Any of those
	 *         values might be returned. Use {@link #getValues()} if you want to
	 *         get all values that were added most often.
	 * @since 1.2.0.0
	 */
	public N getValue();

	/**
	 * 
	 * @return the values that were added most often if more than one value were
	 *         added the same times. E.g. if the value <code>2</code> was added
	 *         6 times and the value <code>5</code> was added 6 times than
	 *         <code>2</code> and <code>5</code> will be returned. While
	 *         {@link #getValue()} will return either <code>2</code> or
	 *         <code>5</code>.
	 * @since 1.2.0.0
	 */
	public List<Integer> getValues();

	/**
	 * Add another value to this {@link Mode}.
	 * 
	 * @param value
	 *            the value to add.
	 * @throws IllegalArgumentException
	 *             if value is null.
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value);

}
