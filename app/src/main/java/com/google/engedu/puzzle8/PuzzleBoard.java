package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    int steps;
    PuzzleBoard previousBoard;
    private static final int[][] NEIGHBOUR_COORDS = {
                                                        { -1, 0 },
                                                        {  1, 0 },
                                                        {  0,-1 },
                                                        {  0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    PuzzleBoard(Bitmap bitmap, int parentWidth)
    {
        tiles=new ArrayList<>();
        Bitmap scaled=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        int basew=parentWidth/NUM_TILES;
        int twiceBaseW=basew*2;
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,0,0,basew,basew),0));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,basew,0,basew,basew),1));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,twiceBaseW,0,basew,basew),2));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,0,basew,basew,basew),3));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,basew,basew,basew,basew),4));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,twiceBaseW,basew,basew,basew),5));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,0,twiceBaseW,basew,basew),6));
        tiles.add(new PuzzleTile(Bitmap.createBitmap(scaled,basew,twiceBaseW,basew,basew),7));
        tiles.add(null);

    }

    PuzzleBoard(PuzzleBoard otherBoard,int steps)
    {
        previousBoard=otherBoard;
        this.steps=steps+1;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours()
    {
        int index=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;i++)
        {
            if(tiles.get(i)==null)
                index=i;
        }
        int x=index%3;
        int y=index/3;
        ArrayList<PuzzleBoard> ar=new ArrayList<>();
        for (int []cordinates:NEIGHBOUR_COORDS)
        {
            int xx=x+cordinates[0];
            int yy=y+cordinates[1];
            if(xx>=0&&xx<NUM_TILES&&yy>=0&&yy<NUM_TILES)
            {
                PuzzleBoard temp=new PuzzleBoard(this,steps);
                temp.swapTiles(XYtoIndex(xx,yy),XYtoIndex(x,y));
                ar.add(temp);
            }
        }
        return ar;
    }

    public int priority()
    {
        int manhattanSteps=0;
        for (int i=0;i<NUM_TILES*NUM_TILES;i++)
        {
            PuzzleTile t=tiles.get(i);
            if(t!=null) {
                int a = t.getNumber();
                int x = a % NUM_TILES;
                int y = a / NUM_TILES;
                int xx = i % NUM_TILES;
                int yy = i / NUM_TILES;
                manhattanSteps = manhattanSteps + Math.abs(x - xx) + Math.abs(y - yy);
            }
        }
        return manhattanSteps+steps;
    }
    PuzzleBoard getPreviousBoard()
    {
        return previousBoard;
    }
    public void setPreviousBoard(PuzzleBoard previousBoard)
    {
        this.previousBoard = previousBoard;
    }
}
