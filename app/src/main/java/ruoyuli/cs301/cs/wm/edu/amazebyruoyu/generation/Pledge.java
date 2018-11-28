package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;

/**
 * 
 * Pledge:  this is a refined wall follower that is able to
 * run around and leave an obstacle. It picks a random direction as its main direction and
 * applies wall following when it hits an obstacle. It counts left (-1) and right (+1) turns and
 * when the total becomes zero, it is able to leave an obstacle following its main direction
 * again. 
 * 
 * Pledge Algorithm extends from ManualDirver using the interface of RobotDriver
 * 
 * Collaborators: A. A controller that holds a maze to explore B. A Robot that record all the information and hold all the moves.
 * 
 * @author ruoyuli
 *
 */

public class Pledge extends ManualDriver implements RobotDriver{
	
	//Contructor
	public Pledge() {
		
	}
	
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception{
		while (!robot.isAtExit()) {
			checkStop();
			pledge();
		}
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
	 * Doing Pledge algorithm when the condition is met.
	 * 
	 * @throws Exception
	 */
	public void pledge() throws Exception {
		while (distanceToObstacleNot0(Direction.FORWARD)) {
			checkStop();
			robot.move(1, false);	
		}
		int counter = 0;
		robot.rotate(Turn.RIGHT,false);
		counter--;
		while (counter != 0 && !robot.isAtExit()) {
			checkStop();
			if (distanceToObstacle0(Direction.LEFT)
					&& distanceToObstacle0(Direction.FORWARD)) {
				robot.rotate(Turn.RIGHT,false);
				counter--;
				checkStop();
				if (distanceToObstacle0(Direction.LEFT)
						&& distanceToObstacle0(Direction.FORWARD)) {
					robot.rotate(Turn.RIGHT,false);
					counter--;
					checkStop();
				}
			}
			else if (distanceToObstacleNot0(Direction.LEFT)) {
				robot.rotate(Turn.LEFT,false);
				counter++;
				checkStop();
			}
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

}
