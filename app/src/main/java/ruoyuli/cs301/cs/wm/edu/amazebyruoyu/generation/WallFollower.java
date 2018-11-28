package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;

/**
 * 
 * WallFollower:  the wall follower is a classic solution technique. The robot needs a distance
 * sensor at the front and at one side (here: pick left) to perform. It follows the wall on its left
 * hand side. 
 * 
 * WallFollower Algorithm extends from ManualDirver using the interface of RobotDriver
 * 
 * Collaborators: A. A controller that holds a maze to explore B. A Robot that record all the information and hold all the moves.
 * 
 * @author ruoyuli
 *
 */

public class WallFollower extends ManualDriver implements RobotDriver{
	//Private Variables)
	protected boolean initialPositionNotInRoom = false;
	//Constructor
	public WallFollower() {
	}
	
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while (!robot.isAtExit()) {
			// If the robot is inside a room and there's no wall on its left, 
			// we need to move it to the wall on its left side and turn right.
			System.out.println(robot.getBatteryLevel());
			checkStop();
			wallFollow();
		}
		System.out.println(1);
		while (!canSeeExit(Direction.FORWARD)) {
			checkStop();
			robot.rotate(Turn.LEFT,false);
		}
		checkStop();
		robot.move(1, false);
		checkStop();
		return true;
	}
	
	/**
	 * Following the left wall.
	 * 
	 * @throws Exception
	 */
	public void wallFollow() throws Exception {
		// If the robot is inside a room and there's no wall on its left, 
		// we need to move it to the wall on its left side and turn right.
		if (canSeeExit(Direction.FORWARD)) {
			checkStop();
			robot.move(1, false);
		}
		else if (robot.isInsideRoom() && distanceToObstacleNot0(Direction.LEFT) && !initialPositionNotInRoom) {
			checkStop();
			//robot.rotate(Turn.LEFT);
			if (distanceToObstacle0(Direction.RIGHT)) {
				checkStop();
				robot.rotate(Turn.AROUND,false);
			}
			else { 
				if (distanceToObstacleNot0(Direction.FORWARD)) {
					checkStop();
					robot.move(1, false);
				}
			}
		}
		else {
			initialPositionNotInRoom = true;
			if (distanceToObstacle0(Direction.LEFT)
					&& distanceToObstacle0(Direction.FORWARD)) {
				checkStop();
				robot.rotate(Turn.RIGHT,false);
				if (distanceToObstacle0(Direction.LEFT)
						&& distanceToObstacle0(Direction.FORWARD)) {
					checkStop();
					robot.rotate(Turn.RIGHT,false);
				}
			}
			else if (distanceToObstacleNot0(Direction.LEFT)) {
				checkStop();
				robot.rotate(Turn.LEFT,false);
			}
			checkStop();
			robot.move(1, false);
		}
	}
	
	/**
	 * Check if the robot is stopped. If it is, throw exception.
	 * @throws Exception
	 */
	private void checkStop() throws Exception {
		if (robot.hasStopped()) {
			robot.getMaze().toEnd(false,false);
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
	 * @return whether the robot gets initialized in a room
	 */
	public boolean getInitialPositionNotInRoom() {
		return initialPositionNotInRoom;
	}
	
	/**
	 * set whether the robot gets initialized in a room
	 * @param iniRoom
	 */
	public void setInitialPositionNotInRoom(boolean iniRoom) {
		initialPositionNotInRoom = iniRoom;
	}

}
