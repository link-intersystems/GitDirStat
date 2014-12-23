package com.link_intersystems.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RelativeWidthResizer<T> extends ComponentAdapter {

	private Map<T, Double> relativeWidths = new HashMap<T, Double>();
	private ComponentResize<T> componentResize;

	private boolean fillLastComponent = true;

	public RelativeWidthResizer(ComponentResize<T> componentResize) {
		this.componentResize = componentResize;
	}

	public void setRelativeWidth(T component, double relativeWidth) {
		if (relativeWidth < 0.0) {
			throw new IllegalArgumentException(
					"Relative width must be greater or equal to 0.0");
		}

		if (relativeWidth > 1.0) {
			throw new IllegalArgumentException(
					"Relative width must be less or equal to 1.0");
		}

		double totalRelativeWidth = 0.0;
		for (Double relativeComponentWidth : relativeWidths.values()) {
			totalRelativeWidth += relativeComponentWidth.doubleValue();
		}

		double availableRelativeWidth = 1.0d - (totalRelativeWidth + relativeWidth);

		boolean totalPercentageExceeded = availableRelativeWidth < 0;
		if (totalPercentageExceeded) {
			double remainingRelativeWidth = 1.0d - totalRelativeWidth;
			String message = MessageFormat.format(
					"Can't set component's relative width to {0}."
							+ " {1} relative width remaining", relativeWidth,
					remainingRelativeWidth);
			throw new IllegalArgumentException(message);
		}

		relativeWidths.put(component, relativeWidth);
	}

	public void setFillLastComponent(boolean fillLastComponent) {
		this.fillLastComponent = fillLastComponent;
	}

	public boolean isFillLastComponent() {
		return this.fillLastComponent;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Component component = e.getComponent();
		apply(component);
	}

	public void apply(Component baseComponent) {
		Dimension size = baseComponent.getSize();
		int maxWidth = (int) size.getWidth();
		int remainingWidth = maxWidth;

		Set<Entry<T, Double>> entrySet = relativeWidths.entrySet();
		Iterator<Entry<T, Double>> entrySetIter = entrySet.iterator();

		while (entrySetIter.hasNext()) {
			Entry<T, Double> componentEntry = entrySetIter.next();
			T componentToResize = componentEntry.getKey();
			Double relativeWidth = componentEntry.getValue();
			boolean lastComponent = !entrySetIter.hasNext();
			ResizeCalcParams<T> resizeCalcParams = new ResizeCalcParams<T>(
					componentToResize, maxWidth, remainingWidth, lastComponent,
					relativeWidth);
			remainingWidth -= resizeCalcParams.getRemainingWidth();

			int width = calculateWidth(resizeCalcParams);

			componentResize.setWidth(componentToResize, width);

		}
	}

	protected int calculateWidth(ResizeCalcParams<T> componentResize) {
		int width = (int) (componentResize.getMaxWidth() * componentResize
				.getRelativeWidth());
		componentResize.substractRemaining(width);

		if (componentResize.isLastComponent() && componentResize.hasRemaining()
				&& fillLastComponent) {
			width += componentResize.getRemainingWidth();
		}
		return width;
	}

	protected static class ResizeCalcParams<T> {

		private int maxWidth;
		private int remainingWidth;
		private boolean lastComponent;
		private Double relativeWidth;
		private T componentToResize;

		public ResizeCalcParams(T componentToResize, int maxWidth,
				int remainingWidth, boolean lastComponent, Double relativeWidth) {
			this.componentToResize = componentToResize;
			this.maxWidth = maxWidth;
			this.remainingWidth = remainingWidth;
			this.lastComponent = lastComponent;
			this.relativeWidth = relativeWidth;
		}

		public Double getRelativeWidth() {
			return relativeWidth;
		}

		public boolean isLastComponent() {
			return lastComponent;
		}

		public int getMaxWidth() {
			return maxWidth;
		}

		public boolean hasRemaining() {
			return remainingWidth > 0;
		}

		public void substractRemaining(int size) {
			this.remainingWidth -= size;
		}

		public int getRemainingWidth() {
			return remainingWidth;
		}

		public T getComponentToResize() {
			return componentToResize;
		}
	}
}