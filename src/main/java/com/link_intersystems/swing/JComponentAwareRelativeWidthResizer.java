package com.link_intersystems.swing;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JComponent;

public class JComponentAwareRelativeWidthResizer extends
		RelativeWidthResizer<Component> {

	public JComponentAwareRelativeWidthResizer(
			ComponentResize<Component> componentResize) {
		super(componentResize);
	}

	@Override
	protected int calculateWidth(ResizeCalcParams<Component> componentResize) {
		int width = super.calculateWidth(componentResize);
		Component componentToResize = componentResize.getComponentToResize();
		if (componentToResize instanceof JComponent) {
			JComponent jcomponentToResize = (JComponent) componentToResize;
			Insets insets = jcomponentToResize.getInsets();
			width = width - (insets.left + insets.right);
		}

		return width;
	}
}
