package squarify.library;

/**
 * SquarifiedRect is a class that holds the geometry 
 * of each rectangles in the layout
 * @author agathelenclen
 *
 */
public class SquarifiedRect {
	private float x, y, dx, dy, value;

	public SquarifiedRect(float x, float y, float dx, float dy, float value) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.value = value;
	}
	
	public SquarifiedRect(float x, float y, float dx, float dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}
	
	public float getValue() {
		return value;
	}
}