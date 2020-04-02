package goos7850;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		ArrayList<KMCluster> clusters = new ArrayList<KMCluster>();
		System.out.print("Enter file name: ");
		String fileName = input.nextLine();
		try {
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] pointData = line.split(",");
				Point2D point = new Point2D.Double(Double.valueOf(pointData[0]), Double.valueOf(pointData[1]));
				points.add(point);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * End import
		 */
                
		System.out.print("Enter K value: ");
		int k = Integer.parseInt(input.nextLine());
                System.out.print("Enter number of Kmeans iterations: ");
		int iterations = Integer.parseInt(input.nextLine());
		//int k=points.size()/3;
                double maxX=points.get(0).getX();
                double maxY=points.get(0).getY();
                double minX=points.get(0).getX();
                double minY=points.get(0).getY();
		//select cluster centers
		Random r;
		int index;
		boolean selected;
		ArrayList<Integer> centers= new ArrayList<Integer>();
		for (int i=0;i<k;i++) {
			selected = false;
			while (selected==false) {
				r = new Random();
				index = r.nextInt(k + 1);
                               
				if (!centers.contains(index)) {
					centers.add(index);
					selected = true;
				}
			}
		}
              
		KMCluster c = new KMCluster();
		for (int i=0;i<k;i++) {
			c = new KMCluster(points.get(centers.get(i)));
			clusters.add(c);
		}
		double closestDist;
		int closest;
		Point2D point;
                //assign points
		for (int i=0;i<points.size();i++) {
			point = points.get(i);
                        //find max and min x/y
                        if (point.getX()>maxX){
                            maxX=point.getX();
                        } else if (point.getX()<minX){
                            minX=point.getX();
                        }
                        if (point.getY()>maxY){
                            maxY=point.getY();
                        } else if (point.getY()<minY){
                            minY=point.getY();
                        }
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCentroid(point);
			//find closest cluster
			for (int j=0;j<k;j++) {
				if(clusters.get(j).distanceFromCentroid(point)<closestDist) {
					closest=j;
					closestDist=clusters.get(0).distanceFromCentroid(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
	
		for (int i=0;i<iterations; i++){
                    //recalculate centers
                    for (int j=0;j<clusters.size(); j++){
                        clusters.get(j).setCentroid();
                        clusters.get(j).clearPoints();
                    }
                    //assign points
                    for (int j=0;j<points.size();j++) {
			point = points.get(j);
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCentroid(point);
			//find closest cluster
			for (int m=0;m<k;m++) {
				if(clusters.get(m).distanceFromCentroid(point)<closestDist) {
					closest=m;
					closestDist=clusters.get(0).distanceFromCentroid(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
                }
                
		
		JFrame f = new JFrame("Test");
		f.setSize(1200,1000);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new GraphView(points, clusters, minX, maxX, minY, maxY));
		f.setVisible(true);
                
                for (int i=0;i<k;i++) {
                    System.out.println(i+"\n");
                    System.out.println("Variance: "+clusters.get(i).getVariance());
                    clusters.get(i).printPoints();
			
		}
		
	}
}
