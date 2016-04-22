package cn.spreadtrum.com.stringtoaction.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Created by SPREADTRUM\joe.yu on 4/5/16.
 */
public class GetYahooCityCodeSaxTools extends DefaultHandler {
    private HashMap<String,String> hashMap;
    private boolean isAdd=false;

    public GetYahooCityCodeSaxTools(HashMap<String,String> hashMap){
        this.hashMap=hashMap;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if(localName=="name"){
            isAdd=true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if(isAdd){
            String str=new String(ch, start, length);
            hashMap.put("name",str);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if(localName=="name"){
            isAdd=false;
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

}

