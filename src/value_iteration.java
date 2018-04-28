/**
 * This class implements the Value Iteration algorithm in a deterministic
 * setting (i.e. Prob(s(t) | a(t-1), s(t-1)) = 1).
 * 
 * @author Sai Chitti
 *
 */
public class value_iteration {
    /**
     * The 2D array representing the maze to solve.
     */
    static State[][] maze;
    
    /**
     * Number of epochs.
     */
    private static int epochs;
    
    /**
     * The discount factor.
     */
    private static double gamma;

    /**
     * Requires command line arguments.
     * args[0] - Path to the input file that contains the maze to solve.
     * args[1] - Path to an output file to which the value function of each state is written.
     * args[2] - Path to an output file to which the Q values are written.
     * args[3] - Path to an output file to which the optimal policy of each state is written.
     * args[4] - Number of epochs for which the Value Iteration algorithm needs to run.
     * args[5] - Discount Factor (Gamma).
     * Sample arguments - ./src/medium_maze.txt ./src/Values.txt ./src/QValues.txt ./src/Policy.txt 10 0.9
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AuxMethods.getMaze(args[0], "VI");
        
        epochs = Integer.parseInt(args[4]);
        gamma = Double.parseDouble(args[5]);
        
        for(int e = 1; e <= epochs; e++) {
            computeVI();
        }
        computeQ();
        
        AuxMethods.writeValues(args[1], maze);
        AuxMethods.writeQValues(args[2], maze);
        AuxMethods.writePolicyFile(args[3], maze);
    }

    /**
     * This method computes one iteration of the Value Iteration algorithm.
     */
    private static void computeVI() {
        //Temporary array to hold the updated value functions that come from this epoch.
        double[][] values = new double[maze.length][maze[0].length];
        
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                
                for(int a = 0; a < 4; a++) {
                    if(s.isBlocked || s.isGoal) {
                        s.q[a] = 0;
                        continue;
                    }
                    s.q[a] = getImmediateReward() + ((gamma) * s_prime(i, j, a).value);
                    //s_prime is the state that the agent reaches on going in the specified direction.
                }
                s.optimal_policy = max(s.q);
                values[i][j] = s.q[s.optimal_policy];
            }
        }
        AuxMethods.copy(values);
    }

    /**
     * Computes the correct values of Q and the optimal policy (at the end of all epochs).
     */
    private static void computeQ() {
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                
                for(int a = 0; a < 4; a++) {
                    if(s.isBlocked || s.isGoal) {
                        s.q[a] = 0;
                        continue;
                    }
                    s.q[a] = getImmediateReward() + ((gamma) * s_prime(i, j, a).value);
                    //s_prime is the state that the agent reaches on going in the specified direction.
                }
                s.optimal_policy = max(s.q);
            }
        }
    }

    /**
     * The immediate reward the agent gets because of a transition from one state to another.
     * For Value Iteration this value is known.
     * @return the reward
     */
    private static double getImmediateReward() {
        return -1; //(-1) is the immediate reward because of the transition in this problem.
    }

    /**
     * Given the coordinates of a state and the direction to head in,
     * this method returns the state that's "adjacent" to it.
     * "adjacent" -> Returns the current state if the computed state is blocked or
     * is outside the maze. Otherwise computes the next state in the direction specified by 'a'.
     * @param x
     * @param y
     * @param a
     * @return next state
     */
    private static State s_prime(int x, int y, int a) {
        State toReturn = maze[x][y];
        
        if(a == 0) { //Direction = West
            if(y - 1 >= 0 && !maze[x][y-1].isBlocked) {
                toReturn = maze[x][y-1];
            }
        }
        else if(a == 1) { //Direction = North
            if(x - 1 >= 0 && !maze[x-1][y].isBlocked) {
                toReturn = maze[x-1][y];
            }
        }
        else if(a == 2) { //Direction = East
            if(y + 1 < maze[0].length && !maze[x][y+1].isBlocked) {
                toReturn = maze[x][y+1];
            }
        }
        else { //Direction = South
            if(x + 1 < maze.length && !maze[x+1][y].isBlocked) {
                toReturn = maze[x+1][y];
            }
        }
        return toReturn;
    }

    /**
     * Find the index with the maximum value in the array.
     * @param q
     * @return index
     */
    private static int max(double[] q) {
        double max = Integer.MIN_VALUE;
        int index = 0;
        
        for(int i = 0; i < q.length; i++) {
            if(q[i] > max) {
                max = q[i];
                index = i;
            }
        }
        return index;
    }
}
