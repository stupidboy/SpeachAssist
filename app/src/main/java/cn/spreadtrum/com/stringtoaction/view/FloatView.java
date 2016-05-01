package cn.spreadtrum.com.stringtoaction.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by SPREADTRUM\joe.yu on 4/26/16.
 */
public class FloatView extends LinearLayout{


    /*
*
* voice recgnized result
*
* action..... 1
*
* voice recgnzied result .....
*
* action ......2
*
*
* */
    Context mContext ;
    public FloatView(Context context) {
        super(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void init(Context context){

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:

        }



        return super.onInterceptTouchEvent(ev);
    }
}
