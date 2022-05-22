import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.Comparator;

public class astar extends JPanel {
    public static Double endx, endy, startx, starty;
    
    public static int starti, endi;
    
    //Put coordinates into a node list 
    public static ArrayList<Node> stars = new ArrayList<>();

    public static ArrayList<Node> coordinates = new ArrayList<>();

    //Saves the specified distance at the start 
    public static double dist;

    //Heuristic distance from node to goal 
    //Cost distance from one node to another as long as within given distance parameter 

    //Class to hold all the information about a node 
    public static class Node {
        public double x_coord; 
        public double y_coord;
        public double heuristic = 0; 
        public double cost = 0; 
        public double f_value = 0;
        
        //Array list of nodes that are within range of current node based off distance
        public ArrayList<Node> nodesinRange = new ArrayList<>();

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

            starti= Integer.parseInt(args[1]);
            endi= Integer.parseInt(args[2]);
            dist = Double.parseDouble(args[3]);

            double x, y;

            //find start and end
            for(int i=1; line!=null; i++){
                
                String[] cord = line.split(",", -1);

                x =  Double.parseDouble(cord[0]);
                y =  Double.parseDouble(cord[1]);

                Node node = new Node(x, y);
                stars.add(node);

                if(i==starti){
                    startx=x;
                    starty=y;
                }
                else if(i==endi){
                    endx=x;
                    endy=y;
                    }
                
                System.out.println(x + "     " + y);
                
                line = reader.readLine();
            }
            
            reader.close();

            System.out.println("Start: " + startx + " " + starty);
            System.out.println("End: " + endx + " " + endy);

            //Precompute what nodes are within distance to each other for easy traversal
            preCalculateRange();

            //Precomputes heuristic
            setHeuristic();

            //Calls the astar algorithm 
            astar_algorithm();

            //Draws the graphical GUI
            drawGraph();
        


        
        }	
        catch(Exception eAStar) {

			System.err.println("Error: " + eAStar);
            
		}

        
    }

    //Drawing graph/gui
    public static void drawGraph(){                        
        
            astar gui = new astar(); 
			JFrame frame = new JFrame("The Starship Enterprise's Mystical Journey Through The Cosmos");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800,750);
            frame.setLocation(200,200);
			frame.add(gui);
			frame.setVisible(true);


    }

    public void paint(Graphics g){
        int width=5, height = 5, margin=100, size=710, count=0;
        Double x=0.0, y=0.0;    
        Graphics2D g1=(Graphics2D)g;
        //draw axis aroud stars
        g1.draw(new Line2D.Double(margin, margin-10, margin, size-margin+20));
        g1.draw(new Line2D.Double(margin, size-margin+20, size, size-margin+20));

        
        g1.draw(new Line2D.Double(size, margin-10, size, size-margin+20));
        g1.draw(new Line2D.Double(margin, margin-10, size, margin-10));
        
        
        Font font = new Font("Arial", Font.BOLD, 16);
        g1.setFont(font);

        //drawing key
        g1.setPaint(Color.PINK);
        g1.drawString("Path", 5, 20);
        g1.draw(new Line2D.Double(95, 5, 109, 15));   

        g1.setPaint(Color.GREEN);
        g1.drawString("Start Point", 5, 40);
        g1.fill(new Ellipse2D.Double(100, 30, width+3, height+3));

        g1.setPaint(Color.RED);
        g1.drawString("End Point", 5, 60);  
        g1.fill(new Ellipse2D.Double(100, 50, width+3, height+3));
        
        g1.setPaint(Color.BLUE);
        g1.drawString("Star", 5, 80);
        g1.fill(new Ellipse2D.Double(100, 70, width+3, height+3));
        
        
        //run through all stars
        for(Node node : stars){
            //get drawing coordinates 
            Double lastx=x;
            Double lasty=y;
            x = node.x_coord *6;
            y = node.y_coord *6;
            //set stars colours
            g1.setPaint(Color.BLUE);
            // if start point
            if(x/6==startx && y/6==starty){
                //set stars colours
                g1.setPaint(Color.GREEN);
                
                //draw starting star
                g1.fill(new Ellipse2D.Double(x+margin,y+margin, width+3, height+3));
            }//else if end point 
            else if(x/6==endx && y/6==endy){
                //set stars colours
                g1.setPaint(Color.RED);
                
                //draw end star
                g1.fill(new Ellipse2D.Double(x+margin,y+margin, width+3, height+3));
            }//else normal star
            else{
                //draw star
                g1.fill(new Ellipse2D.Double(x+margin,y+margin, width, height));
            }
            g1.setPaint(Color.PINK);
            //if not the 1st point
            if(count!=0){g1.draw(new Line2D.Double(lastx+margin+2.5, lasty+margin+2.5, x+margin+2.5, y+margin+2.5));}
            count+=1;
            
        }     
    
    }

    public static double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) 
    {       
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    //Calculates all the nodes that are in range of another node for easy traversal in algortihm  
    public static void preCalculateRange(){
        //Loops through each node in the stars list 
        for(Node currNode : stars){
            //Dummy array list to then be copied to the current nodes list
            ArrayList<Node> inRadius = new ArrayList<>(); 
            //Another loop to see what stars are in range of it 
            for(Node n: stars){
                //Calculates distance between curr node and node its seeing if its close to 
                double point_dist = calculateDistanceBetweenPoints(currNode.x_coord, currNode.y_coord, n.x_coord, n.y_coord);
                //Checks if that distance is valid 
                if(point_dist <= dist){
                    //Adds to dummy array 
                    inRadius.add(n);
                }
            }
            //Sets current nodes in range list 
            currNode.nodesinRange = inRadius;
        }
    }

    //Calculates the heuristic value for each value 
    public static double getHeuristic(Node currNode){
        double distance = Math.sqrt(Math.pow(currNode.x_coord - endx, 2) + Math.pow(currNode.y_coord - endy, 2));
			
		return distance;
    }

    //Frontier management and astar implementation 
    public static void astar_algorithm(){
        
        //Makes the frontier to look through 
        ArrayList<Node> frontier = new ArrayList<>();
        Stack<Node> theStack = new Stack<>();

        //Initialises the start of the frontier with the start node 
        frontier.add(stars.get(starti + 1));

        while(!frontier.isEmpty()){
            Collections.sort(frontier, new Comparator<Node>(){
                public int compare(Node o1, Node o2){
                    if(o1.f_value == o2.f_value)
                        return 0;
                    return o1.f_value< o2.f_value ? -1 : 1;
                }
            });
            //Gets the head of the frontier 
            Node n = frontier.get(0);
            frontier.remove(0);
            //Initally checks if the node pulled off the top is the goal 
            if(n.x_coord == endx && n.y_coord == endy){
                //Found the route 
                //Gets the route from the stack that can then be drawn between 
                System.out.println("FOUND THE GOAL!!!");
                return;
            }
            //Get the next paths from that node and places into frontier  
            for(Node node: n.nodesinRange){
                //Caclulates the cost value for the new node 
                node.cost = node.cost + calculateDistanceBetweenPoints(n.x_coord, n.y_coord, node.x_coord, node.y_coord);
                //Calculates the new nodes f value then places into the frontier 
                node.f_value = calcfValue(node);
                frontier.add(node);
            }
            //Places that node expanded out onto the stack 
            theStack.add(n);
        }
        
    }

    //Loops through each of the nodes presetting their heuristics
    public static void setHeuristic(){
        for(Node n: stars){
            double heuristic = getHeuristic(n);
            n.heuristic = heuristic;
        }
    }


    //Calculates the fvalue
    public static double calcfValue(Node node){
        return node.cost + node.heuristic;
    }

}