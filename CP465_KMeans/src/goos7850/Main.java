package goos7850;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		ArrayList<Point2D> list = new ArrayList<Point2D>();
		try {
			File file = new File("sampledata.txt");
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] pointData = line.split(",");
				Point2D point = new Point2D.Double(Double.valueOf(pointData[0]), Double.valueOf(pointData[1]));
				list.add(point);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFrame f = new JFrame("Test");
		f.setSize(500,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new GraphView(list));
		f.setVisible(true);
	}
}
