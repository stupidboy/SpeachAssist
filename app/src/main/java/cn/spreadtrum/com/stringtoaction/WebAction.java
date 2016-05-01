package cn.spreadtrum.com.stringtoaction;

import android.content.Context;

/**
 * Created by SPREADTRUM\joe.yu on 4/26/16.
 */
public class WebAction  extends BaseAction{
    public WebAction(Context mContext) {
        super(mContext);
    }


    @Override
    protected void init() {
        super.init();
        mPossStrings = mContext.getResources().getStringArray(R.array.web_action);
    }

    @Override
    protected ParseResult runAction(String src, String key) {
        return super.runAction(src, key);
    }

    @Override
    protected String adapt(String action) {
        return super.adapt(action);
    }



}
