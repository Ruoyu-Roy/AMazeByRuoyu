package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

//import generation.CardinalDirection;
//import generation.MazeConfiguration;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Direction;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;

/**
 * Wizard Algorithm: as there is little magic in algorithms, the wizard is in fact a cheater who uses the
 * information of the distance object (handed to it via setDistance method) to find the exit by
 * looking for a neighbor that is closer to the exit. (Hint: you find similar functionality
 * implemented in the MazeContainer.getNeighborCloserToExit() to support drawing the yellow
 * line that shows the path to the exit).The wizard is intended to work as a baseline algorithm
 * to see how the most efficient algorithm can perform in terms of energy consumption and
 * path length.
 * 
 * Wizard Algorithm extends from ManualDirver using the interface of RobotDriver
 * 
 * Collaborators: A. A controller that holds a maze to explore B. A Robot that record all the information and hold all the moves.
 * 
 * @author ruoyuli
 *
 */

public class Wizard extends ManualDriver implements RobotDriver{
	//Basic variables
	protected MazeConfiguration mazeConfig;
	
	//Constructor
	public Wizard() {
		
	}

	/**
	 * Drives the robot towards the exit given it exists and 
	 * given the robot's energy supply lasts long enough. 
	 * @return true if driver successfully reaches the exit, false otherwise
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		// TODO Auto-generated method stub
		while (!robot.isAtExit()) {
			checkStop();
			CardinalDirection direction = robot.getCurrentDirection();
			if (direction != findNext()) {
				findMove();
			}
			checkStop();
			robot.move(1, false);
		}
		checkStop();
		if (robot.canSeeExit(Direction.LEFT)) {
			checkStop();
			robot.rotate(Turn.LEFT,false);
		}
		else if (robot.canSeeExit(Direction.RIGHT)) {
			checkStop();
			robot.rotate(Turn.RIGHT,false);
		}
		checkStop();
		this.robot.move(1, false);
		checkStop();
		return true;
	}
	
	/**
	 * Find the direction corresponding to the next cell close to exit.
	 * 
	 * @return CardinalDirection
	 * @throws Exception
	 */
	public CardinalDirection findNext() throws Exception {
		int [] curPosition = robot.getCurrentPosition();
		int x = curPosition[0];
		int y = curPosition[1];
		int [] nextPosition = robot.getMaze().getMazeConfiguration().getNeighborCloserToExit(x, y);
		int nextX = nextPosition[0];
		int nextY = nextPosition[1];
		if (nextX - x == 0) {
			if (nextY - y == 1) {
				return CardinalDirection.South;
			}
			else if (nextY - y == -1) {
				return CardinalDirection.North;
			}
		}
		else if (nextY - y == 0) {
			if (nextX - x == 1) {
				return CardinalDirection.East;
			}
			else if (nextX - x == -1) {
				return CardinalDirection.West;
			}
		}
		return null;
	}
	
	/**
	 * Rotating based on the information given by findNext().
	 * 
	 * @throws Exception
	 */
	public void findMove() throws Exception {
		if (robot.getCurrentDirection() == CardinalDirection.East) {
			if (findNext() == CardinalDirection.North) {
				robot.rotate(Turn.RIGHT,false);
			}
			else if (findNext() == CardinalDirection.South) {
				robot.rotate(Turn.LEFT,false);
			}
			else if (findNext() == CardinalDirection.West) {
				robot.rotate(Turn.AROUND,false);
			}
		}
		else if (robot.getCurrentDirection() == CardinalDirection.West) {
			if (findNext() == CardinalDirection.North) {
				robot.rotate(Turn.LEFT,false);
			}
			else if (findNext() == CardinalDirection.South) {
				robot.rotate(Turn.RIGHT,false);
			}
			else if (findNext() == CardinalDirection.East) {
				robot.rotate(Turn.AROUND,false);
			}
		}
		else if (robot.getCurrentDirection() == CardinalDirection.North) {
			if (findNext() == CardinalDirection.East) {
				robot.rotate(Turn.LEFT,false);
			}
			else if (findNext() == CardinalDirection.West) {
				robot.rotate(Turn.RIGHT,false);
			}
			else if (findNext() == CardinalDirection.South) {
				robot.rotate(Turn.AROUND,false);
			}
		}
		else if (robot.getCurrentDirection() == CardinalDirection.South) {
			if (findNext() == CardinalDirection.East) {
				robot.rotate(Turn.RIGHT,false);
			}
			else if (findNext() == CardinalDirection.West) {
				robot.rotate(Turn.LEFT,false);
			}
			else if (findNext() == CardinalDirection.North) {
				robot.rotate(Turn.AROUND,false);
			}
		}
	}
	
	/*public void setMazeConfig() {
		mazeConfig = robot.getMaze().getMazeConfiguration();
	}*/
	
	public MazeConfiguration getMazeConfig() {
		return mazeConfig;
	}
	
	/**
	 * Check if the robot is stopped. If it is, throw exception.
	 * @throws Exception
	 */
	private void checkStop() throws Exception {
		if (this.robot.hasStopped()) {
			robot.getMaze().toEnd(false,false);
			throw new Exception();
		}
	}
}
