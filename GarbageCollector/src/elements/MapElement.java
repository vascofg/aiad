package elements;

//generic map element class
public abstract class MapElement implements DrawableElement {
	protected int x;
	protected int y;

	public MapElement(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MapElement(MapElement other) {
		this.x = other.x;
		this.y = other.y;
	}

	public abstract MapElement copy();

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
