package goos7850;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		ArrayList<KMCluster> clusters = new ArrayList<KMCluster>();
		
		try {
			File file = new File("sampledata.txt");
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
		
		
		int k=points.size()/3;
                
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
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCenter(point);
			//find closest cluster
			for (int j=0;j<k;j++) {
				if(clusters.get(j).distanceFromCenter(point)<closestDist) {
					closest=j;
					closestDist=clusters.get(0).distanceFromCenter(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
	
		for (int i=0;i<5; i++){
                    //recalculate centers
                    for (int j=0;j<clusters.size(); j++){
                        clusters.get(j).setCenter();
                        clusters.get(j).clearPoints();
                    }
                    //assign points
                    for (int j=0;j<points.size();j++) {
			point = points.get(j);
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCenter(point);
			//find closest cluster
			for (int m=0;m<k;m++) {
				if(clusters.get(m).distanceFromCenter(point)<closestDist) {
					closest=m;
					closestDist=clusters.get(0).distanceFromCenter(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
                }
		System.out.println("Variance C1: "+clusters.get(0).getVariance());
		System.out.println("Variance C2: "+clusters.get(1).getVariance());
		
		JFrame f = new JFrame("Test");
		f.setSize(700,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new GraphView(points, clusters));
		f.setVisible(true);
	}
}
