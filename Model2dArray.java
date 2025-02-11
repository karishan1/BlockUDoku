package blocks;

/**
 * Logical model for the Blocks Puzzle
 * This handles the game logic, such as the grid, the pieces, and the rules for
 * placing pieces and removing lines and subgrids.
 * <p>
 * Note this has no dependencies on the UI or the game view, and no
 * concept of pixel-space or screen coordinates.
 * <p>
 * The standard block puzzle is on a 9x9 grid, so all placeable shapes will have
 * cells in that range.
 */

import blocks.BlockShapes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model2dArray extends State2dArray implements ModelInterface {
    List<Shape> regions = new RegionHelper().allRegions();
    Set<Cell> occupiedCells = new HashSet<>();

    public Model2dArray() {
        grid = new boolean[width][height]; // 2D Boolean Array
        // initially all cells are empty (false) - they would be by default anyway
        // but this makes it explicit
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = false;
            }
        }
    }

    public int getScore() {
        return score;
    }


    // interestingly, for canPlace we could also use sets to store the occupied cells
    // and then check if the shape's cells intersect with the occupied cells

    public boolean canPlace(Piece piece) {
        List<Cell> pieceCells = piece.cells(); // holds cells occupied by piece
        for (Cell cell : pieceCells) {
            int x = cell.x(); // absolute positions
            int y = cell.y();

            if (x < 0 || x >= width || y < 0 || y >= height) {
                return false; // out of bounds
            }


            if (occupiedCells.contains(cell)) {
                return false; // cell is occupied
            }
        }
        return true;
    }

    @Override
    public void place(Piece piece) {
        // todo: implement
        List<Cell> pieceCells = piece.cells();
        for (Cell cell : pieceCells) {
            int x = cell.x();
            int y = cell.y();

            // marks cell as occupied
            occupiedCells.add(cell);
            grid[x][y] = true;

        }

        processPlacement(piece);

    }

    public void processPlacement(Piece piece) {
        // checks for the poppable regions after placing the piece on the grid

        List<Shape> poppableRegions = getPoppableRegions(piece);
        for (Shape region : poppableRegions) {
            remove(region);
            score += 50; // Increment the score for each region removed
        }
    }

    @Override
    public void remove(Shape region) {

        for (Cell cell : region) {
            int x = cell.x();
            int y = cell.y();

            occupiedCells.remove(cell);

            grid[x][y] = false;
        }

    }

    public boolean isComplete(Shape region) {
        // check if the shape is complete, i.e. all cells are occupied
        for (Cell cell : region) {
            int x = cell.x();
            int y = cell.y();

            if (!grid[x][y]){
                return false;
            }
        }

        return true;
    }

    private boolean wouldBeComplete(Shape region, List<Cell> toAdd) {
        // check if the shape is complete, i.e. all cells are occupied
        for (Cell cell : region) {
            int x = cell.x();
            int y = cell.y();

            if (!grid[x][y] && !toAdd.contains(cell)){
                return false;
            }
        }
        return true;


    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // if any shape in the palette can be placed, the game is not over

        for (Shape shape : palettePieces){
            if (canPlaceAnywhere(shape)){
                return false;
            }
        }
        return true;
    }

    public boolean canPlaceAnywhere(Shape shape) {
        // check if the shape can be placed anywhere on the grid
        // by checking if it can be placed at any loc
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Piece piece = new Piece(shape, new Cell(x, y));
                if (canPlace(piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Shape> getPoppableRegions(Piece piece) {
        List<Shape> poppable = new ArrayList<>();
        for (Shape region : regions) {
            if (wouldBeComplete(region, piece.cells())) {
                poppable.add(region);
            }
        }
        return poppable;
    }




    @Override
    public Set<Cell> getOccupiedCells() {

        return occupiedCells;
    }
}

