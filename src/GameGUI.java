import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.soap.SOAPBinding.Style;
import javax.swing.AbstractButton;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GameGUI extends JPanel implements IUserInterface, ActionListener,
		MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -2030937455049555857L;

	/**
	 * Her ligger cellene
	 */
	private final ImagePanel mainPanel;

	/**
	 * Viser tid nederst
	 */
	private final JPanel statusPanel;

	/**
	 * A label to display status information
	 */
	private final JLabel statusLabel;
	/**
	 * 'Nytt games' knapp
	 */
	private final JPanel controlPanel;
	/**
	 * Knapper for å starte btnNew games
	 */
	private final JButton btnNew, btnPlayPause, btnStep, btnSpeed;
	/**
	 * Used to find the position corresponding to a button
	 */
	private final Map<JButton, IPosition> buttonMap = new HashMap<JButton, IPosition>();

	/**
	 * Used to find the button corresponding to a position
	 */
	private final Map<IPosition, JButton> positionMap = new HashMap<IPosition, JButton>();

	/**
	 * Referanse til spillet
	 */
	private final List<IGame> games;

	private IGame selectedGame;
	/**
	 * Vekker oss hvert halve sekund
	 */
	private javax.swing.Timer timer;
	private JComboBox<String> sizes;
	private final JComboBox<String> gameSelection;
	private JComboBox<String> gameMenu;
	private boolean paused = true;
	private boolean started = true;
	private static final String[] boardSizes = new String[] { "10x10", "12x12",
			"15x15", "20x15", "30x20" };
	private int currentSpeed = 0;
	public static final int DELAY_NORMAL = 400, DELAY_FAST = 150,
			DELAY_FASTER = 50;
	private final int[] speeds = new int[] { DELAY_NORMAL, DELAY_FAST,
			DELAY_FASTER };
	private final List<IClickListener> clickListeners = new ArrayList<>();
	private final List<ITimeStepListener> timeStepListeners = new ArrayList<>();
	private final List<IDragListener> dragListeners = new ArrayList<>();

	private int stepCount;

	private boolean hasPlayPauseButton, hasStepButton, hasSpeedButton;
	private IPosition mousePosition;
	private IPosition draggedFrom;

	private static IArea boardSize(String size) {
		String[] split = size.split("x");
		if (split.length != 2) {
			throw new IllegalArgumentException("Should be on WxH: " + size);
		}
		return new Rectangle(Integer.parseInt(split[0]),
				Integer.parseInt(split[1]));
	}

	public GameGUI(IGame spill) {
		this(Arrays.asList(spill));
	}

	/**
	 * Oppretter en ny games-GameGUI
	 *
	 * @param games
	 *            Spillet som skal kontrolleres
	 */
	public GameGUI(List<IGame> spill) {
		super();
		setLayout(new BorderLayout());

		this.games = new ArrayList<IGame>(spill);
		Collections.sort(games, new GameComparator());
		this.selectedGame = spill.get(0);

		String[] gameNames = new String[games.size()];
		int i = 0;
		for (IGame g : games) {
			gameNames[i++] = g.getName();
		}
		gameSelection = new JComboBox<String>(gameNames);
		gameSelection.setSelectedItem(selectedGame.getName());
		gameSelection.addActionListener(this);
		gameSelection.setToolTipText("Select game");

		JPanel dummyControlPanel = new JPanel();
		dummyControlPanel.setLayout(new BorderLayout());
		dummyControlPanel.setForeground(Color.black);
		dummyControlPanel.setBackground(Color.WHITE);
		controlPanel = new JPanel();
		controlPanel.setForeground(Color.black);
		controlPanel.setBackground(Color.WHITE);
		dummyControlPanel.add(controlPanel, BorderLayout.WEST);

		btnPlayPause = new JButton();
		btnPlayPause.setToolTipText("Play");
		btnPlayPause.addActionListener(this);
		btnSpeed = new JButton();
		btnSpeed.addActionListener(this);
		btnStep = new JButton(ImageLoader.getImage("gui/images/step"));
		btnStep.setToolTipText("Step");
		btnStep.addActionListener(this);
		btnNew = new JButton(ImageLoader.getImage("gui/images/new"));
		btnNew.setToolTipText("Start new game");
		btnNew.addActionListener(this);

		updateSpeed();

		mainPanel = new ImagePanel();
		mainPanel.setForeground(Color.black);
		mainPanel.setBackground(Color.WHITE);
		statusPanel = new JPanel();
		statusPanel.setForeground(Color.black);
		statusPanel.setBackground(Color.WHITE);

		statusLabel = new JLabel();
		statusPanel.add(statusLabel);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// ekstra panel for å få riktig bakgrunn på resten
		JPanel dummyPanel = new JPanel();
		dummyPanel.setForeground(Color.black);
		dummyPanel.setBackground(Color.WHITE);

		add(dummyControlPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.WEST);
		add(statusPanel, BorderLayout.SOUTH);
		add(dummyPanel, BorderLayout.CENTER);
	}

	/**
	 * Denne blir kalt av Java hver gang brukeren trykker på en knapp, eller
	 * hver gang timer-signalet avfyres.
	 *
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNew) {
			newGame();
			updateAll();
		} else if (e.getSource() == btnPlayPause) {
			if (paused) {
				setPlaying();
				timeStep();
				updateAll();
				timer.start();
			} else {
				setPaused();
				timer.stop();
			}
		} else if (e.getSource() == btnStep) {
			setPaused();
			timer.stop();
			timeStep();
			updateAll();
		} else if (e.getSource() == btnSpeed) {
			currentSpeed = (currentSpeed + 1) % speeds.length;

			updateSpeed();

		} else if (e.getSource() == timer) {
			timeStep();
			updateAll();
			timer.restart();
		} else if (e.getSource() == sizes) {
			String size = (String) sizes.getSelectedItem();
			IArea p = boardSize(size);
			selectedGame.setSize(p.getWidth(), p.getHeight());
			if (!selectedGame.canChangeSize()) {
				newGame();
			}
			initializeBoard();
			updateAll();
		} else if (e.getSource() == gameMenu) {
			selectedGame.setMenuChoice((String) gameMenu.getSelectedItem());
			initializeBoard();
			updateAll();
		} else if (e.getSource() == gameSelection) {
			String selection = (String) gameSelection.getSelectedItem();
			if (!selection.equals(selectedGame.getName())) {
				for (IGame game : games) {
					if (selection.equals(game.getName())) {
						selectedGame = game;
						initialize();
					}
				}
			}
		}
	}

	/**
	 * Add a listener which will be called whenever the user clicks a mouse
	 * button.
	 *
	 * @param listener
	 *            The listener
	 */
	@Override
	public void addClickListener(IClickListener listener) {
		clickListeners.add(listener);
	}

	@Override
	public void addDragListener(IDragListener l) {
		dragListeners.add(l);
	}

	/**
	 * Add a listener which will be called for each time step.
	 *
	 * Time steps happen at regular intervals (typically in the ranger of
	 * 50–500 ms, depending on what the game's speed is set to.
	 *
	 * @param listener
	 * @see IGame#hasSpeedButton()
	 */
	@Override
	public void addTimeStepListener(ITimeStepListener listener) {
		timeStepListeners.add(listener);
	}

	@Override
	public void endGame() {
		started = false;
	}

	public void initialize() {
		setupGame();
		initializeControl();
		initializeBoard();
		setVisible(true);
	}

	private void initializeBoard() {
		int width = selectedGame.getWidth();
		int height = selectedGame.getHeight();

		mainPanel.removeAll();
		mainPanel.setLayout(new GridLayout(height + 1, width + 1));
		buttonMap.clear();
		positionMap.clear();

		for (int y = 0; y < height; y++) {
			mainPanel.add(new CoordLabel(height - y - 1));
			for (int x = 0; x < width; x++) {
				JPanel panel = new JPanel(new BorderLayout());
				JButton button = new JButton();
				button.addMouseListener(this);
				button.addMouseMotionListener(this);
				button.setMargin(new Insets(0, 0, 0, 0));
				button.setContentAreaFilled(false);
				button.setBounds(0, 0, selectedGame.getCellWidth(),
						selectedGame.getCellHeight());
				button.setPreferredSize(new Dimension(selectedGame
						.getCellWidth(), selectedGame.getCellHeight()));
				button.setForeground(Color.WHITE);
				button.setBackground(Color.BLACK);
				button.setFocusPainted(false);
				panel.add(button);
				panel.setOpaque(false);
				mainPanel.add(panel);
				Position pos = new Position(x, height - y - 1);
				buttonMap.put(button, pos);
				positionMap.put(pos, button);
			}
		}

		mainPanel.add(new CoordLabel(""));
		for (int x = 0; x < width; x++) {
			mainPanel.add(new CoordLabel(x));
		}
		updateAll();
		updateFrame();
	}

	private void initializeControl() {
		controlPanel.removeAll();

		controlPanel.add(gameSelection);

		List<String> bSizes = selectedGame.getBoardSizes();
		if (bSizes == null) {
			bSizes = Arrays.asList(boardSizes);
		}
		// sørg for at vi har vår egen lokale kopi som vi kan endre på
		bSizes = new ArrayList<String>(bSizes);
		String size = selectedGame.getWidth() + "x" + selectedGame.getHeight();
		if (!bSizes.contains(size)) {
			bSizes.add(size);
		}
		Collections.sort(bSizes);
		if (sizes != null) {
			sizes.removeActionListener(this);
		}
		sizes = new JComboBox<String>(bSizes.toArray(new String[bSizes.size()]));
		sizes.setSelectedItem(size);
		sizes.addActionListener(this);
		sizes.setToolTipText("Select board size");

		controlPanel.add(sizes);

		controlPanel.add(btnNew);

		if (hasPlayPauseButton) {
			controlPanel.add(btnPlayPause);
		}

		if (hasStepButton) {
			controlPanel.add(btnStep);
		}

		if (hasSpeedButton) {
			controlPanel.add(btnSpeed);
		}

		if (gameMenu != null) {
			gameMenu.removeActionListener(this);
			gameMenu = null;
		}
		List<String> choices = selectedGame.getMenuChoices();
		if (choices != null) {
			gameMenu = new JComboBox<String>(choices.toArray(new String[choices
					.size()]));
			gameMenu.addActionListener(this);
			gameMenu.setToolTipText("Game options");
			controlPanel.add(gameMenu);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!started) {
			return;
		}

		if (e.getSource() instanceof JButton) {
			IPosition pos = buttonMap.get(e.getSource());

			if (pos != null) {
				for (IClickListener l : clickListeners) {
					for (int i = 0; i < e.getClickCount(); i++) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							l.clicked(pos);
						} else if (SwingUtilities.isRightMouseButton(e)) {
							l.rightClicked(pos);
						}
					}
				}
				updateAll();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!started) {
			return;
		}

		if (!dragListeners.isEmpty()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

			draggedFrom = buttonMap.get(e.getSource());
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (!started) {
			return;
		}

		mousePosition = buttonMap.get(e.getSource());
		if (draggedFrom != null) {
			selectedGame.getImageAt(draggedFrom).draw(
					(AbstractButton) e.getSource(), stepCount);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!started) {
			return;
		}

		if (draggedFrom != null) {
			update(mousePosition);
		}

		mousePosition = null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!started) {
			return;
		}

		if (draggedFrom != null) {
			for (IDragListener l : dragListeners) {
				l.dragged(draggedFrom, mousePosition);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			update(mousePosition);
			draggedFrom = null;
		}
	}

	@Override
	public void newGame() {
		stepCount = 0;
		started = true;
		selectedGame.newGame();
	}

	private void setPaused() {
		btnPlayPause.setToolTipText("Play");
		btnPlayPause.setIcon(ImageLoader.getImage("gui/images/play"));
		paused = true;
	}

	private void setPlaying() {
		btnPlayPause.setToolTipText("Pause");
		btnPlayPause.setIcon(ImageLoader.getImage("gui/images/pause"));
		paused = false;

	}

	@Override
	public void setPlayPauseButton(boolean enabled) {
		hasPlayPauseButton = enabled;
	}

	@Override
	public void setSpeedButton(boolean enabled) {
		hasSpeedButton = enabled;
	}

	@Override
	public void setSpeeds(int normalDelay, int fastDelay, int fasterDelay) {
		if (normalDelay <= 0 || fastDelay <= 0 || fasterDelay <= 0) {
			throw new IllegalArgumentException();
		}

		speeds[0] = normalDelay;
		speeds[1] = fastDelay;
		speeds[2] = fasterDelay;

		updateSpeed();
	}

	@Override
	public void setStatus(String status) {
		statusLabel.setText(status);
	}

	@Override
	public void setStepButton(boolean enabled) {
		hasStepButton = enabled;
	}

	public void setupGame() {
		clickListeners.clear();
		timeStepListeners.clear();
		dragListeners.clear();
		hasSpeedButton = true;
		hasPlayPauseButton = true;
		hasStepButton = true;
		setStatus(selectedGame.getName());

		setSpeeds(DELAY_NORMAL, DELAY_FAST, DELAY_FASTER);
		selectedGame.setup(this);
		if (hasPlayPauseButton) {
			setPaused();
		} else {
			setPlaying();
		}
		updateSpeed();
		newGame();

	}

	public void timeStep() {
		if (started) {
			for (ITimeStepListener l : timeStepListeners) {
				l.timeStep(stepCount);
			}

			stepCount++;
		}
	}

	@Override
	public void update(IPosition pos) {
		JButton button = positionMap.get(pos);
		selectedGame.getImageAt(pos).draw(button, stepCount);
	}

	@Override
	public void updateAll() {
		for (Entry<JButton, IPosition> e : buttonMap.entrySet()) {
			selectedGame.getImageAt(e.getValue()).draw(e.getKey(), stepCount);
		}
	}

	private void updateFrame() {
		Container parent = getParent();
		while (parent != null) {
			if (parent instanceof JFrame) {
				JFrame frame = (JFrame) parent;
				frame.setTitle(selectedGame.getName());
				frame.pack();
				return;
			} else if (parent instanceof JApplet) {
				JApplet applet = (JApplet) parent;
				applet.setName(selectedGame.getName());
				applet.resize(getPreferredSize());
				applet.validate();
				return;
			}
			parent = parent.getParent();
		}

	}

	private void updateSpeed() {
		if (currentSpeed == 0) {
			btnSpeed.setIcon(ImageLoader.getImage("gui/images/normal"));
			btnSpeed.setToolTipText("Current speed: Normal");
		} else if (currentSpeed == 1) {
			btnSpeed.setIcon(ImageLoader.getImage("gui/images/fast"));
			btnSpeed.setToolTipText("Current speed: Fast");

		} else if (currentSpeed == 2) {
			btnSpeed.setIcon(ImageLoader.getImage("gui/images/faster"));
			btnSpeed.setToolTipText("Current speed: Faster");

		}
		if (timer != null) {
			timer.removeActionListener(this);
			timer.stop();
		}
		timer = new javax.swing.Timer(speeds[currentSpeed], this);
		if (!paused) {
			timer.start();
		}

	}

	private static final class GameComparator implements Comparator<IGame>,
			Serializable {
		private static final long serialVersionUID = 6647481037039732094L;

		@Override
		public int compare(IGame arg0, IGame arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}
	}

	@Override
	public void addButtonRow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addButton(String name, IImage image, IButtonListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableButton(String name, boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectButton(String name, boolean selected) {
		// TODO Auto-generated method stub
		
	}
}
