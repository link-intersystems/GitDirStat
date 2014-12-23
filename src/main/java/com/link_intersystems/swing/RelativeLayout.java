package com.link_intersystems.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager2;

public class RelativeLayout extends FlowLayout implements LayoutManager2 {

	private static final long serialVersionUID = 3055707054782915949L;

	public static class RelativeConstraints {

		private double relativeWidth;

		public RelativeConstraints(double relativeWidth) {
			this.relativeWidth = relativeWidth;
		}
	}

	private RelativeWidthResizer<Component> relativeWidthResizer;

	public RelativeLayout() {
		super(FlowLayout.LEFT);

		relativeWidthResizer = new JComponentAwareRelativeWidthResizer(
				new ComponentPrefferedSizeResize());
		relativeWidthResizer.setFillLastComponent(false);
	}

	public boolean isFillLastComponent() {
		return relativeWidthResizer.isFillLastComponent();
	}

	public void setFillLastComponent(boolean fillLastComponent) {
		relativeWidthResizer.setFillLastComponent(fillLastComponent);
	}

	@Override
	public void layoutContainer(Container parent) {
		super.layoutContainer(parent);
		relativeWidthResizer.apply(parent);
		super.layoutContainer(parent);
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof RelativeConstraints) {
			RelativeConstraints relativeConstraints = (RelativeConstraints) constraints;
			relativeWidthResizer.setRelativeWidth(comp,
					relativeConstraints.relativeWidth);
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {

	}

}
