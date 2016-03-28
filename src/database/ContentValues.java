/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

import java.util.ArrayList;

/**
 *
 * @author Nik
 */

public class ContentValues {

    ArrayList<Value> List;

    public ContentValues() {
        List = new ArrayList<>();
    }
    
    public boolean containsKey(String Key){
        return List.stream().anyMatch((List1) -> (List1.name.compareTo(Key) == 0));
    }
    
    public void put(String name,String value){
        List.add(new Value(name, value));
    }
    
    public Value get(int i){
       return List.get(i);
    }
    
    public boolean isEmpty(){
        return List.isEmpty();
    }    
    
    public int size(){
        return List.size();
    }
    
    public Value set(int i,Value v){
        return List.set(i, v);
    }    
    
    public Value remove(int i){
        return List.remove(i);
    }
    
    public void clear(){
        List.clear();
    }
    
}
