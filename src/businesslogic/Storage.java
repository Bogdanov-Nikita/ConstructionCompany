/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class Storage {
    int Id;                         //Номер склада (на будущее если появится несколько складов)
    String Location;                //Адрес склада
    ArrayList<Resource> Resources;  //Ресурсы.

    public Storage(int Id, String Location, ArrayList<Resource> Resources) {
        this.Id = Id;
        this.Location = Location;
        this.Resources = Resources;
    }

    public int getId() {
        return Id;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }
    
    /**
     * взять ресурс со склада
     * @param i - номер ресурса на складе
     * @return ресурс который мы взяли.
     */
    public Resource TakeResources(int i){
        if(Resources != null){
            if(i < Resources.size()){
                return Resources.remove(i);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    
    /**
     * получить ресурс
     * @param res - ресурс который мы отправляем на склад
     */
    public void SendResources(Resource res){
        if(Resources == null){
            Resources = new ArrayList<>();
        }
        Resources.add(res);
    }
    
    public Resource getResource(int i) {
        return (Resources != null) ? (
                (i < Resources.size()) ? Resources.get(i) : null) : null;
    }    
    
    public boolean isEmpty(){
        return((Resources == null) ? true : Resources.isEmpty());
    }
    
    public ArrayList<Resource> getResources() {
        return Resources;
    }

    public void setResources(ArrayList<Resource> Resources) {
        this.Resources = Resources;
    }
    
}
