import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ReversiMain implements ActionListener, MouseListener, Runnable {

	/*
	 * Reversi (Othello) board game
	 * Aidan Derocher and Rohan Press
	 * Java Programming
	 * 6/15/17
	 * 
	 * This game implements a 8 by 8 board game with 64 white and black disks (32 each).
	 * There is both a CPU single-player option and a 
	 * multiplayer, non-CPU option
	 * 
	 * If you hover over a valid move, the pieces that will change color due to that move will be highlighted in yellow.
	 * In the CPU game, the CPU will highlight the square where it is about to make its move, and play there after 2 seconds.
	 * 
	 * The scores of both players are shown at bottom--a pass option is also available at any time.
	 * 
	 * The player with the most disks of his/her color wins the game!
	 */
	
	JFrame frame = new JFrame(); //main frame
	
	JLabel playerOneScore = new JLabel("Player One Score: 2"); //text fields with score
	JLabel playerTwoScore = new JLabel("Player Two Score: 2");
	
	JButton pass = new JButton("Pass"); //pass button
	JLabel mvsAvail = new JLabel("Available Moves: 4"); //text fields
	
	Container buttonContainer = new Container();
	
	//ReversiPaint paint = new ReversiPaint(this);
	Container boardContainer = new Container();
	
	JButton[][] ar = new JButton[8][8]; //main board
	
	Disk[][] disks = new Disk[ar.length][ar.length]; //disk object array
	
	boolean turn = true; //controls the turn with a boolean value
	boolean isCpu = false; //controls single vs multiplayer...
	
	final int CHANGE = 0; //ints that control the parameter run in our checkTile method
	final int HIGHLIGHT = 1;
	final int COUNT = 2;
	final int CHECK = 3;
	
	ArrayList<JButton> High_light = new ArrayList<JButton>();
	
	public ReversiMain() {
		/*
		 * ar stores columns, rows
		 * or x, y
		 * 
		 * but disks stores columns, rows
		 * or x, y
		 */
		
		Object options[] = {"Singleplayer Game", "Multiplayer game"};
		JPanel panel = new JPanel(); //Panel for initial pop-up dialog message
		
		JLabel lbl = new JLabel("Would you like to play a singleplayer or multiplayer game?");
		
		panel.add(lbl);
		
		int check = -1;
		while (check==-1) {
			check = JOptionPane.showOptionDialog(null, panel, "Multiplayer or Singleplayer?", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); //initial pop-up dialog
		}
		
		//singleplayer==0
		//multi == 1
		//x in corner == -1
		
		if (check==0) { //singleplayer was clicked
			isCpu = true;
		}
		else if (check==1) {
			isCpu = false;
		}
		
		for (int j = 0; j < ar.length; j++) { //j == x
			for (int i = 0; i < ar.length; i++) { //i == y
				ar[j][i] = new JButton(""); //creates new button
				ar[j][i].setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.decode("#737373"))); //added color properties
				ar[j][i].addActionListener(this);
				ar[j][i].addMouseListener(this);
				ar[j][i].setIcon(null); //no initial icon
				//ar[j][i].setText(j + " " + i);
				ar[j][i].setActionCommand("Button;" + Integer.toHexString(j) + ";" + Integer.toHexString(i)); //Button;x;y
				Disk d = new Disk(j, i, 0); //x, y;
				d.setColor(0); //null color
				disks[j][i] = d; //x, y
				
				if ((i==3&&j==4) || (i==4&&j==3)) { //for the middle buttons, set an image icon and a corresponding color
					disks[j][i].setColor(1);
					setBlackImage(j, i);
				}
				else if ((i==3&&j==3) || (j==4 && i==4)) {
					disks[j][i].setColor(2);
					setWhiteImage(j, i);
				}
			}
		}
		
		frame.addMouseListener(this); //add mouse listener to the frame
		
		frame.setLayout(new BorderLayout());
		
		boardContainer.setLayout(new GridLayout(ar.length,ar.length)); //grid layout
		
		for(int i=0; i<ar.length; i++) {
			for (int j = 0; j < ar.length; j++) {
				boardContainer.add(ar[i][j]);
			}
		}
		
		frame.add(boardContainer, BorderLayout.CENTER);
		
		mvsAvail.setOpaque(true); //allows for clicked event to not trigger a deactivation of button
		playerOneScore.setOpaque(true);
		playerTwoScore.setOpaque(true);
		mvsAvail.setBackground(Color.white);
		playerOneScore.setBackground(Color.white);
		playerTwoScore.setBackground(Color.white);
		
		playerOneScore.setBorder(BorderFactory.createMatteBorder(1,0,0,1, Color.LIGHT_GRAY)); //creates borders for all fields
		playerTwoScore.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.LIGHT_GRAY));
		pass.setBorder(BorderFactory.createMatteBorder(1,0,0,1, Color.LIGHT_GRAY));
		pass.addActionListener(this); //action listener
		mvsAvail.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.LIGHT_GRAY));
		
		buttonContainer.setLayout(new GridBagLayout()); //grid bag layout — thanks to Gage
		buttonContainer.setPreferredSize(new Dimension(-1, 100));
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5; //sets the x-direction weight
		c.gridx = 0;
		c.gridy = 0;
		//c.ipady=40;
		c.weighty = 1;
		buttonContainer.add(playerOneScore, c); //adds to the container with contraints c
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		//c.ipady=40;
		c.weighty = 1;
		buttonContainer.add(playerTwoScore, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		//c.ipady=40;
		c.weighty = 1;
		buttonContainer.add(pass, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		//c.ipady=40;
		c.weighty = 1; //weight in the y direction
		buttonContainer.add(mvsAvail, c);
		
		frame.add(buttonContainer, BorderLayout.SOUTH); //bottom portion of the container
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800); //size of frame (not preferred size)
		frame.setVisible(true);
		frame.setResizable(false);
		frame.repaint(); //in reversiPaint
		
	}
	
	private void setWhiteImage(int column, int row) {
		try {
			Image img = ImageIO.read(getClass().getResource("white.png")); //set image to white disk
			ar[column][row].setIcon(new ImageIcon(img));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setBlackImage(int i, int j) { //in same order as ar array
		try {
			Image img = ImageIO.read(getClass().getResource("black.png")); //set image to black disk
			ar[i][j].setIcon(new ImageIcon(img));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new ReversiMain(); //code starts here!
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) { //on mouseEntered event
		//System.out.println("rgkrg");
		
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar.length; j++) { //loops through all buttons
				if (arg0.getSource().equals(ar[i][j])) {
					//System.out.println("j (x): " + j + " \t i (y): " + i); //i is y-corrdinate
					//System.out.println(disks[i][j].getColor());
					if (disks[i][j].getColor()==0) { //if color is null
						
						int newColor;
						if (turn) { //==true
							newColor = 2;
						}
						else {
							newColor = 1;
						}
						
						//System.out.println(disks[x][y].getColor());
						if (checkTile(i, j, CHECK, newColor)==-1) { ///checks to see if it is a valid move
							checkTile(i, j, HIGHLIGHT, newColor); //x==columns, y==rows and highlights the squares that would be affected
						}
					}
				}
			}
		}
		
		highlightElements(); //highlights elements
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) {
		clearHighlight();
	}
	
	public void clearHighlight() {
		High_light.clear(); //clears the array
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar.length; j++) {
				if (ar[i][j].getBackground().equals(Color.decode("#FFD590"))) { //if color equals the yellow (does not affect CPU)
					ar[i][j].setBackground(null); //set background color to normal
				}
			}
		}	
	}

	public void highlightElements() {
		if (High_light.size()!=0) { //highLight array has elements added to it in the checkTile method...
			for (JButton jButton : High_light) {
				jButton.setBackground(Color.decode("#FFD590")); //yellow color
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] parse = e.getActionCommand().split(";"); //splits over semicolon
		if (e.getSource().equals(pass)) { //if pass button was clicked
			turn = !turn; //turn equals its opposite value
			
			if (isCpu) { //is true
				cpuStuff(); //calls method
			}
		}
		
		else if (parse[0].equals("Button")) { //if some button was clicked
			//System.out.println(parse[1] + " " + parse[2]);
			
			
			int x = Integer.parseInt(parse[1]); //x index of the clicked button
			int y = Integer.parseInt(parse[2]); //y index of the clicked button
			
			/*System.out.println("x: " + x); //column
			System.out.println("y: " + y);*/ //row
			
			if (disks[x][y].getColor()==0) { //if null color
				
				if (turn) {
					disks[x][y].setColor(2); //set color to white
				}
				else {
					if (isCpu == false){
						disks[x][y].setColor(1); //set to black
					}
				}

				//System.out.println(disks[x][y].getColor());
				if (checkTile(x, y, CHECK, disks[x][y].getColor())==-1) { //if it is a valid move...
	
					if (turn) {
						setWhiteImage(x, y); //set white ImageIcon
					}
					
					else {
						setBlackImage(x, y); //set black ImageIcon
					}
			
					checkTile(x, y, CHANGE, disks[x][y].getColor()); //x==columns, y==rows
					
					turn = !turn; //reverses turn
				}

				else {
					disks[x][y].setColor(0); //null color
				}
			}
		}
	}
	
	public int checkTile(int column, int row, int willChange, int color) {

		boolean hasReached = false;
		int subrow = row;
		int subcol = column;
		int count = 0;
		
		while (hasReached == false) { //Goes up through possibilities
			if (subrow-1 >= 0) {//prevents going off board
				//System.out.println("col: " + column);
				//System.out.println("row: " + (subrow-1));
				if(disks[column][subrow-1].getColor() != color && disks[column][subrow-1].getColor() != 0) { //If one up is not same color as passed in and not null
					subrow--;
					//System.out.println("minus");
				}
				else if (disks[column][subrow-1].getColor() == color) { //is same color
					//System.out.println("running");
					for (int i = subrow; i < row; i++) {
						if (willChange == CHANGE) {

							disks[column][i].setColor(color);//makes all disks in range change color
							
							if (color == 1) {
								setBlackImage(column, i);
							}
							else if (color == 2) {
								setWhiteImage(column, i);
							}
						}
						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[column][i].getColor()==colVar) {
								High_light.add(ar[column][i]); //column, row (x,y)
								//System.out.println(column + ": " + i);
							}
						}
						
						else if (willChange == CHECK){
							return -1;
						}
						count++;
					}
					
					hasReached = true;
				}
				else { //if blank
					hasReached = true;
				}
			}
			else { //if off board
				hasReached = true;
			}	
		}

		hasReached = false;
		subrow = row;
		while (hasReached == false){ //Goes down through possibilities
			if (subrow+1 <= 7) { //prevents going off board
				if(disks[column][subrow+1].getColor() != color && disks[column][subrow+1].getColor() != 0 ){ //If one down is not same color as passed in and not null
					subrow++; //Goes down 1 more
				}
				else if (disks[column][subrow+1].getColor() == color){ //is same color
					for (int i = subrow; i > row; i--) {
						if (willChange == CHANGE) {
							disks[column][i].setColor(color);//makes all disks in range change color
							//System.out.println("WAIT--" + column + ": " + i);
							if (color == 1) {
								setBlackImage(column, i);
							}
							else if (color == 2) {
								setWhiteImage(column, i);
							}
						}

						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[column][i].getColor()==colVar) {
								High_light.add(ar[column][i]); //column, row (x,y)
								//System.out.println(column + ": " + i);
							}
						}
							
						else if (willChange == CHECK){
							return -1;
						}
						count++;
					}
					
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
			}
		}
		
		hasReached = false;
		while (hasReached == false) { //Goes left through possibilities
			
			if (subcol-1 >= 0) { //prevents going off board
				
				if(disks[subcol-1][row].getColor() != color && disks[subcol-1][row].getColor() != 0 ){ //If one to the left is not same color as passed in and not null
					subcol --; //Goes left 1 more
				}
				
				else if (disks[subcol-1][row].getColor() == color){ //is same color
					for (int i = subcol; i < column; i++) {
						if (willChange == CHANGE) {
							
							disks[i][row].setColor(color);//makes all disks in range change color
							//System.out.println("WAIT--" + column + ": " + i);
							
							if (color == 1) {
								setBlackImage(i, row);
							}
							else if (color == 2) {
								setWhiteImage(i, row);
							}

						}
						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][row].getColor()==colVar) {
								High_light.add(ar[i][row]); //column, row (x,y)
								//System.out.println(i + ": " + row);
							}
						}
					
						else if (willChange == CHECK){
							return -1;
						}
						count++;
					}
					
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			
			else { //if blank or off board
				hasReached = true;
			}
		}
		
		hasReached = false;
		subcol = column;
		while (hasReached == false){ //Goes right through possibilities
			
			if (subcol+1 <= 7){//prevents going off board
				if (disks[subcol+1][row].getColor() != color && disks[subcol+1][row].getColor() != 0 ){ //If one to the right is not same color as passed in and not null
					subcol ++; //Goes up 1 more
				}
				
				else if (disks[subcol+1][row].getColor() == color){ //is same color
					for (int i = subcol; i > column; i--) {
						if (willChange == CHANGE) {
							//System.out.println("i: " + i + " row: " + row);
							disks[i][row].setColor(color);//makes all disks in range change color
							//System.out.println("WAIT--" + column + ": " + i);

							if (color == 1) {
								setBlackImage(i, row);
							}
							else if (color == 2) {
								setWhiteImage(i, row);
							}
						}

						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][row].getColor()==colVar) {
								High_light.add(ar[i][row]); //column, row (x,y)
								//System.out.println(i + ": " + row);
							}							
						}

						else if (willChange == CHECK){
							return -1;
						}
						count++;
					}
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
				
			}
		}
		
		hasReached = false;
		subcol = column;
		subrow = row;
		while (hasReached == false) { //Goes right through possibilities
			
			if (subcol+1 <= 7 && subrow+1 <= 7) {//prevents going off board
				
				/*
				 * direction
				 * \
				 *  \
				 *   v
				 */
				
				if(disks[subcol+1][subrow+1].getColor() != color && disks[subcol+1][subrow+1].getColor() != 0 ){ //If one to the right is not same color as passed in and not null
					subcol++; //Goes up 1 more
					subrow++;
				}
				
				else if (disks[subcol+1][subrow+1].getColor() == color){ //is same color
					int j = subrow;
					for (int i = subcol; i > column; i--) {
						if (willChange == CHANGE) {
								
							disks[i][j].setColor(color);//makes all disks in range change color
							if (color == 1) {
								setBlackImage(i, j);
							}
							else if (color == 2) {
								setWhiteImage(i, j);
							}
						}
	
						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][j].getColor()==colVar) {
								High_light.add(ar[i][j]); //column, row (x,y)
								//System.out.println(i + ": " + j);
							}					
						}
						
						else if (willChange == CHECK) {
							return -1;
						}
						j--;
						count++;
					}
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
				
			}
		}
		
		hasReached = false;
		subcol = column;
		subrow = row;
		while (hasReached == false){ //Goes right through possibilities
	
			if (subcol-1 >= 0 && subrow-1 >= 0){//prevents going off board
				
				if(disks[subcol-1][subrow-1].getColor() != color && disks[subcol-1][subrow-1].getColor() != 0 ){ //If one to the right is not same color as passed in and not null
					subcol--; //Goes up 1 more
					subrow--;
				}
				else if (disks[subcol-1][subrow-1].getColor() == color){ //is same color
					int j = subrow;
					for (int i = subcol; i < column; i++) {
						if (willChange == CHANGE) {
								disks[i][j].setColor(color);//makes all disks in range change color
								if (color == 1) {
									setBlackImage(i, j);
								}
								else if (color == 2) {
									setWhiteImage(i, j);
								}
						}
						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][j].getColor()==colVar) {
								High_light.add(ar[i][j]); //column, row (x,y)
								//System.out.println(i + ": " + j);
							}						
						}
								
						else if (willChange == CHECK) {
							return -1;
						}
						j++;
						count++;
					}
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
			}
		}
				
		hasReached = false;
		subcol = column;
		subrow = row;
		while (hasReached == false){ //Goes right through possibilities
			
			if (subcol-1 >= 0 && subrow+1 <= 7){//prevents going off board
				
				if(disks[subcol-1][subrow+1].getColor() != color && disks[subcol-1][subrow+1].getColor() != 0 ){ //If one to the right is not same color as passed in and not null
					subcol--; //Goes up 1 more
					subrow++;
				}
				else if (disks[subcol-1][subrow+1].getColor() == color){ //is same color
					int j = subrow;
					for (int i = subcol; i < column; i++) {
						if (willChange == CHANGE) {
							disks[i][j].setColor(color);//makes all disks in range change color
							if (color == 1) {
								setBlackImage(i, j);
							}
							else if (color == 2) {
								setWhiteImage(i, j);
							}
						}

						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][j].getColor()==colVar) {
								High_light.add(ar[i][j]); //column, row (x,y)
								//System.out.println(i + ": " + j);
							}
						}
							
						else if (willChange == CHECK){
							return -1;
						}
						j--;
						count++;
					}
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
			}
		}
		
		hasReached = false;
		subcol = column;
		subrow = row;
		while (hasReached == false) { //Goes right through possibilities
			
			if (subcol+1 <= 7 && subrow-1 >= 0){//prevents going off board
				
				if(disks[subcol+1][subrow-1].getColor() != color && disks[subcol+1][subrow-1].getColor() != 0 ){ //If one to the right is not same color as passed in and not null
					subcol++; //Goes up 1 more
					subrow--;
				}
				else if (disks[subcol+1][subrow-1].getColor() == color){ //is same color
					int j = subrow;
					for (int i = subcol; i > column; i--) {
						if (willChange == CHANGE) {
								disks[i][j].setColor(color);//makes all disks in range change color
								if (color == 1) {
									setBlackImage(i, j);
								}
								else if (color == 2) {
									setWhiteImage(i, j);
								}
						}
						else if (willChange == HIGHLIGHT) {
							int colVar;
							if (turn) {
								//highlight only black elements
								colVar = 1;
							}
							else {
								//highlight only white elements
								colVar = 2;
							}
							
							if (disks[i][j].getColor()==colVar) {
								High_light.add(ar[i][j]); //column, row (x,y)
								//System.out.println(i + ": " + j);
							}		
						}
							
						else if (willChange == CHECK){
							return -1;
						}
						
						else if (willChange == COUNT) {
							return count;
						}
						j++;
						count++;
					}
					hasReached = true;
				}
				else { //if blank or off board
					hasReached = true;
				}
			}
			else { //if blank or off board
				hasReached = true;
			}
		}

		if (willChange == COUNT){
			return count;
		}
		
		if (isCpu && turn && willChange == CHANGE) {
			cpuStuff();		
		}
		
		return 0;
	}

	private void cpuStuff() {
		turn = false;
		String s = Cpu(); //runs cpu and returns the x y of best move
		if (s != "a"){ //If not a (value that there is no move
			String[] parse = s.split(" "); //Splits x y into two ints
			int x = Integer.parseInt(parse[0]);
			int y = Integer.parseInt(parse[1]);
			
			ar[y][x].setBackground(Color.decode("#FFCDCD")); //AI's move 
			frame.repaint(); //repaints frame
			java.awt.EventQueue.invokeLater(new Runnable() { //waits until frame repaint is done
		        public void run() {
		        	turn = false; // it is cpu turn(needed again since it gets set to true by time repaint is done)
		            long init = System.currentTimeMillis(); //time wait started at
		            long end = System.currentTimeMillis();//current time
		            while((end - init) < 2000){//if over 2 seconds heven't elapsed
		            	end = System.currentTimeMillis(); //updates time
		            }
		            ar[y][x].setBackground(null);//resets background
		            checkTile(y,x,CHANGE,1); //plays the move for the cpu
					disks[y][x].setColor(1); //sets the tile played to black
					setBlackImage(y, x);
					turn = true;
					mReleasedMethod();// runs mouse released method for pass button
		        }
		    });
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		mReleasedMethod();// runs mouse released method for pass button
		
		
	}

	private void mReleasedMethod() {
		clearHighlight(); //Runs clear highlight
		
		int countP1 = 0; //updates p1's tile
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar.length; j++) {
				if (disks[i][j].getColor()==2) {
					countP1++; //adds 1 to p1 count
				}
			}
		}
		
		playerOneScore.setText("Player One Score: " + Integer.toString(countP1)); //changes text of p1 count
		
		int countP2 = 0;//updates p2's count
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar.length; j++) {
				if (disks[i][j].getColor()==1) {
					countP2++; //adds 1 to p2 count
				}
			}
		}
		
		playerTwoScore.setText("Player Two Score: " + Integer.toString(countP2));//changes text of p2 count
		
		int c = 0;
		
		if (turn) { //finds whether color is white or black
			c = 2;//white
		}
		
		else {
			c = 1;//black
		}
		
		int num = checkMovesAvail(c);//runs check moves available with color
			
		mvsAvail.setText("Available Moves: " + Integer.toString(num)); //sets number of moves available
		
		if (checkMovesAvail(1) == 0 && checkMovesAvail(2) == 0) { //if no moves for either players
			int blackCount = 0;
			int whiteCount = 0;
			String string;
			for (int i = 0; i < ar.length; i++) {
				for (int j = 0; j < ar.length; j++) {
					if (disks[i][j].getColor()==1) {
						blackCount++; //gets black tiles
					}
					if (disks[i][j].getColor()==2) {
						whiteCount++;//get white tiles
					}
				}
			}
			
			if (whiteCount>blackCount) { //if more white than black
				string = "White wins!";
			}
			else if (blackCount>whiteCount) {
				string = "Black wins!"; //if mor black than white
			}
			else { //if same amount
				string = "It's a tie!";
			}
			
			String[] ar1 = {"OK"};
			
			JOptionPane.showOptionDialog(null, string, string, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ar1, null); //prints who wins
			
			String[] array = {"Yes", "No"}; 
			
			int check = JOptionPane.showOptionDialog(null, "Would you like to play again?", "Play again", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, array, null); //want to play again
			
			if (check == 0){
				Object options[] = {"Singleplayer Game", "Multiplayer game"};
				JPanel panel = new JPanel(); //Panel for initial pop-up dialog message
				
				JLabel lbl = new JLabel("Would you like to play a singleplayer or multiplayer game?");
				
				panel.add(lbl);
				
				int check1 = -1;
				while (check1==-1) {
					check1 = JOptionPane.showOptionDialog(null, panel, "Multiplayer or Singleplayer?", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); //single or multi player
				}
				
				//singleplayer==0
				//multi == 1
				//x in corner == -1
				
				if (check1==0) {
					isCpu = true; //singleplayer
				}
				else if (check1==1) {
					isCpu = false; //multiplayer
				}
				//resets board
				for (int a = 0; a < ar.length; a++) {
					for (int b = 0; b < ar.length; b++) {
						disks[b][a].setColor(0);
						ar[b][a].setIcon(null);
						if ((a==3&&b==4) || (a==4&&b==3)) {
							disks[b][a].setColor(1);
							setBlackImage(b, a);
						}
						else if ((a==3&&b==3) || (a==4 && b==4)) {
							disks[b][a].setColor(2);
							setWhiteImage(b, a);
						}
					}
				}
				turn = true;
				mvsAvail.setText("Available Moves: 4");
				playerOneScore.setText("Player One Score: 2");
				playerTwoScore.setText("Player Two Score: 2");
			}
			else {
				System.exit(0); //ends game...
			}
		}
		
	}


	private int checkMovesAvail(int c) {
		//System.out.println("\n\n");
		frame.repaint();
		int moveRemain = 0;
		
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar.length; j++) {
				if (checkTile(i, j, CHECK, c)==-1 && disks[i][j].getColor()==0) {
					//System.out.println("i: " + i + " j: " + j);
					moveRemain++;
				}
			}
		}
		
		return moveRemain;
		
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
		
	public String Cpu() {
		int r = -1; //Sets variables for position to null
		int c = -1; //Sets variables for position to null
		String rc = ""; //resets string to pass
		int move = 1; //the max number of moves so far
		int newMove = 0;//the next move
		
		for (int a = 0; a < ar.length; a++) {
			for (int b = 0; b < ar.length; b++) {
				if (disks[a][b].getColor() == 0){ //If it is blank
					newMove = checkTile(a,b,COUNT,1); //runs checktile in count mode, giving number of moves
					//System.out.println("count: " + newMove);
					if (newMove >= move){ //if new number of moves is more than previous max (or equal)
						move = newMove;//max is the new max
						r = b;//records row
						c = a;//records column
					}
				}
			}
		}
		
		if (r != -1 && c != -1) { //if row and column have valid value
			ar[c][r].setBackground(null);
			rc = Integer.toString(r);
			rc += " " + Integer.toString(c);
			
			return rc;
		}
		
		else {
			rc = "a";//value signifying
			
			return rc;
		}

		
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
