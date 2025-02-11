package blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import blocks.BlockShapes.Shape;
import blocks.BlockShapes.ShapeSet;
import blocks.BlockShapes.SpriteState;
import blocks.BlockShapes.Sprite;
import blocks.BlockShapes.PixelLoc;

public class Palette {
        ArrayList<Shape> shapes = new ArrayList<>();
    List<Sprite> sprites;
        int nShapes = 3;

    public Palette() {
        shapes.addAll(new ShapeSet().getShapes());
        sprites = new ArrayList<>();
        replenish();
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public ArrayList<Shape> getShapesToPlace() {

        return sprites.stream()
                .filter(sprite -> sprite.state == SpriteState.IN_PALETTE)
                .map(sprite -> sprite.shape)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    public Sprite getSprite(PixelLoc mousePoint, int cellSize) {
        // todo: implement
        for (Sprite sprite : sprites) {
            if (sprite.contains(mousePoint, cellSize)) {
                return sprite;
            }
        }
        return null;
    }

    private int nReadyPieces() {
        int count = 0;
        for (Sprite sprite : sprites) {
            if (sprite.state == SpriteState.IN_PALETTE || sprite.state == SpriteState.IN_PLAY) {
                count++;
            }
        }
        System.out.println("nReadyPieces: " + count);
        return count;
    }

    public void doLayout(int x0, int y0, int cellSize) {
        // todo: implement
        final int spacing = 60;
        int xPos = x0;
        int yPos = y0;

        for (Sprite sprite : sprites) {
            sprite.px = xPos;
            sprite.py = yPos;


            xPos += (3 * cellSize) + spacing;

        }
    }

    public void replenish() {
        // todo: implement
        if (nReadyPieces() > 0) {
            return;
        }
        sprites.clear();

        for (int i = 0; i < nShapes; i++){
            int r = (int) (Math.random() * shapes.size());
            Shape shape = shapes.get(r);

            Sprite sprite = new Sprite(shape, 0, 0);
            sprite.state = SpriteState.IN_PALETTE;

            sprites.add(sprite);
        }
        doLayout(5, 5 + ModelInterface.height * 40, 20);

        System.out.println("Replenished: " + sprites);

    }

    public static void main(String[] args) {    
        Palette palette = new Palette();
        System.out.println(palette.shapes);
        System.out.println(palette.sprites);
        palette.doLayout(0, 0, 20);
        System.out.println(palette.sprites);
    }
}
