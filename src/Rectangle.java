

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Rectangle implements IArea {
	private final int width;

	private final int height;

	public static IArea make(int w, int h) {
		return new Rectangle(w, h);
	}

	public Rectangle(int width, int height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException(
					"Width and height must be positive");
		}
		if ((width == 0 || height == 0) && width != height) {
			throw new IllegalArgumentException(
					"For a 0x0 area, both width and height must be 0");
		}

		this.width = width;
		this.height = height;
	}

	/**
	 * @param x
	 *            X-coordinate
	 * @return The same x, wrapped to wrapX(x)
	 * @throws IndexOutOfBoundsException
	 *             if coordinate is out of range, and wrapping is not enabled
	 */
	protected int checkX(int x) {
		x = wrapX(x);
		if (x < 0 || x >= width) {
			throw new IndexOutOfBoundsException("" + x);
		}

		return x;
	}

	/**
	 * @param y
	 *            Y-coordinate
	 * @return The same y, wrapped to wrapY(y)
	 * @throws IndexOutOfBoundsException
	 *             if coordinate is out of range, and wrapping is not enabled
	 */
	protected int checkY(int y) {
		y = wrapY(y);
		if (y < 0 || y >= height) {
			throw new IndexOutOfBoundsException("" + y);
		}
		return y;
	}

	@Override
	public boolean contains(int x, int y) {
		x = wrapX(x);
		y = wrapY(y);
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	@Override
	public boolean contains(IPosition pos) {
		int x = wrapX(pos.getX());
		int y = wrapY(pos.getY());
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Rectangle other = (Rectangle) obj;
		if (height != other.height) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getIndex(int x, int y) {
		x = checkX(x);
		y = checkY(y);
		return y * width + x;
	}

	@Override
	public int getIndex(IPosition pos) {
		int x = checkX(pos.getX());
		int y = checkY(pos.getY());
		return y * width + x;
	}

	@Override
	public int getSize() {
		return width * height;
	}

	@Override
	public int getWidth() {
		return /* FIXME */width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean isOnEdge(IPosition pos) {
		int x = wrapX(pos.getX());
		int y = wrapY(pos.getY());

		if (!wrapsHorizontally() && (x == 0 || x == width - 1)) {
			return true;
		}
		if (!wrapsVertically() && (y == 0 || y == height - 1)) {
			return true;
		}
		return false;
	}

	@Override
	public Iterator<IPosition> iterator() {
		return new AreaIterator(width, height);
	}

	@Override
	public Collection<IPosition> neighboursOf(IPosition pos) {
		Collection<IPosition> coll = new ArrayList<>();

		for (IPosition p : Arrays.asList(pos.northWest(), pos.north(),
				pos.northEast(), /* FIXME */pos.east(), pos.southEast(),
				pos.south(), pos.southWest(), pos.west())) {
			// FIXME
			if (contains(p)) {
				coll.add(p);
			}
		}
		return coll;
	}

	@Override
	public String toString() {
		return "Rectangle [width=" + width + ", height=" + height + "]";
	}

	@Override
	public boolean wrapsHorizontally() {
		return false;
	}

	@Override
	public boolean wrapsVertically() {
		return false;
	}

	protected int wrapX(int x) {
		return x;
	}

	protected int wrapY(int y) {
		return y;
	}

	private static class AreaIterator implements Iterator<IPosition> {
		private int x;
		private int y;
		private final int x1;
		private final int y1;

		public AreaIterator(int x1, int y1) {
			if (0 > x1 || 0 > y1) {
				throw new IllegalArgumentException();
			}

			this.x = 0;
			this.y = 0;
			this.x1 = x1;
			this.y1 = y1;
		}

		@Override
		public boolean hasNext() {
			return x < x1 && y < y1;
		}

		@Override
		public IPosition next() {
			if(!hasNext())
				throw new NoSuchElementException();
			
			IPosition pos = new Position(x, y);
			x = x + 1;
			if (x >= x1) {
				x = 0;
				y = y + 1;
			}
			return pos;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
