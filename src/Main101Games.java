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

	/*
	 * @Override public void init() { final JApplet applet = this; try {
	 * SwingUtilities.invokeAndWait(new Runnable() {
	 * 
	 * @Override public void run() { GameGUI game = startGame();
	 * applet.add(game); game.initialize(); } }); } catch (InterruptedException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (InvocationTargetException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

}
