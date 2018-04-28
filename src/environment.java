import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This class simulates an environment for an agent in the Q-Learning setting
 * to interact with.
 *  
 * @author Sai Chitti
 *
 */
public class environment {
    /**
     * The maze in the environment that the agent can explore.
     */
    static State[][] env_maze;
    
    /**
     * The initial state of the agent.
     */
    private int initx, inity;
    
    /**
     * The current position of the agent in the environment.
     */
    private int curr_x, curr_y;
    
    /**
     * Requires command line arguments.
     * args[0] - Path to the input file that contains the maze to solve.
     * args[1] - Path to an output file to which the intermediate states will be printed.
     * args[2] - Path to an input file which contains the actions to take care of.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        environment e = new environment(args[0]);
        carryOutActions(args[2], args[1], e);
    }

    /**
     * Constructor that initializes the maze based on the given file.
     * @param fileName
     * @throws Exception
     */
    public environment(String fileName) throws Exception {
        AuxMethods.getMaze(fileName, "env");
        InitState();
    }

    /**
     * Finds the initial state(S) in the maze.
     */
    private void InitState() {
        for(int i = 0; i < env_maze.length; i++) {
            for(int j = 0; j < env_maze[0].length; j++) {
                State s = env_maze[i][j];
                if(s.isStart) {
                    initx = i;
                    inity = j;
                    curr_x = i;
                    curr_y = j;
                    return;
                }
            }
        }
    }
    
    /**
     * This function resets the agent state to the initial state and returns the initial state.
     */
    public void reset() {
        curr_x = initx;
        curr_y = inity;
    }

    /**
     * This function takes in an action a, simulates a step,
     * sets the current state to the next state.
     * If the "next" state as determined by the action from the current state is
     * not defined, the current state is returned.
     * @param a the step(one of {0,1,2,3})
     * @return an array containing the essential information about the step.
     */
    public int[] step(int a) {
        int reward;
        int isTerminal;
        
        if(env_maze[curr_x][curr_y].isGoal) {  //If we are already at a terminal/goal state, no other action needs to be taken.
            reward = 0;
            isTerminal = 1;
            return new int[] {curr_x, curr_y, reward, isTerminal};
        }
        else if(a == 0) { //Direction = West
            if(curr_y - 1 >= 0 && !env_maze[curr_x][curr_y-1].isBlocked) {
                curr_y--;
            }
        }
        else if(a == 1) { //Direction = North
            if(curr_x - 1 >= 0 && !env_maze[curr_x-1][curr_y].isBlocked) {
                curr_x--;
            }
        }
        else if(a == 2) { //Direction = East
            if(curr_y + 1 < env_maze[0].length && !env_maze[curr_x][curr_y+1].isBlocked) {
                curr_y++;
            }
        }
        else { //Direction = South
            if(curr_x + 1 < env_maze.length && !env_maze[curr_x+1][curr_y].isBlocked) {
                curr_x++;
            }
        }
        
        int[] returnPackage = new int[4];
        returnPackage[0] = curr_x;
        returnPackage[1] = curr_y;
        
        if(env_maze[curr_x][curr_y].isGoal) {
            reward = -1;
            isTerminal = 1;
        }
        else {
            reward = -1;
            isTerminal = 0;
        }
        returnPackage[2] = reward;
        returnPackage[3] = isTerminal;
        return returnPackage;
    }
    
    /**
     * Given a file, it reads the actions to carry out and goes on to do them.
     * The file contains a bunch of space-separated actions(given as an integer in {0,1,2,3}) that the agent does.
     * @param fielPath
     * @throws Exception
     */
    private static void carryOutActions(String filePath, String outputFile, environment e) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        
        String line = br.readLine();
        String[] actions = line.split(" ");
        
        for(String action : actions) {
            int a = Integer.parseInt(action);
            //Take the step.
            int[] step_package = e.step(a);

            writer.write(step_package[0] + " " + step_package[1] + " " + step_package[2] + " " + step_package[3] + "\n");
        }
        br.close();
        writer.close();
    }
    
    /**
     * Returns the current state of the agent in the environment.
     */
    public int[] getCurrentState() {
        return new int[] {curr_x, curr_y};
    }
}
