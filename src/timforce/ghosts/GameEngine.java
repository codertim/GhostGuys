package timforce.ghosts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GameEngine extends Activity {
    /** Called when the activity is first created. */
	final static String TAG = "GameEngine";
	static final int screenHeight = 500;
	static final int screenWidth = 300;
	static int bgColor = Color.BLACK;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.game_engine);
        setContentView(new GraphicsView(this));
    }
    
    
    static public class GraphicsView extends View {
    	Ghost ghost = null;
    	public GraphicsView(Context context) {
    		super(context);
    		ghost = new Ghost(screenWidth, screenHeight, bgColor);
    	}
    	
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		Log.d(TAG, "#onTouchevent - event message=" + event.toString());
    		int action = event.getAction();
    		switch(action) {
    		case MotionEvent.ACTION_DOWN:
        		Log.d(TAG, "#onTouchevent - CASE - ACTION_DOWN");
        		checkIfObjectsClicked(event);
    		}
    		return super.onTouchEvent(event);
    	}
    	
    	
    	protected void onDraw(Canvas canvas) {
    		startGame(canvas);
    	}
   	
    	
    	private void startGame(Canvas canvas) {
			canvas.drawColor(bgColor);
			updateObjects();
			drawObjects(canvas);
			invalidate();  
			SystemClock.sleep(50);   
    	}
    	
    	
    	private void drawObjects(Canvas canvas) {
    		ghost.drawOnCanvas(canvas);
    	}
    	
    	
    	private void updateObjects() {
    		// move objects TODO
    		ghost.incrementPosition();
    		// TODO: set dead ghosts to null
    	}
    	
    	private void checkIfObjectsClicked(MotionEvent e) {
    		if(ghost.isClicked(e)) {
    			Log.d(TAG, "#checkIfObjectsClicked - Clicked = TRUE");
    			ghost.reactToClicked();
    		}
    	}
    }
    
 

}

