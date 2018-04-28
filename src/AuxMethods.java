import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a bunch of auxiliary methods that the other classes use. 
 * 
 * @author Sai Chitti
 *
 */
public class AuxMethods {
    /**
     * Given the file containing the maze, this method reads
     * the file and puts the contents in the array.
     * @param filePath
     * @param env this is true if the maze to modify comes from environment.java
     */
    public static void getMaze(String filePath, String type) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<List<State>> temp_states = new ArrayList<>();
        String line;
        
        while((line = br.readLine()) != null) {
            List<State> row = new ArrayList<>();
            
            for(int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                State temp = null;
                
                if(ch == '*') {
                    temp = new State(false, true, false); //Blocked state
                }
                else if(ch == 'S') {
                    temp = new State(false, false, true); //Start state
                }
                else if(ch == 'G') {
                    temp = new State(true, false, false); //Goal state
                }
                else {
                    temp = new State(false, false, false);
                }
                row.add(temp);
            }
            
            temp_states.add(row);
        }
        
        br.close();
        
        int x = temp_states.size();
        int y = temp_states.get(0).size();
        
        if(type.equals("env")) {
            environment.env_maze = new State[x][y];
            copy(temp_states, "env");
        }
        else if(type.equals("VI")) {
            value_iteration.maze = new State[x][y];
            copy(temp_states, "VI");
        } else {
            q_learning.maze = new State[x][y];
            copy(temp_states, "Q");
        }
    }

    /**
     * Copies the elements in the list to the array.
     * @param temp_states
     * @param env this is true if the maze to modify comes from environment.java
     */
    private static void copy(List<List<State>> temp_states, String type) {
        for(int i = 0; i < temp_states.size(); i++) {
            for(int j = 0; j < temp_states.get(0).size(); j++) {
                if(type.equals("env"))
                    environment.env_maze[i][j] = temp_states.get(i).get(j);
                else if(type.equals("VI"))
                    value_iteration.maze[i][j] = temp_states.get(i).get(j);
                else
                    q_learning.maze[i][j] = temp_states.get(i).get(j);
            }
        }
    }

    /**
     * Copies the values in the given array to the maze.values.
     * This method exists since the updates are being made
     * synchronously at the end of the computations of all the V(S) values.
     * @param values
     */
    public static void copy(double[][] values) {
        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < values[0].length; j++) {
                value_iteration.maze[i][j].value = values[i][j];
            }
        }
    }

    /**
     * Prints the maze.
     */
    public static void printMaze(State[][] maze) {
        for(State[] row : maze) {
            for(State s : row) {
                //System.out.print((s.isBlocked || s.isGoal || s.isStart) + " ");
                System.out.print(s.value + " ");
            }
            System.out.println();
        }
    }

    /**
     * Prints the Value function values of each state to a file.
     * @param filePath Path to the output file
     */
    public static void writeValues(String valuesPath, State[][] maze) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(valuesPath));
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                if(s.isBlocked)
                    continue;
                writer.write(i + " " + j + " " + s.value + "\n");
            }
        }
        writer.close();
    }

    /**
     * Print the q arrays of each state to a file.
     * @param qvaluePath Path to the output file
     * @throws Exception
     */
    public static void writeQValues(String qvaluePath, State[][] maze) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(qvaluePath));
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                if(s.isBlocked)
                    continue;
                for(int a = 0; a < 4; a++)
                    writer.write(i + " " + j + " " + a + " " + s.q[a] + "\n");
            }
        }
        writer.close();
    }

    /**
     * Prints the policy values to the output file.
     * @param policyFile Path to the output file
     * @throws IOException
     */
    public static void writePolicyFile(String policyFile, State[][] maze) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(policyFile));
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                if(s.isBlocked)
                    continue;
                writer.write(i + " " + j + " " + (double)s.optimal_policy + "\n");
            }
        }
        writer.close();
    }
}
