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
	private int[][] cellVisitTimes;
	protected SingleRandom random;
	private Handler handler;
	private boolean needToMove = false;
	private List<List<int[]>> mainList = new ArrayList<List<int[]>>();
	private List<int[]> doorList = new ArrayList<int[]>();
	Cells cells;
	boolean pause = false;

	//Contructor
	public Explorer() {
		random = SingleRandom.getRandom();
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
		cells = robot.getMaze().getMazeConfiguration().getMazecells();
	}

	/**
	 * Drives the robot towards the exit given it exists and
	 * given the robot's energy supply lasts long enough.
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// The process will continue until robot reaches the exit.
		if (pause) {
			pause();
			return true;
		}
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

	private void performRotation(CardinalDirection current, CardinalDirection project) {
		if(current == CardinalDirection.North) {
			if(project == CardinalDirection.North) {
				return;
			}
			if(project == CardinalDirection.South) {
				robot.rotate(Turn.AROUND,false);
			}
			if(project == CardinalDirection.West) {
				robot.rotate(Turn.RIGHT,false);
			}
			if(project == CardinalDirection.East) {
				robot.rotate(Turn.LEFT,false);
			}
		}
		if(current == CardinalDirection.South) {
			if(project == CardinalDirection.North) {
				robot.rotate(Turn.AROUND,false);
			}
			if(project == CardinalDirection.South) {
				return;
			}
			if(project == CardinalDirection.West) {
				robot.rotate(Turn.LEFT,false);
			}
			if(project == CardinalDirection.East) {
				robot.rotate(Turn.RIGHT,false);
			}
		}
		if(current == CardinalDirection.West) {
			if(project == CardinalDirection.North) {
				robot.rotate(Turn.LEFT,false);
			}
			if(project == CardinalDirection.South) {
				robot.rotate(Turn.RIGHT,false);
			}
			if(project == CardinalDirection.West) {
				return;
			}
			if(project == CardinalDirection.East) {
				robot.rotate(Turn.AROUND,false);
			}
		}
		if(current == CardinalDirection.East) {
			if(project == CardinalDirection.North) {
				robot.rotate(Turn.RIGHT,false);
			}
			if(project == CardinalDirection.South) {
				robot.rotate(Turn.LEFT,false);
			}
			if(project == CardinalDirection.West) {
				robot.rotate(Turn.AROUND,false);
			}
			if(project == CardinalDirection.East) {
				return;
			}
		}
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

	public int findBorderOfRoom1(int x, int y) {
		for (int i = x; i < 401; i ++) {
			if (!cells.isInRoom(i, y)) {
				return i - 1;
			}
		}
		return -1;
	}
	public int findBorderOfRoom2(int x, int y) {
		for (int i = x; i >= 0; i --) {
			if (!cells.isInRoom(i, y)) {
				return i + 1;
			}
		}
		return -1;
	}
	public int findBorderOfRoom3(int x, int y) {
		for (int i = y; i < 401; i ++) {
			if (!cells.isInRoom(x, i)) {
				return i - 1;
			}
		}
		return -1;
	}
	public int findBorderOfRoom4(int x, int y) {
		for (int i = y; i >= 0; i --) {
			if (!cells.isInRoom(x, i)) {
				return i + 1;
			}
		}
		return -1;
	}

	public boolean checkIfInList(List<List<int[]>> mainList, int[] door) {
		for (int i = 0; i < mainList.size(); i ++) {
			for (int j = 0; j < mainList.get(i).size(); j ++) {
				if (mainList.get(i).get(j)[0] == door[0] && mainList.get(i).get(j)[1] == door[1]) {
					return false;
				}
			}
		}
		return true;
	}

	public int indexOfDoorList(List<List<int[]>> mainList, int[] door) {
		for (int i = 0; i < mainList.size(); i ++) {
			for (int j = 0; j < mainList.get(i).size(); j ++) {
				if (mainList.get(i).get(j)[0] == door[0] && mainList.get(i).get(j)[1] == door[1]) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean addDoorListToMainList(List<List<int[]>> mainList, List<int[]> doorList) {
		for (int i = 0; i < mainList.size(); i ++) {
			for (int j = 0; j < mainList.get(i).size() && j < doorList.size(); j ++) {
				if (mainList.get(i).get(j).equals(doorList.get(j))) {
					return false;
				}
			}
		}
		return true;
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
			cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
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
						needToMove = true;
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
						needToMove = true;
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
						needToMove = true;
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
			else {
				needToMove = true;
				drive2Exit();
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
						needToMove = true;
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
			else {
				needToMove = true;
				drive2Exit();
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
						needToMove = true;
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
			else {
				needToMove = true;
				drive2Exit();
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
			else {
				needToMove = true;
				drive2Exit();
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
		cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
		int xRight = 0;
		try {
			xRight = findBorderOfRoom1(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
			int xLeft = findBorderOfRoom2(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
			int yUp = findBorderOfRoom3(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
			int yDown = findBorderOfRoom4(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1]);
			for (int a = xLeft; a <= xRight; a++) {
				if (cells.hasNoWall(a, yDown, CardinalDirection.North)) {
					int[] door = {a, yDown, 0};
					if (checkIfInList(mainList, door)) {
						doorList.add(door);
					} else {
						doorList = mainList.get(indexOfDoorList(mainList, door));
					}
				}
				if (cells.hasNoWall(a, yUp, CardinalDirection.South)) {
					int[] door = {a, yUp, 0};
					if (checkIfInList(mainList, door)) {
						doorList.add(door);
					} else {
						doorList = mainList.get(indexOfDoorList(mainList, door));
					}
				}
			}
			for (int b = yDown; b <= yUp; b++) {
				if (cells.hasNoWall(xLeft, b, CardinalDirection.West)) {
					int[] door = {xLeft, b, 0};
					if (checkIfInList(mainList, door)) {
						doorList.add(door);
					} else {
						doorList = mainList.get(indexOfDoorList(mainList, door));
					}
				}
				if (cells.hasNoWall(xRight, b, CardinalDirection.East)) {
					int[] door = {xRight, b, 0};
					if (checkIfInList(mainList, door)) {
						doorList.add(door);
					} else {
						doorList = mainList.get(indexOfDoorList(mainList, door));
					}
				}
			}
			if (addDoorListToMainList(mainList, doorList)) {
				mainList.add(doorList);
			}
			for (int j = 0; j < doorList.size(); j++) {
				if (doorList.get(j)[0] == robot.getCurrentPosition()[0] && doorList.get(j)[1] == robot.getCurrentPosition()[1]) {
					doorList.get(j)[2] += 1;
					break;
				}
			}
			//select the door to escape room
			int min = doorList.get(0)[2];

			for (int m = 0; m < doorList.size(); m++) {
				if (doorList.get(m)[2] < min) {
					min = doorList.get(m)[2];
				}
			}
			List<int[]> doorUsageList = new ArrayList<int[]>(doorList);
			for (int n = 0; n < doorUsageList.size(); n++) {
				if (doorUsageList.get(n)[2] > min) {
					doorUsageList.remove(n);
					n -= 1;
				}
			}
			int r = random.nextIntWithinInterval(0, doorUsageList.size() - 1);
			int q = doorList.indexOf(doorUsageList.get(r));

			//move robot to the selected door
			int doorx = doorList.get(q)[0];
			int doory = doorList.get(q)[1];
			int initx = robot.getCurrentPosition()[0];
			int inity = robot.getCurrentPosition()[1];

			//add 1 to the visit where it went out
			cellVisitTimes[doorx][doory] += 1;
			if (initx > doorx) {
				performRotation(robot.getCurrentDirection(), CardinalDirection.West);
				for (int i = 0; i < initx - doorx; i++) {
					checkStop();
					robot.move(1, false);
				}
				if (inity > doory) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.North);
					for (int i = 0; i < inity - doory; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
				if (doory > inity) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.South);
					for (int i = 0; i < doory - inity; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
			}
			else if (initx < doorx) {
				performRotation(robot.getCurrentDirection(), CardinalDirection.East);
				for (int i = 0; i < doorx - initx; i++) {
					checkStop();
					robot.move(1, false);
				}
				if (inity > doory) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.North);
					for (int i = 0; i < inity - doory; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
				if (doory > inity) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.South);
					for (int i = 0; i < doory - inity; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
			}
			else {
				if (inity > doory) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.North);
					for (int i = 0; i < inity - doory; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
				else if (doory > inity) {
					performRotation(robot.getCurrentDirection(), CardinalDirection.South);
					for (int i = 0; i < doory - inity; i++) {
						checkStop();
						robot.move(1, false);
					}
				}
			}
			for (CardinalDirection cdd : CardinalDirection.values()) {
				if (robot.getMaze().getMazeConfiguration().getMazecells().hasNoWall(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], cdd) && !robot.getMaze().getMazeConfiguration().getMazecells().isInRoom(robot.getCurrentPosition()[0] + cdd.getDirection()[0], robot.getCurrentPosition()[1] + cdd.getDirection()[1])){
					performRotation(robot.getCurrentDirection(), cdd);
					for (int k = 0; k < doorList.size(); k ++) {
						if (doorList.get(k)[0] == robot.getCurrentPosition()[0] && doorList.get(k)[1] == robot.getCurrentPosition()[1]) {
							doorList.get(k)[2] += 1;
							break;
						}
					}
					//walk out of the room
					move1(1, false);
					cellVisitTimes[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]] += 1;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				try {
					robot.move(distance, manual);
					drive2Exit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 500);
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

	private void pause() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					drive2Exit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		},100);
	}

	@Override
	public void setPause() {
		pause = !pause;
	}
}