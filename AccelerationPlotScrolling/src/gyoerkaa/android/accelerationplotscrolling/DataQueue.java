package gyoerkaa.android.accelerationplotscrolling;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author gyoerkaa
 * Queue, that holds data Points. Can delete old points
 */
public class DataQueue {
	public Queue<DataPoint> queue = new LinkedList<DataPoint>(); 
	int    entries;
	
	/** Adds the specified data point to the queue  
	 * */	
	public boolean offer(DataPoint dataPoint) {
    	if (queue.offer(dataPoint)) {
    		entries += 1;
    		return true;
    	} else {
    		return false;
    	}
    }
	
	/** Creates a data point based off the given values 
	 * and adds it to the queue
	 *  */
	public void offer(long time, float[] values) {
    	DataPoint dataPoint = new DataPoint();
    	
    	dataPoint.time = time; 
    	for (int i=0;i<3; i++){
    	    dataPoint.values[i] = values[i];  	
    	}   	
    	this.offer(dataPoint);
    }
	
	/** Retrieves the first data point 
	 *  without removing it from the queue
	 *  */   
	public DataPoint peek() {
		return queue.peek();
    	
    }
	
	/** Retrieves the first data point 
	 * and removes it from the queue
	 *  */        
    public DataPoint poll() {
    	if (entries > 0)
    	    entries -= 1;
    	
    	return queue.poll();		
    }
  
	/** Retrieves the first data point, which matches the minimum time
	 *  and removes it from the queue
	 *  */     
    public DataPoint poll(long minTime) {
    	DataPoint dataPoint = new DataPoint();    	
    	
    	dataPoint = this.peek();
    	while (dataPoint.time < minTime) {
    		dataPoint = this.poll();
    	}
    	return dataPoint;  		
    }
	
    /** Removes the first data point from the queue
	 *  */  
    public void delete() {
    	this.poll();		
    }
    
    /** Removes the all data point from the queue,
     * which do not match the minimum time
	 *  */  
    public void delete(long minTime) {
        DataPoint dataPoint = new DataPoint();    	
        	
        dataPoint = this.peek();
        while (dataPoint.time < minTime) {
            dataPoint = this.poll();
        } 		
    }    
}
