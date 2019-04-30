package squarify.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to generate a squarified treemap layout from a set of values.
 * 
 * @author agatheblues
 * @example Squarify 
 */

public class Squarify {
	
	private ArrayList<SquarifiedRect> rects;
	private ArrayList<Float> sizes;
	private float width, height;
	
	public Squarify(ArrayList<Float> sizes, float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		this.sizes = this.sortDescending(sizes);
		
		List<Float> values = this.normalizeSizes(this.sizes, width, height);
		rects = squarify(values, x, y, width, height);
	}
	
	/**
	 * Getter for the list of rectangle's geometry
	 * @return rects
	 */
	public ArrayList<SquarifiedRect> getRects() {
		return this.rects;
	}
	
	/**
	 * Helper that sorts a list in descending order
	 * @param s A list of values
	 * @return The list of values ordered descending
	 */
	private ArrayList<Float> sortDescending(ArrayList<Float> s) {
		Collections.sort(s);
		Collections.reverse(s);
		return s;
	}
	
	/**
	 * Helper that normalizes the sizes to the proportion of the canvas. 
	 * 
	 * @param sizes  A list of values to squarify
	 * @param width  The canvas width
	 * @param height The canvas height
	 * @return
	 */
	private ArrayList<Float> normalizeSizes(ArrayList<Float> sizes, float width, float height) {
		float totalSize = sum(sizes);
		float totalArea = width * height;
		ArrayList<Float> normalized = new ArrayList<Float>(sizes.size());
		
		for (int i = 0; i < sizes.size(); i++) {
			normalized.add(sizes.get(i) * totalArea / totalSize);
		}
		return normalized;
	}
	
	/**
	 * Helper that denormalized a normalized size to its original value
	 * This helper is only needed if you want to print the size in the rectangles on your canvas.
	 * 
	 * @param sizes 			The list of sizes to squarify 
	 * @param normalizedValue   The normalized size to reverse
	 * @param width  The canvas width
	 * @param height The canvas height
	 * @return
	 */
	private Float denormalizeSize(ArrayList<Float> sizes, float normalizedValue, float width, float height) {
		float totalSize = sum(sizes);
		float totalArea = width * height;
		return (float) normalizedValue * totalSize / totalArea;
	}
	
	/** 
	 * Helper that sums all the values of float list
	 * @param values A list of float values
	 * @return The sum of these values
	 */
	private float sum(List<Float> values) {
		float result = 0;
		for (int i = 0; i < values.size(); i++) {
			result += values.get(i);
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
	private ArrayList<SquarifiedRect> layoutRow(List<Float> values, float x, float y, float dx, float dy) {
		float coveredArea = sum(values);
		float width = coveredArea / dy;
		ArrayList<SquarifiedRect> rects = new ArrayList<SquarifiedRect>();
		for (int i = 0; i < values.size(); i++) {
			rects.add(new SquarifiedRect(x, y, width, values.get(i) / width,  denormalizeSize(this.sizes, values.get(i), this.width, this.height)));
			y += values.get(i) / width;
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
	private ArrayList<SquarifiedRect> layoutCol(List<Float> values, float x, float y, float dx, float dy) {
		float coveredArea = sum(values);
		float height = coveredArea / dx;
		ArrayList<SquarifiedRect> rects = new ArrayList<SquarifiedRect>();
		for (int i = 0; i < values.size(); i++) {
			rects.add(new SquarifiedRect(x, y, values.get(i) / height, height, denormalizeSize(this.sizes, values.get(i), this.width, this.height)));
			x += values.get(i) / height;
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
	private SquarifiedRect leftoverRow(List<Float> values, float x, float y, float dx, float dy) {
		float coveredArea = sum(values);
		float width = coveredArea / dy;
		float leftoverX = x + width;
		float leftoverY = y;
		float leftoverDx = dx - width;
		float leftoverDy = dy;
		return new SquarifiedRect(leftoverX, leftoverY, leftoverDx, leftoverDy);
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
	private SquarifiedRect leftoverCol(List<Float> values, float x, float y, float dx, float dy) {
		float coveredArea = sum(values);
		float height = coveredArea / dx;
		float leftoverX = x;
		float leftoverY = y + height;
		float leftoverDx = dx;
		float leftoverDy = dy - height;
		return new SquarifiedRect(leftoverX, leftoverY, leftoverDx, leftoverDy);
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
	private SquarifiedRect leftover(List<Float> values, float x, float y, float dx, float dy) {
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
	private ArrayList<SquarifiedRect> layout(List<Float> values, float x, float y, float dx, float dy) {
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
	private float worstRatio(List<Float> values, float x, float y, float dx, float dy) {
		ArrayList<SquarifiedRect> rects = layout(values, x, y, dx, dy);
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
	private ArrayList<SquarifiedRect> squarify(List<Float> values, float x, float y, float dx, float dy) {
		ArrayList<SquarifiedRect> rects = new ArrayList<SquarifiedRect>();
		
		if (values.size() == 0) {
	        return rects;
		}
		
		if (values.size() == 1) {
			rects.add(new SquarifiedRect(x, y, dx, dy, denormalizeSize(this.sizes, values.get(0), this.width, this.height)));
			return rects;
		}
		
		int i = 1;
		while ((i < values.size()) && (worstRatio(values.subList(0, i), x, y, dx, dy) >= worstRatio(values.subList(0, i+1), x, y, dx, dy))) {
			i += 1;
		}
		
		List<Float> current = values.subList(0, i);
		List<Float> remaining = values.subList(i, values.size());
		
		SquarifiedRect currentLeftover = leftover(current, x, y, dx, dy);
		rects.addAll(layout(current, x, y, dx, dy));
		rects.addAll(squarify(remaining, currentLeftover.getX(), currentLeftover.getY(), currentLeftover.getDx(), currentLeftover.getDy()));
		return rects;
	}
}

