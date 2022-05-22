import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class astar extends Canvas{
    public static Double endx, endy, startx, starty;
    
    //Put coordinates into a node list 
    public static ArrayList<Node> stars = new ArrayList<>();
    //2D array of costs 
    public static double[][] costLookup;

    //Heuristic distance from node to goal 
    //Cost distance from one node to another as long as within given distance parameter 

    //Class to hold all the information about a node 
    public static class Node{
        double x_coord; 
        double y_coord;
        double heuristic; 
        double cost; 
        double f_value;

        public Node(double x, double y){
            x_coord = x; 
            y_coord = y;
        }

    }

    public static void main(String[] args){
        try{
            //java Stars [galaxy_csv_filename] [start_index] [end_index] [D]
            if(args.length != 4) {

                System.err.println("");
            }

            //Read in coordinates 
            FileReader file = new FileReader(args[0]);
            BufferedReader reader = new BufferedReader(file);
            String line = reader.readLine();

            int starti= Integer.parseInt(args[1]);
            int endi= Integer.parseInt(args[2]);
            double x, y;
            //node = new Node(x, y);
            //stars.add(node);

            //find start and end
            for(int i=1; line!=null; i++){
                
                String[] cord = line.split(",", -1);

                x =  Double.parseDouble(cord[0]);
                y =  Double.parseDouble(cord[1]);
                Node node = new Node(x, y);
                stars.add(node);
                System.out.println(x + "     " + y);
                if(i==starti){
                    startx=x;
                    starty=y;
                }
                if(i==endi){
                    endx=x;
                    endy=y;
                    }
                line = reader.readLine();
            }
            
            System.out.println("Start: " + startx + " " + starty);
            System.out.println("End: " + endx + " " + endy);

            //Precompute graph of costs how close they are to each other for quick lookup in 2D array  
            createCostArray();
            
            reader.close();
            drawGraph();

        
        }	
        catch(Exception eAStar) {

			System.err.println("Error: " + eAStar);
            
		}

        
    }

    //Drawing graph/gui
    public static void drawGraph(){                        
        //CREATING GUI
            astar gui = new astar(); 
			JFrame frame = new JFrame("Title?");


			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,800);

			frame.add(gui);
			frame.setVisible(true);
        
    }

    public static void createCostArray(){
        //costLookup = new double[stars.length][stars.length];
    }

    //Calculates the heuristic value for each value 
    public double heuristic(Node node){
        return 0.0;
    }

    //Frontier management and astar implementation 
    public void astar_algorithm(){

    }
}