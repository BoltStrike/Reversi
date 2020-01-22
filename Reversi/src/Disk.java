public class Disk {
	
	int i; //vertical component
	int j; //horizontal component
	int color1;
	  
	//Also controls click variable...if 0, then has not been clicked.

	/*0--no color
	 *1--black
	 *2--white
	 */
	
	public Disk(int iint, int jint, int color) { //constructor
		i = iint;
		j = jint;
		color1 = color;
	}
	
	public int[] getPos() { //returns the x and y coordinates
		int[] ar = {i, j};
		return ar;
	}
	
	public int getColor() { //returns the color of a disk
		return color1;
	}
	
	public void setColor(int newState) { //sets the color of a disk
		color1 = newState;
	}
	
}