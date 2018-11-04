import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Minesweeper implements IGame, IClickListener, ITimeStepListener,
IMinesweeper {
	private IGrid2D<Boolean> mineBoard;
	private IGrid2D<SweepState> userBoard;
	private int width;
	private int height;
	private int numCells;
	private int numMines;
	private int numExplored;
	private int numFlags;
	private long startTime;

	private final Random random = new Random();
	/**
	 * How often mines are placed on the board – a mine is placed if
	 * random.nextInt(difficulty) is 0.
	 */
	private int difficulty = 10;
	private IUserInterface ui;
	private GameStatus status;

	public Minesweeper(int x, int y) {
		width = x;
		height = y;
	}

	@Override
	public boolean canChangeSize() {
		return false;
	}

	@Override
	public void clear() {
		mineBoard.clear(false);
		userBoard.clear(SweepState.UNEXPLORED);
	}

	@Override
	public void clicked(IPosition pos) {
		System.out.println("Leftclick!");

		if (getState(pos).isFlagged()) {
			rightClicked(pos);
		} else {
			explore(pos);
		}
	}

	public void debug() {
		for (IPosition pos : mineBoard) {
			if (mineBoard.get(pos)) {
				userBoard.set(pos, SweepState.DISARMED);
			} else {
				int k = neighbours(pos);
				userBoard.set(pos, SweepState.valueOf(k));
			}
		}
		ui.updateAll();
	}

	/**
	 * Find the number of mines neighbouring a position on the board.
	 *
	 * @param pos
	 *            Position on the board
	 * @return number of neighbours with mines
	 */
	@Override
	public void explore(IPosition pos) {
		if (hasMine(pos)) {
			userBoard.set(pos, SweepState.EXPLOSION);
			ui.update(pos);
			lose();
			return;
		} else {
			List<IPosition> toExplore = new ArrayList<IPosition>();
			toExplore.add(pos);

			while (!toExplore.isEmpty()) {
				pos = toExplore.remove(0);

				if (!getState(pos).isExplored()
						&& !getState(pos).isFlagged()) {
					numExplored++;
					int n = neighbours(pos);
					userBoard.set(pos, SweepState.valueOf(n));
					ui.update(pos);

					if (n == 0) {
						toExplore.addAll(userBoard.neighboursOf(pos));
					}
				}
			}
		}

		if (numExplored + numMines >= numCells) {
			win();
		}

		return;
	}

	@Override
	public IArea getArea() {
		return mineBoard; // all 2D grids are also areas
	}

	@Override
	public List<String> getBoardSizes() {
		return null;
	}

	@Override
	public int getCellHeight() {
		return 32;
	}

	@Override
	public int getCellWidth() {
		return 32;
	}

	private String getElapsedTime() {
		long t = System.currentTimeMillis() - startTime;

		long secs = t / 1000;
		long mins = secs / 60;
		secs -= mins * 60;
		long hrs = mins / 60;
		mins -= hrs * 60;

		String time;
		if (hrs > 0) {
			time = String.format("%d:%02d:%02d", hrs, mins, secs);
		} else {
			time = String.format("%d:%02d", mins, secs);
		}

		return time;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public IImage getImageAt(IPosition pos) {
//		if(getState(pos) == SweepState.ZERO)
//			return SweepState.ZERO.getImage();
		
		return getState(pos).getImage();
	}

	@Override
	public List<String> getMenuChoices() {
		return Arrays.asList("Easy", "Medium", "Hard");
	}

	@Override
	public String getName() {
		return "Minesweeper";
	}

	@Override
	public SweepState getState(IPosition pos) {
		return userBoard.get(pos);
	}

	@Override
	public GameStatus getStatus() {
		return status;
	}

	@Override
	public int getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see inf101.games.minesweeper.IMinesweeper#hasMine(inf101.grid.IPosition)
	 */
	@Override
	public boolean hasMine(IPosition pos) {
		if(mineBoard.get(pos))
			return true;
		else
			return false;
	}

	private void lose() {
		status = GameStatus.LOST;
		for(IPosition pos : userBoard) {
			if(hasMine(pos) && getState(pos) != SweepState.EXPLOSION)
				userBoard.set(pos, SweepState.MINE);
		}
		ui.setStatus("WHOOPS! YOU'RE DEAD!");
		ui.endGame();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * inf101.games.minesweeper.IMinesweeper#neighbours(inf101.grid.IPosition)
	 */
	@Override
	public int neighbours(IPosition pos) {
		int n = 0;
		for (IPosition p : mineBoard.neighboursOf(pos)) {
			if (hasMine(p)) {
				n++;
			}
		}
		return n;
	}

	@Override
	public void newGame() {
		status = GameStatus.PLAYING;
		numCells = width * height;
		numExplored = 0;
		numFlags = 0;
		numMines = 0;

		mineBoard = new MyGrid2D<Boolean>(new Rectangle(width, height));
		for (IPosition pos : mineBoard) {
			boolean mine = random.nextInt(difficulty) == 0;
			mineBoard.set(pos, mine);
			if (mine) {
				numMines++;
			}
		}

		userBoard = new MyGrid2D<SweepState>(new Rectangle(width, height));
		userBoard.clear(SweepState.UNEXPLORED);
//		for (IPosition pos : mineBoard) {
//			userBoard.set(pos, SweepState.UNEXPLORED);
//		}

		startTime = System.currentTimeMillis();
	}

	@Override
	public void rightClicked(IPosition pos) {
		System.out.println("Rightclick!");

		SweepState oldState = getState(pos);

		if (!oldState.isExplored()) {
			if (oldState.isFlagged()) {
				userBoard.set(pos, SweepState.UNEXPLORED);
				numFlags--;
			} else if (numFlags < numMines) {
				userBoard.set(pos, SweepState.FLAG);
				numFlags++;
			}
			ui.update(pos);
		}
	}

	@Override
	public void setMenuChoice(String s) {
		switch (s) {
		case "Easy":
			difficulty = 10;
		case "Medium":
			difficulty = 5;
		case "Hard":
			difficulty = 3;
		}
		ui.newGame();
	}

	@Override
	public void setMine(IPosition pos, boolean mine) {
		mineBoard.set(pos, mine);
	}

	@Override
	public void setSize(int nyBredde, int nyHøyde) {
		if (width == nyBredde && height == nyHøyde) {
			return;
		}

		width = nyBredde;
		height = nyHøyde;
	}

	@Override
	public void setup(IUserInterface ui) {
		ui.addClickListener(this);
		ui.addTimeStepListener(this);

		ui.setPlayPauseButton(false);
		ui.setSpeedButton(false);
		ui.setStepButton(false);

		// we use the time step facility to get notified every second, to update
		// our timer
		ui.setSpeeds(1000, 1000, 1000);
		this.ui = ui;
	}

	@Override
	public void timeStep(int count) {
		ui.setStatus("" + numFlags + "/" + numMines + " mines flagged, "
				+ (numExplored + numFlags) + "/" + numCells + " explored, "
				+ getElapsedTime() + " elapsed");
	}

	private void win() {
		status = GameStatus.WON;
		for(IPosition pos : userBoard) {
			if(hasMine(pos))
				userBoard.set(pos, SweepState.DISARMED);
		}
		ui.setStatus("Congratulations! YOU WIN! Time: " + getElapsedTime());
		ui.endGame();
	}
}
