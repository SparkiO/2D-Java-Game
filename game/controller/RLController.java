package game.controller;

import game.*;
import game.model.*;
import utilities.Vector2D;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * Reinforcement Learning controller based on Lab7 Challange Tutorial. Used by Helper Pod objects.
 */
public class RLController implements Controller {

    //to take take its own decision the controller may need access to the pod state and to mothership that droped it also to the game
    private HelperPod pod;
    private PlayerShip mothership;
    public final Game game;

    private static double[] qVal = null; //the value of taking a specific action in a specific state
    private static final double discount = 0.99; //discount factor
    private static final double learning_fact = 0.1; //learning factor

    State currentState, oldState; //current and old States of the pod
    private GameObject target; //target - other Game Object
    int oldActionIndex = 0; //index of the action taken before
    private int currentActionIndex; //index of action taken now

    //They determine for how long the pod will behave quite randomly (exploring strategies) at the beginning
    private static final double explorationFactor = 1;
    private static int[] counts = null;
    private static int[] countsXState = null;

    private long activationTime; //make the pod inactive for some time after being drop
    Action action = new Action(); //action to take
    private boolean initialised = false;

    /**
     * Constructor. Sets the mothership and game reference.
     *
     * @param pgame - reference to the player ship
     * @param pship - reference to the current game object
     */
    public RLController(Game pgame, Ship pship) {
        game = pgame;
        mothership = (PlayerShip) pship;

        //wait for half second before starting the pod
        activationTime = System.currentTimeMillis() + 1000;


    }

    public void init(){
        oldState = new State();
        currentState = new State();

        //loading q values from files if saved before
        if (qVal == null) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("QVal.ser"));
                qVal = (double[]) in.readObject();
                in.close();
                if (qVal.length != (NUM_STATES * NUM_ACTIONS)) throw new Exception();
                System.out.println("QVal:" + Arrays.toString(qVal));
            } catch (Exception qvalexc) {
                qVal = new double[NUM_STATES * NUM_ACTIONS];
            }
        }

        //loading count values from files if saved before
        if (counts == null || countsXState == null) {

            try {
                ObjectInputStream in1 = new ObjectInputStream(new FileInputStream("Counts.ser"));
                counts = (int[]) in1.readObject();
                in1.close();
                in1 = new ObjectInputStream(new FileInputStream("CountsXState.ser"));
                countsXState = (int[]) in1.readObject();
                in1.close();
                if (counts.length != (NUM_STATES * NUM_ACTIONS) || (countsXState.length != NUM_STATES))
                    throw new Exception();
            } catch (Exception countvalexc) {
                counts = new int[NUM_STATES * NUM_ACTIONS];
                countsXState = new int[NUM_STATES];
            }
        }

        initialised = true;
    }

    /**
     * Sets the Helper Pod object that uses this controller.
     *
     * @param helperPod - reference to Helper Pod object
     */
    public void setPod(HelperPod helperPod) {
        pod = helperPod;
        lowerLevelController = new SeekNShootTarget(false, false, new Action(), pod, mothership);
    }

    private final static int NUM_STATES = 91; //Depends on the conditions we use to define the state space
    private static final double NEAR = 350; //Near distance from the Pod
    private static final double VERYNEAR = 150; //Very near distance from the Pod
    private static final int fakeFeatures[] = {0, 2, 2}; //"Fake" features for the original, first State, used in State constructor
    private static final int fakeIdx = 80; //"Fake" index to use in State constructor

    /**
     * Inner class holding state of the Game and of the Pod.
     */
    class State {
        int featureVec[]; //array holding different features to classify
        int index; //index of the state
        GameObject nearToPod; //gameObject which is near to the Healper Pod

        /**
         * First constructor. Sets the index to fake one, does same with Features.
         */
        State() {
            index = fakeIdx;
            featureVec = fakeFeatures;
        }

        /**
         * Second constructor. Sets the index to original State's index and clone its features.
         *
         * @param ori
         */
        State(State ori) {
            index = ori.index;
            featureVec = ori.featureVec.clone();
        }

        /**
         * Updates the State object: index and features.
         */
        void updateStateAndFeatures() {

            //sets the feature array depending on the nearest object and states of the pod and mothership
            int featureVec1[] = {
                    featureNearestObject(pod),
                    featureAlive(pod),
                    featureAlive(mothership)};
            featureVec = featureVec1;
            index = 0;
            for (int i = 0; i < featureVec.length; i++) {
                index = featureVec[i] + 3 * index;
            }
        }

        /**
         * Checks whether the object is dead.
         *
         * @param obj - model to be checked
         * @return boolean indicating the state of the object
         */
        boolean isDead(GameObject obj) {
            if (obj == pod) {
                return featureVec[1] == 0;
            } else if (obj == mothership) {
                return featureVec[2] == 0;
            } else if (obj instanceof Asteroid || obj instanceof EnemyShip) {
                return true;
            }
            return false;
        }

        /**
         * Checks whether the object is alive.
         *
         * @param obj - model to be checked
         * @return boolean indicating the state of the object
         */
        int featureAlive(GameObject obj) {
            if (obj.dead) return 0;
            else return 1;
        }

        /**
         * Gets the nearest enemy from the Pod
         *
         * @param pos - Vector2D with position of the Pod
         * @return reference to the nearest GameObject
         */
        GameObject getNearestEnemy(Vector2D pos) {
            GameObject result = null;
            double bestdist = Double.MAX_VALUE;
            for (GameObject object : game.objects) {

                //making sure it doesn't check itself or PlayerShip or BlackHole which should be ignored
                if (object instanceof EnemyShip || object instanceof Asteroid) {
                    double d = pos.dist(object.position);
                    if (d < bestdist) {
                        result = object;
                        bestdist = d;
                    }
                }
            }
            target = result;
            return result;
        }

        /**
         * Depending on the distance to the nearest object returns different int value.
         *
         * @param obj - GameObject to be checked
         */
        int featureNearestObject(GameObject obj) {
            GameObject nearestEnemy = getNearestEnemy(obj.position);
            if (obj == pod) {
                nearToPod = nearestEnemy;
            }
            if (nearestEnemy != null) {
                double d = obj.position.dist(nearestEnemy.position);
                if (d < 0 || d > NEAR) return 2;
                if (d < VERYNEAR) return 0;
                return 1;
            }
            return 2;
        }
    }

    final static int NUM_ACTIONS = 3; //Number of possible actions to be taken
    SeekNShootTarget lowerLevelController;//Controlled used by the HelperPod

    /**
     * Dictatates which actions should be taken by setting appropriate values in SeekNShootTarget controller.
     *
     * @return Action object to execute.
     */
    @Override
    public Action action() {
        if (initialised) {
            //if the Pod is not activated yet - do nothing
            if (activationTime > System.currentTimeMillis()) {
                action.turn = 0;
                action.shoot = false;
                return action;
            }

            //sets the action index to current step in Reinforcement Learning
            currentActionIndex = rlStep();

            switch (currentActionIndex) {
                case 0: // wait action
                    lowerLevelController.setShoot(false);
                    lowerLevelController.setSeek(false);
                    lowerLevelController.setTarget(null);
                    break;

                case 1: //follow the ship
                    lowerLevelController.setShoot(false);
                    lowerLevelController.setSeek(true);
                    lowerLevelController.setTarget(mothership);
                    break;

                case 2: //attack the ship near the pod
                    lowerLevelController.setShoot(true);
                    lowerLevelController.setSeek(true);
                    lowerLevelController.setTarget(currentState.nearToPod);
                    break;
            }
            return lowerLevelController.action();
        }
        else
            return new Action();
    }

    /**
     * Calculates the best appropriate next action. Sometimes chooses randomly.
     *
     * @return int indicating which action to take
     */
    int selectNextAction() {
        int result = 0;


        // selects the best action most of the time
        // but it may randomly choose to take a random action
        // this is useful to support exploration of multiple stategies
        if (Math.random() > explorationFactor / Math.sqrt(1 + countsXState[currentState.index])) {
            double maxQ = Double.NEGATIVE_INFINITY;
            for (int i = currentState.index * NUM_ACTIONS; i < NUM_ACTIONS + currentState.index * NUM_ACTIONS; i++) {

                double val = qVal[i];
                if (val > maxQ) {
                    maxQ = val;
                    result = i - currentState.index * NUM_ACTIONS;
                }
            }
        } else result = (int) Math.floor(Math.random() * NUM_ACTIONS); //random action

        return result;
    }

    /**
     * Calculates the possible reward for the action in passed State.
     *
     * @return double indicating the reward value
     */
    double getReward(State state) {
        if (state.isDead(mothership)) return -1;
        if (state.isDead(pod)) return -0.5;
        if (target instanceof Asteroid && state.isDead(target))
            return 0.5;
        if (target instanceof EnemyShip && state.isDead(target))
            return 1;
        return 0;
    }

    /**
     * Makes a "step" in the RL. Assigns the values of this State to old State. Does the same for other attributes
     * of State: actionIndex, countXState and counts. Calls to updateQLearning.
     *
     * @return int representing index of current action
     */
    int rlStep() {
        oldState = new State(currentState);
        currentState.updateStateAndFeatures();
        oldActionIndex = currentActionIndex;
        currentActionIndex = selectNextAction();
        countsXState[currentState.index] = countsXState[currentState.index] + 1;
        counts[currentState.index * NUM_ACTIONS + currentActionIndex] = counts[currentState.index * NUM_ACTIONS + currentActionIndex] + 1;
        updateQLearning();
        return currentActionIndex;
    }

    /**
     * Calculates the new Q-learning Value and adds it to the array of qValues.
     */
    void updateQLearning() {
        double R = getReward(currentState);
        double OldQVal = qVal[oldState.index * NUM_ACTIONS + oldActionIndex];
        double maxQ = Double.NEGATIVE_INFINITY;

        for (int i = currentState.index * NUM_ACTIONS; i < NUM_ACTIONS + currentState.index * NUM_ACTIONS; i++) {
            double val = qVal[i];
            if (val > maxQ) maxQ = val;
        }
        double newQval = OldQVal + learning_fact * (R + discount * maxQ - OldQVal);
        //System.out.println(newQval); - for debugging
        qVal[oldState.index * NUM_ACTIONS + oldActionIndex] = newQval;
    }
}
