package timforce.ghosts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GameEngine extends Activity {
    /** Called when the activity is first created. */
	final static String TAG = "GameEngine";
	static MediaPlayer mPlayer = new MediaPlayer();
	static final int screenHeight = 700;
	static final int screenWidth = 500;
	static int bgColor = Color.BLACK;
	static final Ghost [] ghosts = new Ghost[10];
	static GameEngine gameEngine = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.game_engine);
        setContentView(new GraphicsView(this));
        gameEngine = this;
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
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null)
    				ghosts[i].drawOnCanvas(canvas);
    		}
    		drawBackgroundObjects(canvas);
    	}
    	
    	
    	
    	private void drawBackgroundObjects(Canvas canvas) {
    		int moonX = screenWidth - 75;
    		int moonY = 5;
    		int moonWidth = 50;
    		int moonHeight = 50;
    		int moonShadowOffsetX = 5;
    		int moonShadowOffsetY = -5;
    		
    		// draw visible moon
    		RectF moonRectF = new RectF(moonX, moonY, (moonX + moonWidth), (moonY + moonHeight));
    		Paint moonPaint = new Paint();
    		moonPaint.setColor(Color.argb(255, 255, 255, 100));
    		moonPaint.setAntiAlias(true);
    		canvas.drawArc(moonRectF, 0f, 360f, true, moonPaint);
    		
    		// draw moon shadow
    		RectF moonShadowRectF = new RectF(moonX + moonShadowOffsetX, moonY + moonShadowOffsetY, (moonX + moonWidth + moonShadowOffsetX), (moonY + moonHeight + moonShadowOffsetY));
    		Paint moonShadowPaint = new Paint();
    		moonShadowPaint.setColor(bgColor);
    		moonShadowPaint.setAntiAlias(true);
    		canvas.drawArc(moonShadowRectF, 0, 360f, true, moonShadowPaint);
    	}
    	
    	
    	
    	private void updateObjects() {
    		// move objects TODO
    		ghost.incrementPosition();
    		// TODO: set dead ghosts to null
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null)
    				ghosts[i].incrementPosition();
    		}
    	}
    	
    	
    	
    	private void checkIfObjectsClicked(MotionEvent e) {
    		if(ghost.isClicked(e)) {
    			Log.d(TAG, "#checkIfObjectsClicked - Clicked = TRUE");
    			boolean hasGhostJustDied = false;
    			hasGhostJustDied = ghost.reactToClicked();
    			if(hasGhostJustDied) {
    				handleGhostDeath(ghost);
    			}
    		}
    		
    		int counter =0;
    		for(Ghost ghost : ghosts) {
    			if( (ghost != null) && (ghost.isClicked(e)) ) {
        			boolean hasGhostJustDied = false;
        			hasGhostJustDied = ghost.reactToClicked();
        			if(hasGhostJustDied) {
        				handleGhostDeath(ghost);
        				ghost = null;
        				ghosts[counter] = null;
        				break;
        			}    				
    			}
    			counter++;
    		}
    	}
    	
    	
    	
    	private void handleGhostDeath(Ghost ghost) {
    		int deadGhostPosX = ghost.getPosX();
    		int deadGhostPosY = ghost.getPosY();
    		int deadGhostStartWidth       = ghost.getStartWidth();
    		int deadGhostStartHeight      = ghost.getStartHeight();
    		float deadGhostStartEyeRadius = ghost.getStartEyeRadius();
    		Log.d(TAG, "#handleGhostDeat - deadGhostPosX=" + deadGhostPosX);
    		playSoundWhenDie();
    		if(ghost.isBigEnoughToMakeChildren()) {
    			makeNewGhostsWhenGhostDies(deadGhostPosX, deadGhostPosY, deadGhostStartWidth, deadGhostStartHeight, deadGhostStartEyeRadius);
    		}
    		ghost = null;
    	}
    	
    	
    	
    	private void makeNewGhostsWhenGhostDies(int posX, int posY, int deadGhostStartWidth, int deadGhostStartHeight, float deadGhostStartEyeRadius) {
    		for(int i=0 ; i < 2; i++) {
    			int indexEmptyGhost = findIndexEmptyGhost();
    			if(indexEmptyGhost > -1) {
    	    		ghosts[indexEmptyGhost] = new Ghost(screenWidth, screenHeight, bgColor, posX, posY, deadGhostStartWidth, deadGhostStartHeight, deadGhostStartEyeRadius);
    			}
    		}
    	}
    	
    	
    	
    	// find empty slot in ghost array
    	private int findIndexEmptyGhost() {
    		int indexEmptyGhost = -1;
    		
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] == null) {
    				indexEmptyGhost = i;
    				break;
    			}
    		}
    		
    		return indexEmptyGhost;
    	}
    	
    	
    	private void playSoundWhenDie() {
    		if(mPlayer != null) {
    			mPlayer.release();
    		}
    		gameEngine.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    		mPlayer = MediaPlayer.create(gameEngine.getBaseContext(), R.raw.balloon_burst_05);
    		// mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    		
    		try {
    			// mPlayer.reset();   mPlayer.prepare();
    			mPlayer.start();
    			// mPlayer.release();
    		} catch(Exception e) {
    			Log.e(TAG, "ERROR tring to play baloon pop - message=" + e.getMessage());
    		}

    	}
    		
    }


}

