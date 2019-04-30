package squarify.library;

import java.util.ArrayList;
import java.util.List;

import squarify.library.SquarifyData.DataPoint;

/**
 * A class to generate a squarified treemap layout from a set of values.
 * 
 * @author agatheblues
 * @example Squarify 
 */

public class Squarify {
	
	private ArrayList<SquarifyRect> rects;
	private SquarifyData data;
	
	public Squarify(ArrayList<Float> sizes, float x, float y, float width, float height) {
		data = new SquarifyData(sizes, width, height);
		rects = squarify(data.getDataPoints(), x, y, width, height);
	}
	
	/**
	 * Getter for the list of rectangle's geometry
	 * @return rects
	 */
	public ArrayList<SquarifyRect> getRects() {
		return this.rects;
	}
		
	/** 
	 * Helper that sums all the values of DataPoint
	 * @param values A list of DataPoint values
	 * @return The sum of these values
	 */
	private float sumNormalizedValues(List<DataPoint> l) {
		float result = 0;
		for (int i = 0; i < l.size(); i++) {
			result += l.get(i).getNormalizedValue();
		}
		return result;
	}
	
	/**
	 * Generate rects for each size in sizes
	 * Case when dx >= dy
	 * They will fill up height dy, and width will be determined by their area 
	 * Sizes should be pre-normalized/
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private ArrayList<SquarifyRect> layoutRow(List<DataPoint> values, float x, float y, float dx, float dy) {
		float coveredArea = sumNormalizedValues(values);
		float width = coveredArea / dy;
		ArrayList<SquarifyRect> rects = new ArrayList<SquarifyRect>();
		for (int i = 0; i < values.size(); i++) {
			rects.add(new SquarifyRect(x, y, width, values.get(i).getNormalizedValue() / width, values.get(i).getId(), values.get(i).getValue()));
			y += values.get(i).getNormalizedValue() / width;
		}
		return rects;
	}
	
	/**
	 * Generate rects for each size in sizes
	 * Case when dx < dy
	 * They will fill up width dx, and height will be determined by their area
	 * Sizes should be pre-normalized
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private ArrayList<SquarifyRect> layoutCol(List<DataPoint> values, float x, float y, float dx, float dy) {
		float coveredArea = sumNormalizedValues(values);
		float height = coveredArea / dx;
		ArrayList<SquarifyRect> rects = new ArrayList<SquarifyRect>();
		for (int i = 0; i < values.size(); i++) {
			rects.add(new SquarifyRect(x, y, values.get(i).getNormalizedValue() / height, height, values.get(i).getId(), values.get(i).getValue()));
			x += values.get(i).getNormalizedValue() / height;
		}
		return rects;
	}
	
	/**
	 * Compute remaining area when dx >= dy
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private SquarifyRect leftoverRow(List<DataPoint> values, float x, float y, float dx, float dy) {
		float coveredArea = sumNormalizedValues(values);
		float width = coveredArea / dy;
		float leftoverX = x + width;
		float leftoverY = y;
		float leftoverDx = dx - width;
		float leftoverDy = dy;
		return new SquarifyRect(leftoverX, leftoverY, leftoverDx, leftoverDy);
	}
	
	/**
	 * Compute remaining area when dx >= dy
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private SquarifyRect leftoverCol(List<DataPoint> values, float x, float y, float dx, float dy) {
		float coveredArea = sumNormalizedValues(values);
		float height = coveredArea / dx;
		float leftoverX = x;
		float leftoverY = y + height;
		float leftoverDx = dx;
		float leftoverDy = dy - height;
		return new SquarifyRect(leftoverX, leftoverY, leftoverDx, leftoverDy);
	}
	
	/**
	 * Compute remaining area, depending on whether dx is bigger or smaller than dy
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private SquarifyRect leftover(List<DataPoint> values, float x, float y, float dx, float dy) {
		if (dx >= dy) {
			return leftoverRow(values, x, y, dx, dy);
		}
		return leftoverCol(values, x, y, dx, dy);
	}
	
	/**
	 * Generate rects for each size in sizes, depending on whether dx is bigger or smaller than dy
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private ArrayList<SquarifyRect> layout(List<DataPoint> values, float x, float y, float dx, float dy) {
		if (dx >= dy) {
			return layoutRow(values, x, y, dx, dy);
		}
		return layoutCol(values, x, y, dx, dy);
	}
	
	/**
	 * Determines which ratio is the worst (farthest from 1) to form a square
	 * 
	 * @param values
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 * @return
	 */
	private float worstRatio(List<DataPoint> values, float x, float y, float dx, float dy) {
		ArrayList<SquarifyRect> rects = layout(values, x, y, dx, dy);
		float max = 0;
		for (int i = 0; i < rects.size(); i++) {
			float rectDx = rects.get(i).getDx();
			float rectDy = rects.get(i).getDy();
			float maxValue = Math.max(rectDx / rectDy, rectDy / rectDx);
			max = (maxValue > max) ? maxValue : max;
		}
		return max;
	}
	
	/**
	 * Compute the list of rectangles that fill up the canvas, in proportion to the values.
	 * Optimized to have rectangles with a ratio closest to 1 to have squares.
	 * 
	 * @param values The list of values, normalized, to squarify
	 * @param x		 Current x coordinate of leftover canvas
	 * @param y      Current y coordinate of leftover canvas
	 * @param dx     Current width of leftover canvas
	 * @param dy     Current height of leftover canvas
	 * @return
	 */
	private ArrayList<SquarifyRect> squarify(List<DataPoint> values, float x, float y, float dx, float dy) {
		ArrayList<SquarifyRect> rects = new ArrayList<SquarifyRect>();
		
		if (values.size() == 0) {
	        return rects;
		}
		
		if (values.size() == 1) {
			rects.add(new SquarifyRect(x, y, dx, dy, values.get(0).getId(), values.get(0).getValue()));
			return rects;
		}
		
		int i = 1;
		while ((i < values.size()) && (worstRatio(values.subList(0, i), x, y, dx, dy) >= worstRatio(values.subList(0, i+1), x, y, dx, dy))) {
			i += 1;
		}
		
		List<DataPoint> current = values.subList(0, i);
		List<DataPoint> remaining = values.subList(i, values.size());
		
		SquarifyRect currentLeftover = leftover(current, x, y, dx, dy);
		rects.addAll(layout(current, x, y, dx, dy));
		rects.addAll(squarify(remaining, currentLeftover.getX(), currentLeftover.getY(), currentLeftover.getDx(), currentLeftover.getDy()));
		return rects;
	}
}

