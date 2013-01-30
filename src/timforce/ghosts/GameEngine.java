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
	static Ghost firstGhost = null;   // TODO: determine if this is really needed
	static GameEngine gameEngine = null;
	static Moon moon = null;
	static BackgroundEyes backgroundEyes = null;
	final static int ghostSplitApartOffset = 50;
	static int updateCounter = 0;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.game_engine);
        setContentView(new GraphicsView(this));
        gameEngine = this;

    }
    
    
     public class GraphicsView extends View {
    	public GraphicsView(Context context) {
    		super(context);
    		Log.d(TAG, "CONSTRUCTOR: GraphicsView");
    		// ghost = new Ghost(screenWidth, screenHeight, bgColor);
    		ghosts[0] = new Ghost(screenWidth, screenHeight, bgColor);
    		firstGhost = ghosts[0];
    		moon  = new Moon(screenWidth, bgColor);
    		backgroundEyes = new BackgroundEyes();
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
    		drawBackgroundObjects(canvas);
    		
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null)
    				ghosts[i].drawOnCanvas(canvas);
    		}
    	}
    	
    	
    	
    	private void drawBackgroundObjects(Canvas canvas) {
    		moon.draw(canvas);
    		backgroundEyes.drawSelf(canvas);
    		// draw visible moon
    	}
    	
    	
    	
    	private void updateObjects() {
    		updateCounter++;
    		
    		// update ghosts in array and check for death
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null) {
    				ghosts[i].incrementPosition();
    				boolean hasGhostJustDied2 = false;
    				hasGhostJustDied2 = ghosts[i].getIsJustDied();
    				if(hasGhostJustDied2) {
    					handleGhostDeath(ghosts[i]);
    				}
    			}

    		}
    		
    		backgroundEyes.incrementPosition();
    		if(backgroundEyes.isAnyAboutToPop()) {
    			playSoftSoundWhenPop();
    		}
    		
    		sweepUpDeadGhosts();
    		
    		int countGhosts = getGhostCount();
    		if( (updateCounter % 50) == 0) {
    			// occasionally show active ghost count
    			Log.d(TAG, "updateObject - current active ghost count = " + countGhosts);
    		}
    	}
    	
    	
    	
    	private int getGhostCount() {
    		int ghostCount = 0;
    		
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null) {
    				ghostCount++;
    			}
    		}
    		
    		return ghostCount;
    	}
    	
    	
    	
    	private void sweepUpDeadGhosts() {
    		for(int i=0; i < ghosts.length; i++) {
    			if(ghosts[i] != null) {
    				if(ghosts[i].isDead) {
    					// clear space for new ghost
    					ghosts[i] = null;
    				}
    			}
    		}
    	}
    	
    	
    	
    	private void checkIfObjectsClicked(MotionEvent e) {    		
    		int counter =0;
    		for(Ghost ghost : ghosts) {
    			if( (ghost != null) && (ghost.isClicked(e)) ) {
        			ghost.reactToClicked();
        			/*  TODO: move this somewhere else
        			if(hasGhostJustDied) {
        				handleGhostDeath(ghost);
        				ghost = null;
        				ghosts[counter] = null;
        				break;
        			}    		
        			*/		
    			}
    			counter++;
    		}
    		
    		backgroundEyes.handleClick(e);
    	}
    	
    	
    	
    	private void handleGhostDeath(Ghost ghost) {
    		int deadGhostPosX = ghost.getPosX();
    		int deadGhostPosY = ghost.getPosY();
    		int deadGhostStartWidth       = ghost.getStartWidth();
    		int deadGhostStartHeight      = ghost.getStartHeight();
    		float deadGhostStartEyeRadius = ghost.getStartEyeRadius();
    		Log.d(TAG, "#handleGhostDeat - deadGhostPosX=" + deadGhostPosX);
    		playSoundWhenPop();
    		if(ghost.isBigEnoughToMakeChildren()) {
    			makeNewGhostsWhenGhostDies(deadGhostPosX, deadGhostPosY, deadGhostStartWidth, deadGhostStartHeight, deadGhostStartEyeRadius);
    		}
    		ghost = null;
    	}
    	
    	
    	
    	private void makeNewGhostsWhenGhostDies(int posX, int posY, int deadGhostStartWidth, int deadGhostStartHeight, float deadGhostStartEyeRadius) {
    		// first new child ghost
    		int indexEmptyGhost1 = findIndexEmptyGhost();
    		if(indexEmptyGhost1 > -1) {
    	    	ghosts[indexEmptyGhost1] = new Ghost(screenWidth, screenHeight, bgColor, posX-ghostSplitApartOffset, posY, deadGhostStartWidth, deadGhostStartHeight, deadGhostStartEyeRadius);
    		}
    		
    		// second new child ghost
    		int indexEmptyGhost2 = findIndexEmptyGhost();
    		if(indexEmptyGhost2 > -1) {
    	    	ghosts[indexEmptyGhost2] = new Ghost(screenWidth, screenHeight, bgColor, posX+ghostSplitApartOffset, posY, deadGhostStartWidth, deadGhostStartHeight, deadGhostStartEyeRadius);
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
    		
    		Log.d(TAG, "findIndexEmptyGhost - ghost died - index for next ghost = " + indexEmptyGhost);
    		return indexEmptyGhost;
    	}
    	
    	
    	private void playSoundWhenPop() {
    		if(mPlayer != null) {
    			mPlayer.release();
    		}
    		mPlayer = MediaPlayer.create(gameEngine.getBaseContext(), R.raw.balloon_burst_05);
    		gameEngine.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    		// mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    		
    		try {
    			// mPlayer.reset();   mPlayer.prepare();
    			mPlayer.start();
    			// mPlayer.release();
    		} catch(Exception e) {
    			Log.e(TAG, "ERROR tring to play baloon pop - message=" + e.getMessage());
    		}

    	}
    	
    	
    	private void playSoftSoundWhenPop() {
    		if(mPlayer != null) {
    			mPlayer.release();
    		}
    		mPlayer = MediaPlayer.create(gameEngine.getBaseContext(), R.raw.cork);
    		gameEngine.setVolumeControlStream(AudioManager.STREAM_MUSIC);
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

