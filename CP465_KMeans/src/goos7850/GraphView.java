package goos7850;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GraphView extends JComponent{

	int X_PADDING = 20;
	int Y_PADDING = 20;
	
	int GRAPH_W = 400;
	int GRAPH_H = 400;
	int POINT_RAD = 3;
	double SCALE = 1;
	
	int MAX_X = 6;
	int MAX_Y = 7;
	
	int SCALENOS_OFFSET=10;
	
	ArrayList<Point2D> points = null;
	
	public GraphView() {
		points = new ArrayList<Point2D>();
	}
	
	public GraphView(ArrayList<Point2D> pts) {
		points = new ArrayList<Point2D>(pts);
	}
	
	public void paintComponent(Graphics g) {
		// TODO: Remove coupling with inputs for axes. They are global variables
		g.setColor(Color.WHITE);
		g.fillRect(X_PADDING, Y_PADDING, GRAPH_W, GRAPH_H);
		g.setColor(Color.BLACK);
		drawXaxis(X_PADDING, Y_PADDING+GRAPH_H, GRAPH_W, MAX_X, 4, g);
		drawYaxis(X_PADDING, Y_PADDING+GRAPH_H, GRAPH_H, MAX_Y, 4, g);
		
		g.setColor(Color.RED);
		for(Point2D point: points) {
			drawPoint(point.getX(),point.getY(), g);
		}
	}
	
	private void drawXaxis(int x, int y, int axisLength, int noTicks, int tickSize, Graphics g) {
		int tickDistance = axisLength/noTicks;
		int tickDelta = tickSize/2;
		int val = 0;
		for(int currTick=x; currTick<=axisLength+x; currTick+=tickDistance) {
			g.drawLine(currTick, y-tickDelta, currTick, y+tickDelta);
			g.drawString(""+val, currTick, y+tickDelta+SCALENOS_OFFSET);
			val+=1;
		}
		g.drawLine(x,y,x+axisLength,y);
	}
	// draws bottom-up
	private void drawYaxis(int x, int y, int axisLength, int noTicks, int tickSize, Graphics g) {
		int tickDistance = axisLength/noTicks;
		int tickDelta = tickSize/2;
		int val = 0;
		for(int currTick=y; currTick>=y-axisLength; currTick-=tickDistance) {
			g.drawLine(x-tickDelta, currTick, x+tickDelta, currTick);
			g.drawString(""+val, x-tickDelta-SCALENOS_OFFSET, currTick);
			val+=1;
		}
		g.drawLine(x, y, x, y-axisLength);
		g.drawLine(x,y,x+axisLength,y);
	}
	
	private void drawPoint(double x, double y, Graphics g) {
		int newX = (int) ((x/(double)MAX_X)*(GRAPH_W)) + X_PADDING;
		int newY = GRAPH_H+Y_PADDING - (int)((y/(double)MAX_Y)*(GRAPH_H));
		g.fillOval(newX-POINT_RAD, newY-POINT_RAD, POINT_RAD*2, POINT_RAD*2);
	}
}
