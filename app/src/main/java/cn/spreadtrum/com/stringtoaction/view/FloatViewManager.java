package cn.spreadtrum.com.stringtoaction.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.spreadtrum.com.stringtoaction.R;

/**
 * Created by SPREADTRUM\joe.yu on 4/26/16.
 */
public class FloatViewManager {
    Context mContext;
    WindowManager mWm;
    FloatView mView;
    boolean show = false;
    WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    public FloatViewManager(Context context){
        mContext = context;
        mWm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = (FloatView) LayoutInflater.from(mContext).inflate(R.layout.float_view, null);
       // mParams = (WindowManager.LayoutParams) mView.getLayoutParams();
        //mParams.flags |= WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.width = 300;
        mParams.height = 300;
        mParams.x = 0;
        mParams.y = 0;
        mParams.gravity = Gravity.TOP;
        mView.setLayoutParams(mParams);
        mView.getBackground().setAlpha(50);
        show = false;

    }
    public void showText(String text,boolean left){
        TextView view = new TextView(mContext);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setTextDirection(View.TEXT_ALIGNMENT_TEXT_END);
        int i = 0;
        show(view);
    }

    public void showText(String text){
        TextView view = new TextView(mContext);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        show(view);
    }
    public void setParam(WindowManager.LayoutParams param){
        mParams = param;
    }
    public WindowManager.LayoutParams getmParams(){
        return mParams;
    }
   public void show(View content){
       mView.addView(content);
       if(!show){
       mWm.addView(mView, mParams);
       show = true;
       }
       else{
           mWm.updateViewLayout(mView,mParams);
       }
   }

    public void hide(){
        mWm.removeViewImmediate(mView);
        show = false;
    }


}
