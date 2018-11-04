import java.util.List;

public interface IGame {

	boolean canChangeSize();

	List<String> getBoardSizes();

	int getCellHeight();

	int getCellWidth();

	int getHeight();

	IImage getImageAt(IPosition pos);

	List<String> getMenuChoices();

	String getName();

	int getWidth();

	void newGame();

	void setMenuChoice(String s);

	void setSize(int width, int height);

	void setup(IUserInterface ui);
}
