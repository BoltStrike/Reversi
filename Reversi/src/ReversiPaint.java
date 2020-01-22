import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ReversiPaint extends JPanel {
	
	ReversiMain rv;
	
	public ReversiPaint(ReversiMain newrv) { //constructor
		rv = newrv;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		//System.out.println("repainting");
		
		super.paintComponent(g);
		Graphics2D g1 = (Graphics2D) g; //graphics 2d object
		
		g1.setColor(Color.black); //sets color
		g1.setStroke(new BasicStroke());
		
		int trimmedval = rv.frame.getHeight()-rv.buttonContainer.getHeight(); //removes the container height for configuration of lines on the board
		System.out.println(trimmedval);
	

		//rv.frame.setSize(new Dimension(rv.frame.getWidth(), rv.frame.getHeight()+));
		while((trimmedval) % (rv.ar.length + 1) != 0) {
			//rv.frame.setPreferredSize(new Dimension(rv.frame.getWidth(), rv.frame.getHeight()+1));
			rv.frame.setSize(new Dimension(rv.frame.getWidth(), rv.frame.getHeight()+1)); //resizes the frame to match  dimensions that would fit evenly into a column height/row height
			rv.frame.repaint(); //reapaints the frame
			trimmedval = rv.frame.getHeight()-rv.buttonContainer.getHeight(); //removes button container height
		}
		
		while((trimmedval) % (rv.ar.length + 1) != 0) { 
			//rv.frame.setPreferredSize(new Dimension(rv.frame.getWidth(), rv.frame.getHeight()+1));
			rv.frame.setSize(new Dimension(rv.frame.getWidth(), rv.frame.getHeight()+1));
			rv.frame.repaint();
			trimmedval = rv.frame.getHeight()-rv.buttonContainer.getHeight();
		}
		
		//rv.frame.setResizable(false);
//		
//		for (int i = 0; i < rv.frame.getWidth(); i+=(rv.frame.getWidth()/(rv.ar.length))) {
//			g.drawLine(i, 0, i, rv.frame.getHeight());
//		}
//		for (int i = 0; i < (trimmedval); i+=(trimmedval/(rv.ar.length))) {
//			g.drawLine(0, i, rv.frame.getWidth(), i);
//		}
		
	}

	private ReversiMain newReversiMain(ReversiPaint reversiPaint) { //never called
		// TODO Auto-generated method stub
		return null;
	}
}
