/**
 * This class represents a State in a maze.
 * 
 * @author Sai Chitti
 *
 */
public class State {
    double[] q; //The Q(s,a) values for this state.
    boolean isGoal; //True if this state is the goal.
    boolean isBlocked; //True if this state is blocked.
    boolean isStart; //True if this state is the start state.
    double value; //The value function value for this state in the VI algorithm.
    int optimal_policy; //The optimal policy to take at this state.
    
    State(boolean goal, boolean blocked, boolean start) {
        q = new double[4];  //There are only 4 actions - left(0), up(1), right(2) and down(3).
        isGoal = goal;
        isBlocked = blocked;
        isStart = start;
        value = 0;
        optimal_policy = -1; //Initializing to a dummy value.
    }
}