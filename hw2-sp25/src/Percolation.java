import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.*;

public class Percolation {
    private int openSites;
    private int side;
    private Site[] grid;
    private WeightedQuickUnionUF PercolateSets;
    private WeightedQuickUnionUF isFullSets;
    private int virtualTop;
    private int virtualBottom;

    private class Site {
        boolean isOpen;

        public Site() {
            isOpen = false;
        }
    }

    public Percolation(int N) {
        // TODO: Fill in this constructor.
        openSites = 0;
        side = N;
        virtualTop = N * N;
        virtualBottom = N * N + 1;
        grid = new Site[N*N];
        for (int i = 0; i < N*N; i++) {
            grid[i]= new Site();
        }
        PercolateSets = new WeightedQuickUnionUF(N*N + 2);
        isFullSets= new WeightedQuickUnionUF(N*N + 1);
    }

    public void open(int row, int col) {
        // TODO: Fill in this method.
        int index = getGridIndex(row, col);
        if (grid[index].isOpen) {
            return;
        }
        grid[index].isOpen = true;
        openSites++;
        if (row == 0) {
            PercolateSets.union(virtualTop, index);
            isFullSets.union(virtualTop, index);
        }
        if (row == side - 1) {
            PercolateSets.union(virtualBottom, index);
        }
        List<Integer> neighbors = Neighbors(row, col);
        for (int n : neighbors) {
            if (grid[n].isOpen) {
                PercolateSets.union(n, index);
                isFullSets.union(n, index);
            }
        }
    }


    public boolean isOpen(int row, int col) {
        // TODO: Fill in this method.
        int index = getGridIndex(row, col);
        return grid[index].isOpen;
    }

    public boolean isFull(int row, int col) {
        // TODO: Fill in this method.
        int index = getGridIndex(row, col);
        return isFullSets.connected(virtualTop, index);
    }

    public int numberOfOpenSites() {
        // TODO: Fill in this method.
        return openSites;
    }

    public boolean percolates() {
        // TODO: Fill in this method.
        return PercolateSets.connected(virtualTop, virtualBottom);
    }

    // TODO: Add any useful helper methods (we highly recommend this!).

    private int getGridIndex(int row, int col) {
        return row * side + col;
    }

    private List<Integer> Neighbors(int row, int col) {
        List<Integer> neighbors = new ArrayList<>();
        if (row - 1 >= 0) {
            neighbors.addLast(getGridIndex(row - 1, col));
        }
        if (row + 1 < side) {
            neighbors.addLast(getGridIndex(row + 1, col));
        }
        if (col - 1 >= 0) {
            neighbors.addLast(getGridIndex(row, col - 1));
        }
        if (col + 1 < side) {
            neighbors.addLast(getGridIndex(row, col + 1));
        }
        return neighbors;
    }
    // TODO: Remove all TODO comments before submitting.










    /* Mine original solution
    // TODO: Add any necessary instance variables.
    private int openSites;
    private int side;
    private Site[] grid;
    private WeightedQuickUnionUF Sets;

    private class Site {
        boolean isFull;
        boolean isOpen;

        public Site() {
            isFull = false;
            isOpen = false;
        }
    }

    public Percolation(int N) {
        // TODO: Fill in this constructor.
        openSites = 0;
        side = N;
        grid = new Site[N*N];
        for (int i = 0; i < N*N; i++) {
            grid[i]= new Site();
        }
        Sets = new WeightedQuickUnionUF(N*N);
    }

    public void open(int row, int col) {
        // TODO: Fill in this method.
        int index = getSetIndex(row, col);
        if (grid[index].isOpen) {
            return;
        }
        grid[index].isOpen = true;
        openSites++;
        if (row == 0) {
            grid[index].isFull = true;
        }
        List<Integer> neighbors = Neighbors(row, col);
        for (int n : neighbors) {
            int rootNeighbor = Sets.find(n);
            int rootCenter =Sets.find(index);
            if (grid[rootNeighbor].isOpen) {
                Sets.union(n, index);
                int newRoot = Sets.find(index);
                grid[newRoot].isOpen = true;
                if (grid[rootNeighbor].isFull || grid[rootCenter].isFull) {
                    grid[newRoot].isFull = true;
                }
            }
        }
    }

    public boolean isOpen(int row, int col) {
        // TODO: Fill in this method.
        int index = getSetIndex(row, col);
        return grid[Sets.find(index)].isOpen;
    }

    public boolean isFull(int row, int col) {
        // TODO: Fill in this method.
        int index = getSetIndex(row, col);
        return grid[Sets.find(index)].isFull;
    }

    public int numberOfOpenSites() {
        // TODO: Fill in this method.
        return openSites;
    }

    public boolean percolates() {
        // TODO: Fill in this method.
        for (int i = 0; i < side; i++) {
            if(isFull(side - 1, i)) {
                return true;
            }
        }
        return false;
    }

    // TODO: Add any useful helper methods (we highly recommend this!).
    private int getSetIndex(int row, int col) {
        return row * side + col;
    }

    private List<Integer> Neighbors(int row, int col) {
        List<Integer> neighbors = new ArrayList<>();
        if (row - 1 >= 0) {
            neighbors.addLast(getSetIndex(row - 1, col));
        }
        if (row + 1 < side) {
            neighbors.addLast(getSetIndex(row + 1, col));
        }
        if (col - 1 >= 0) {
            neighbors.addLast(getSetIndex(row, col - 1));
        }
        if (col + 1 < side) {
            neighbors.addLast(getSetIndex(row, col + 1));
        }
        return neighbors;
    }
    // TODO: Remove all TODO comments before submitting.
    */
}
