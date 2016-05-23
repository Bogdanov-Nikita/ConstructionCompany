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
    
    //TakeResources
    public final static int TAKE_RESORSE_SUCCESS = 0x21;
    public final static int INSUFFICIENTLY_RESORSE = 0x22;
    public final static int RESORSE_EMPTY = 0x23;
    public final static int RESORSE_NOT_FOUND = 0x24;
    public final static int STORAGE_EMPTY = 0x25;
    //SendResources
    public final static int SEND_RESORSE_SUCCESS = 0x26;
    public final static int STORAGE_RESOURSE_FAIL = 0x27;
    public final static int ADD_RESOURSE_FAIL = 0x28;
    
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

    public void setId(int Id) {
        this.Id = Id;
    }    
    
    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }
    
    public int findResoursePositionByType(int type){
        for(int i = 0; i < Resources.size(); i++){
            if(Resources.get(i).getType() == type){
                return i;
            }
        }
        return -1;
    }
    
    public int findResoursePositionById(int Id){
        for(int i = 0; i < Resources.size(); i++){
            if(Resources.get(i).getId() == Id){
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Устарел
     * взять ресурс со склада
     * @param i - номер ресурса на складе
     * @param Amount - количество ресурса
     * @return успешно или тип ошибки.
     */
    public int TakeResources(int i,int Amount){
        if(Resources != null){
            if(i < Resources.size()){
                if(Resources.get(i).getAmount() > 0 ){
                    int tempAmount = Resources.get(i).getAmount() - Amount;
                    if(tempAmount >= 0){
                        Resources.get(i).setAmount(tempAmount);
                        return TAKE_RESORSE_SUCCESS;
                    }else{
                        Resources.get(i).setAmount(0);
                        return INSUFFICIENTLY_RESORSE;
                    }
                }else{
                    return RESORSE_EMPTY;
                }
            }else{
                return RESORSE_NOT_FOUND;
            }
        }else{
            return STORAGE_EMPTY;
        }
    }
    
    /**
     * получить ресурс
     * @param Type - тип ресурса который мы отправляем на склад
     * @param Amount - количество ресурса котрое хотим добавить
     * @return успех или тип ошибки. 
     */
    public int SendResources(int Type,int Amount){
        if(Resources != null){
            int index = findResoursePositionByType(Type);
            if(index >= 0){
                if(Resources.get(index).setAmount(Resources.get(index).getAmount() + Amount)){
                    return SEND_RESORSE_SUCCESS;
                }else{
                    return ADD_RESOURSE_FAIL;
                }
            }else{
                return RESORSE_NOT_FOUND;
            }
        }else{
            return STORAGE_RESOURSE_FAIL;
        }
    }
    
    public void addResource(Resource res) {
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
