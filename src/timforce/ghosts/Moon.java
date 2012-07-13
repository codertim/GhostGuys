package timforce.ghosts;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class Moon {
	private static final int moonWidth = 50;
	private static final int moonHeight = 50;
	private static int moonX = 300;
	private static int moonY = 5;
	private static final int startMoonShadowOffsetX= 5;	
	private static final int startMoonShadowOffsetY = -5;
	private static int moonShadowOffsetX = startMoonShadowOffsetX;
	private static int moonShadowOffsetY = startMoonShadowOffsetY;
	private static final Paint moonPaint       = new Paint();
	private static final Paint moonShadowPaint = new Paint();
	private static final RectF moonRectF       = new RectF();
	private static final RectF moonShadowRectF = new RectF();
	private static final int moonColor = Color.argb(255, 255, 255, 100);
	private static final int rightPadding = moonWidth + 5;
	private static int bgColor = 0;
	
	
	public Moon(int screenWidth, int bgColor) {
		// this.moonX = screenWidth - moonWidth - 50;  TODO: remove
		this.bgColor = bgColor;
		moonPaint.setColor(moonColor);
		moonPaint.setAntiAlias(true);
		moonShadowPaint.setColor(bgColor);
		moonShadowPaint.setAntiAlias(true);
	}
	
	
	public void draw(Canvas canvas) {
		int canvasWidth = canvas.getWidth();
		moonX = canvasWidth - rightPadding;
		
		// draw moon
		moonRectF.set(moonX, moonY, (moonX + moonWidth), (moonY + moonHeight));
		canvas.drawArc(moonRectF, 0f, 360f, true, moonPaint);
		
		// draw moon shadow
		moonShadowRectF.set(moonX + moonShadowOffsetX, moonY + moonShadowOffsetY, (moonX + moonWidth + moonShadowOffsetX), (moonY + moonHeight + moonShadowOffsetY));
		canvas.drawArc(moonShadowRectF, 0, 360f, true, moonShadowPaint);		
	}

}

