package com.link_intersystems.tools.git.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestEnvironmentProperties {

	private Properties properties;

	public TestEnvironmentProperties() throws IOException {
		InputStream resourceAsStream = TestEnvironmentProperties.class
				.getResourceAsStream("/maven.properties");
		properties = new Properties();
		properties.load(resourceAsStream);
	}

	public File getOutputDirectory() {
		String property = properties.getProperty("project.build.directory");
		return new File(property);
	}
}
