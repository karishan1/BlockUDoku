package blocks;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.PixelLoc;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Piece;

public class Controller extends MouseAdapter {
    GameView view;
    ModelInterface model;
    Palette palette;
    JFrame frame;
    Sprite selectedSprite = null;
    Piece ghostShape = null;
    String title = "Blocks Puzzle";
    boolean gameOver = false;

    public Controller(GameView view, ModelInterface model, Palette palette, JFrame frame) {
        this.view = view;
        this.model = model;
        this.palette = palette;
        this.frame = frame;
        frame.setTitle(title);
        // force palette to do a layout
        palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.paletteCellSize);
        System.out.println("Palette layout done : " + palette.sprites);
    }

    public void mousePressed(MouseEvent e) {

        PixelLoc loc = new PixelLoc(e.getX(), e.getY());
        selectedSprite = palette.getSprite(loc, view.paletteCellSize);
        if (selectedSprite == null) {
            return;
        }
        selectedSprite.state = SpriteState.IN_PLAY;
        view.repaint();
    }

    public void mouseDragged(MouseEvent e) {

        if (selectedSprite == null) {
            return;
        }
        selectedSprite.state = SpriteState.IN_PLAY;
        selectedSprite.px = e.getX();
        selectedSprite.py = e.getY();

        ghostShape = selectedSprite.snapToGrid(view.margin, view.cellSize);
        view.setGhostShape(ghostShape);
        view.setPoppableRegions(ghostShape);

        view.repaint();

        }

    public void mouseReleased(MouseEvent e) {

        if (selectedSprite == null) {
            return;
        }

        Piece snappedPiece = selectedSprite.snapToGrid(view.margin, view.cellSize);

        if (model.canPlace(snappedPiece)) {
            model.place(snappedPiece);
            selectedSprite.state = SpriteState.PLACED;

            if (palette.getShapesToPlace().isEmpty()) {
                palette.replenish();
            }
        } else {
            selectedSprite.state = SpriteState.IN_PALETTE;

            palette.doLayout(view.margin, view.margin + ModelInterface.height * view.cellSize, view.paletteCellSize);
        }
        gameOver = model.isGameOver(palette.getShapesToPlace());

        view.poppableRegions = null;
        view.setGhostShape(null);
        selectedSprite = null;
        frame.setTitle(getTitle());
        view.repaint();


    }

    private String getTitle() {
        String title = this.title + " Score: " + model.getScore();
        if (gameOver) {
            title += " Game Over!";
        }
        return title;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModelInterface model = new ModelSet();
        //ModelInterface model = new Model2dArray();
        Palette palette = new Palette();
        GameView view = new GameView(model, palette);
        Controller controller = new Controller(view, model, palette, frame);
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);
        frame.add(view);
        frame.pack();
        frame.setVisible(true);
    }
}
