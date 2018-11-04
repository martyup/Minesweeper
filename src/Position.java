

/**
 * En hendig liten klasse for å lagre koordinater.
 *
 * @author Anya Helene Bagge
 *
 */
public class Position implements IPosition {
	public final int x;

	public final int y;

	public static IPosition make(int x, int y) {
		return new Position(x, y);
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public IPosition east() {
		return make(x + 1, y);
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
		Position other = (Position) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public IPosition north() {
		return make(x, y + 1);
	}

	@Override
	public IPosition northEast() {
		return make(x + 1, y + 1);
	}

	@Override
	public IPosition northWest() {
		return make(x - 1, y + 1);
	}

	@Override
	public IPosition south() {
		return make(x, y - 1);
	}

	@Override
	public IPosition southEast() {
		return make(x + 1, y - 1);
	}

	@Override
	public IPosition southWest() {
		return make(x - 1, y - 1 /* FIXME */);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public IPosition west() {
		return make(x - 1, y);
	}
}