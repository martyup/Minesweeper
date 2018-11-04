import javax.swing.AbstractButton;

public interface IImage {

	void draw(AbstractButton button, int frameNo);

	IImage copy();

	boolean isAnimation();
}
