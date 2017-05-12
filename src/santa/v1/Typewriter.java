package santa.v1;

import santa.v1.Engine.TutorialState;
import android.R.mipmap;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class Typewriter extends TextView {

    private String mText;
    private int mIndex;
    private long mDelay = 30; //Default 500ms delay
    private long mDelayScroll = 1000;
    private boolean isWaiting = false;


    public Typewriter(Context context) {
        super(context);
    }

    public Typewriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
        	if(mIndex < mText.length()){
	        	if(mText.charAt(mIndex)== '\n') {
	        		mText = mText.substring(mIndex + 1, mText.length()-1);
	        		mIndex = 0;
	 	            if(mIndex < mText.length()){
	 	            	if(Engine.TutorialCurrentState == TutorialState.Screen1){
		            		new Handler().postDelayed(new Runnable() {
								public void run() {
									Engine.TutorialGrayY = 0.2f;
				            		Engine.TutorialGrayRMin = 0.3f;
				            		Engine.TutorialGrayRMax = 0.4f;
								}
							}, mDelayScroll + 600);		            	
		            	}
	 	                mHandler.postDelayed(characterAdder, mDelayScroll);
	 	                isWaiting = true;
	 	            }
	        	}
	        	else {
	        		isWaiting = false;
		        	while(mIndex < mText.length() && mText.charAt(mIndex)== ' ') {
		        		if(mIndex + 1 < mText.length())
		        			if(mText.charAt(mIndex+1)== '\n')break;
		        		mIndex++;		        		
		        	}	        		
		            setText(mText.subSequence(0, mIndex++));
		            if(mIndex < mText.length()) {
		                mHandler.postDelayed(characterAdder, mDelay);
		            }	        	
	        	}
        	}
        	
        	if (mIndex >= mText.length()){
        		if(Engine.TutorialCurrentState == TutorialState.Screen2 && !Engine.TutorialDrawAnim){
        			new Handler().postDelayed(new Runnable() {
						public void run() {
							Engine.TutorialCurrentState = SantaActivity.nextState();
						}
					}, 600);      			
        		}
        		else Engine.TutorialTextFinished = true;
        	}
        }
    };

    public void nextLine()
    {
    	
    	if(mText.length() > 0){
	    	if(Engine.TutorialTextFinished) Engine.TutorialCurrentState = SantaActivity.nextState();
	    	else{
		    	 while(mIndex < mText.length()){
		     		if(mText.charAt(mIndex)== '\n') {
		     			break;
		     		}
		     		else mIndex++;
		     	}
		    	 if(mIndex < mText.length()) {
			    	 mText = mText.substring(mIndex + 1, mText.length()-1);
			    	 mIndex =0;
			    	 mHandler.removeCallbacks(characterAdder);
			         mHandler.postDelayed(characterAdder, 0);
		    	 }
		    	 else if (isWaiting) {
		    		 mIndex = 0;
		    		 mHandler.removeCallbacks(characterAdder);
			         mHandler.postDelayed(characterAdder, 0);
		    	 }
		    	 else {
		    		 setText(mText);
		    		 Engine.TutorialTextFinished = true;
		    	 }
	    	}
    	}
    	 
    }
    
    public void animateText(String text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }
    

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}