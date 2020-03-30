package goos7850;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class KMCluster {
	private ArrayList<Point2D> points;
	
	public KMCluster() {
		points = new ArrayList<Point2D>();
	}
	
	public KMCluster(ArrayList<Point2D> pts) {
		points = new ArrayList<Point2D>(pts);
	}
	
	public void addPoint(Point2D p) {
		points.add(p);
	}
	/**
	 * For CALCULATIONS
	 * @return
	 */
	public Point2D getCentroid() {
		double xSum=0, ySum=0;
		for(Point2D point: points) {
			xSum+=point.getX();
			ySum+=point.getY();
		}
		return new Point2D.Double(xSum/points.size(), ySum/points.size());
	}
	/**
	 * for GRAPHING
	 * @return
	 */
	public Point2D getCenter() {
		double minX, maxX, minY, maxY;
		Point2D p = points.get(0);
		minX = p.getX(); maxX = p.getX();
		minY = p.getY(); maxY = p.getY();
		for(int i=1; i<points.size(); i++) {
			p = points.get(i);
			if(p.getX()<minX) minX = p.getX();
			if(p.getY()<minY) minY = p.getY();
			if(p.getX()>maxX) maxX = p.getX();
			if(p.getX()>maxY) maxY = p.getY();
		}
		return new Point2D.Double((minX+maxX)/2, (minY+maxY)/2);
	}
	
	public double getRadius() {
		Point2D center = this.getCenter();
		double maxRad = 0;
		for(Point2D point: points) {
			if(maxRad < distance(center, point)) {
				maxRad = distance(center, point);
			}
		}
		return maxRad;
	}
	
	private static double distance(Point2D p1, Point2D p2) {
		return Math.sqrt( (p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()) );
	}
	
	public String toString() {
		return points+", center="+this.getCentroid();
	}
}
