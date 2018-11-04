

import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.SwingConstants;

public class TextImage implements IImage {

	private String text;
	private Color foreground;
	private Color background;

	/**
	 * @param text
	 *            Button text
	 */
	public TextImage(String text) {
		this(text, null, null);
	}

	/**
	 * @param text
	 *            Button text
	 * @param foreground
	 *            Foreground color, or null for default
	 */
	public TextImage(String text, Color foreground) {
		this(text, foreground, null);
	}

	/**
	 * @param text
	 *            Button text
	 * @param foreground
	 *            Foreground color, or null for default
	 * @param background
	 *            Background color, or null for default
	 */
	public TextImage(String text, Color foreground, Color background) {
		if (text == null) {
			throw new IllegalArgumentException("Text must not be null");
		}

		this.text = text;
		this.foreground = foreground;
		this.background = background;
	}

	@Override
	public void draw(AbstractButton button, int frameNo) {
		button.setIcon(null);
		button.setText(text);
		if (foreground != null) {
			button.setForeground(foreground);
		}
		if (background != null) {
			button.setBackground(background);
		}
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setOpaque(true);
	}

	@Override
	public IImage copy() {
		return this;
	}

	@Override
	public boolean isAnimation() {
		return false;
	}

}
