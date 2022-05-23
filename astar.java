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

    //Holds the stack of the final path 
    public static Stack<Node> path = new Stack<>();

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
        public Node prev;
        
        //Array list of nodes that are within range of current node based off distance
        public ArrayList<Node> nodesinRange = new ArrayList<>();

        public Node(double x, double y){
            x_coord = x; 
            y_coord = y;
        }
    

    }

    public static void main(String[] args){
        try{
            //checks input is in correct format with right number of entries
            if(args.length != 4) {

                System.err.println("Invalid Input- Please use format below");
                System.err.println("java Stars [galaxy_csv_filename] [start_index] [end_index] [D]");
            }

            //Read in CSV file
            FileReader file = new FileReader(args[0]);
            BufferedReader reader = new BufferedReader(file);
            String line = reader.readLine();
            //save args as global varibles
            starti= Integer.parseInt(args[1]);
            endi= Integer.parseInt(args[2]);
            dist = Double.parseDouble(args[3]);
            //declaring coordinate values
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
                
                line = reader.readLine();
            }
            reader.close();

            System.out.println("Start: " + startx + " " + starty);
            System.out.println("End: " + endx + " " + endy);
        }
        catch(Exception e){
            System.err.println("Error occurred while extracting CSV information. Try again");
        }
        try{

        
            //Precompute what nodes are within distance to each other for easy traversal
            preCalculateRange();

            //Precomputes heuristic
            setHeuristic();

            //Calls the astar algorithm 
            astar_algorithm();

            //Draws the graphical GUI
            drawGraph();

        
        }	
        catch(Exception e) {

			System.err.println("Error: " + e);
            
		}

        
    }

    //Drawing graph/gui
    public static void drawGraph(){                        
            //create graphics frame to draw map onto
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
        //draw box around stars
        g1.draw(new Line2D.Double(margin, 90, margin, 630));
        g1.draw(new Line2D.Double(margin, 630, size, 630));
        g1.draw(new Line2D.Double(size, 90, size, 630));
        g1.draw(new Line2D.Double(margin, 90, size, 90));
        
        //create a font to draw labels
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
        g1.fill(new Ellipse2D.Double(100, 70, width, height));
        
      
        //run through and draw all stars
        for(Node node : stars){

            //get coordinates and scale for better visualization
            x = (node.x_coord *6);
            y = (node.y_coord *6);

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
            
        }  
        //
        //draw path to goal
        for (Node nodes : path){
            //save previous start coordinates
            Double lastx=x;
            Double lasty=y;
            //get coordinates and scale for better visualization
            x = (nodes.x_coord *6);
            y = (nodes.y_coord *6);
            System.out.println("the size of path is " + path.size());
            //set to path colour
            g1.setPaint(Color.PINK);
            //if not the 1st point draw path between points
            if(count!=0){g1.draw(new Line2D.Double(lastx+2.5+margin, lasty+2.5+margin, x+2.5+margin, y+2.5+margin));}
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
        //Holds the previously tried node
        Node prev_tryNode = null;
        Node startNode = stars.get(starti-1);
        //Initialises the start of the frontier with the start node 
        frontier.add(startNode);
        try{
            while(!frontier.isEmpty()){
                Collections.sort(frontier, new Comparator<Node>(){
                    public int compare(Node o1, Node o2){
                        if(o1.f_value == o2.f_value)
                            return 0;
                        return o1.f_value< o2.f_value ? -1 : 1;
                    }
                });
                //Gets the head of the frontier 
                Node curr = frontier.get(0);
                frontier.remove(0);
                //Check if the node in the frontier was tried before and then gets another node if so to stop looping
                if(prev_tryNode == curr){
                    curr = frontier.get(0);
                    frontier.remove(0);
                }
                //Sets the previous node to the current 
                prev_tryNode = curr;
                //Initally checks if the node pulled off the top is the goal 
                if(curr.x_coord == endx && curr.y_coord == endy){
                    //Found the route 
                    //Gets the route from the stack that can then be drawn between 
                    System.out.println("Path to goal:");
                     
                    //Loops through the full list of stars backtracking through the end goals previous 
                    while (curr != null && curr != stars.get(starti-1)) {
                        //And adding it to the path 
                        path.add(curr);
                        //Then moves onto the next node until the end of the path has been gathered 
                        curr = curr.prev;
                    }
                    path.add(curr);

                    return;
                }
                //Get the next paths from that node and places into frontier  
                for(Node node: curr.nodesinRange){
                    //Caclulates the cost value for the new node 
                    double cost = node.cost + calculateDistanceBetweenPoints(curr.x_coord, curr.y_coord, node.x_coord, node.y_coord);
                    //Calculates the new nodes f value r
                    double f_value = calcfValue(node);
                    //If the calculated cost is less than the nodes current cost 
                    if (cost < node.cost || node.cost == 0) {
                        //Updates the nodes previous node
                        node.prev = curr;
                        //Updates the cost and f value of the current node 
                        node.cost = cost;
                        node.f_value = f_value;
                        //If node is not in the frontier currently 
                        if (!frontier.contains(node)) {
                            frontier.add(node);
                        }
                    }
                }
                //Places that node expanded out onto the stack 
                //path.add(curr);
            }
        //catches of the algorithm does not find the goal star
        }catch(Exception e){
            System.err.println("No Route Found");
        }
    }

    //Prints out the goal from the stack 
    //while(!path.isEmpty()){
    //    Node n = (Node)path.pop();
    //    System.out.println(n.x_coord + " " + n.y_coord);
    //}

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