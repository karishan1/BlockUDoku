package blocks;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;
import blocks.BlockShapes.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelSet extends StateSet implements ModelInterface {

    Set<Cell> locations = new HashSet<>();
    List<Shape> regions = new RegionHelper().allRegions();

    // we need a constructor to initialise the regions
    public ModelSet() {
        super();
        initialiseLocations();
    }
    // method implementations below ...

    public int getScore() {
        return score;
    }

    private void initialiseLocations() {
        // having all grid locations in a set is in line with the set based approach
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                locations.add(new Cell(i, j));
            }
        }
    }

    @Override
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
        // check if the shape can be placed at this loc
        return true;
    }



    @Override
    public void place(Piece piece) {

        List<Cell> pieceCells = piece.cells();
        for (Cell cell : pieceCells) {

            // marks cell as occupied
            occupiedCells.add(cell);
            locations.add(cell);

        }
        processPlacement(piece);
    }
    public void processPlacement(Piece piece) {
        // checks for the poppable regions after placing the piece on the grid

        List<Shape> poppableRegions = getPoppableRegions(piece);
        for (Shape region : poppableRegions) {
            remove(region);
            score+=50; // Increment the score for each region removed
        }
    }

    @Override
    public void remove(Shape region) {


        for (Cell cell : region) {

            occupiedCells.remove(cell);
            locations.remove(cell);
        }
    }

    @Override
    public boolean isComplete(Shape region) {
        // use a stream to check if all the cells in the region are occupied
        return region.stream().allMatch(cell -> locations.contains(cell));
    }

    @Override
    public boolean isGameOver(List<Shape> palettePieces) {
        // if any shape in the palette can be placed, the game is not over
        // use a helper function to check whether an indiviual shape can be placed anywhere
        // and
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
        // return the regions that would be popped if the piece is placed
        // to do this we need to iterate over the regions and check if the piece overlaps enough to complete it
        // i.e. we can make a new set of occupied cells and check if the region is complete
        // if it is complete, we add it to the list of regions to be popped

        List<Shape> poppable = new ArrayList<>();
        for (Shape region : regions) {
            Set<Cell> occupiedCells = new HashSet<>(getOccupiedCells());
            occupiedCells.addAll(piece.cells());

            if (occupiedCells.containsAll(region)) {
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
