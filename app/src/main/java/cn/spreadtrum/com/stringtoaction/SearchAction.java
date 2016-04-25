package cn.spreadtrum.com.stringtoaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Created by SPREADTRUM\joe.yu on 4/22/16.
 */
public class SearchAction extends BaseAction {


    public SearchAction(Context mContext) {
        super(mContext);
    }

    @Override
    protected void init() {
        super.init();
        mPossStrings = mContext.getResources().getStringArray(R.array.search_actions);
    }


    class NavigationData {
        public String source = "";
        public String target = "";

        public NavigationData(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }


    void startNavigationApps(NavigationData data) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (data.source == null || "".equals(data.source)) {
            uri = Uri.parse("http://maps.google.com/maps?daddr=" + data.target);
        } else {
            uri = Uri.parse("http://maps.google.com/maps?saddr=" + data.source + "&daddr=" + data.target);
        }
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    NavigationData getNavigationDataFromSrc(String src, String key) {
        String source = "";
        String target = "";
        // 从xxx到xxx的路线
        // 到xxx怎么走
        //  xxx怎么走
        // xxxd到yyy怎么走
        // 最近的xxxx
        // 去最近的xxx
        //最近的xxx怎么走
        String[] from_fix = mContext.getResources().getStringArray(R.array.search_prefixs_from);
        String[] to_fix  = mContext.getResources().getStringArray(R.array.search_prefixs_to);
        String[] how_fix = mContext.getResources().getStringArray(R.array.search_prefixs_how);

        //step 1 ,try to get the target:
        String to = "";
        try {
            for (String tmp : to_fix) {
                // if src contains "到"
                if (src.contains(tmp)) {
                    int index_to = src.indexOf(tmp);
                    //
                    String subString = src.substring(index_to+tmp.length());
                    for(String tmp1 :how_fix){
                        if(subString.contains(tmp1)) {
                            subString = subString.substring(0, subString.indexOf(tmp1));
                        }
                    }
                    // normally the target:
                    target = subString;
                    Log.e("joe","target:"+target);
                    if(index_to != 0){
                    int start_index = 0;
                    String subStringFrom = src;
                    for(String tmp2: from_fix){
                         if(src.contains(tmp2)){
                             start_index = src.indexOf(tmp2);
                             subStringFrom = subStringFrom.replace(tmp2,"");
                             break;
                         }
                    }
                    source = subStringFrom.substring(start_index,src.indexOf(tmp)-tmp.length());
                        Log.e("joe","source:"+source);
                    }
                    break;
                }
            }
            // if src not contains “到”，use src as target
            String temp = src;
            if(target.equals("")){
                 for (String tmp : how_fix){
                     temp = temp.replace(tmp,"");
                 }
                if(!temp.equals("")){
                    target = temp;
                    Log.e("joe","target2:"+target);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("joe","navigatin:source->"+source+": target->"+target);
        return new NavigationData(source, target);
    }


    ParseResult handleSearchAction(String src, String key) {
       NavigationData data = getNavigationDataFromSrc(src,key);
        if(!data.target.equals("")){
            startNavigationApps(data);
            return new ParseResult(ParseResult.RES_OK);
        }


        return new ParseResult(ParseResult.RES_NOT_REC);
    }

    @Override
    protected ParseResult runAction(String src, String key) {
        return handleSearchAction(src, key);
    }
}
