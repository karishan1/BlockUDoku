package blocks;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

import blocks.BlockShapes.Piece;
import blocks.BlockShapes.Shape;
import blocks.BlockShapes.Cell;
import blocks.BlockShapes.ShapeSet;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Sprite;


public class GameView extends JComponent {
    ModelInterface model;
    Palette palette;
    int margin = 5;
    int shapeRegionHeight;
    int cellSize = 40;
    int paletteCellSize = 20;
    int shrinkSize = 30;
    Piece ghostShape = null;
    List<Shape> poppableRegions = null;

    public GameView(ModelInterface model, Palette palette) {
        this.model = model;
        this.palette = palette;
        this.shapeRegionHeight = cellSize * ModelInterface.height / 2;
    }

    private void paintShapePalette(Graphics g, int cellSize) {
        // paint a background color

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(margin, margin + ModelInterface.height * cellSize, ModelInterface.width * cellSize, shapeRegionHeight);


        for (Sprite sprite : palette.getSprites()) {
            if (sprite.state == SpriteState.IN_PALETTE) {
                for (Cell cell : sprite.shape){
                    int px =  sprite.px + (cell.x() * paletteCellSize);
                    int py =  sprite.py + (cell.y() * paletteCellSize);
                    g.setColor(Color.MAGENTA);
                    g.fill3DRect(px,py,paletteCellSize,paletteCellSize,true);
                }
            } else if (sprite.state == SpriteState.IN_PLAY) {
                g.setColor(Color.BLUE);
            }


        }
    }


    private void paintPoppableRegions(Graphics g, int cellSize) {
        g.setColor(new Color(0, 13, 255, 100));
        if (poppableRegions!=null){
            for (Shape shape : poppableRegions){
                for (Cell cell : shape){
                    g.fillRect(margin + cell.x() * cellSize, margin + cell.y() * cellSize, cellSize, cellSize);
                }
            }
        }

    }

    public void setPoppableRegions(Piece region){
        if (!model.canPlace(ghostShape)){
            return;
        }
        this.poppableRegions = model.getPoppableRegions(region);
        repaint();
    }


    private void paintGhostShape(Graphics g, int cellSize) {
        if (ghostShape == null){
            return;
        }
        if (!model.canPlace(ghostShape)){
            return;
        }

        for (Cell cell : ghostShape.cells()){
            int xPos = margin + cell.x() * cellSize;
            int yPos = margin + cell.y() * cellSize;
            g.setColor(new Color(0, 255, 255, 100));
            g.fillRect(xPos, yPos, cellSize, cellSize);
            g.setColor(new Color(0, 0, 255, 100));

            g.fillRect(xPos + 5, yPos + 5, shrinkSize, shrinkSize);  // Paint each cell

        }
    }

    public void setGhostShape(Piece ghostShape){

        this.ghostShape = ghostShape;
        repaint();
    }

    private void paintGrid(Graphics g) {
        int x0 = margin;
        int y0 = margin;
        int width = ModelInterface.width * cellSize;
        int height = ModelInterface.height * cellSize;
        Set<Cell> occupiedCells = model.getOccupiedCells();
        g.setColor(Color.BLACK);
        g.drawRect(x0, y0, width, height);
        for (int x = 0; x < ModelInterface.width; x++) {
            for (int y = 0; y < ModelInterface.height; y++) {
                Cell cell = new Cell(x,y);
                if (occupiedCells.contains(cell)){
                    g.setColor(Color.GREEN);
                    g.fill3DRect(x0 + x * cellSize, y0 + y * cellSize, cellSize, cellSize, true);

                }
                else{
                    g.setColor(Color.WHITE);
                    g.fill3DRect(x0 + x * cellSize, y0 + y * cellSize, cellSize, cellSize, true);
                }


            }
        }
        repaint();
    }

    private void paintMiniGrids(Graphics2D g) {
        // for now we're going to do this based on the cellSize multiple
        int s = ModelInterface.subSize;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        for (int x = 0; x < ModelInterface.width; x += s) {
            for (int y = 0; y < ModelInterface.height; y += s) {
                g.drawRect(margin + x * cellSize, margin + y * cellSize, s * cellSize, s * cellSize);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintGrid(g);
        paintMiniGrids((Graphics2D) g); // cosmetic
        paintGhostShape(g, cellSize);
        paintPoppableRegions(g, cellSize);
        paintShapePalette(g, cellSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                ModelInterface.width * cellSize + 2 * margin,
                ModelInterface.height * cellSize + 2 * margin + shapeRegionHeight
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clean Blocks");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
        Shape shape = new ShapeSet().getShapes().get(0);
        Piece piece = new Piece(shape, new Cell(0, 0));
        Palette palette = new Palette();
        model.place(piece);
        frame.add(new GameView(model, palette));
        frame.pack();
        frame.setVisible(true);
    }
}
