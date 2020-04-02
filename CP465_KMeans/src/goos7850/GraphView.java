package goos7850;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JComponent;
/**
 * GraphView.java
 * @author Theodore Goossens
 * A JComponent object for displaying a graph for the K-Means algorithm.
 */
@SuppressWarnings("serial")
public class GraphView extends JComponent{
	// Paddings for the top and left side of the graph
	private int X_PADDING = 20;
	private int Y_PADDING = 20;
	// Graph dimensions
	private int GRAPH_W = 1100;
	private int GRAPH_H = 900;
	// Maximum values for x and y-axes. 
	//TODO: Implement scale variable, to allow skips b/w numbers presented on axes.
	private int MAX_X = 100;
	private int MAX_Y = 100;
        private int MIN_X = 100;
	private int MIN_Y = 100;
	// Offset for the numbers presented on the axes.
	private int SCALENOS_OFFSET=10;
	// Radius of the points on the graph. 
	private int POINT_RAD = 3;
	
	private ArrayList<Point2D> points;
	private ArrayList<KMCluster> clusters;
	/**
	 * Constructors:
	 */
	/**
	 * Builds the view of the graph without and points or clusters (ie: An empty graph).
	 */
	public GraphView() {
		points = new ArrayList<Point2D>();
		clusters = new ArrayList<KMCluster>();
	}
	/**
	 * Builds the view of the graph with the given points on it.
	 * @param pts: An ArrayList of Point2D objects to place on the graph.
	 */
	public GraphView(ArrayList<Point2D> pts) {
		points = new ArrayList<Point2D>(pts);
		clusters = new ArrayList<KMCluster>();
	}
	/**
	 * Builds the view of the graph with both points and their clusters. (Points in the ArrayList should 
	 * be partitioned completely into the clusters).
	 * @param pts: An ArrayList of Point2D objects to place on the graph.
	 * @param clstrs: An ArrayList of KMCluster objects to place on the graph. Refer to KMCluster.java.
	 */
	public GraphView(ArrayList<Point2D> pts, ArrayList<KMCluster> clstrs) {
		points = new ArrayList<Point2D>(pts);
		clusters = new ArrayList<KMCluster>(clstrs);
	}
        /**
	 * Builds the view of the graph with both points and their clusters. (Points in the ArrayList should 
	 * be partitioned completely into the clusters).
	 * @param pts: An ArrayList of Point2D objects to place on the graph.
	 * @param clstrs: An ArrayList of KMCluster objects to place on the graph. Refer to KMCluster.java.
         * @param minX: smallest x value
	 * @param maxX: 
         * @param minY: 
         * @param maxY: 
	 */
	public GraphView(ArrayList<Point2D> pts, ArrayList<KMCluster> clstrs, double minX, double maxX, double minY, double maxY) {
		points = new ArrayList<Point2D>(pts);
		clusters = new ArrayList<KMCluster>(clstrs);
                MAX_X=(int)maxX+1;
                MAX_Y=(int)maxY+1;
                MIN_X=(int)maxX+1;
                MIN_Y=(int)maxY+1;
               
	}
	/**
	 * Draws out the graph.
	 */
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
			p = cluster.getCentroid();
			drawPoint(p.getX(),p.getY(),g);
			
		}
		
		g.setColor(Color.BLACK);
		for(KMCluster cluster: clusters) {
			p = cluster.getCenter();
			drawPoint(p.getX(),p.getY(), g);
			drawCluster(cluster, g);
		}
	}
	/**
	 * Helper function for paintComponent(). Draws the x-axis of the graph. (Drawn left to right.)
	 * TODO: remove inputs, the are globally available.
	 * @param x
	 * @param y
	 * @param axisLength
	 * @param noTicks
	 * @param tickSize
	 * @param g: The Graphics object passed to paintComponent().
	 */
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
	/**
	 * Helper function for paintComponent(). Draws the y-axis of the graph. (Drawn bottom-up.)
	 * TODO: remove inputs, the are globally available.
	 * @param x
	 * @param y
	 * @param axisLength
	 * @param noTicks
	 * @param tickSize
	 * @param g: The Graphics object passed to paintComponent().
	 */
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
	/**
	 * Helper function for paintComponent(). Draws a point on the graph at the point (x,y).
	 * TODO: Probably could just take a Point2D object, instead of breaking it into x, y parts. (overload?)
	 * @param x: x-coordinate of the point.
	 * @param y: y-coordinate of the point.
	 * @param g: The Graphics object passed to paintComponent().
	 */
	private void drawPoint(double x, double y, Graphics g) {
		int newX = (int) ((x/(double)MAX_X)*(GRAPH_W)) + X_PADDING;
		int newY = GRAPH_H+Y_PADDING - (int)((y/(double)MAX_Y)*(GRAPH_H));
		g.fillOval(newX-POINT_RAD, newY-POINT_RAD, POINT_RAD*2, POINT_RAD*2);
	}
	/**
	 * Helper function for paintComponent(). Outlines the given cluster's region on the graph.
	 * @param clstr: A KMCluster object the draw on the graph.
	 * @param g: The Graphics object passed to paintComponent().
	 */
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
