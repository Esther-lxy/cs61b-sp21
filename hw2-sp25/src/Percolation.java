import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.*;


public class Percolation {
    // TODO: Add any necessary instance variables.
    int openSites;
    int side;
    Site[] grid;
    WeightedQuickUnionUF Sets;
    boolean isPercolate;

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
    private int[] getCoordinates(int SetIndex) {
        int coordinates[] = new int[2];
        int row = SetIndex / side;
        int column = SetIndex % side;
        coordinates[0] = row;
        coordinates[1] = column;
        return coordinates;
    }

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

}
