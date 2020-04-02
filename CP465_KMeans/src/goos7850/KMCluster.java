package goos7850;

import java.awt.geom.Point2D;
import java.util.ArrayList;
/**
 * KMCluster.java
 * @author Theodore Goossens
 * Cluster object for K-Means algorithm. Provides useful statistics on the points contained in it such as:
 * The center, the centroid, and the variance of the given set of points.
 */
public class KMCluster {
	private ArrayList<Point2D> points;
	/**
	 * Constructors for KMCluster:
	 */
	/**
	 * Initializes a cluster with no points.
	 */
	private Point2D center;
	/**
	 * Gives the center of this cluster. Useful for graphing the cluster on an XY-plane.
	 * @return: The center of this cluster as a Point2D object.
	 */
	public KMCluster() {
		points = new ArrayList<Point2D>();
	}
	/**
	 * Initializes a cluster with the given list of points.
	 * @param pts: An ArrayList of Point2D objects to cluster.
	 */
	public KMCluster(ArrayList<Point2D> pts) {
		points = new ArrayList<Point2D>(pts);
		center= pts.get(0);
	}
        /**
	 * Initializes a cluster with a single point
	 * @param pts: An ArrayList of Point2D objects to cluster.
	 */
	public KMCluster(Point2D pt) {
		points = new ArrayList<Point2D>();
		center= pt;
	}
	/**
	 * Methods for KMCluster:
	 */
	/**
	 * Adds a point to this cluster.
	 * @param p: A Point2D object to add to the cluster.
	 */
	public void addPoint(Point2D p) {
		points.add(p);
	}
        /**
	 * Adds a point to this cluster.
	 * @param p: A Point2D object to add to the cluster.
	 */
	public void clearPoints() {
		points = new ArrayList<Point2D>();
	}
	/**
	 * Calculates the variance of this particular cluster. The sum of these variances between all clusters 
	 * gives the squared error of all clusters.
	 * @return: The squared error of this cluster.
	 */
	public double getVariance() {
		Point2D center = this.getCentroid();
		double variance = 0;
		for(Point2D p: points) {
			variance += (center.getX()-p.getX())*(center.getX()-p.getX()) + (center.getY()-p.getY())*(center.getY()-p.getY());
		}
		return variance;
	}
	/**
	 * Gives the centroid for this cluster. Used by the K-Means algorithm to determine the candidacy for a 
	 * point towards cluster.
	 * @return: The centroid (center of mass) of this cluster as a Point2D object.
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
	 * Sets the center of the cluster
	 * @return: The center of this cluster as a Point2D object.
	 */
	public Point2D setCenter() {
		double minX, maxX, minY, maxY;
                if (points.size()>0){
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
		this.center=new Point2D.Double((minX+maxX)/2, (minY+maxY)/2);
		return new Point2D.Double((minX+maxX)/2, (minY+maxY)/2);
                }
                return this.center;
		
	}
	/**
	 * Gives the center of this cluster. Useful for graphing the cluster on an XY-plane.
	 * @return: The center of this cluster as a Point2D object.
	 */
	public Point2D getCenter() {
		return this.center;
	}
	
	/**
	 * Calculates the radius of the cluster (to the furthest point from the center). Useful for graphing the 
	 * cluster. 
	 * @return: The minimum radius needed to enclose all the points in the cluster.
	 */
	public double getRadius() {
		
		double maxRad = 0;
		for(Point2D point: points) {
			if(maxRad < distance(center, point)) {
				maxRad = distance(center, point);
			}
		}
		return maxRad;
	}
	/**
	 * Calculates the distance between p1 and p2.
	 * @param p1: A Point2D object.
	 * @param p2: Another Point2D object.
	 * @return: The distance from p1 to p2, ||p1p2||.
	 */
	private static double distance(Point2D p1, Point2D p2) {
		return Math.sqrt( (p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()) );
	}
	/**
	 * Calculates the distance between a point and the center of the cluster
	 * @param p1: A Point2D object.
	 * @return: The distance from p1 to the center of the cluster, ||p1center||.
	 */
	public double distanceFromCenter(Point2D p1) {
		Point2D p2 = this.center;
		return Math.sqrt( (p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()) );
	}
	/**
	 * String representation of this cluster.
	 */
	public String toString() {
		return points+", center="+this.getCentroid();
	}
	
}
