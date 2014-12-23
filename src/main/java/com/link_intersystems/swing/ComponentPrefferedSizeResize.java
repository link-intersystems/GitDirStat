package com.link_intersystems.swing;

import java.awt.Component;
import java.awt.Dimension;

public class ComponentPrefferedSizeResize implements ComponentResize<Component> {

	@Override
	public void setWidth(Component component, int width) {
		Dimension size = component.getSize();
		double prefferedHeight = size.getHeight();
		Dimension resizedPreferredSize = new Dimension(width,
				(int) prefferedHeight);
		component.setPreferredSize(resizedPreferredSize);
	}
}