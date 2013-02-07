package gyoerkaa.android.accelerationlogger;

import android.util.FloatMath;
import android.util.Log;

public class DataPoint {
	
	public long  time;
	public float values[] = new float[3];	
	
	public DataPoint() {		
	}

	
	public DataPoint(long time, float[] values) {
		this.time = time;
    	for (int i=0; i<3; i++) {
    		this.values[i] = values[i];                   	    
        }		
	}

	
	/** Set values */
	public void set (long time, float[] values) {
		this.time = time;
    	for (int i=0; i<3; i++) {
    		this.values[i] = values[i];                   	    
        }
	}

	
	/** Converts fValue to radians */	
	public float degToRad(float fvalue) {
		return (fvalue * (float)Math.PI / 180) ;
	}

	
	/** Converts acceleration values to world coordinates, where
	 * World coordinates are
	 *   X-axis   east
	 *   Y-axis   north
	 *   Z-axis   towars sky
	 * 
	 * Device coordinates are
	 *   x-axis   short side of display
	 *   y-axis   long side of display
	 *   z-axis   orthogongal to x & y, point up, if phone lies flat on table
	 *   
	 * Axes from orientation sensor are
	 *   values[0] = rotation around z-axis (pointing up, orthogonal to x & y)
	 *               azimuth, angle between y-axis & Y-axis 
	 *               0° => y pointing North,   90° => y pointing East)
	 *               (0 to 360) 
	 *   values[1] = rotation around x-axis (short side of screen)              
	 *               pitch, angle between z-axis & Y-axis
	 *               0° => horizontal
	 *               (-180 to 180)
	 *   values[2] = rotation around y-axis (long side of screen)
	 *               roll, angle between y-axis & Y-axis   
	 *               0° => horizontal
	 *               (-90 to 90)
	 * 
	 * Axes from accelerometer sensor are:
	 *   values[0] = force on x-axis (short side of screen)
	 *   values[1] = force on y-axis (long side of screen)
	 *   values[2] = force on z-axis (pointing up, orthogonal to x & y)
	 * 
	 * to transform a vector from world to device coord:
	 *      Rx(-[1]) * Ry(-[2]) * Rz(-[0]) * v
	 *      
	 * to transform it back:
	 *      Rz'(-[0])* Ry'(-[2]) * Rx'(-[1]) * v   (R' = inv. M.)
	 *   =  Rz^(-[0])* Ry^(-[2]) * Rx^(-[1]) * v   (R^ = transp. M.)
	 *   =  Rz([0])  * Ry([2])   * Rx([1])   * v   (sin(-x) = -sin(x), cos(-x)=cos(x))         
	 *  */	  
	public void toWorldCoordinates(float[] orientation) {
		values = rotateY(values, degToRad( orientation[2]));			
		values = rotateX(values, degToRad(-orientation[1]));
		values = rotateZ(values, degToRad( orientation[0]));				
	}

	
	/** Rotates fVector by fangle (in radians) around Z-axis 
	 *  y-axis towars x-axis
	 * */
	private float[] rotateX(float fVector[], float fangle) {
		float fResult[] = new float[3]; // Result  
		float fRot[]    = new float[9]; // Rotation Matrix
		
		// Set up rotation matrix
		//  /    1      0      0      \
		// |     0  cos(a)   -sin(a)   |
		//  \    0  sin(a)    cos(a)  /
		fRot[0] = 1;        
		fRot[1] = 0; 
		fRot[2] = 0;
		fRot[3] = 0;
		fRot[4] = FloatMath.cos(fangle);  
		fRot[5] = -FloatMath.sin(fangle);  
		fRot[6] = 0; 
		fRot[7] = FloatMath.sin(fangle);
		fRot[8] = FloatMath.cos(fangle); 
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fResult[i] = 
				fRot[0+i*3]*fVector[0] +
			    fRot[1+i*3]*fVector[1] +
			    fRot[2+i*3]*fVector[2];				
		}
		
		return fResult;
	}

	
	/** Rotates fVector by fangle (in radians) around Y-axis 
	 *  z-axis towars x-axis
	 * */
	private float[] rotateY(float fvector[], float fangle) {
		float fresult[] = new float[3]; // result  
		float frot[]    = new float[9]; // Rotation Matrix
		

		
		// Set up rotationmatrix
		//  /  cos(a)      0      sin(a)  \
		// |     0         1        0      |
		//  \  -sin(a)     0      cos(a)  /		
		frot[0] = FloatMath.cos(fangle); 
		frot[1] = 0; 
		frot[2] = FloatMath.sin(fangle); 
		frot[3] = 0; 
		frot[4] = 1; 
		frot[5] = 0; 
		frot[6] = -FloatMath.sin(fangle);
		frot[7] = 0;
		frot[8] = FloatMath.cos(fangle);
		Log.d(">> RotY_mat", Float.toString(frot[0]) + ", " + 
				           Float.toString(frot[2]) + ", " + 
				           Float.toString(frot[6]) + ", " + 
				           Float.toString(frot[8]));
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fresult[i] = 
				frot[0+i*3]*fvector[0] +
			    frot[1+i*3]*fvector[1] +
			    frot[2+i*3]*fvector[2];				
		}
		
        Log.d(">>> out", Float.toString(fresult[0]) + ", " + 
		           Float.toString(fresult[1]) + ", " + 
		           Float.toString(fresult[2]));
        
		return fresult;
	}

	
	/** Rotates fVector by fangle (in radians) around Z-axis 
	 *  x-axis towars y-axis
	 * */
	private float[] rotateZ(float fVector[], float fangle) {
		float fResult[] = new float[3]; // Result  
		float fRot[]    = new float[9]; // Rotation Matrix
		
		// Set up rotation matrix
		//  /  cos(a)   -sin(a)     0     \
		// |   sin(a)    cos(a)     0      |
		//  \    0         0        1     /			
		fRot[0] = FloatMath.cos(fangle); 
		fRot[1] = -FloatMath.sin(fangle); 
		fRot[2] = 0; 
		fRot[3] = FloatMath.sin(fangle);  
		fRot[4] = FloatMath.cos(fangle);  
		fRot[5] = 0; 
	    fRot[6] = 0;
	    fRot[7] = 0;
		fRot[8] = 1;
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fResult[i] = 
				fRot[0+i*3]*fVector[0] +
			    fRot[1+i*3]*fVector[1] +
			    fRot[2+i*3]*fVector[2];				
		}
				
		return fResult;
	}
	
	protected static float round(float val) {
        return ((float) Math.round(val * 1000)/ 1000);
	}
	
}

