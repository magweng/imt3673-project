package com.imt3673.project.Objects;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

import com.imt3673.project.media.TextureSet;
import com.imt3673.project.utils.Vector2;

import java.util.ArrayList;

/**
 * A level in the game, consisting of blocks
 */
public class Level {
    private static final String TAG = Level.class.getName();

    private Block background;
    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<BreakableBlock> breakableBlocks = new ArrayList<>();
    private ArrayList<Pair<RectF, ArrayList<Block>>> collisionGroups = new ArrayList<>();
    private static float pixelSize;
    private Vector2 spawnPoint;
    private TextureSet textureSet;

    /**
     * Draws all blocks in level
     * @param canvas canvas
     * @param cameraPosition camera position
     */
    public void draw(Canvas canvas, Vector2 cameraPosition){
        for(Block block : blocks){
            block.draw(canvas, cameraPosition);
        }
    }

    /**
     * Updates the level
     * @param deltaTime delta time
     */
    public void update(float deltaTime){
        for (int i = 0; i < breakableBlocks.size(); i++){
            if (breakableBlocks.get(i).update(deltaTime)){
                blocks.remove(breakableBlocks.get(i));
                breakableBlocks.remove(i);
                i--;
            }
        }
    }

    /**
     * Gets the spawn point for the level
     * @return Vector2 spawnPoint
     */
    public Vector2 getSpawnPoint(){
        return spawnPoint;
    }

    /**
     * Gets all blocks in level
     * @return ArrayList<GameObject> blocks
     */
    public ArrayList<Block> getBlocks(){
        return blocks;
    }

    /**
     * Gets the collision groups
     * @return ArrayList<Pair<RectF, ArrayList<Block>>>  collisionGroups
     */
    public ArrayList<Pair<RectF, ArrayList<Block>>> getCollisionGroups(){
        return collisionGroups;
    }

    /**
     * Size of one pixel in bitmap in world
     */
    public static float getPixelSize(){
        return pixelSize;
    }

    /**
     * Builds level from a bitmap
     * @param level bitmap to use
     * @param phoneHeight height of canvas
     */
    public void buildFromPNG(Bitmap level, int phoneHeight){
        Log.d(TAG, "BUILD LEVEL! Width: " + level.getWidth() + " Height: " + level.getHeight());
        float scaling = phoneHeight / level.getHeight();
        pixelSize = scaling;

        addBackground(new PointF(level.getWidth() * scaling, level.getHeight() * scaling));
        createCollisionGroups(level, scaling);

        for (int x = 0; x < level.getWidth(); x++){
            for (int y = 0; y < level.getHeight(); y++){
                int clr = level.getPixel(x, y);
                switch (clr) {
                    case Block.TYPE_SPAWN:
                        addSpawnPoint(level, x, y, scaling);
                        break;
                    case Block.TYPE_HOLE:
                        createRect(level, x, y, Block.TYPE_HOLE, scaling);
                        break;
                    case Block.TYPE_BREAKABLE:
                        createRect(x, y, 1, 1, clr, scaling);
                        level.setPixel(x, y, Block.TYPE_CLEAR);
                        break;
                    case Block.TYPE_GOAL:
                    case Block.TYPE_OBSTACLE:
                        createRect(level, x, y, clr, scaling);
                        break;
                }
            }
        }
    }

    /**
     * Creates all collision groups for level
     * @param level bitmap of level
     * @param scaling scaling to use
     */
    private void createCollisionGroups(Bitmap level, float scaling){
        int start = 0;
        final int collisionGroupLen = 20;
        while (start < level.getWidth()){
            RectF rect = new RectF(
                    start * scaling,
                    0,
                    (start + collisionGroupLen) * scaling,
                    level.getHeight() * scaling
            );
            Pair<RectF, ArrayList<Block>> collisionGroup = new Pair<>(rect, new ArrayList<>());
            collisionGroups.add(collisionGroup);
            start += collisionGroupLen;
        }
    }

    /**
     * Creates the biggest possible rect starting at x,y of type type
     * @param level bitmap to use
     * @param x x coordinate of rect start
     * @param y y coordinate of rect start
     * @param type type of the block
     * @param scaling scaling to apply to rect
     */
    private void createRect(Bitmap level, int x, int y, int type, float scaling){
        int startX = x;
        int startY = y;
        int w = 0;
        int h = 0;
        int clr = type;
        while (clr == type && startX + w < level.getWidth()){ //Scan width of rect
            w++;
            if (startX + w >= level.getWidth()){
                break;
            }
            clr = level.getPixel(startX + w, startY);
        }

        boolean fullRow = true;
        while (fullRow) { //Scan height of rect
            h++;

            if (startY + h >= level.getHeight()){
                break;
            }

            for (x = startX; x < startX + w; x++){
                if (level.getPixel(x, startY + h) != type){
                    fullRow = false;
                }
            }
        }

        for (x = startX; x < startX + w; x++){ //Clear pixels of rectangle from bitmap
            for (y = startY; y < startY + h; y++){
                level.setPixel(x, y, Block.TYPE_CLEAR);
            }
        }

        createRect(startX, startY, w, h, type, scaling);
    }

    /**
     * Creates a rect
     * @param x x pos
     * @param y y pos
     * @param w width
     * @param h height
     * @param type type of block
     * @param scaling scaling to use
     */
    private void createRect(int x, int y, int w, int h, int type, float scaling){
        Vector2 pos = new Vector2(x * scaling, y * scaling);
        Block block;
        if (type == Block.TYPE_BREAKABLE) {
            block = new BreakableBlock(pos, w * scaling, h * scaling, type, textureSet);
            breakableBlocks.add((BreakableBlock)block);
        } else {
            block = new Block(pos, w * scaling, h * scaling, type);
        }

        addBlockTexture(block, type, x, y);
        blocks.add(block);
        for (Pair<RectF, ArrayList<Block>> collisionGroup : collisionGroups){
            if (RectF.intersects(block.getRectangle(), collisionGroup.first)){
                collisionGroup.second.add(block);
            }
        }
    }

    /**
     * Adds texture to block
     * @param block block to texture
     * @param type type of the block
     * @param x position
     * @param y position
     */
    private void addBlockTexture(Block block, int type, int x, int y) {
        switch (type) {
            case Block.TYPE_OBSTACLE:
                block.setTexture(textureSet, TextureSet.WALL_TEX);
                break;
            case Block.TYPE_GOAL:
                block.setTexture(textureSet, TextureSet.GOAL_TEX);
                break;
            case Block.TYPE_BREAKABLE:
                block.setTexture(textureSet, TextureSet.CRATE_TEX);
                break;
            case Block.TYPE_HOLE:
                block.setTexture(textureSet, TextureSet.HOLE_TEX);
                break;
            default:
                Log.d(TAG, "" + x + " " + y);
                Log.d(TAG, "WHAT " + type);
                break;
        }
    }

    /**
     * Adds a spawn point
     * @param level Bitmap of level
     * @param x coordinate
     * @param y coordinate
     * @param scaling scaling for level
     */
    private void addSpawnPoint(Bitmap level, int x, int y, float scaling){
        spawnPoint = new Vector2(x * scaling, y * scaling);
        level.setPixel(x, y, Block.TYPE_CLEAR);
    }

    /**
     * Set up the background block
     */
    private void addBackground(PointF levelDims){
        background = new Block(Vector2.zero, levelDims.x, levelDims.y, Block.TYPE_CLEAR);
        background.setTexture(textureSet, TextureSet.FLOOR_TEX);
    }

    /**
     * Set the texture set
     * @param textureSet texture set
     */
    public void setTextureSet(TextureSet textureSet) {
        this.textureSet = textureSet;
    }

    public Block getBackground() {
        return background;
    }
}
