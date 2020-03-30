package goos7850;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
		
		KMCluster c;
		c = new KMCluster();
		c.addPoint(points.get(0));
		c.addPoint(points.get(2));
		clusters.add(c);
		
		c = new KMCluster();
		c.addPoint(points.get(1));
		c.addPoint(points.get(3));
		c.addPoint(points.get(4));
		c.addPoint(points.get(5));
		clusters.add(c);
		
		System.out.println("Variance C1: "+clusters.get(0).getVariance());
		System.out.println("Variance C2: "+clusters.get(1).getVariance());
		
		JFrame f = new JFrame("Test");
		f.setSize(700,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new GraphView(points, clusters));
		f.setVisible(true);
	}
}
