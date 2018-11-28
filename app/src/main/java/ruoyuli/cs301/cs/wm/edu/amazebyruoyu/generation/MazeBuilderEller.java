package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import java.util.ArrayList;

/**
 * This is a maze builder that use Eller's algorithm to build
 * 
 * @author ruoyuli
 */

public class MazeBuilderEller extends MazeBuilder implements Runnable{
	
	private int [][] cellSet;
	
	/**
	 *  Set up the constructor
	 */
	public MazeBuilderEller() {
		super();
		System.out.println("MazeBuilderEller uses Prim's algorithm to generate maze.");
	}
	
	public MazeBuilderEller(boolean det) {
		super(det);
		System.out.println("MazeBuilderEller uses Prim's algorithm to generate maze.");
	}
	
	/**
	 * This method generates pathways into the maze by using Eller's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wall separating these cells in the maze.
	 */
	@Override
	protected void generatePathways() {
		// Set all needed variables
		cellSet = new int[height][width];
		int setNumber = 1;
		int rolIter = 0;
		int colIter = 0;
		
		// Initialize all the set number as 0 which means they are not in a set
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				cellSet[row][col] = 0;
			}
		}
		
		// Iterate the maze row by row
		for (rolIter = 0; rolIter < height; rolIter++) {
			// Set the first row
			if (rolIter == 0) {
				for (int col = 0; col < width; col++) 
					cellSet[0][col] = setNumber++;
			}
			// Set the rest rows except the last one
			else {
				for (colIter = 0; colIter < width; colIter++) {
					if (cells.hasWall(colIter, rolIter, CardinalDirection.North))
						cellSet[rolIter][colIter] = setNumber++;
					else
						cellSet[rolIter][colIter] = cellSet[rolIter-1][colIter];
				}
			}
			
			// Randomly delete the right-side walls for every row.
			for (colIter = 0; colIter < width-1; colIter++) {
				Wall wall = new Wall(colIter, rolIter, CardinalDirection.East);
				// First to n-1 rows
				if (rolIter != height - 1) {
					// if two neighbored cells are in different set and No boarder in between
					if (cellSet[rolIter][colIter] != cellSet[rolIter][colIter+1]
							&& cells.canGo(wall)) {
						int randomNum = random.nextIntWithinInterval(0, 1);
						if (randomNum == 1) {
							cells.deleteWall(wall);
							for (int j = 0; j <= rolIter; j++) {
								for (int i = 0; i < width; i++){
									if (cellSet[j][i] == cellSet[rolIter][colIter+1])
										cellSet[j][i] = cellSet[rolIter][colIter];
								}
							}
						}
					}
				}
				// Last row
				if (rolIter == height - 1) {
					// Join all adjacent cells that do not share a set, and omit the vertical connections
					if (cellSet[rolIter][colIter] != cellSet[rolIter][colIter+1]) {
						cells.deleteWall(wall);
						for (int j = 0; j <= rolIter; j++) {
							for (int i = 0; i < width; i++){
								if (cellSet[j][i] == cellSet[rolIter][colIter+1])
									cellSet[j][i] = cellSet[rolIter][colIter];
							}
						}
					}
				}
			}
			// SET UP VERTICAL CONNECTION
			for (colIter = 0; colIter < width; colIter++) {
				Wall wall = new Wall(colIter, rolIter, CardinalDirection.South);
				if (rolIter != height - 1) {
					int col = colIter;
					if (colIter != 0) {
						if (cellSet[rolIter][colIter] == cellSet[rolIter][colIter-1]) {
							continue;
						}
					}
					int wallDeleted = 0; // Record the number of walls deleted
					ArrayList<Integer> set = new ArrayList<>(); // Record the cell in the same set
					while (col < width) {
						if (cellSet[rolIter][colIter] == cellSet[rolIter][col]) {
							set.add(col);
							wall.setWall(col, rolIter, CardinalDirection.South);
							int randomNum = random.nextIntWithinInterval(0, 1);
							if (randomNum == 1 && cells.canGo(wall)) {
								cells.deleteWall(wall);
								cellSet[rolIter+1][col] = cellSet[rolIter][col];
								wallDeleted++;
							}
						}
						col++;
					}
					col--;
					if (wallDeleted == 0) { // If no wall has been deleted from this set
						int randomN = random.nextIntWithinInterval(0, set.size()-1);
						wall.setWall(set.get(randomN), rolIter, CardinalDirection.South);
						cells.deleteWall(wall);
						cellSet[rolIter+1][set.get(randomN)] = cellSet[rolIter][set.get(randomN)];
					}
				}
				else if (rolIter == height-1) {
					break;
				}
			}
		}
	}
}
