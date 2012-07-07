package timforce.ghosts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;



public class Ghost  {
    /** Called when the activity is first created. */
	final static String TAG = "Ghost";
	static int screenHeight;
	static int screenWidth;
	int width = 100, height = 150;
	int posX  = width + 5, posY   = height + headHeight + 5;
	final Paint gPaint   = new Paint();
	final Paint bgPaint  = new Paint();
	final Paint eyePaint = new Paint();
	static int bgColor = Color.RED;   // default red, indicates color not set
	static final int bottomCurvature = 5;   // do not want the bottom straight
	static final int eyeOffset = 10;
	float eyeRadius = 5.0f;
	static final int moveAmount = 3;   // movement per frame
	boolean isVerticalMovement = false;
	int yDirection = 1;  // 1 or -1, down or up
	int xDirection = 1;  // 1 or -1, up or down
	int stepsToGoBeforeChangingDirection = 10;
	static final int headHeight = 30;
	boolean isDead = false;
	boolean isUseRandomColors = true;
	int numTimesTouched = 0;
	int startWidth = width;
	int startHeight = height;
	float startEyeRadius = eyeRadius;
	
	
	
	public Ghost(int screenWidth, int screenHeight, int bgColor) {
		this.screenWidth  = screenWidth;
		this.screenHeight = screenHeight;
		gPaint.setColor(Color.LTGRAY);
		this.bgColor = bgColor;
		bgPaint.setColor(bgColor);
		eyePaint.setColor(Color.BLUE);
		
		
	}
	
	
	
	public Ghost(int screenWidth, int screenHeight, int bgColor, int posX, int posY, int parentGhostStartWidth, int parentGhostStartHeight, float parentGhostStartEyeRadius) {
		this(screenWidth, screenHeight, bgColor);
		this.width     = this.startWidth     = parentGhostStartWidth - 10;
		this.height    = this.startHeight    = parentGhostStartHeight - 15;
		this.eyeRadius = this.startEyeRadius = parentGhostStartEyeRadius - 0.5f;
		// this.headHeight = this.startHeadHeight = parentGhostStartHead
		
		if(isUseRandomColors) {
			int randomColor = assignRandomColor();
			gPaint.setColor(randomColor);
			ensureEyeColorIsNotSameAsBodyColor(randomColor);
		}
		int randomOffset = (int) (Math.random() * 20);
		
		if(Math.random() > 0.5) {
			this.posX = posX + randomOffset;
			this.posY = posY + randomOffset;			
		} else {
			this.posX = posX - randomOffset;
			this.posY = posY - randomOffset;
		}
	}
	

	public void drawOnCanvas(Canvas canvas) {
		if(!isDead) {
			// body
			canvas.drawRect(posX-width/2, posY-height, posX + width/2, posY, gPaint);
			
			// bottom
			RectF bottomOval = new RectF(posX - width/2, posY-bottomCurvature, posX + width/2, posY+bottomCurvature);
			canvas.drawOval(bottomOval, bgPaint);
			
			// head
			RectF oval = new RectF(posX-width/2,posY-height-headHeight,posX+width/2,posY-height+headHeight);
			canvas.drawArc(oval, 180f, 180f, false, gPaint);
			
			// eyes
			canvas.drawCircle(posX-eyeOffset, posY-height-3, eyeRadius, eyePaint);
			canvas.drawCircle(posX+eyeOffset, posY-height-3, eyeRadius, eyePaint);
		}
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
		
	
	
	// handle click internally
	public boolean reactToClicked() {
		boolean isStateChangedToDead = false;
		
		if(isDead) {
			return false;
		}
		
		numTimesTouched++;
		// height    -= 3;
		// width     -= 2;
		// eyeRadius -= 0.5;
		changeColor();
		
		if(isTimeToDie()) {
			isStateChangedToDead = true;
			this.die();   // so sad
		}
		
		return isStateChangedToDead;
	}
	
	// test if small enough to remove
	private boolean isTimeToDie() {
		if(isDead) {
			return true;
		}
		
		if(numTimesTouched >= 3) {
			// kill off if too small
			return true;
		} else {
			return false;
		}
	}
	
	
	private void die() {
		isDead = true;
		Log.d(TAG, "#die - Time to die!");
	}
	
	
	private int assignRandomColor() {
		int [] randomColors = {Color.BLUE, Color.CYAN, Color.GREEN, Color.RED, Color.YELLOW };
		int randomColor = 0;
		randomColor = randomColors[(int)(Math.random()*randomColors.length)];
		return randomColor;
	}
	
	
	private void changeColor() {
		int randomColor = assignRandomColor();
		gPaint.setColor(randomColor);
		ensureEyeColorIsNotSameAsBodyColor(randomColor);
	}
	
	
	private void ensureEyeColorIsNotSameAsBodyColor(int bodyColor) {
		if(bodyColor == Color.BLUE) {
			eyePaint.setColor(Color.LTGRAY);   // change, else we can't see eyes
		}		
	}
	
	
	public boolean isBigEnoughToMakeChildren() {
		if(width>35 && height > 50) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public int getPosX() { return posX; }
	public int getPosY() { return posY; }
	public int getStartWidth() { return startWidth; }
	public int getStartHeight() { return startHeight; }
	public float getStartEyeRadius() { return startEyeRadius; }
}




	