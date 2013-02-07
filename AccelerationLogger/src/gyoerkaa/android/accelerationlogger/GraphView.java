package gyoerkaa.android.accelerationlogger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.view.View;
 
public class GraphView extends View 
{   
    private Bitmap  mBitmap;
    private Paint   mPaint  = new Paint();
    private Canvas  mCanvas = new Canvas();
    
    // Last Acceleration Values for the 3 axes
    private float   fLastY[] = new float[3];  
    // Last x-position, same for each axis
    private float   fLastX;
    // Holds the colors for the axes
	private float   fScale;
    // Offset for the 3 Axes. For visibility reasons the 
    // 3 acceleration components are displayed with an offset
    // of SensorManager.STANDARD_GRAVITY
    private float   fOffset[] = new float[3];
    // Position of 0, preferably the center of the screen
    private float  fZeroPos;
    private float  fMaxX;
    private float  fViewWidth;
    private float  fViewHeight;       
    private float  fUpdateSpeed = 3f; // ~2,5 Seconds with SENSOR_DELAY_FASTEST 
    
    public GraphView(Context context) {
        super(context);
        
    	// Offset for the 3 accel-components
        fOffset[0] = 0;	
    	fOffset[1] = 0;
    	fOffset[2] = 0;
    	
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        // Fill Background with white
        mCanvas.drawColor(Color.WHITE);
        // Set Position for 0 to Screen Center
        fZeroPos = h * 0.5f;
        fScale   = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        fViewWidth  = w;
        fViewHeight = h;
        if (fViewWidth < fViewHeight) {
            fMaxX = w;
        } else {
            fMaxX = w-50;
        }
        fLastX = fMaxX;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
                final Paint paint = mPaint;
                // Simply start from 0, if out of bounds 
                if (fLastX >= fMaxX) {
                    fLastX = 0;
                    final Canvas tempCanvas = mCanvas;
                    final float maxx = fMaxX;
                    final float fdistance = SensorManager.STANDARD_GRAVITY * fScale;
                 	
                    // Fill Background with white
                	tempCanvas.drawColor(Color.WHITE);
                    // Paint with Black
                    paint.setColor(Color.BLACK);
                    // Zero of X-accel
                    tempCanvas.drawLine(0,    fZeroPos+fdistance, 
                        		   maxx, fZeroPos+fdistance, 
                        		   paint);	                        
                    // Zero of Y-accel
                    tempCanvas.drawLine(0,    fZeroPos,      
                        		   maxx, fZeroPos,      
                        		   paint);
                    // Zero of Z-accel
                    tempCanvas.drawLine(0,    fZeroPos-fdistance, 
                        		   maxx, fZeroPos-fdistance, 
                        		   paint);
                }
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
    }

    public void drawDataPoint(DataPoint datapoint, int[] Colors) {    		
        if (mBitmap != null) {  	          
            final Canvas canvas = mCanvas;
            final Paint  paint  = mPaint;          	
        	
        	float fCurrentX = fLastX + fUpdateSpeed;
        	int i;
            for (i=0 ; i<3 ; i++) {
            	final float fCurrentY = fZeroPos + (datapoint.values[i]+fOffset[i]) * fScale;
                paint.setColor(Colors[i]);
                canvas.drawLine(fLastX, this.fLastY[i], fCurrentX, fCurrentY, paint);
                this.fLastY[i] = fCurrentY;
            }	                        	
            fLastX += fUpdateSpeed;  
        }
        invalidate();
    } 
    
    public void setUpdateSpeed(float fSpeed) {       
        this.fUpdateSpeed = fSpeed;
    }

}
