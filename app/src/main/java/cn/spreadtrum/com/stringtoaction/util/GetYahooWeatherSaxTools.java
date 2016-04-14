package cn.spreadtrum.com.stringtoaction.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SPREADTRUM\joe.yu on 4/5/16.
 */
public class GetYahooWeatherSaxTools extends DefaultHandler {
    private ArrayList<HashMap<String,String>> arrayList;
    private HashMap<String,String> hashMap;
    public GetYahooWeatherSaxTools(ArrayList<HashMap<String,String>> arrayList){
        this.arrayList=arrayList;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if(qName.equals("yweather:forecast")){
            hashMap=new HashMap<String,String>();

            hashMap.put("day",attributes.getValue("day"));
            hashMap.put("date",attributes.getValue("date"));
            hashMap.put("low",attributes.getValue("low"));
            hashMap.put("high",attributes.getValue("high"));
            hashMap.put("text",attributes.getValue("text"));
            hashMap.put("code",attributes.getValue("code"));
            arrayList.add(hashMap);

        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

}
