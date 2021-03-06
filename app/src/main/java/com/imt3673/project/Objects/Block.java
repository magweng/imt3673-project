package com.imt3673.project.Objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.imt3673.project.utils.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * A rectangular block
 * The position is "baked" into the rectangle.
 */
public class Block extends GameObject{
    //Types of blocks, clear means no block
    //Made types color, so that its compatibly with the bitmaps
    public static final int TYPE_CLEAR = Color.WHITE;
    public static final int TYPE_OBSTACLE = Color.BLACK;
    public static final int TYPE_GOAL = Color.GREEN;
    public static final int TYPE_SPAWN = Color.BLUE;
    public static final int TYPE_HOLE = Color.CYAN;
    public static final int TYPE_BREAKABLE = Color.RED;
    public static final Map<Integer, Integer> TYPE_VALUES;
    static
    {
        TYPE_VALUES = new HashMap<>();
        TYPE_VALUES.put(TYPE_CLEAR, 0);
        TYPE_VALUES.put(TYPE_OBSTACLE, 1);
        TYPE_VALUES.put(TYPE_BREAKABLE, 2);
        TYPE_VALUES.put(TYPE_HOLE, 3);
        TYPE_VALUES.put(TYPE_GOAL, 4);
        TYPE_VALUES.put(TYPE_SPAWN, -1);
    }


    protected int type;
    protected RectF rectangle;

    /**
     * Creates a boundry box
     * @param width of box
     * @param height of box
     */
    public Block(Vector2 position, float width, float height, int type){
        this.position = position;
        this.type = type;

        rectangle = new RectF(position.x, position.y, position.x + width, position.y + height);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Gets the rectangle
     * @return RectF rectangle
     */
    public RectF getRectangle(){
        return rectangle;
    }

    /**
     * Returns the center of the block
     * @return Vector2 center
     */
    public Vector2 getCenter() {
        return new Vector2(rectangle.centerX(), rectangle.centerY());
    }

    /**
     * Returns the type of the block
     * @return int type
     */
    public int getType(){
        return type;
    }

    @Override
    public void draw(Canvas canvas, Vector2 cameraPosition){
        Matrix m = new Matrix();
        m.postScale(Level.getPixelSize()/bitmap.getScaledWidth(canvas), Level.getPixelSize()/bitmap.getScaledWidth(canvas));
        m.postTranslate(-cameraPosition.x, -cameraPosition.y);
        shader.setLocalMatrix(m);

        canvas.drawRect(Vector2.subtract(cameraPosition, rectangle), paint);
    }
}
