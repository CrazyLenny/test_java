
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
/**
 * This class tests an N by N grid to see if there are enough open
 * locations to connect the top of the grid with the bottom of the
 * grid. It uses an two dimensional array of integers to represent
 * the grid, and an instructor supplied WeightedQuickUnionUF object
 * to keep track of the connections between open sites in the grid.
 *
 * The percolates() method returns true if a path can be found from a
 * site at the top to the bottom. These two sites are are not actually
 * represented in the grid, although they are represented in the 
 * union-find object at locations 0 and N*N + 2. They are "located"  
 * above and below the grid, and each connects to all the sites 
 * immediately above or below it. This simplifies the pathfinding
 * process.
 *
 * This class was written by Ken Austin in partial fulfillment of the
 * requirements for the Coursera online course, Introduction to Algorithms.
 *
 * Dependencies: WeightedQuickUnionUF.java, StdRandom.java,
 */
 
public class Percolation {
 
    private static final int FULL = 0;     // Makes it easier to keep track of the
    private static final int OPEN = 1;     // status of the sites in the grid.
 
    private int N;          // Dimensions of the N by N grid used in the simulation.
    private int[][] grid;   // Keeps track of which sites are open or full.
                            // Begins with default initialization: FULL (0)
                             
    private WeightedQuickUnionUF ufHelper; // Class supplied by course instructor.
                                           // We use union(), find(), connected(),
                                           // and count() in this program.
     
    /**
     * Constructor initializes two dimensional array of integers
     * with default values: all sites set to CLOSED (0).
     *
     * The WeightedQuickUnionUF object is initialized to a size that will
     * contain one element for each site in the grid (N*N), plus two more
     * to represent "imaginary" sites at the top and bottom of the grid
     * that will make it easier to test the grid for percolation.
     *
     * @param n the number of rows and column in the grid
     */
    public Percolation(int n) {
        N = n;
        grid = new int[N][N];
        ufHelper = new WeightedQuickUnionUF((N * N) + 2);
    } // end constructor
 
    /**
     * Open site (row i, column j) by setting site to OPEN
     * and calling the union method in the union-find object
     * for any open adjacent sites.
     *
     * @param i the integer representing the site location on the x axis
     * @param j the integer representing the site location on the y axis
     * @throws  IndexOutOfBoundsException if the values for i and j are 
     *          off the grid
     */
    public void open(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N)
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        grid[row][col] = OPEN;
        if (row == 0) { // If it's on the top row, connect to imaginary site at 0.
            ufHelper.union(0, xyTo1D(row, col));
        }
        if (row == N - 1) { // If it's on the bottom row, connect to imaginary
                            // site at (N*N) + 1.
            ufHelper.union((N*N) + 1, xyTo1D(row, col));
        }
        if ((row + 1) < N) { // Make sure we don't fall off the grid
            if (grid[row+1][col] == OPEN)
                ufHelper.union(xyTo1D(row, col), xyTo1D(row+1, col));
        }
        if ((row - 1) >= 0) { // Make sure we don't fall off the grid
            if (grid[row-1][col] == OPEN)
                ufHelper.union(xyTo1D(row, col), xyTo1D(row-1, col));
        }
        if ((col + 1) < N) { // Make sure we don't fall off the grid
            if (grid[row][col+1] == OPEN)
                ufHelper.union(xyTo1D(row, col), xyTo1D(row, col+1));
        }
        if ((col - 1) >= 0) { // Make sure we don't fall off the grid
            if (grid[row][col-1] == OPEN)
                ufHelper.union(xyTo1D(row, col), xyTo1D(row, col-1));
        }
    } // end open()
     
    /**
     * Is site (row i, column j) open?
     *
     * @param i the integer representing the site location on the x axis
     * @param j the integer representing the site location on the y axis
     * @return  true if the site at (i, j) is OPEN
     * @throws  IndexOutOfBoundsException if the values for i and j are 
     *          off the grid
     */
    public boolean isOpen(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N)
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        return grid[row][col] == OPEN;
    } // end isOpen()
     
    /**
     * Is site (row i, column j) full?
     *
     * @param i the integer representing the site location on the x axis
     * @param j the integer representing the site location on the y axis
     * @return  true if the site at (i, j) is FULL
     * @throws  IndexOutOfBoundsException if the values for i and j are 
     *          off the grid
     */
    public boolean isFull(int i, int j) {
        int row = i - 1;
        int col = j - 1;
        if (row < 0 || row > N || col < 0 || col > N)
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        return grid[row][col] == FULL;
    } // end isFull()
     
    /**
     * Does the system percolate? Checks to see if the imaginary site
     * at location 0 in the union-find object is in the same set as 
     * the imaginary site at location N*N+1 in the union-find object.
     * These two sites are imaginary; they are not actually represented
     * in the grid, although they are represented in the union-find
     * object. They are "located" at the top and the bottom of the 
     * grid, and each connects to all the sites above/below it.
     *
     * @return true if open path from the bottom of the grid to the top exists
     */
    public boolean percolates() {
        return ufHelper.connected(0, (N*N)+1);
    } // end percolates()
     
    /**
     * Converts 2D point to 1D array location for the union-find object
     * to use.
     *
     * @param i the integer representing the site location on the x axis
     * @param j the integer representing the site location on the y axis
     * @return  the location in the union-find object where the site
     *          should be stored
     * @throws  IndexOutOfBoundsException if the values for i and j are 
     *          off the grid
     */
    private int xyTo1D(int i, int j) {
        if (i < 0 || i > N || j < 0 || j > N)
            throw new IndexOutOfBoundsException("Illegal parameter value.");
        return ((i*N) + j) + 1;  // The +1 compensates for the site at the
                                 // beginning used to facilitate the 
                                 // percolation algorithm. (There is a
                                 // corresponding single site added to the
                                 // end that does not affect this
                                 // calculation.)
    }
     
    private int count() {        // Convenience method. Returns the number of
        return ufHelper.count(); // groups in the union find object.
    }
    private int getN() {         // Convenience method. Returns N.
        return N;
    }
     
    public static void main(String[] args) {
        final int TESTS = 1000;
        final int GRID_SIZE = 20;
         
        System.out.println("\n***Percolation: Monte Carlo Simulation ***\n");
         
        Percolation perc = new Percolation(GRID_SIZE);
        System.out.println("Successfully created Percolation object.");
        System.out.println("N: " + perc.getN());
        System.out.println();
         
        System.out.println("Starting to open random sites...");
        int row, col, ct;
        double sum = 0.0;
        for (int i = 0; i < TESTS; i++) {
            ct = 0;
            perc = new Percolation(GRID_SIZE);
            while (!perc.percolates()) {
                row = StdRandom.uniform(perc.getN()) + 1;
                col = StdRandom.uniform(perc.getN()) + 1;
                if (perc.isFull(row, col)) {
                    perc.open(row, col);
                    ct++;
                }
                // System.out.println("Count: " + ct);
            }
            sum += ct;
        }
        System.out.println("After " + TESTS + " attempts, the average number of sites");
        System.out.printf("opened was %.2f", sum/TESTS);
        System.out.printf(" or %.2f", ((sum/TESTS)/(GRID_SIZE * GRID_SIZE)) * 100);
        System.out.println("%.");
        System.out.println("\nDone.\n");
    }
}
