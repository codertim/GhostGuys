package timforce.ghosts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


public class BackgroundEyes {
	private static final String TAG = "BackgroundEyes";
	final static private Paint eyesPaint = new Paint();
	final private static float MIN_RADIUS = 1.0f;
	private static final int eyeOffset = 4;
	private static final int NUM_EYES = 10;
	private Point [] points = new Point[NUM_EYES];
	private float  [] radii = new float[NUM_EYES];   // allow eyes to have separate raidus
	private static int eyeCounter = 0;

	
	public BackgroundEyes() {
		eyesPaint.setColor(Color.RED);
	}
	
	
	public void drawSelf(Canvas canvas) {
		makePointsActive(canvas);
		// for(Point point : points) {
		for(int i=0; i < points.length; i++) {
			if(shouldShowEye(i)) {
				// draw all eyes except one of them
				// even then, only 10% chance of not drawing
				canvas.drawCircle(points[i].x - eyeOffset, points[i].y, radii[i], eyesPaint);
				canvas.drawCircle(points[i].x + eyeOffset, points[i].y, radii[i], eyesPaint);
			}
		}
		updateCounter();
	}
	
	
	private boolean shouldShowEye(int i) {
		if(i != eyeCounter) {
			return true;
		}
	
		if( Math.random() < 0.1 ) {
			// small chance of not drawing eye - blinking effect
			return false;
		} else {
			return true;
		}
	}

	
	private void updateCounter() {
		eyeCounter++;
		if(eyeCounter >= points.length) {
			// start over
			eyeCounter = 0;
		}
	}
	
	
	// initialize, ideally happens only once
	// lazy initializtion because we need to wait to see how
	//    big the canvas is
	private void makePointsActive (Canvas canvas) {
		if(points[0] != null) {
			return;
		}
		
		// points not initialized yet, so initialize
		int canvasHeight = canvas.getHeight();
		int canvasWidth  = canvas.getWidth();
		
		for(int i = 0; i < points.length; i++) {
			int posX = (int) (Math.random()*canvasWidth);		
			int posY = (int) (Math.random()*canvasHeight);
			points[i] = new Point(posX, posY);
			radii[i] = MIN_RADIUS + (float)Math.random();
		}
	} 
}
