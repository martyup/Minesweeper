public interface IUserInterface {

	void addClickListener(IClickListener l);

	void addDragListener(IDragListener l);

	void addTimeStepListener(ITimeStepListener l);

	void endGame();

	void newGame();

	void setPlayPauseButton(boolean enabled);

	void setSpeedButton(boolean enabled);

	void setSpeeds(int normalDelay, int fastDelay, int fasterDelay);

	void setStatus(String status);

	void setStepButton(boolean enabled);

	void update(IPosition pos);

	void updateAll();

	void addButtonRow();

	void addButton(String name, IImage image, IButtonListener listener);

	void enableButton(String name, boolean enabled);

	void selectButton(String name, boolean selected);
}
