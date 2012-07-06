package timforce.ghosts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class GhostGuysActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	switch(v.getId()) {
    	case R.id.start_button:
    		Intent i = new Intent(this, GameEngine.class);
    		startActivity(i);
    		break;
    	}
    }
}