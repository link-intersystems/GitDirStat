package com.link_intersystems.io;

class StringBuilderMarker {

	private int begin = -1;
	private StringBuilder stringBuilder;
	private int end = -1;

	public StringBuilderMarker(StringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	public void delete() {
		if (begin > -1) {
			stringBuilder.delete(begin, end);
		}
	}

	public void begin() {
		this.begin = stringBuilder.length();
		this.end = stringBuilder.length();
	}

	public void end() {
		this.end = stringBuilder.length();
	}

	public void appendBeforeMark(String string) {
		stringBuilder.insert(begin, string);

		int begin = this.begin;
		int end = this.end;

		if (begin < 0) {
			begin = stringBuilder.length();
		}

		if (end < 0) {
			end = stringBuilder.length();
		}

		int diff = end - begin;
		this.begin += string.length();
		this.end = this.begin + diff;
	}

}