import java.util.Arrays;

public class Main101Games /* extends JApplet */{
	// private static final long serialVersionUID = -5830018712727696869L;

	public static void main(String[] args) {
		new GUIFrame(startGame());
	}

	public static GameGUI startGame() {
		IGame sweeper = new Minesweeper(15, 17);
		return new GameGUI(Arrays.asList(sweeper));
	}

}
