package santa.v1;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class Typewriter extends TextView {

    private String mText;
    private int mIndex;
    private long mDelay = 30; //Default 500ms delay
    private long mDelayScroll = 1000;


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
        	if(mText.charAt(mIndex)== '\n') {
        		mText = mText.substring(mIndex + 1, mText.length()-1);
        		mIndex = 0;
 	            if(mIndex < mText.length()) {
 	                mHandler.postDelayed(characterAdder, mDelayScroll);
 	            }
        	}
        	else {
	        	while(mIndex < mText.length() && mText.charAt(mIndex)== '\n') mIndex++;
	            setText(mText.subSequence(0, mIndex++));
	            if(mIndex < mText.length()) {
	                mHandler.postDelayed(characterAdder, mDelay);
	            }	        	
        	}
        }
    };

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