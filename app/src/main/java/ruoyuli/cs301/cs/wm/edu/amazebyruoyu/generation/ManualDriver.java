package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

/**
 * The manual driver let user to take control of the robot
 *
 * @author ruoyuli
 */


//import generation.Distance;
import java.util.logging.Handler;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Constants.UserInput;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation.Robot.Turn;
import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.gui.PlayAnimationActivity;

public class ManualDriver implements RobotDriver{
	// basic varaible
	protected Robot robot;
	protected int width;
	protected int height;
	protected Distance distance;
	protected float initialBattery = 300000;
	protected int distanceTraveled;
	public PlayAnimationActivity playAnimationActivity;
	private Handler handler;




	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		// TODO Auto-generated method stub
		this.robot = r;
	}

	@Override
	public void setAnimation(PlayAnimationActivity animationActivity) {
		playAnimationActivity = animationActivity;
	}

	/**
	 * Provides the robot driver with information on the dimensions of the 2D maze
	 * measured in the number of cells in each direction.
	 * @param width of the maze
	 * @param height of the maze
	 * @precondition 0 <= width, 0 <= height of the maze.
	 */
	@Override
	public void setDimensions(int width, int height) {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
	}

	/**
	 * Provides the robot driver with information on the distance to the exit.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param distance gives the length of path from current position to the exit.
	 * @precondition null != distance, a full functional distance object for the current maze. 
	 */
	@Override
	public void setDistance(Distance distance) {
		// TODO Auto-generated method stub
		this.distance = distance;
	}

	// The manual driver is controlled by user. Thus, there's no algo.
	@Override
	public boolean drive2Exit() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public float getEnergyConsumption() {
		// TODO Auto-generated method stub
		return (this.initialBattery - robot.getBatteryLevel());
	}

	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 */
	@Override
	public int getPathLength() {
		// TODO Auto-generated method stub
		return robot.getOdometerReading();
	}

	/**
	 * The key down method needed to let user control the robot.
	 * @param key: the keyboard input
	 * @param value
	 * @return whether the input is a solid input accepted by the manual driver
	 */
	public boolean keyDown(UserInput key, int value) {
		if (key != UserInput.Left && key != UserInput.Right && key != UserInput.Up && key != UserInput.Down) {
			return false;
		}
		switch(key) {
			case Left:
				this.rotate(Turn.LEFT);
				break;
			case Right:
				this.rotate(Turn.RIGHT);
				break;
			case Up:
				this.move();
				break;
			case Down:
				this.turnAround();
				this.move();
				this.turnAround();
				break;
		}
		return true;
	}

	// Rotating the robot
	public void rotate(Robot.Turn t) {
		robot.rotate(t, true);
		System.out.println(this.getEnergyConsumption());
	}

	// Move the robot one step ahead
	public void move() {
		robot.move(1, true);
		System.out.println(this.getEnergyConsumption());
	}

	// Turn the robot around (180 degrees)
	public void turnAround() {
		robot.rotate(Robot.Turn.AROUND, true);
		System.out.println(this.getEnergyConsumption());
	}

	@Override
	public void setPause() {
	}

}
