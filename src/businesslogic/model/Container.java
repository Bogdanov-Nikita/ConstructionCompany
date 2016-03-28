/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import database.ContentValues;

/**
 *
 * @author Nik
 */
public class Container {
    ContentValues Data; 
    boolean empty;

    public Container(ContentValues Data) {
        this.Data = Data;
        empty = (Data != null);
    }
    //TODO: Добавить getInt,getString, ... как в Bundle от Android если нужно
    //вопрос дубликатов Key (одинаковых значений) для разных данных открытый!
    public void putObject(String Key,Object obj){
        if(Key != null && obj != null){
            Data.put(Key,String.valueOf(obj));
        }
    }
    public boolean containsKey(String Key){
        return Data.containsKey(Key);
    }
    public void putBolean(String Key,boolean Value){
        if(Key != null){
            Data.put(Key,String.valueOf(Value));
            empty = false;
        }
    }
    
    public void putDouble(String Key,double Value){
        if(Key != null){
            Data.put(Key,String.valueOf(Value));
            empty = false;
        }
    }
    
    public void putFloat(String Key,float Value){
        if(Key != null){
            Data.put(Key,String.valueOf(Value));
            empty = false;
        }
    }
    
    public void putInteger(String Key,int Value){
        if(Key != null){
            Data.put(Key, String.valueOf(Value));
            empty = false;
        }
    }
    
    public void putLong(String Key,long Value){
        if(Key != null){
            Data.put(Key, String.valueOf(Value));
            empty = false;
        }
    }    
    
    public void putString(String Key,String Value){
        if(Key != null && Value != null){
            Data.put(Key, Value);
            empty = false;
        }
    }
    
    public ContentValues getData() {
        return Data;
    }

    public boolean isEmpty() {
        return empty;
    }
    
}
