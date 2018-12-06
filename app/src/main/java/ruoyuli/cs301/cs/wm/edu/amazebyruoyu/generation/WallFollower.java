package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;
import android.os.Handler;

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
	private Handler handler;
	boolean needToMove = false;
	boolean pause = false;
	//Constructor
	public WallFollower() {
		handler = new Handler();
	}
	
	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		if (pause) {
			pause();
			return true;
		}
		if (!robot.isAtExit()) {
			// If the robot is inside a room and there's no wall on its left, 
			// we need to move it to the wall on its left side and turn right.
			System.out.println(robot.getBatteryLevel());
			checkStop();
			wallFollow();
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
	 * Following the left wall.
	 * 
	 * @throws Exception
	 */
	public void wallFollow() throws Exception {
		// If the robot is inside a room and there's no wall on its left, 
		// we need to move it to the wall on its left side and turn right.
		if (canSeeExit(Direction.FORWARD)) {
			checkStop();
			move(1, false);
		}
		else if (needToMove) {
			needToMove = false;
			checkStop();
			move(1,false);
		}
		else if (robot.isInsideRoom() && distanceToObstacleNot0(Direction.LEFT) && !initialPositionNotInRoom) {
			checkStop();
			//rotate(Turn.LEFT);
			if (distanceToObstacle0(Direction.RIGHT)) {
				checkStop();
				rotate(Turn.AROUND,false);
			}
			else if (distanceToObstacleNot0(Direction.FORWARD)) {
					checkStop();
					move(1, false);
			}
			else if (distanceToObstacle0(Direction.FORWARD)) {
				checkStop();
				rotate(Turn.RIGHT);
			}
		}
		else {
			initialPositionNotInRoom = true;
			if (distanceToObstacle0(Direction.LEFT)
					&& distanceToObstacle0(Direction.FORWARD)
					&& distanceToObstacle0(Direction.RIGHT)) {
				checkStop();
				rotate(Turn.AROUND,false);
			}
			else if (distanceToObstacle0(Direction.LEFT)
					&& distanceToObstacle0(Direction.FORWARD)) {
				checkStop();
				rotate(Turn.RIGHT,false);
			}
			else if (distanceToObstacleNot0(Direction.LEFT)) {
				checkStop();
				rotate(Turn.LEFT,false);
			}
			else {
				checkStop();
				move(1, false);
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
