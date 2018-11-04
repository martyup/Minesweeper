import java.util.Collection;

public interface IArea extends Iterable<IPosition> {

	boolean contains(int x, int y);

	boolean contains(IPosition pos);

	@Override
	boolean equals(Object other);

	int getHeight();

	int getIndex(int x, int y);

	int getIndex(IPosition pos);

	int getSize();

	int getWidth();

	@Override
	int hashCode();

	boolean isOnEdge(IPosition pos);
	
	Collection<IPosition> neighboursOf(IPosition pos);

	@Override
	String toString();

	boolean wrapsHorizontally();

	boolean wrapsVertically();
}
