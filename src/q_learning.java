import java.util.Random;

/**
 * This class implements the Q-Learning algorithm.
 * 
 * @author Sai Chitti
 * 
 */
public class q_learning {
    /**
     * The 2D array representing the maze to solve. 
     */
    static State[][] maze;
    
    /**
     * Discount Factor.
     */
    private static double gamma;
    
    /**
     * The learning factor, alpha.
     */
    private static double learning_rate;
    
    /**
     * Epsilon that determines the trade-off factor between exploring and exploiting.
     */
    private static double epsilon;
    
    /**
     * Maximum length of an episode.
     */
    private static double episode_length;
    
    /**
     * Total number of episodes.
     */
    private static int episodes;
    
    /**
     * Requires command line arguments.
     * args[0] - Path to the input file that contains the maze to solve.
     * args[1] - Path to an output file to which the value function of each state is written.
     * args[2] - Path to an output file to which the Q values are written.
     * args[3] - Path to an output file to which the optimal policy of each state is written.
     * args[4] - Number of episodes for which the Q-Learning algorithm runs.
     * args[5] - Maximum length of an episode.
     * args[6] - Learning Rate (Alpha).
     * args[7] - Discount factor(Gamma).
     * args[8] - Epsilon for the epsilon-greedy strategy.
     * Sample args - ./src/medium_maze.txt ./src/QLearn_Values.txt ./src/QLearn_QValues.txt ./src/QLearn_Policy.txt 1000 20 0.8 0.9 0.05
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AuxMethods.getMaze(args[0], "Q");
        
        episodes = Integer.parseInt(args[4]);
        episode_length = Integer.parseInt(args[5]);
        learning_rate = Double.parseDouble(args[6]);
        gamma = Double.parseDouble(args[7]);
        epsilon = Double.parseDouble(args[8]);
        
        //The environment to interact with.
        //This is where we get our reward values and next states for our steps from.
        environment env = new environment(args[0]);
        
        for(int e = 1; e <= episodes; e++) {
          //Start from the starting state of the maze.
            env.reset();
            
            if(episode_length == 0)
                break;
            
            boolean isTerminal = qLearn(env);
            int length = 1;
            
            while(!isTerminal && length < episode_length) {
                qLearn(env);
                length++;
            }
            updateValues();
        }
        updateValues();
        AuxMethods.writeValues(args[1], maze);
        AuxMethods.writeQValues(args[2], maze);
        AuxMethods.writePolicyFile(args[3], maze);
    }

    /**
     * This method implements the QLearning algorithm for one episode that explores the environment until
     * either the max number of steps has been reached or a terminal state has been reached.
     * In the Q-Learning setting, we don't have any information about the maze 
     * to iterate from a "beginning" to an "end". All we can do is pick an action from 
     * our current state and ask the environment to give us info about that one state-action pair.
     * @param env The environment object that simulates the env the agent is exploring.
     * @return Returns if the next state at the end of an episode is a terminal state.
     */
    private static boolean qLearn(environment env) {
        int[] currentState = env.getCurrentState();
        
        //The current state of the agent.
        State s = maze[currentState[0]][currentState[1]];
        
        //Pick a state. Here is the eternal quest of finding the optimal trade-off between exploration and exploitation.
        //With a prob of epsilon pick a random action from the current state. With a prob (1-epsilon), pick the optimal action.
        int best_current_policy = max(s.q);
        s.optimal_policy = best_current_policy;
        
        int action = pickAction(s.optimal_policy, epsilon);
        
        //One interaction with the environment.
        //From the current state of the agent take a single step; the direction of which is determined by the current policy at this state.
        
        int[] stateInfo = env.step(action);
        State s_prime = maze[stateInfo[0]][stateInfo[1]];
        
        //Determine the best policy at s_prime.
        int best_policy = max(s_prime.q);
        s_prime.optimal_policy = best_policy;
        
        s.q[action] = ((1.0 - learning_rate) * s.q[action]) + ((learning_rate) * (stateInfo[2] + (gamma) * s_prime.q[best_policy]));
        return (stateInfo[3] == 1);
    }

    /**
     * Updates the V(S), and optimal_policy values.
     */
    private static void updateValues() {
        for(int i = 0; i < maze.length; i++) {
            for(int j = 0; j < maze[0].length; j++) {
                State s = maze[i][j];
                
                for(int a = 0; a < 4; a++) {
                    if(s.isBlocked || s.isGoal) {
                        s.q[a] = 0;
                        continue;
                    }
                }
                s.optimal_policy = max(s.q);
                s.value = s.q[s.optimal_policy];
            }
        }
    }
    
    /**
     * Pick an action and return.
     * @param optimal_policy the best policy at this state
     * @param epsilon
     */
    private static int pickAction(int optimal_policy, double epsilon) {
        double prob = Math.random();
        if(prob < epsilon) {
            return randomAction();
        }
        else
            return optimal_policy;
    }

    /**
     * Returns a random action (indicated by a number in {0,1,2,3}).
     * @return An integer between 0 and 3
     */
    private static int randomAction() {
        return new Random().nextInt(4);
    }

    /**
     * Find the index with the maximum value in the array.
     * @param q the array
     * @return index The index where the maximum element sits.
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
