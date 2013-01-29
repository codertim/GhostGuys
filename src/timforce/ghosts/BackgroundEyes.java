package timforce.ghosts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;


public class BackgroundEyes {
	private static final String TAG = "BackgroundEyes";
	final static private Paint eyesPaint = new Paint();
	final private static float MIN_RADIUS = 1.0f;
	private static final int eyeOffset = 4;
	private static final int NUM_EYES = 10;
	private Point [] points = new Point[NUM_EYES];
	private static boolean [] isEyesActive = new boolean[NUM_EYES];
	private static boolean [] eyesJustPopped = new boolean[NUM_EYES];
	private float  [] radii = new float[NUM_EYES];   // allow eyes to have separate radius
	private static int eyeCounter = 0;
	private static int eyesBigCounters [] = new int[NUM_EYES];
	private static final int moveEyeAmount = 3;
	private static int canvasHeight = -1;
	private static int canvasWidth  = -1;
	private static final int skyOffset = 75;
	
	
	public BackgroundEyes() {
		eyesPaint.setColor(Color.RED);
		
		// initialize eyes to active
		for(int i=0; i < isEyesActive.length; i++) {
			isEyesActive[i] = true;
		}
	}
	
	
	private void popEyes(int eyesIndex) {
		// pop means to set particular eye to inactive
		isEyesActive[eyesIndex]   = false;
		eyesJustPopped[eyesIndex] = true;
	}
	
	
	public void drawSelf(Canvas canvas) {
		canvasHeight = canvas.getHeight();
		canvasWidth  = canvas.getWidth();

		makePointsActive(canvas);
		// for(Point point : points) {
		for(int i=0; i < points.length; i++) {
			if(shouldShowEye(i)) {
				// draw all eyes except one of them
				// even then, only 10% chance of not drawing
				if(eyesBigCounters[i] > 10) {
					popEyes(i);
				} else if(eyesBigCounters[i] > 0) {
					int counter = eyesBigCounters[i];
					canvas.drawCircle(points[i].x - eyeOffset - (counter*20), points[i].y, radii[i] + (counter*20), eyesPaint);
					canvas.drawCircle(points[i].x + eyeOffset + (counter*20), points[i].y, radii[i] + (counter*20), eyesPaint);
					eyesBigCounters[i]++;
				} else {
					// draw normal size
					canvas.drawCircle(points[i].x - eyeOffset, points[i].y, radii[i], eyesPaint);
					canvas.drawCircle(points[i].x + eyeOffset, points[i].y, radii[i], eyesPaint);
				}
			}
		}
		updateCounter();
	}
	
	
	private boolean shouldShowEye(int i) {
		if(!isEyesActive[i] ) {
			// early return - when not active, do not show
			return false;
		}
		
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

	
	public void incrementPosition() {
		
		if(points[0] != null) {
			// if one point is not null then they all are not null
			int randomEyesIndex = (int) Math.floor(Math.random() * NUM_EYES);

			// x position
			if(Math.random() < 0.5) {
				points[randomEyesIndex].x += moveEyeAmount;
			} else {
				points[randomEyesIndex].x -= moveEyeAmount;
				

			}
			
			// x direction - prevent going offscreen
			if(points[randomEyesIndex].x < 0) {
				points[randomEyesIndex].x = 10;
			}
			if(points[randomEyesIndex].x > canvasWidth) {
				points[randomEyesIndex].x = canvasWidth - 10;
			}
			
			
			// y position
			if(Math.random() < 0.5) {
				points[randomEyesIndex].y -= moveEyeAmount;
			} else {
				points[randomEyesIndex].y += moveEyeAmount;
			}
			
			// y direction - prevent going offscreen
			if(points[randomEyesIndex].y < skyOffset) {
				points[randomEyesIndex].y = skyOffset + 10;
			}
			if(points[randomEyesIndex].y > canvasHeight) {
				points[randomEyesIndex].y = canvasHeight - 10;
			}
			
			
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
	
	
	
	public void handleClick(MotionEvent e) {
		Point   currentPoint  = null;
		
		for(int i=0; i < points.length; i++) {
			currentPoint = points[i];
			if( (currentPoint.x > e.getX()-20) && (currentPoint.x < e.getX() + 20) ) {
				if( (currentPoint.y > e.getY() - 20) && (currentPoint.y < e.getY() + 20) ) {
					eyesBigCounters[i] = 1;
					break;
				}
			}
		}
		
	}
	
	
	
	public boolean isAnyAboutToPop() {
		boolean isAboutToPop = false;
		
		for(int i=0; i < NUM_EYES; i++) {
			if(eyesBigCounters[i] == 5) {
				isAboutToPop = true;
				break;
			}
		}
		
		return isAboutToPop;
	}
	
	
	
	public boolean isAnyJustPopped() {
		boolean isPopped = false;
		
		for(int i=0; i < NUM_EYES; i++) {
			if(eyesJustPopped[i]) {
				isPopped = true;
				eyesJustPopped[i] = false;
				break;
			}
		}
		
		return isPopped;
	}
}

