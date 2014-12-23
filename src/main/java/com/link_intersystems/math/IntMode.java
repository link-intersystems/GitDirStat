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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Mode} with an {@link Integer} precision.
 * 
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 * 
 */
public class IntMode implements Mode<Integer> {

	/**
	 * The key represents an added integer while the value represents the
	 * integers count.
	 */
	private Map<Integer, Integer> modeCount = new HashMap<Integer, Integer>();

	private List<Integer> modes = new ArrayList<Integer>();

	private Max<Integer> maxCount = new IntMax();

	/**
	 * 
	 * @inherited
	 * 
	 * @return the the value that was added most. If two or more values have
	 *         been added the same times, the result is undefined. Any of those
	 *         values might be returned. Use {@link #getValues()} if you want to
	 *         get all values that were added most often.
	 * @since 1.2.0.0.
	 */
	public Integer getValue() {
		Integer value = null;
		if (!modes.isEmpty()) {
			value = modes.get(0);
		}
		return value;
	}

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
	public List<Integer> getValues() {
		return Collections.unmodifiableList(modes);
	}

	/**
	 * @inherited
	 * @since 1.2.0.0
	 */
	public boolean addValue(Number value) {
		int oldModeCount = modes.size();
		Integer intValue = value.intValue();

		Integer valueCount = modeCount.get(intValue);
		if (valueCount == null) {
			valueCount = 1;
		} else {
			valueCount++;
		}
		modeCount.put(intValue, valueCount);

		if (maxCount.addValue(valueCount)) {
			modes.clear();
			modes.add(intValue);
		} else if (maxCount.getValue().equals(valueCount)) {
			modes.add(intValue);
		}

		int newModeCount = modes.size();
		return oldModeCount != newModeCount;
	}
}
