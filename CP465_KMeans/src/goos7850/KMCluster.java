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
	
	public Point2D getCenter() {
		double xSum=0, ySum=0;
		for(Point2D point: points) {
			xSum+=point.getX();
			ySum+=point.getY();
		}
		return new Point2D.Double(xSum/points.size(), ySum/points.size());
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
		return points+", center="+this.getCenter();
	}
}
