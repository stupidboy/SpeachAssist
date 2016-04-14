package cn.spreadtrum.com.stringtoaction.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SPREADTRUM\joe.yu on 4/11/16.
 */
public class GetCityFormGoogleMap {
    static String RESULT_TAG = "results";
    static String ADDRES_COM_TAG = "address_components";
    static String ADDRES_ROTES = "types";
    static String ADDRES_SUB_LOCAL = "sublocality_level_1";
    static String ADDRES_LOCAL = "locality";
    static String  LONG_NAME  = "long_name";
    static String SHORT_NAME = "short_name";
    static String  TYPE = "types";


    static public  String getCity(JSONObject data){
        String sublocal = "";
        String local = "";
        int idex = 0;
        try {
            JSONArray result = data.getJSONArray(RESULT_TAG);
            JSONObject addres_com = result.getJSONObject(0);
            JSONArray addres = addres_com.getJSONArray(ADDRES_COM_TAG);
            Log.e("joe", "addres_com -->" + addres);
            for( idex= 0; idex < addres.length() -1;idex++)
            {
                String type = addres.getJSONObject(idex).getString(TYPE);
                if(type.contains(ADDRES_SUB_LOCAL)) {
                    sublocal = addres.getJSONObject(idex).getString(LONG_NAME);
                }
                if(type.contains(ADDRES_LOCAL)) {
                    local = addres.getJSONObject(idex).getString(LONG_NAME);
                }
            }
            Log.e("joe", "sublocal -->" + sublocal);
            Log.e("joe", "local -->" + local);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return sublocal + local;
    }





}
