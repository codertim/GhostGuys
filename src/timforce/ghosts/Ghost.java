package timforce.ghosts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;



public class Ghost  {
    /** Called when the activity is first created. */
	final static String TAG = "Ghost";
	static int screenHeight;
	static int screenWidth;
	int posX  = 50, posY   = 50;
	int width = 20, height = 30;
	static final Paint gPaint   = new Paint();
	static final Paint bgPaint  = new Paint();
	static final Paint eyePaint = new Paint();
	static int bgColor = Color.RED;   // default red, indicates color not set
	static final int bottomCurvature = 2;   // do not want the bottom straight
	static final int eyeOffset = 4;
	static final int moveAmount = 3;   // movement per frame
	boolean isVerticalMovement = false;
	int yDirection = 1;  // 1 or -1, down or up
	int xDirection = 1;  // 1 or -1, up or down
	int stepsToGoBeforeChangingDirection = 10;
	static final int headHeight = 10;
	boolean isDead = false;
	
	
	public Ghost(int screenWidth, int screenHeight, int bgColor) {
		this.screenWidth  = screenWidth;
		this.screenHeight = screenHeight;
		gPaint.setColor(Color.LTGRAY);
		this.bgColor = bgColor;
		bgPaint.setColor(bgColor);
		eyePaint.setColor(Color.BLUE);
	}
	

	public void drawOnCanvas(Canvas canvas) {
		// body
		canvas.drawRect(posX-width/2, posY-height, posX + width/2, posY, gPaint);
		
		// bottom
		RectF bottomOval = new RectF(posX - width/2, posY-bottomCurvature, posX + width/2, posY+bottomCurvature);
		canvas.drawOval(bottomOval, bgPaint);
		
		// head
		RectF oval = new RectF(posX-width/2,posY-height-headHeight,posX+width/2,posY-height+headHeight);
		canvas.drawArc(oval, 180f, 180f, false, gPaint);
		
		// eyes
		canvas.drawCircle(posX-eyeOffset, posY-height-3, 3.0f, eyePaint);
		canvas.drawCircle(posX+eyeOffset, posY-height-3, 3.0f, eyePaint);

	}
	
	
	private void changeDirection() {
		stepsToGoBeforeChangingDirection = (int) (10 + Math.ceil(Math.random()*10));
		
		if(Math.random() >= 0.5) {
			isVerticalMovement = true;
		} else {
			isVerticalMovement = false;
		}
		
		if(Math.random() >= 0.5) {
			yDirection *= -1;
		}
		
		if(Math.random() >= 0.5) {
			xDirection *= -1;
		}
	}
	
	
	public void incrementPosition() {
		stepsToGoBeforeChangingDirection--;
		if(stepsToGoBeforeChangingDirection <= 0) {
			changeDirection();
		}
		
		if(isVerticalMovement) {
			if(isGoingOutOfBoundsY()) {
				yDirection *= -1;   // reverse direction
			} else {
				posY += (moveAmount*yDirection);
			}
		} else {
			if(isGoingOutOfBoundsX()) {
				xDirection *= -1;   // reverse direction
			} else {
				posX += (moveAmount*xDirection);
			}
		}
	}
	
	
	private boolean isGoingOutOfBoundsY() {
		if(yDirection == -1) { 
			// up
			if(posY - height - headHeight - moveAmount < 0) {
				return true;
			} else {
				return false;
			}
		} else {
			// down
			if(posY + moveAmount > screenHeight) {
				return true;
			} else {
				return false;
			}
			
		}
	}
	
	
	private boolean isGoingOutOfBoundsX() {
		if(xDirection == -1) { 
			// up
			if(posX - moveAmount - width/2 < 0) {
				return true;
			} else {
				return false;
			}
		} else {
			// down
			if(posX + moveAmount > screenWidth) {
				return true;
			} else {
				return false;
			}
			
		}
	}
	
	
	public boolean isClicked(MotionEvent e) {
		boolean isCloseToX = false;
		boolean isCloseToY = false;
		
		if(   (e.getY() > (posY - height))   &&   (e.getY() < (posY))   ) {
			isCloseToY = true;
		}
		
		if(   (e.getX() > (posX - (width/2)) )   &&   (e.getX() < (posX + (width/2)))  ) {
			isCloseToX = true;
		}
		
		return(isCloseToX && isCloseToY);
	}
		
	
	public void reactToClicked() {
		height -= 2;
		width -= 1;
		if(width < 5) {
			isDead = true;
		}
	}
}



	