package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import java.util.Arrays;
import java.util.*;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.CardinalDirection;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.SingleRandom;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;
import android.os.Handler;

/**
 * 
 * Explorer: 
 * 1. If outside of a room, it 1) checks its options by asking its available distance sensors, 2)
 * picks a direction to the adjacent cell least traveled (ties are resolved by throwing the dice,
 * i.e., randomization) 3) it rotates if necessary and moves a step into the chosen direction. 
 * 2. If the explorer gets into a room, it scans it to find all possible doors, picks the door least
 * used (ties are resolved by randomization) and then runs for the door to leave the room. 
 * 3. If it is at the exit or it can see the exit, it goes for it. 
 * 
 * Pledge Algorithm extends from ManualDirver using the interface of RobotDriver
 * 
 * Collaborators: A. A controller that holds a maze to explore B. A Robot that record all the information and hold all the moves.
 * 
 * @author ruoyuli
 *
 */

public class Explorer extends ManualDriver implements RobotDriver{
	//Variables
	protected int[][] cellVisitTimes;
	protected SingleRandom random;
	protected boolean initialPositionInRoom;
	private Handler handler;
	private boolean needToMove = false;
	//private boolean newRoom = false;
	//private ArrayList<int[]> doors = new ArrayList<int[]>();// The new door the robot finds
	//private int [] newDoor = null;
	//private int [] curDoor = null;
	
	//Contructor
	public Explorer() {
		random = SingleRandom.getRandom();
		initialPositionInRoom = true;
		handler = new Handler();
	}

	public void setUp() {
		setDimensions(robot.getMaze().getMazeConfiguration().getWidth(), robot.getMaze().getMazeConfiguration().getHeight());
		cellVisitTimes = new int[robot.getMaze().getMazeConfiguration().getWidth()][robot.getMaze().getMazeConfiguration().getHeight()];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cellVisitTimes[i][j] = 0;
			}
		}
	}
	
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// The process will continue until robot reaches the exit.
		if (!robot.isAtExit()) {
			checkStop();
			int [] curP = robot.getCurrentPosition();
			// If the robot can see the exit on certain direction, it goes for it.
			if (canSeeExit(Direction.FORWARD)) {
				move(1, false);
			}
			else if (canSeeExit(Direction.LEFT)) {
				rotate(Turn.LEFT,false);
			}
			else if (canSeeExit(Direction.RIGHT)) {
				rotate(Turn.RIGHT,false);
			}
			else if (!robot.isInsideRoom()) {
				initialPositionInRoom = false;
				//doors.clear();
				//newDoor = null;
				//curDoor = null;
				//newRoom = true;
				notInRoomExplore(curP);
			}
			else if (robot.isInsideRoom()) {
				inRoomExplore();
			}
		}
		else if (canSeeExit(Direction.LEFT)) {
			checkStop();
			rotate(Turn.LEFT,false);
		}
		else if (canSeeExit(Direction.RIGHT)) {
			checkStop();
			rotate(Turn.RIGHT,false);
		}
		else {
			checkStop();
			move(1, false);
			checkStop();
		}
		return true;
	}
	
	// Find a cell on a certain direction
	private int[] cellFinder(int x, int y, Direction dir) throws Exception {
		int [] curPosition = robot.getCurrentPosition();
		CardinalDirection curDir = robot.getCurrentDirection();
		// Find the overall direction in the maze that we want to use.
		if (dir == Direction.LEFT) {
			if (robot.getCurrentDirection() == CardinalDirection.East)
				curDir = CardinalDirection.South;
			else if (robot.getCurrentDirection() == CardinalDirection.West)
				curDir = CardinalDirection.North;
			else if (robot.getCurrentDirection() == CardinalDirection.North)
				curDir = CardinalDirection.East;
			else if (robot.getCurrentDirection() == CardinalDirection.South)
				curDir = CardinalDirection.West;
		}
		if (dir == Direction.RIGHT) {
			if (robot.getCurrentDirection() == CardinalDirection.East)
				curDir = CardinalDirection.North;
			else if (robot.getCurrentDirection() == CardinalDirection.West)
				curDir = CardinalDirection.South;
			else if (robot.getCurrentDirection() == CardinalDirection.North)
				curDir = CardinalDirection.West;
			else if (robot.getCurrentDirection() == CardinalDirection.South)
				curDir = CardinalDirection.East;
		}
		// As we know the overall direction,
		// When the direction is north, we make the height minus one.
		if (curDir == CardinalDirection.North) {
			curPosition[1]--;
		}
		// When the direction is south, we make the height plus one.
		else if (curDir == CardinalDirection.South) {
			curPosition[1]++;
		}
		// When the direction is east, we make the width plus one.
		else if (curDir == CardinalDirection.East) {
			curPosition[0]++;
		}
		// When the direction is west, we make the width minus one.
		else if (curDir == CardinalDirection.West) {
			curPosition[0]--;
		}
		return curPosition;
	}
	
	/**
	 * How robot explore in maze when it is not in a room
	 * 
	 * @param curP
	 * @throws Exception
	 */
	private void notInRoomExplore(int [] curP) throws Exception {
		// Find cell on the three direction to see the available options.
		int [] cellF = cellFinder(curP[0], curP[1], Direction.FORWARD);
		int [] cellL = cellFinder(curP[0], curP[1], Direction.LEFT);
		int [] cellR = cellFinder(curP[0], curP[1], Direction.RIGHT);

		if (needToMove) {
			needToMove = false;
			move(1,false);
		}
		// If all three directions are available, 
		else if (distanceToObstacleNot0(Direction.FORWARD)
				&& distanceToObstacleNot0(Direction.LEFT)
				&& distanceToObstacleNot0(Direction.RIGHT)) {
			// If they all tie, randomly choose one direction.
			checkStop();
			if (cellVisitTimes[cellF[0]][cellF[1]] == cellVisitTimes[cellL[0]][cellL[1]] 
					&& cellVisitTimes[cellR[0]][cellR[1]] == cellVisitTimes[cellL[0]][cellL[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 2);
				switch(randomNum) {
				case 0:
					drive2Exit();
					break;
				case 1:
					rotate(Turn.LEFT, false);
					break;
				case 2:
					rotate(Turn.RIGHT, false);
					break;
				}
			}
			// If two of them tie, just randomly pick one from these two.
			else if (cellVisitTimes[cellF[0]][cellF[1]] == cellVisitTimes[cellL[0]][cellL[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 1);
				switch(randomNum) {
				case 0:
					drive2Exit();
					break;
				case 1:
					this.rotate(Turn.LEFT, false);
					break;
				}
			}
			// If two of them tie, just randomly pick one from these two.
			else if (cellVisitTimes[cellF[0]][cellF[1]] == cellVisitTimes[cellR[0]][cellR[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 1);
				switch(randomNum) {
				case 0:
					drive2Exit();
					break;
				case 1:
					this.rotate(Turn.RIGHT, false);
					break;
				}
			}
			// Choose the least visited cell
			else if (cellVisitTimes[cellF[0]][cellF[1]] < cellVisitTimes[cellL[0]][cellL[1]]
					&& cellVisitTimes[cellF[0]][cellF[1]] < cellVisitTimes[cellR[0]][cellR[1]]) {
			}
			else if (cellVisitTimes[cellL[0]][cellL[1]] < cellVisitTimes[cellF[0]][cellF[1]]
					&& cellVisitTimes[cellL[0]][cellL[1]] < cellVisitTimes[cellR[0]][cellR[1]]){
				rotate(Turn.LEFT, false);
			}
			else if (cellVisitTimes[cellR[0]][cellR[1]] < cellVisitTimes[cellF[0]][cellF[1]]
					&& cellVisitTimes[cellR[0]][cellR[1]] < cellVisitTimes[cellL[0]][cellL[1]]){
				rotate(Turn.RIGHT, false);
			}
		}
		// If there are only two options available,
		else if (distanceToObstacleNot0(Direction.FORWARD) 
				&& distanceToObstacleNot0(Direction.LEFT)
				&& distanceToObstacle0(Direction.RIGHT)){
			checkStop();
			// If they tie, randomly pick one
			if (cellVisitTimes[cellF[0]][cellF[1]] == cellVisitTimes[cellL[0]][cellL[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 1);
				switch(randomNum) {
				case 0:
					drive2Exit();
					break;
				case 1:
					rotate(Turn.LEFT, false);
					break;
				}
			}
			// Choose the one with less visited time.
			else if (cellVisitTimes[cellL[0]][cellL[1]] < cellVisitTimes[cellF[0]][cellF[1]]) {
				rotate(Turn.LEFT, false);
			}
		}
		// If there are only two options available,
		else if (distanceToObstacleNot0(Direction.FORWARD) 
				&& distanceToObstacleNot0(Direction.RIGHT)
				&& distanceToObstacle0(Direction.LEFT)){
			checkStop();
			// If they tie, randomly pick one
			if (cellVisitTimes[cellF[0]][cellF[1]] == cellVisitTimes[cellR[0]][cellR[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 1);
				switch(randomNum) {
				case 0:
					drive2Exit();
					break;
				case 1:
					rotate(Turn.RIGHT, false);
					break;
				}
			}
			// Choose the one with less visited time.
			else if (cellVisitTimes[cellR[0]][cellR[1]] < cellVisitTimes[cellF[0]][cellF[1]]) {
				rotate(Turn.RIGHT, false);
			}
		}
		// If there are only two options available,
		else if (distanceToObstacleNot0(Direction.LEFT) 
				&& distanceToObstacleNot0(Direction.RIGHT)
				&& distanceToObstacle0(Direction.FORWARD)){
			checkStop();
			// If they tie, randomly pick one
			if (cellVisitTimes[cellL[0]][cellL[1]] == cellVisitTimes[cellR[0]][cellR[1]]) {
				int randomNum = random.nextIntWithinInterval(0, 1);
				switch(randomNum) {
				case 0:
					rotate(Turn.LEFT, false);
					break;
				case 1:
					rotate(Turn.RIGHT, false);
					break;
				}
			}
			// Choose the one with less visited time.
			else if (cellVisitTimes[cellR[0]][cellR[1]] < cellVisitTimes[cellL[0]][cellL[1]]) {
				this.rotate(Turn.RIGHT, false);
			}
			// Choose the one with less visited time.
			else if (cellVisitTimes[cellL[0]][cellL[1]] < cellVisitTimes[cellR[0]][cellR[1]]) {
				rotate(Turn.LEFT,false);
			}
		}
		// If there's only one option, pick that one.
		else if (distanceToObstacle0(Direction.FORWARD)
				&& distanceToObstacle0(Direction.LEFT)
				&& distanceToObstacle0(Direction.RIGHT)) {
			checkStop();
			rotate(Turn.AROUND,false);
		}
		else if (distanceToObstacle0(Direction.FORWARD) 
				&& distanceToObstacle0(Direction.LEFT)) {
			checkStop();
			this.rotate(Turn.RIGHT,false);
		}
		else if (distanceToObstacle0(Direction.FORWARD) 
				&& distanceToObstacle0(Direction.RIGHT)) {
			checkStop();
			rotate(Turn.LEFT,false);
		}
		else if (distanceToObstacle0(Direction.LEFT)
				&& distanceToObstacle0(Direction.RIGHT)){
			checkStop();
			move(1, false);
			cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
		}
	}
	
	/** 
	 * How robot explore when it is inside a room.
	 * 
	 * @throws Exception
	 */
	private void inRoomExplore() throws Exception {
		// Create a list that records all the doors in a room.
		ArrayList<int[]> doors = new ArrayList<int[]>();
		// The new door the robot finds
		int [] newDoor = null;
		// The door that robot finds most recently
		int [] curDoor = null;
		// If the robot's position is initialized in a room, move the robot to let the wall being on its left side.
		if (initialPositionInRoom && distanceToObstacleNot0(Direction.LEFT)) {
			if (distanceToObstacle0(Direction.RIGHT)) {
				checkStop();
				rotate1(Turn.AROUND,false);
			}
			else {
				while (distanceToObstacleNot0(Direction.FORWARD)) {
					checkStop();
					move1(1, false);
					if (!robot.isInsideRoom()) {
						checkStop();
						rotate1(Turn.AROUND,false);
						checkStop();
						move1(1, false);
						checkStop();
						rotate1(Turn.LEFT,false);

					}
					if (distanceToObstacle0(Direction.LEFT))
						break;
				}
			}
			checkStop();
			if (distanceToObstacle0(Direction.FORWARD))
				rotate1(Turn.RIGHT,false);
			initialPositionInRoom = false;
		}
		// If the robot enters a new room, just turn left and start scanning by going around in the room.
		// Whenever the robot finds a door, compares it to the door contains in the list.
		// If the door has been visited more times, just forget it and move on.
		// If the door has been visited less times, clear the list and add it to the list.
		// If the door ties with those in the least, simply add it to the list.
		// Then we randomly pick one door in the list.
		else if (!initialPositionInRoom && distanceToObstacleNot0(Direction.LEFT)) {
			checkStop();
			rotate1(Turn.LEFT,false);
		}
		checkStop();
		int [] iniPos = robot.getCurrentPosition();
		do {
			do {
				checkStop();
				if (distanceToObstacle0(Direction.FORWARD) && this.robot.isInsideRoom()) {
					rotate1(Turn.RIGHT,false);
				}
				if (!robot.isInsideRoom()) {
					checkStop();
					rotate1(Turn.AROUND,false);
					checkStop();
					move1(1, false);
					checkStop();
					rotate1(Turn.LEFT,false);
				}
				else
					move1(1, false);
				System.out.println(robot.getBatteryLevel());
			} while (distanceToObstacle0(Direction.LEFT));
			this.cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
			curDoor = cellFinder(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], Direction.LEFT);
			if (newDoor == null) {
				newDoor = curDoor;
				doors.add(robot.getCurrentPosition());
			}
			else if (cellVisitTimes[newDoor[0]][newDoor[1]] == cellVisitTimes[curDoor[0]][curDoor[1]]) {
				doors.add(robot.getCurrentPosition());
			}
			else if (cellVisitTimes[newDoor[0]][newDoor[1]] > cellVisitTimes[curDoor[0]][curDoor[1]]) {
				doors.clear();
				doors.add(robot.getCurrentPosition());
				newDoor = curDoor;
			}
			checkStop();
		} while (!Arrays.equals(robot.getCurrentPosition(), iniPos));
		int randomNum1 = random.nextIntWithinInterval(0, doors.size()-1);
		int [] deDoor = doors.get(randomNum1);
		do {
			checkStop();
			if (distanceToObstacle0(Direction.FORWARD) && this.robot.isInsideRoom()) {
				rotate1(Turn.RIGHT,false);
				checkStop();
			}
			if (!robot.isInsideRoom()) {
				rotate1(Turn.AROUND,false);
				checkStop();
				move1(1, false);
				checkStop();
				rotate1(Turn.LEFT,false);
			}
			else
				move1(1, false);
		} while (!Arrays.equals(robot.getCurrentPosition(), deDoor));
		checkStop();
		if (distanceToObstacleNot0(Direction.LEFT))
			rotate1(Turn.LEFT,false);
		checkStop();
		move1(1, false);
		checkStop();
		cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
	}
	
	/**
	 * Check if the robot is stopped. If it is, throw exception.
	 * @throws Exception
	 */
	private void checkStop() throws Exception {
		if (robot.hasStopped()) {
			robot.getMaze().toEnd(false, false);
			throw new Exception();
		}
	}
	
	/**
	 * Check whether robot is stopped before canSeeExit()
	 * 
	 * @param direction
	 * @return whether robot can see the exit
	 * @throws Exception
	 */
	private boolean canSeeExit(Direction direction) throws Exception {
		checkStop();
		return robot.canSeeExit(direction);
	}
	
	/**
	 * Check whether robot is stopped before.
	 * 
	 * @param direction
	 * @return whether the distance to the obstacle on certain direction is 0;
	 * @throws Exception
	 */
	private boolean distanceToObstacle0(Direction direction) throws Exception {
		checkStop();
		return robot.distanceToObstacle(direction) == 0;
	}

	private void move(final int distance, final boolean manual) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					robot.move(distance, manual);
					drive2Exit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 200);
	}

	private void rotate(final Turn turn, final boolean manual) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					needToMove = true;
					robot.rotate(turn, manual);
					drive2Exit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 200);
	}

	private void move1(final int distance, final boolean manual) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				robot.move(distance,false);
			}
		}, 200);
	}

	private void rotate1(final Turn turn, final boolean manual) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				robot.rotate(turn,false);
			}
		}, 200);
	}
	
	/**
	 * Check whether robot is stopped before.
	 * 
	 * @param direction
	 * @return whether the distance to the obstacle on certain direction is 0;
	 * @throws Exception
	 */
	private boolean distanceToObstacleNot0(Direction direction) throws Exception {
		checkStop();
		return robot.distanceToObstacle(direction) != 0;
	}
	
	/**
	 * 
	 * @return the list contains cell's visit information
	 */
	public int[][] getCellVisitTimes(){
		return cellVisitTimes;
	}
	
	/**
	 * Set the list contains cell's visit information
	 * @param cellV
	 */
	public void setCellVisitTimes(int[][] cellV){
		cellVisitTimes = cellV;
	}
	
	/**
	 * 
	 * @return whether the robot gets initialized in a room
	 */
	public boolean getInitialPositionInRoom() {
		return initialPositionInRoom;
	}
	
	/**
	 * set whether the robot gets initialized in a room
	 * @param iniRoom
	 */
	public void setInitialPositionInRoom(boolean iniRoom) {
		initialPositionInRoom = iniRoom;
	}
}


