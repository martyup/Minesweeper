public interface IPosition {

	int getX();

	int getY();
	
	IPosition east();
	
	IPosition north();

	IPosition northEast();

	IPosition northWest();

	IPosition south();

	IPosition southEast();

	IPosition southWest();

	IPosition west();

}
