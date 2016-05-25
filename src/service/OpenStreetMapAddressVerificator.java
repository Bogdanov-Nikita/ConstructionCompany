/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.stream.JsonParser;

/**
 *
 * @author Nik
 */
public class OpenStreetMapAddressVerificator {

    ArrayList<String> addreslist = null;
    
    public OpenStreetMapAddressVerificator(String addres) throws UnsupportedEncodingException, MalformedURLException, IOException {
        String encodestr = URLEncoder.encode(addres, "UTF-8").replaceAll("\\%28", "(")                          
            .replaceAll("\\%29", ")")   		
            .replaceAll("\\+", "%20")                          
            .replaceAll("\\%27", "'")   			   
            .replaceAll("\\%21", "!")
            .replaceAll("\\%7E", "~");        
        URL url = new URL("http://nominatim.openstreetmap.org/search?q="+encodestr+"&format=json&polygon=1&addressdetails=1");
        addreslist = null;
        try (InputStream ios = url.openStream()) {
            addreslist = new ArrayList<>();
            boolean KeyValue = false;
            JsonParser parser = Json.createParser(ios);
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                switch(event) {
                    case START_ARRAY:
                    case END_ARRAY:
                    case START_OBJECT:
                    case END_OBJECT:
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case VALUE_TRUE:break;
                    case KEY_NAME:
                        if(parser.getString().equalsIgnoreCase("display_name")){KeyValue = true;}
                        break;
                    case VALUE_STRING:
                        if(KeyValue){
                            addreslist.add(parser.getString());
                            KeyValue = false;
                        }
                        break;
                    case VALUE_NUMBER:
                        break;
                }
            }
        }
    }

    public ArrayList<String> getAddreslist() {
        return addreslist;
    }
    
}
