package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private PuzzleBoard puzzleBoardSolved;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
        puzzleBoardSolved = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            int r = random.nextInt(100);
            for (int i = 0; i < r; i++) {
                ArrayList<PuzzleBoard> p = puzzleBoard.neighbours();
                int rr = random.nextInt(p.size());
                puzzleBoard = p.get(rr);
            }
            invalidate();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        Comparator<PuzzleBoard> c = new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard puzzleBoard, PuzzleBoard t1) {
                return puzzleBoard.priority() - t1.priority();
            }
        };
        PriorityQueue<PuzzleBoard> pq = new PriorityQueue<>(1, c);
        PuzzleBoard pb = new PuzzleBoard(puzzleBoard, -1);
        pb.setPreviousBoard(null);
        pq.add(pb);
        while (!pq.isEmpty()) {
            PuzzleBoard temp = pq.poll();
            if (temp.resolved()) {
                ArrayList<PuzzleBoard> apb = new ArrayList<PuzzleBoard>();
                while (temp.getPreviousBoard() != null) {
                    apb.add(temp);
                    temp = temp.getPreviousBoard();
                }
                Collections.reverse(apb);
                animation = apb;
                invalidate();
                break;
            }
            else {
                pq.addAll(temp.neighbours());
            }
        }
    }
}
