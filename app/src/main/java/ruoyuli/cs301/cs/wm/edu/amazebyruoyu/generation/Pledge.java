package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;
import android.os.Handler;

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

	private Handler handler;
	private int counter = 0;
	private boolean pledge = false;
	private boolean needToMove = false;
	
	//Contructor
	public Pledge() {
		handler = new Handler();
	}
	
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception{
		if (!robot.isAtExit()) {
			checkStop();
			pledge();
		}
		else if (!canSeeExit(Direction.FORWARD)) {
			checkStop();
			rotate(Turn.LEFT,false);
		}
		else {
			checkStop();
			move(1, false);
			checkStop();
		}
		return true;
	}
	
	/**
	 * Doing Pledge algorithm when the condition is met.
	 * 
	 * @throws Exception
	 */
	private void pledge() throws Exception {
		if (!pledge) {
			if (distanceToObstacleNot0(Direction.FORWARD)) {
				checkStop();
				move(1, false);
			}
			else if (distanceToObstacle0(Direction.FORWARD)) {
				pledge = true;
				rotate(Turn.RIGHT, false);
				counter--;
			}
		}
		else if (pledge) {
			if (counter != 0 && !robot.isAtExit()) {
				if (needToMove) {
					needToMove = false;
					checkStop();
					move(1,false);
				}
				else if (distanceToObstacle0(Direction.LEFT)
						&& distanceToObstacle0(Direction.FORWARD)
						&& distanceToObstacle0(Direction.RIGHT)) {
					needToMove = true;
					counter--;
					counter--;
					rotate(Turn.AROUND,false);
					checkStop();
				}
				else if (distanceToObstacle0(Direction.LEFT)
						&& distanceToObstacle0(Direction.FORWARD)) {
					rotate(Turn.RIGHT, false);
					needToMove = true;
					counter--;
					checkStop();
				}
				else if (distanceToObstacleNot0(Direction.LEFT)) {
					rotate(Turn.LEFT, false);
					needToMove = true;
					counter++;
					checkStop();
				}
				else {
					move(1, false);
				}
			}
			else if (counter == 0) {
				pledge = false;
				drive2Exit();
			}
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
					robot.rotate(turn, manual);
					drive2Exit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 200);
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
