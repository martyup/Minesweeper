public interface IGrid2D<T> extends IArea {

	void clear(T val);

	IGrid2D<T> copy();

	T get(int x, int y);

	T get(int x, int y, T defaultValue);

	T get(IPosition pos);

	T get(IPosition pos, T defaultValue);

	@Override
	int getHeight();

	@Override
	int getWidth();

	T set(int x, int y, T val);

	T set(IPosition pos, T val);

}
