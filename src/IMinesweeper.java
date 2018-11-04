public interface IMinesweeper extends IGame, IClickListener, ITimeStepListener {

	void clear();

	void explore(IPosition pos);

	IArea getArea();

	SweepState getState(IPosition pos);

	GameStatus getStatus();

	boolean hasMine(IPosition pos);

	int neighbours(IPosition pos);

	void setMine(IPosition pos, boolean mine);
}