

import java.awt.Color;

/**
 * Represents the state of a grid cell, from the user's perspective; e.g.
 * whether it is explored or not, how many mine neighbours it has if it is
 * explored — and for use in the end game display, whether it has a
 * (armed/disarmed/exploded) mine.
 *
 * @author anya
 *
 */
public enum SweepState {

	/**
	 * Cell is unexplored
	 */
	UNEXPLORED(" "),
	/**
	 * Cell has 0 neighbours with mines
	 */
	ZERO(" ", null, SweepColors.EXPLORED_BG),
	/**
	 * Cell has 1 neighbour with mines
	 */
	ONE(1),
	/**
	 * Cell has 2 neighbours with mines
	 */
	TWO(2),
	/**
	 * Cell has 3 neighbours with mines
	 */
	THREE(3),
	/**
	 * Cell has 4 neighbours with mines
	 */
	FOUR(4),
	/**
	 * Cell has 5 neighbours with mines
	 */
	FIVE(5),
	/**
	 * Cell has 6 neighbours with mines
	 */
	SIX(6),
	/**
	 * Cell has 7 neighbours with mines
	 */
	SEVEN(7),
	/**
	 * Cell has 8 neighbours with mines
	 */
	EIGHT(8),
	/**
	 * User has placed a flag on this cell
	 */
	FLAG("!"),
	/**
	 * User has placed a question mark on this cell
	 */
	MAYBE("?"),
	/**
	 * Mine has exploded (end game display)
	 */
	EXPLOSION("X", null, Color.RED),
	/**
	 * Cell has an armed mine (end game display)
	 */
	MINE("M", null, Color.RED),
	/**
	 * Mine is safely disarmed (end game display)
	 */
	DISARMED("M", null, Color.GREEN);

	/**
	 * Stores number of neighbours for cases ZERO..EIGHT
	 */
	private int value;

	/**
	 * The image to show for this state
	 */
	private IImage image;

	/**
	 * @param x
	 *            number of neighbours (0..8)
	 * @return The SweepState associated with having that number of neighbours
	 */
	public static SweepState valueOf(int x) {
		switch (x) {
		case 0:
			return ZERO;
		case 1:
			return ONE;
		case 2:
			return TWO;
		case 3:
			return THREE;
		case 4:
			return FOUR;
		case 5:
			return FIVE;
		case 6:
			return SIX;
		case 7:
			return SEVEN;
		case 8:
			return EIGHT;
		default:
			throw new IllegalArgumentException("Expected a value >= 0 and <= 8");
		}
	}

	SweepState(int x) {
		this.value = x;
		this.image = new TextImage("" + x, null, SweepColors.EXPLORED_BG);
	}

	SweepState(String s) {
		this(s, null, SweepColors.UNEXPLORED_BG);
	}

	SweepState(String s, Color fg) {
		this(s, fg, SweepColors.UNEXPLORED_BG);
	}

	SweepState(String s, Color fg, Color bg) {
		this.value = -1;
		this.image = new TextImage(s, fg, bg);
	}

	/**
	 * @return the image that should be shown for this state
	 */
	public IImage getImage() {
		return image;
	}

	/**
	 * @return The number of neighbours this state corresponds to (or -1 if this
	 *         is some other kind of state)
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return True if this state indicates that a cell has been explored or
	 *         flagged
	 */
	public boolean isExplored() {
		return this != UNEXPLORED && this != FLAG && this != MAYBE;
	}

	/**
	 * @return True is this == {@link #FLAG}
	 */
	public boolean isFlagged() {
		return this == FLAG;
	}

	/**
	 * @return True if this == {@link #MAYBE}
	 */
	public boolean isMaybe() {
		return this == MAYBE;
	}

	private static class SweepColors {
		private static final Color EXPLORED_BG = Color.BLACK;
		private static final Color UNEXPLORED_BG = Color.DARK_GRAY;

	}
}
