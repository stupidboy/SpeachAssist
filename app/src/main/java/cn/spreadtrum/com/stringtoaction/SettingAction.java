package cn.spreadtrum.com.stringtoaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SPREADTRUM\joe.yu on 4/3/16.
 */
public class SettingAction extends BaseAction {

   private String mStringOpen;
    private String mStringClose;
    private String mStringTurn;

    static final int ACT_WIFI = 0;
    static final int ACT_BT  = 1;
    static final int ACT_DATA = 2;
    static final int ACT_AIR = 3;
    static final int ACT_SPOT = 4;
    private  HashMap<String,Integer>mMap ;
    private  HashMap<String, ResolveInfo>mAppsMap;
    public SettingAction(Context mContext) {
        super(mContext);
    }


    ParseResult codeToActions(int code, boolean on){
        int ret = ParseResult.RES_OP_FAILED;
        switch (code){
            case ACT_WIFI:
                ret = handleWifi(on);
                break;
            case ACT_BT:
                ret = handleBt(on);
                break;
            case ACT_DATA:
                ret = handleMobileData(on);
                break;
            case ACT_AIR:
                ret = handleAirplanMode(on);
                break;
            case ACT_SPOT:
                ret = handleHotspot(on);
                break;
            default:
                break;
        }
        return new ParseResult(ret, ret == ParseResult.RES_OK ? mContext.getString(R.string.parse_result_op_ok_now):mContext.getString(R.string.parse_result_op_failed_op_not_support));
    }
    @Override
    protected void init() {
        mPossStrings = mContext.getResources().getStringArray(R.array.settingsactions);
        mStringOpen = mContext.getResources().getString(R.string.settingactions_open);
        mStringClose = mContext.getResources().getString(R.string.settingactions_close);
        mStringTurn = mContext.getResources().getString(R.string.settingactions_turn);
        // init hashmap:
        // for wifi:
        mMap = new HashMap<>();
        mAppsMap = new HashMap<>();
        String[] wifi = mContext.getResources().getStringArray(R.array.settingsitems_wifi);
        for(String string : wifi)
            mMap.put(string,ACT_WIFI);
        String[] bt = mContext.getResources().getStringArray(R.array.settingsitems_bt);
        for(String string : bt)
            mMap.put(string,ACT_BT);
        String[] data = mContext.getResources().getStringArray(R.array.settingsitems_data);
        for(String string : data)
            mMap.put(string,ACT_DATA);
        String[] air = mContext.getResources().getStringArray(R.array.settingsitems_air);
        for(String string: air)
            mMap.put(string,ACT_AIR);
        String[] spot = mContext.getResources().getStringArray(R.array.settingsitems_share);
        for(String string : spot)
            mMap.put(string,ACT_SPOT);
        getInstalledApplications();
    }

    ParseResult handleOpenCloseActions(String src, String key, boolean on){
        String cmd = src.replace(key,"").intern().toLowerCase();
        int code =  -1 ;
        if(mMap.get(cmd) != null) {
            code =mMap.get(cmd) ;
        }
        if(code >= 0){
            return codeToActions(code,on);
        }
        if(on && handleOpenApplicationActions(cmd).getResultCode() == ParseResult.RES_OK){
            return new ParseResult(ParseResult.RES_OK);
        }
        return new ParseResult(ParseResult.RES_NOT_REC,mContext.getString(R.string.parse_result_op_failed_op_not_support));
    }
    ParseResult handleTurnActions(String src, String key){
        return new ParseResult(ParseResult.RES_OP_FAILED);
    }
    @Override
    protected ParseResult runAction(String src, String key) {
        if(key.equals(mStringOpen)){
            return handleOpenCloseActions(src, key,true);
        }else if(key.equals(mStringClose)){
            return handleOpenCloseActions(src, key,false);
        }else if(key.equals(mStringTurn)){
            return handleTurnActions(src, key);
        }else{
            // try app directlly
            return handleOpenApplicationActions(src);
        }
    }

    @Override
    protected String adapt(String action) {
        action = action.toLowerCase();
        if(mPossStrings != null){
            for(String string : mPossStrings){
                if(action.contains(string)){
                    //found!
                    return string;
                }
            }
        }
        if(mAppsMap != null){
            if(mAppsMap.get(action) != null){
                return action;
            }
        }
        return null;
    }

    int handleWifi(boolean on){
        WifiManager wifimanager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        if(wifimanager != null){
            wifimanager.setWifiEnabled(on);
            return ParseResult.RES_OK;
        }
        return ParseResult.RES_OP_FAILED;
    }
    int handleBt(boolean on){
        BluetoothManager bm = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bm != null){
            if(on){
            bm.getAdapter().enable();
            }else {
                bm.getAdapter().disable();
            }
            return ParseResult.RES_OK;
        }
        return ParseResult.RES_OP_FAILED;
    }
    int handleMobileData(boolean on){
        TelephonyManager tele = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class[] args = new Class[1];
        args[0] = boolean.class;
        try {
            Method enable = tele.getClass().getMethod("setDataEnabled",args);
            enable.invoke(tele,on);
            return ParseResult.RES_OK;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return ParseResult.RES_OP_FAILED;
    }
    int handleAirplanMode(boolean on){
        Settings.Global.putInt(mContext.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,on? 1:0);
        return ParseResult.RES_OK;
    }
    int handleHotspot(boolean on){
        return ParseResult.RES_OP_FAILED;
    }

    void getInstalledApplications(){
        PackageManager pm = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent,0);
        if(resolveInfoList != null){
            for(ResolveInfo info : resolveInfoList){
                String tag = (String) info.loadLabel(pm);
                Log.e("joe", "tag:" + tag + "---->package " + info.activityInfo.packageName);
                mAppsMap.put(tag,info);
                //for camera
                if(info.activityInfo.packageName.contains("com.android.camera")){
                    String[] cam = mContext.getResources().getStringArray(R.array.camera_actions);
                    for (String string : cam){
                        mAppsMap.put(string, info);
                    }
                }

            }
        }
    }
    ParseResult handleOpenApplicationActions(String appName){
        ResolveInfo info = mAppsMap.get(appName);
        ComponentName componentName  = null;
        if(info != null){
         //start activity.....
            Intent intent = new Intent(Intent.ACTION_MAIN,null);
            intent.setPackage(info.activityInfo.packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            componentName = new ComponentName(info.activityInfo.packageName,info.activityInfo.name);
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Log.e("joe","---->"+componentName);
            try{
            mContext.startActivity(intent);}
            catch (Exception e){
                e.printStackTrace();
            }
            return new ParseResult(ParseResult.RES_OK);
        }
        return new ParseResult(ParseResult.RES_OP_FAILED,mContext.getString(R.string.parse_result_op_failed_op_not_support));
    }

}
