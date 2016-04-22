package cn.spreadtrum.com.stringtoaction;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.spreadtrum.com.stringtoaction.util.GetCityFormGoogleMap;
import cn.spreadtrum.com.stringtoaction.util.GetYahooCityCodeSaxTools;
import cn.spreadtrum.com.stringtoaction.util.GetYahooWeatherSaxTools;

/**
 * Created by SPREADTRUM\joe.yu on 4/5/16.
 */
public class WeatherAction extends BaseAction {


    private static final long MAX_TIMEOUT = 10000;
    private HashMap<String, String> cityCodeHashMap;
    private ArrayList<HashMap<String,String>> weatherArrayList;
    private String CityCodeUrl = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D'"+ "上海" + "'&diagnostics=true";




    public WeatherAction(Context mContext) {
        super(mContext);
        //initLocation();
        //getCurrentLocation();

    }

    String  updateLocation(){
        LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("joe","location changed--->"+location.getLatitude()+" : "+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e("joe","onProviderEnabled --->"+provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e("joe","onProviderDisabled --->"+provider);
            }
        };
        boolean wifienabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsenabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Location tempLocation = null;
        if(wifienabled || gpsenabled){
            Location lastLocation_wifi = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location lastLocation_gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("joe","lastLocation_wifi -->"+lastLocation_wifi);
            Log.e("joe","lastLocation_gps -->"+lastLocation_gps);
            if(lastLocation_gps != null){
                tempLocation = lastLocation_gps;
            }else{
                tempLocation = lastLocation_wifi;
            }
        }else{
            // let user enable...
        }
        String city = "";
        if(tempLocation != null){
            Log.e("joe","current location...."+tempLocation.getLatitude()+":"+tempLocation.getLongitude());
            CodinateToCityAsync  ct = new CodinateToCityAsync();
            try {
                city = ct.execute(tempLocation.getLatitude() + "," + tempLocation.getLongitude()).get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        Log.e("joe","city--->"+city);
        return city;
    }

    @Override
    protected ParseResult runAction(String src, String key) {
        //return super.runAction(src, key);

        //return super.runAction(src,key);
        Log.e("joe","weather--->"+src);
        String resultString  = parseWeatherRequest(src, key);
        if(resultString != null && !resultString.equals("")) {
            return new ParseResult(ParseResult.RES_OK, resultString);
        }
        return new ParseResult(ParseResult.RES_OP_FAILED,mContext.getResources().getString(R.string.parse_result_op_failed_op_failed));
    }

    @Override
    protected void init() {
       mPossStrings = mContext.getResources().getStringArray(R.array.weather_actions);
    }





    static final int DAY_TODAY = 0;
    static final int DAY_TOMORROW = 1;
    static final int DAY_AF_TOMORROW = 2;
    static final int DAY_WEEK = 3;

    private class WeatherElement{
        int date;
        String city;

        public WeatherElement(int date, String city) {
            this.date = date;
            this.city = city;
        }
    }


    WeatherElement getDateFromSrc(String src){
        int ret  = DAY_WEEK;
        Log.e("joe","getDateFromSrc --->"+src);
        String [] today = mContext.getResources().getStringArray(R.array.date_today);
        String [] tomorrow = mContext.getResources().getStringArray(R.array.date_tomorrow);
        String [] af_tomorrow = mContext.getResources().getStringArray(R.array.date_day_after_tomorrow);
        String [] af_week = mContext.getResources().getStringArray(R.array.date_day_week);
        if(src.contains("")){
            ret = DAY_TODAY;
        }
        for(String tmp : today){
            if(src.contains(tmp)){
                src = src.replace(tmp,"");
                ret = DAY_TODAY;
            }

        }
        for(String tmp : tomorrow){
            if(src.contains(tmp)){
                src = src.replace(tmp,"");
                ret =  DAY_TOMORROW;
            }
        }
        for (String tmp: af_tomorrow){
            if(src.contains(tmp)){
                ret =  DAY_AF_TOMORROW;
                src = src.replace(tmp,"");
            }
        }
        for (String tmp: af_week){
            if(src.contains(tmp)){
                ret =  DAY_WEEK;
                src = src.replace(tmp,"");
            }
        }
        Log.e("joe","getDateFromSrc --->"+src+"date --->"+ret);
        return new WeatherElement(ret,src);
    }

    String getCityFromSrc(String src){
        String city = "";
        try {
            city = new SaxCityCodeAsync().execute(src).get(MAX_TIMEOUT,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return city;
    }
    String    parseWeatherRequest(String src,String key){
        String [] preFixs = mContext.getResources().getStringArray(R.array.weather_prefix);
        String tmp = src.intern();
        String result = "";
        int date = DAY_WEEK;
        String city = "";
        Log.e("joe","Weather---->"+tmp);
        // delete weather
        tmp = tmp.replace(key,"");
        // delete how/what about/ show me
        Log.e("joe","Weather non-key ---->"+tmp);
        for(String prefix :preFixs){
            tmp = tmp.replace(prefix,"");
        }
        Log.e("joe","Weather non-fix ---->"+tmp);
        // usually we get the city and the date now :
        WeatherElement element =  getDateFromSrc(tmp);
        date = element.date;
        tmp = element.city;
        Log.e("joe","Weather non-date ---->"+tmp);
        if(!tmp.equals("")){
            city = getCityFromSrc(tmp);
        }
        if(city == null || city.equals("")){
            city = updateLocation();
        }
        if(!city.equals("")){
            element.city = city;
            result = getWeatherReport(element);
        }
        return result;
    }


    float WToC(float W){
        float base = ((W-32)/1.8f);
        return  (float)(Math.round(base*10))/10;
    }

    String genWeatherResultSpecific(ArrayList<HashMap<String,String>> result,int offset, String city){
        HashMap<String,String> oneday = result.get(offset);
        String day = mContext.getResources().getStringArray(R.array.weather_answer_date)[offset];
        String high = mContext.getResources().getString(R.string.weather_answer_temp_high);
        String low = mContext.getResources().getString(R.string.weather_answer_temp_low);
        String unit = mContext.getResources().getString(R.string.weather_answer_temp_unit_w);
        String weather = oneday.get("text");
        float degree_high = Float.valueOf(oneday.get("high"));
        float degree_low = Float.valueOf(oneday.get("low"));
        if(mContext.getResources().getConfiguration().locale.getCountry().equals(Locale.CHINA.getCountry())){
            unit = mContext.getResources().getString(R.string.weather_answer_temp_unit_c);
            degree_high = WToC(degree_high);
            degree_low = WToC(degree_low);
            int code = Integer.valueOf(oneday.get("code"));
            int len = mContext.getResources().getStringArray(R.array.weather_results_yahoo).length -1;
            if(code >len ){
                code = len;
            }
            weather = mContext.getResources().getStringArray(R.array.weather_results_yahoo)[code];

        }
        return day+city+weather+high+degree_high+unit+low+degree_low+unit;
    }

    String handleWeatherResult(ArrayList<HashMap<String,String>> result,WeatherElement element){
        String ret = "";
        if(element.date < DAY_WEEK){
            ret =  genWeatherResultSpecific(result,element.date,element.city);
        }else {
            ret = genWeatherResultSpecific(result,DAY_TODAY,element.city)+genWeatherResultSpecific(result,DAY_TOMORROW,element.city);
        }
        return ret;
    }

    String getWeatherReport(WeatherElement element){
        try {
            ArrayList<HashMap<String,String>> result = new SaxWeatherAsync().execute(element.city).get(MAX_TIMEOUT,TimeUnit.MILLISECONDS);
            return handleWeatherResult(result,element);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        return null;
    }




































    class CodinateToCityAsync extends AsyncTask<String, Void, String>{
        String city = "";
        @Override
        protected void onPostExecute(String s) {
            //new SaxWeatherAsync().execute(city);
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            if(params != null){
                data = (String)params[0];
            }
            String tag = mContext.getResources().getConfiguration().locale.toLanguageTag();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=%20"+data+"%20&language="+tag+"&sensor=true");
            HttpResponse response = null;
            String result = null;
            try {
                response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    result =  EntityUtils.toString(entity);
                    city = GetCityFormGoogleMap.getCity(new JSONObject(result));
                } else {
                    Log.e("joe", "Failed to download file");
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return city;
        }
    }


    class SaxCityCodeAsync extends AsyncTask<String, Void, String> {
        SAXParserFactory factory = null;
        SAXParser saxParser = null;
        XMLReader xmlReader = null;
        GetYahooCityCodeSaxTools tools = null;

        @Override
        protected void onPreExecute() {
            try {
                factory = SAXParserFactory.newInstance();
                saxParser = factory.newSAXParser();
                xmlReader = saxParser.getXMLReader();
                cityCodeHashMap = new HashMap<String, String>();
                tools = new GetYahooCityCodeSaxTools(cityCodeHashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String ret = "";
            String city = "";
            if(params != null){
                city = params[0];
            }
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D'"+ city+ "'&diagnostics=true");
                HttpResponse response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    xmlReader.setContentHandler(tools);
                    xmlReader.parse(new InputSource(new InputStreamReader(content)));
                    ret = cityCodeHashMap.get("name");
                } else {
                    Log.e("joe", "Failed to download file");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ret;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("joe", ""+result);
           // new SaxWeatherAsync().execute(result);
        }

    }

    class SaxWeatherAsync extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {
        SAXParserFactory factory = null;
        SAXParser saxParser = null;
        XMLReader xmlReader = null;
        GetYahooWeatherSaxTools tools = null;
        String WeatherUrl=null;

        @Override
        protected void onPreExecute() {
            try {
                factory = SAXParserFactory.newInstance();
                saxParser = factory.newSAXParser();
                xmlReader = saxParser.getXMLReader();
                weatherArrayList=new ArrayList<HashMap<String,String>>();
                 tools = new GetYahooWeatherSaxTools(weatherArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... params) {
            String citycode=params[0];
            //WeatherUrl="http://xml.weather.yahoo.com//forecastrss?w="+citycode+"&u=c";
            Log.e("joe","city:"+citycode);
            WeatherUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D'"+citycode+"')%20&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(WeatherUrl);
                HttpResponse response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    xmlReader.setContentHandler(tools);
                    xmlReader.parse(new InputSource(new InputStreamReader(content)));

                } else {
                    Log.e("joe", "Failed to download file");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return weatherArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
            Log.e("joe", "weather:"+result.toString());
        }

    }


}
