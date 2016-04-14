package cn.spreadtrum.com.stringtoaction;

import android.content.Context;
import android.util.Log;

/**
 * Created by SPREADTRUM\joe.yu on 3/21/16.
 */
public class BaseAction {
protected Context mContext;
protected String[] mPossStrings = null;

protected  ParseResult runAction(String src, String key) {
    return new ParseResult(ParseResult.RES_NOT_REC,mContext.getString(R.string.parse_result_op_failed_op_not_support));

}

    public BaseAction(Context mContext) {
        this.mContext = mContext;
        init();
    }
    protected void  init(){

    }

    protected  String getName(){
        return "base";
    }
    protected String adapt(String action){
        action = action.toLowerCase();
        Log.e("joe","action--->"+action);
        if(mPossStrings != null){
            for(String string : mPossStrings){
                if(action.contains(string)){
                    //found!
                    return string;
                }
            }
        }
        return null;
    }
}
