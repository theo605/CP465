package goos7850;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GraphView extends JComponent{

	private int X_PADDING = 20;
	private int Y_PADDING = 20;
	
	private int GRAPH_W = 400;
	private int GRAPH_H = 400;
	private int POINT_RAD = 3;
	private double SCALE = 1;
	
	private int MAX_X = 6;
	private int MAX_Y = 7;
	
	private int SCALENOS_OFFSET=10;
	
	private ArrayList<Point2D> points;
	private ArrayList<KMCluster> clusters;
	
	public GraphView() {
		points = new ArrayList<Point2D>();
		clusters = new ArrayList<KMCluster>();
	}
	
	public GraphView(ArrayList<Point2D> pts) {
		points = new ArrayList<Point2D>(pts);
		clusters = new ArrayList<KMCluster>();
	}
	
	public GraphView(ArrayList<Point2D> pts, ArrayList<KMCluster> clstrs) {
		points = new ArrayList<Point2D>(pts);
		clusters = new ArrayList<KMCluster>(clstrs);
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
		
		g.setColor(Color.GREEN);
		Point2D p;
		for(KMCluster cluster: clusters) {
			p = cluster.getCenter();
			drawPoint(p.getX(),p.getY(),g);
			drawCluster(cluster, g);
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
	
	private void drawCluster(KMCluster clstr, Graphics g) {
		Point2D center = clstr.getCenter();
		double radius = clstr.getRadius();
		double modelCornerX = center.getX()-radius;
		double modelCornerY = center.getY()+radius;
		int newX = (int) ((modelCornerX/(double)MAX_X)*(GRAPH_W)) + X_PADDING;
		int newY = GRAPH_H+Y_PADDING - (int) ((modelCornerY/(double)MAX_Y)*(GRAPH_H));
		int width = (int) ((radius*2/(double)MAX_X)*(GRAPH_W));
		int height = (int) ((radius*2/(double)MAX_Y)*(GRAPH_H));
		g.drawOval(newX, newY, width, height);
	}
}
