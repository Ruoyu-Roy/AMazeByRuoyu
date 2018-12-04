package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.CardinalDirection;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Constants.UserInput;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui.PlayAnimationActivity;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui.PlayAnimationActivity;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui.PlayManuallyActivity;


/**
 * CRC Card for BasicRobot:
 * 		1. Purpose or responsibility of this class:
 * 			a. Manipulating the player's direction and position in the game:
 * 				i. Move forward
 * 				ii. Rotates 90 degrees left
 * 				iii. Rotates 90 degrees right
 * 			b. Track the position of the player
 * 			c. Check if the player is at the exit
 * 			d. Check if the player can see the exit
 * 			e. Track the energy level of the player(robot)
 * 			f. Record the energy level and distance traveled
 * 		2. Collaborators: a controller that holds a maze to be explored, a robotdriver class that operates robot
 * 
 * 
 * @author ruoyuli
 *
 */

public class BasicRobot implements Robot{
	// Basic Variables
	final int SENSING_ENERGY = 1;
	final int ROTATING_ENERGY = 3;
	final int MOVE_ENERGY = 5;
	final int INITIAL_ENERGY = 3000; 
	
	private float batteryLevel = INITIAL_ENERGY;
	private int distanceTraveled = 0;
	private boolean stop = false;
	private boolean manual;
	
	//private Controller controller;
    private StatePlaying controller;
	
	/**
	 * Rotate the direction of the robot
	 */
	@Override
	public void rotate(Turn turn, boolean manual) {
		// TODO Check if the robot has been stopped.
		if ((batteryLevel < ROTATING_ENERGY) || hasStopped()) {
			stop = true;
			System.out.println("Battery is too low to rotate.\n");
			System.out.println("Battery Remains: " + getBatteryLevel());
			controller.keyDown(UserInput.Left, ROTATING_ENERGY);
			return;
		}
		else {
			switch(turn) {
			case LEFT:
				controller.keyDown(UserInput.Left, ROTATING_ENERGY, manual);
				break;
			case RIGHT:
				controller.keyDown(UserInput.Right, ROTATING_ENERGY, manual);
				break;
			case AROUND:
				controller.keyDown(UserInput.Right, ROTATING_ENERGY, manual);
				controller.keyDown(UserInput.Right, ROTATING_ENERGY, manual);
				break;
			}
			batteryLevel -= ROTATING_ENERGY;
		}
		checkBatteryLevel();
		
	}

	/**
	 * Robot moves forward a given number of steps
	 */
	@Override
	public void move(int distance, boolean manual) {
		// TODO Check if the robot is out of battery or if it meets an obstacle
		this.manual = manual;
		if (hasStopped()) {
			System.out.println("The robot has been stopped");
		}
		// Keep move forward
		int manualStep = 0;
		int robotStep = 0;
		if (manual) {
			while (manualStep < distance && !hasStopped()) {
				if (controller.getMazeConfiguration().hasWall(controller.getCurrentPosition()[0], 
						controller.getCurrentPosition()[1], controller.getCurrentDirection())) {
					System.out.println("Wall encountered");
					return;
				}
				else {
					manualStep++;
					distanceTraveled++;
					moveRobot();
				}
			}
		}
		else {
			while (robotStep < distance && !hasStopped()) {
				if (controller.getMazeConfiguration().hasWall(controller.getCurrentPosition()[0], 
						controller.getCurrentPosition()[1], controller.getCurrentDirection())) {
					System.out.println("Wall encountered");
					stop = true;
					return;
				}
				else {
					robotStep++;
					distanceTraveled++;
					moveRobot();
				}
			}
		}
		checkBatteryLevel();
		
	}
	
	/**
	 * Move the robot. (Note: if robot runs out of energy, switch the controller to winning state and assign stop to true)
	 */
	private void moveRobot() {
		if (batteryLevel < MOVE_ENERGY) {
			stop = true;
			System.out.println("Run out of energy");
			controller.keyDown(UserInput.Up, MOVE_ENERGY, manual);
			return;
		}
		else {
			batteryLevel -= MOVE_ENERGY;
			controller.keyDown(UserInput.Up, MOVE_ENERGY, manual);
		}
	}
	
	/**
	 * Give the current position of user or robot
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		// TODO Check if the robot is outside of maze
		int x = controller.getCurrentPosition()[0];
		int y = controller.getCurrentPosition()[1];
		if (x < 0 || x >= controller.getMazeConfiguration().getWidth() || y < 0 || y >= controller.getMazeConfiguration().getHeight()) {
			throw new Exception("Current position is outside of the maze");
		}
		return new int[] {x,y};
	}

	/**
	 * Provides the robot with a reference to the controller to cooperate with.
	 */
	@Override
	public void setMaze(StatePlaying maze) {
		// TODO Auto-generated method stub
		this.controller = maze;
	}
	
	public StatePlaying getMaze() {
		return controller;
	}
	
	/**
	 * Check if the robot is at the exit.
	 */
	@Override
	public boolean isAtExit() {
		// TODO Get the current position
		int [] position  = new int[2];
		try {
			position  = getCurrentPosition();
		} catch(Exception e) {
			System.out.println("Your current position is outside of the maze");
		}
		// Use the maze config to check
		if (controller.getMazeConfiguration().getMazedists().isExitPosition(position[0], position[1]))
			return true;
		return false;
	}

	/**
	 * Check whether the robot can see the exit (wall to outside)
	 */
	@Override
	public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		if (!hasDistanceSensor(direction))
			throw new UnsupportedOperationException();
		if (batteryLevel < SENSING_ENERGY) {
			stop = true;
			controller.keyDown(UserInput.Left, ROTATING_ENERGY);
		}
		batteryLevel -= SENSING_ENERGY;
		checkBatteryLevel();
		return (distanceToObstacle(direction) == Integer.MAX_VALUE);
	}

	/**
	 * Check whether the robot is inside a room
	 */
	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		int [] p = new int[2];
		if (!hasRoomSensor()) {
			throw new UnsupportedOperationException();
		}
		try {
			p = getCurrentPosition();
		} catch (Exception e) {
			System.out.println("You are now out side of the maze.");
			return false;
		}
		return (controller.getMazeConfiguration().getMazecells().isInRoom(p[0], p[1]));
	}

	/**
	 * Whether the robot has a room senser.
	 */
	@Override
	public boolean hasRoomSensor() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * return the current direction
	 */
	@Override
	public CardinalDirection getCurrentDirection() {
		// TODO Auto-generated method stub
		return controller.getCurrentDirection();
	}

	/**
	 * return the current battery level
	 */
	@Override
	public float getBatteryLevel() {
		// TODO Auto-generated method stub
		return batteryLevel;
	}

	/**
	 * set the battery level to a certain number
	 */
	@Override
	public void setBatteryLevel(float level) {
		// TODO Auto-generated method stub
		batteryLevel = level;
	}
	
	/**
	 * return the path length traveled by the robot
	 */
	@Override
	public int getOdometerReading() {
		// TODO Auto-generated method stub
		return distanceTraveled;
	}

	/**
	 * reset the path length to 0
	 */
	@Override
	public void resetOdometer() {
		// TODO Auto-generated method stub
		distanceTraveled = 0;
	}

	/**
	 * return the energy needed for a full (360 degree) rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		// TODO Auto-generated method stub
		return (4*ROTATING_ENERGY);
	}

	/**
	 * return the energy needed for a step
	 */
	@Override
	public float getEnergyForStepForward() {
		// TODO Auto-generated method stub
		return (MOVE_ENERGY);
	}

	/**
	 * return whether the robot has stopped
	 */
	@Override
	public boolean hasStopped() {
		// TODO Auto-generated method stub
		return stop;
	}
	
	/**
	 * reset the stop to false
	 */
	@Override
	public void resetStop() {
		stop = false;
	}

	/**
	 * return the distance to the wall in certain direction
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		if (!hasDistanceSensor(direction))
			throw new UnsupportedOperationException();
		if (batteryLevel < SENSING_ENERGY) {
			stop = true;
			controller.keyDown(UserInput.Left, ROTATING_ENERGY);
		}
		CardinalDirection curDirection = controller.getCurrentDirection();
		if (direction == Direction.LEFT) {
			if (getCurrentDirection() == CardinalDirection.East)
				curDirection = CardinalDirection.South;
			else if (getCurrentDirection() == CardinalDirection.West)
				curDirection = CardinalDirection.North;
			else if (getCurrentDirection() == CardinalDirection.North)
				curDirection = CardinalDirection.East;
			else if (getCurrentDirection() == CardinalDirection.South)
				curDirection = CardinalDirection.West;
		}
		if (direction == Direction.RIGHT) {
			if (getCurrentDirection() == CardinalDirection.East)
				curDirection = CardinalDirection.North;
			else if (getCurrentDirection() == CardinalDirection.West)
				curDirection = CardinalDirection.South;
			else if (getCurrentDirection() == CardinalDirection.North)
				curDirection = CardinalDirection.West;
			else if (getCurrentDirection() == CardinalDirection.South)
				curDirection = CardinalDirection.East;
		}
		int width = controller.getMazeConfiguration().getWidth();
		int height =  controller.getMazeConfiguration().getHeight();
		int [] curPosition = new int[2];
		try {
			curPosition = controller.getCurrentPosition();
		} catch (Exception e) {
			System.out.println("You are outside of the maze now.");
		}
		boolean hitObs = false;
		int dist = 0;
		while (!hitObs) {
			if (curPosition[0] < 0 || curPosition[0] >= width || curPosition[1] < 0 || curPosition[1] >= height) {
				return Integer.MAX_VALUE;
			}
			hitObs = controller.getMazeConfiguration().getMazecells().hasWall(curPosition[0], curPosition[1], curDirection);
			if (!hitObs) {
				if (curDirection == CardinalDirection.East)
					curPosition[0]++;
				if (curDirection == CardinalDirection.West)
					curPosition[0]--;
				if (curDirection == CardinalDirection.North)
					curPosition[1]--;
				if (curDirection == CardinalDirection.South)
					curPosition[1]++;
				dist++;
			}
		}
		batteryLevel -= SENSING_ENERGY;
		checkBatteryLevel();
		return dist;
	}

	/**
	 * The robot only has left and front distance sensor.
	 */
	@Override
	public boolean hasDistanceSensor(Direction direction) {
		// TODO Auto-generated method stub
		if (direction == Direction.LEFT || direction == Direction.RIGHT || direction == Direction.FORWARD)
			return true;
		return false;
	}
	
	/**
	 * Check whether the robot runs out of battery.
	 */
	private void checkBatteryLevel() {
		if (batteryLevel == 0) {
			stop = true;
			controller.toEnd(false, false);
		}
	}
	
	/**
	 * Return whether in manual mode.
	 * 
	 * @return whether in manual mode
	 */
	public boolean getManual() {
		return manual;
	}
	
	/**
	 * Set the manual.
	 * 
	 * @param manual(boolean)
	 */
	public void setManual(boolean manual) {
		this.manual = manual;
	}

}
