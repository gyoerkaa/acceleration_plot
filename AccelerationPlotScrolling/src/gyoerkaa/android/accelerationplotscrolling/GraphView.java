package gyoerkaa.android.accelerationplotscrolling;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.view.View;

/**
 * @author gyoerkaa
 * Draws the plot
 */
public class GraphView extends View 
{ 
         
    private Bitmap  mBitmap;
    private Paint   mPaint  = new Paint();
    private Canvas  mCanvas = new Canvas();
        
    // X-position, same for each axis, computed based of time values
    private float   fLastX;
    private float   fCurrentX; 
    // Holds the colors for the axes   
    private float   fScale;
    // Offset for the 3 Axes. For visibility reasons the 
    // 3 acceleration components are displayed with an offset
    // of SensorManager.STANDARD_GRAVITY
    private float   fOffset[] = new float[3];
    // Position of 0, preferably the center of the screen
    private float   fZeroY;
    private float   fMaxX;
    private float   fViewWidth;
    private float   fViewHeight;
      
    DataQueue dataQueue    = new DataQueue();
    DataPoint dataPoint;
    DataPoint dataPointOld;   
    DataPoint currentPoint = new DataPoint();
  
    
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
        // Fill with white
        mCanvas.drawColor(Color.WHITE);
        // Set Position for 0 to center of view
        fZeroY      = h * 0.5f;
        fScale      = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        fViewWidth  = w;
        fViewHeight = h;
        if (fViewWidth < fViewHeight) {
            fMaxX = w;
        } else {
            fMaxX = w-50;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    
    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
         }
    }

    /**
     * Draw all points in dataQueue
     */    
    public void drawDataPoints(DataQueue dataQueue, float[] fLastY, int[] Colors) {    	
        final Canvas tempCanvas = mCanvas;
        final Paint  tempPaint  = mPaint;    	
        final float fdistance = SensorManager.STANDARD_GRAVITY * fScale;

        if (mBitmap != null) {  	
            // Fill with white, i.e. delete old graph
            mCanvas.drawColor(Color.WHITE);
                     	            
            // Paint 0-lines with Black
            tempPaint.setColor(Color.BLACK);
            // Zero-line of X-accel
            tempCanvas.drawLine(0, fZeroY+fdistance, 
            		fMaxX, fZeroY+fdistance, 
            		tempPaint);	                        
            // Zero-line of Y-accel
            tempCanvas.drawLine(0, fZeroY,      
            		fMaxX, fZeroY,      
            		tempPaint);
            // Zero-line of Z-accel
            tempCanvas.drawLine(0, fZeroY-fdistance, 
            		fMaxX, fZeroY-fdistance, 
            		tempPaint);   
                        
            // Now start drawing a new graph by iterating
            // through all (new) data points
            // 2 Options:
            
            // A. Compute x displacement directly from time values
            // => Accurate, looks ugly
            
    	    // B. Compute custom Interval is calculated based on the
    	    // number of data points and screen Width
            // => Inaccurate, looks better, 
            //    Graph is sometimes distorted, due to difference in
            //    sampling intervals
            
            fLastX = 0.0f;
            float fInterval = (fViewWidth/dataQueue.entries);
            fCurrentX = 0.0f;
            
            Iterator<DataPoint> iter = dataQueue.queue.iterator();
            while (iter.hasNext()) {
        	    currentPoint = iter.next();
            	
        	    // Compute current X-Position based on time values
        	    // This is more accurate, but it looks ugly
 	        	//fCurrentX = (((float) currentPoint.time) - fStart);
 	        	
        	    // Draw the 3 acceleration components
        	    for (int i=0 ; i<3 ; i++) {
                	final float fCurrentY = fZeroY + (currentPoint.values[i]+fOffset[i]) * fScale;
                	tempPaint.setColor(Colors[i]);
                	tempCanvas.drawLine(fLastX, fLastY[i], fCurrentX, fCurrentY, tempPaint);
                    fLastY[i] = fCurrentY;                   
                }
        	    fLastX    = fCurrentX;
        	    fCurrentX = fLastX + fInterval;
        	    
            }
            invalidate();
        }
    }
    
		    

}