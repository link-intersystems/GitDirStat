package com.link_intersystems.lang;
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


import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Assertion logic that is mostly used to validate methods pre- and
 * post-conditions. Encapsulates assertion logic.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public abstract class Assert {

	/**
	 * Assert that a non-null value is returned by either returning the
	 * <code>value</code> that is checked to be not <code>null</code> or the
	 * <code>defaultIfNull</code> value.
	 *
	 * @param value
	 * @param defaultIfNull
	 * @return
	 * @since 1.2.0.0
	 */
	public static <T> T defaultIfNull(T value, T defaultIfNull) {
		notNull("defaultIfNull", defaultIfNull);
		T guaranteedNotNull = ObjectUtils.defaultIfNull(value, defaultIfNull);
		return guaranteedNotNull;
	}

	/**
	 * Assert that a named value is not <code>null</code> or throw an
	 * {@link IllegalArgumentException}.
	 *
	 * @param name
	 *            the name of the value that must of the Assert.
	 * @param value
	 *            the value of the Assert.
	 * @since 1.2.0.0
	 */
	public static void notNull(String name, Object value)
			throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException(name + " must not be null");
		}
	}

	/**
	 * Assert that a named string value is not blank. A string is considered
	 * blank if is is
	 * <ul>
	 * <li>null</li>
	 * <li>""</li>
	 * <li>" " contains only whitespaces</li>
	 * </ul>
	 *
	 * @param name
	 *            a descriptive name of the string value.
	 * @param value
	 *            the string value.
	 */
	public static void notBlank(String name, String value)
			throws IllegalArgumentException {
		if (StringUtils.isBlank(value)) {
			throw new IllegalArgumentException(name + " must not be blank");
		}
	}

	/**
	 * Assert that a value is greater than a limit.
	 *
	 * @param greaterThan
	 *            the greater limit.
	 * @param name
	 *            a descriptive name of the value.
	 * @param value
	 *            the value.
	 *
	 * @since 1.2.0.0
	 */
	public static void greater(String name, int greaterThan, int value) {
		if (not(value > greaterThan)) {
			String formatted = String.format("%s must be greater than %s",
					name, Integer.toString(greaterThan));
			throw new IllegalArgumentException(formatted);
		}
	}

	/*
	 * Increase the readability of conditional statements.
	 */
	private static boolean not(boolean condition) {
		return !condition;
	}

	/**
	 * Assert that a value is greater than or equal to a limit.
	 *
	 * @param greaterOrEqual
	 *            the limit.
	 * @param name
	 *            a descriptive name of the value.
	 * @param value
	 *            the value.
	 *
	 * @since 1.2.0.0
	 */
	public static void greaterOrEqual(String name, int greaterOrEqual, int value) {
		if (not(value >= greaterOrEqual)) {
			String formatted = String.format(
					"%s must be greater than or equal to %s", name,
					Integer.toString(greaterOrEqual));
			throw new IllegalArgumentException(formatted);
		}
	}

	/**
	 * Assert that a double is greater than or equal to a limit.
	 *
	 * @param greaterOrEqual
	 *            the limit.
	 * @param name
	 *            a descriptive name of the value.
	 * @param value
	 *            the value.
	 *
	 * @since 1.2.0.0
	 */
	public static void greaterOrEqual(String name, double greaterOrEqual,
			double value) {
		if (not(value >= greaterOrEqual)) {
			String formatted = String.format(
					"%s must be greater than or equal to %s", name,
					Double.toString(greaterOrEqual));
			throw new IllegalArgumentException(formatted);
		}
	}

	/**
	 * Assert that a value is lower than a limit.
	 *
	 * @param lowerThan
	 *            the lower limit.
	 * @param name
	 *            a descriptive name of the value.
	 * @param value
	 *            the value.
	 *
	 * @since 1.2.0.0
	 */
	public static void lower(String name, int lowerThan, int value) {
		if (not(value < lowerThan)) {
			String formatted = String.format("%s must be lower than %s", name,
					Integer.toString(lowerThan));
			throw new IllegalArgumentException(formatted);
		}
	}

	/**
	 * Assert that a value is lower than or equal to a limit.
	 *
	 * @param lowerOrEqual
	 *            the limit.
	 * @param name
	 *            a descriptive name of the value.
	 * @param value
	 *            the value.
	 *
	 * @since 1.2.0.0
	 */
	public static void lowerOrEqual(String name, int lowerOrEqual, int value) {
		if (not(value <= lowerOrEqual)) {
			String formatted = String.format(
					"%s must be lower than or equal to %s", name,
					Integer.toString(lowerOrEqual));
			throw new IllegalArgumentException(formatted);
		}
	}

	/**
	 * Assert that a value mets a condition or throw an
	 * {@link IllegalArgumentException}.
	 *
	 * @param expectedTruth
	 *            the truth that is expected.
	 * @param messageFormat
	 *            the message of the {@link IllegalArgumentException}. The
	 *            message can be a formatted message as defined by
	 *            {@link String#format(String, Object...)}.
	 * @param messageFormatArgs
	 *            the arguments that should be applied to the formatted message.
	 * @throws IllegalArgumentException
	 *             if the condition is not met.
	 * @since 1.2.0.0
	 */
	public static void isTrue(boolean expectedTruth, String messageFormat,
			Object... messageFormatArgs) throws IllegalArgumentException {
		if (not(expectedTruth)) {
			String formatted = String.format(messageFormat, messageFormatArgs);
			throw new IllegalArgumentException(formatted);
		}
	}

	/**
	 * Assert that a value is an instance of one of expected classes or throw an
	 * {@link IllegalArgumentException}.
	 *
	 * @param name
	 *            the name of the value.
	 * @param value
	 *            the value.
	 * @param expectedInstanceOfs
	 *            the expected classes that the value must be an instance of.
	 * @since 1.2.0.0
	 */
	public static void instanceOf(String name, Object value,
			Class<?>... expectedInstanceOfs) {
		for (int i = 0; i < expectedInstanceOfs.length; i++) {
			Class<?> expectedInstanceOf = expectedInstanceOfs[i];
			if (expectedInstanceOf.isInstance(value)) {
				return;
			}
		}
		String exceptionMessage = String.format("%s must be an instance of %s",
				name, Arrays.toString(expectedInstanceOfs));
		throw new IllegalArgumentException(exceptionMessage);
	}

	/**
	 * Assert that a value is the same class as the expected class or throws an
	 * {@link IllegalArgumentException}.
	 *
	 * @param name
	 *            the name of the value.
	 * @param expectedClass
	 *            the expected class.
	 * @param value
	 *            the value.
	 * @since 1.2.0.0
	 */
	public static void sameClass(String name, Class<?> expectedClass,
			Class<?> value) {
		if (expectedClass != value) {
			String exceptionMessage = String.format(
					"%s must be %s, but was %s", name, expectedClass, value);
			throw new IllegalArgumentException(exceptionMessage);
		}
	}

	/**
	 * Assert that the value <code>int</code> is not equal to the notExpected
	 * <code>int</code>.
	 *
	 * @param name
	 *            the name of the value.
	 * @param notExcepted
	 *            the <code>int</code> value that the value should not be equal
	 *            to.
	 * @param value
	 *            the <code>int</code> value to ensure to be not equal to the
	 *            notExpected <code>int</code>.
	 * @since 1.2.0.0
	 */
	public static void notEqual(String name, int notExcepted, int value) {
		if (notExcepted == value) {
			String exceptionMessage = String.format(
					"%s was %s, but must not be equal to %s", name, value,
					notExcepted);
			throw new IllegalArgumentException(exceptionMessage);
		}
	}

	/**
	 * Assert that the value <code>int</code> is equal to the expected
	 * <code>int</code>.
	 *
	 * @param name
	 *            the name of the value.
	 * @param expected
	 *            the <code>int</code> value that the value should be equal to.
	 * @param value
	 *            the <code>int</code> value to ensure to be equal to the
	 *            expected <code>int</code>.
	 * @since 1.2.0.0
	 */
	public static void equal(String name, int expected, int value) {
		if (expected != value) {
			String exceptionMessage = String.format(
					"%s was %s, but must be %s", name, value, expected);
			throw new IllegalArgumentException(exceptionMessage);
		}
	}

	/**
	 * Assert that the value {@link Object} is equal to the expected
	 * {@link Object} according to the {@link Object#equals(Object)}
	 * specification.
	 *
	 * @param name
	 *            the name of the value.
	 * @param expected
	 *            the {@link Object} value that the value should be equal to.
	 * @param value
	 *            the {@link Object} value to ensure to be equal to the expected
	 *            {@link Object}.
	 * @since 1.2.0.0
	 */
	public static void equal(String name, Object expected, Object value) {
		if (expected == null && value == null) {
			return;
		}
		Object compare = expected;
		Object other = value;
		if (expected == null) {
			compare = value;
			other = expected;
		}
		if (!compare.equals(other)) {
			String exceptionMessage = String.format(
					"%s was %s, but must be %s", name, value, expected);
			throw new IllegalArgumentException(exceptionMessage);
		}

	}

	/**
	 * Assert that the value is within a defined range.
	 *
	 * @param name
	 *            the name of the value.
	 * @param min
	 *            the minimum that the value must be equal to or greater than.
	 * @param max
	 *            the maximum that the value must be equal to or less than.
	 * @param value
	 *            the value to ensure to be in an expected range. .
	 * @since 1.2.0.0
	 */
	public static void inRange(String name, int value, int min, int max) {
		if (not(min <= value && value <= max)) {
			String exceptionMessage = String.format(
					"%s was %s, but must be within range [%s,%s]", name, value,
					min, max);
			throw new IllegalArgumentException(exceptionMessage);
		}
	}

	/**
	 * Assert that the value is within a defined range, exclusive the range's
	 * max value.
	 *
	 * @param name
	 *            the name of the value.
	 * @param min
	 *            the minimum that the value must be equal to or greater than.
	 * @param max
	 *            the maximum that the value must be less than.
	 * @param value
	 *            the value to ensure to be in an expected range. .
	 * @since 1.2.0.0
	 */
	public static void inRangeExclusiveMax(String name, int value, int min,
			int max) {
		if (not(min <= value && value < max)) {
			String exceptionMessage = String.format(
					"%s was %s, but must be within range [%s,%s]", name, value,
					min, max);
			throw new IllegalArgumentException(exceptionMessage);
		}

	}
}