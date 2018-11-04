

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MyGrid2D<T> implements IGrid2D<T> {
	private final List<T> data;
	private final IArea area;

	public MyGrid2D(IArea area) {
		this(area, null);
	}

	public MyGrid2D(IArea area, T initial) {
		this.area = area;
		this.data = new ArrayList<T>(area.getSize());
		for (int i = 0; i < area.getSize(); i++) {
			data.add(initial);
		}
	}

	public MyGrid2D(int width, int height) {
		this(new Rectangle(width, height));
	}

	public MyGrid2D(int width, int height, T initial) {
		this(new Rectangle(width, height), initial);
	}

	@Override
	public void clear(T val) {
		for (int i = 0; i < area.getSize(); i++) {
			data.set(i, val);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		return area.contains(x, y);
	}

	@Override
	public boolean contains(IPosition pos) {
		return area.contains(pos);
	}

	@Override
	public IGrid2D<T> copy() {
		MyGrid2D<T> copy = new MyGrid2D<>(area, null);
		for (int i = 0; i < data.size(); i++) {
			copy.data.set(i, data.get(i));
		}
		return copy;
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
		MyGrid2D<?> other = (MyGrid2D<?>) obj;
		if (area == null) {
			if (other.area != null) {
				return false;
			}
		} else if (!area.equals(other.area)) {
			return false;
		}
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

	@Override
	public T get(int x, int y) {
		return data.get(area.getIndex(x, y));
	}

	@Override
	public T get(int x, int y, T defaultValue) {
		if (contains(x, y)) {
			return data.get(area.getIndex(x, y));
		} else {
			return defaultValue;
		}
	}

	@Override
	public T get(IPosition pos) {
		return data.get(area.getIndex(pos));
	}

	@Override
	public T get(IPosition pos, T defaultValue) {
		if (contains(pos)) {
			return data.get(area.getIndex(pos));
		} else {
			return defaultValue;
		}
	}

	@Override
	public int getHeight() {
		return area.getHeight();
	}

	@Override
	public int getIndex(int x, int y) {
		return area.getIndex(x, y);
	}

	@Override
	public int getIndex(IPosition pos) {
		return area.getIndex(pos);
	}

	@Override
	public int getSize() {
		return area.getSize();
	}

	@Override
	public int getWidth() {
		return area.getWidth();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean isOnEdge(IPosition pos) {
		return area.isOnEdge(pos);
	}

	@Override
	public Iterator<IPosition> iterator() {
		return area.iterator();
	}

	@Override
	public Collection<IPosition> neighboursOf(IPosition pos) {
		return area.neighboursOf(pos);
	}

	@Override
	public T set(int x, int y, T val) {
		return data.set(area.getIndex(x, y), val);
	}

	@Override
	public T set(IPosition pos, T val) {
		return data.set(area.getIndex(pos), val);
	}

	@Override
	public String toString() {
		return "MyGrid2D [area=" + area + ", data=" + data + "]";
	}

	@Override
	public boolean wrapsHorizontally() {
		return area.wrapsHorizontally();
	}

	@Override
	public boolean wrapsVertically() {
		return area.wrapsVertically();
	}

}
