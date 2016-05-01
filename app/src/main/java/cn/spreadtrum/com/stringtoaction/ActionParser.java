package cn.spreadtrum.com.stringtoaction;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SPREADTRUM\joe.yu on 3/21/16.
 */
public class ActionParser {

    ArrayList<BaseAction>map = new ArrayList<>();

    Context mContext ;
    void initActions() {
        // init call :
        CallAction mCallAction= new CallAction(mContext);
        SettingAction mSettingsAction = new SettingAction(mContext);
        WeatherAction weatherAction =  new WeatherAction(mContext);
        SearchAction  searchAction = new SearchAction(mContext);
        map.add(weatherAction);
        map.add(searchAction);
        map.add(mCallAction);
        map.add(mSettingsAction);


    }

    public ActionParser(Context context){
        mContext = context;
        initActions();
    }
    ParseResult Parse(String src){

            for(BaseAction action:map){
                String key = null;
                if((key = action.adapt(src) )!= null){
                    return action.runAction(src,key);
                }
            }

        return new ParseResult(ParseResult.RES_NOT_REC,mContext.getString(R.string.parse_result_op_failed_op_not_support));
    }
}
