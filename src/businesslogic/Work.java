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
 * Работа - соответствует позиции работы в заказе,
 * для выполнения котрой требуется определённое количесво ресурсов,
 * количество ресурсов может быть и нулевым.
 */
public class Work {
    
    double ServiceCoast;            //Стоимось услуги без стоимости ресурсов.
    String Description;             //Описание услуги.
    ArrayList<Resource> Resources;  //Ресурсы.

    public Work(ArrayList<Resource> Resources,double ServiceCoast,String Description) {
        if(ServiceCoast < 0){
            this.ServiceCoast = 0;
        }else{
            this.ServiceCoast = ServiceCoast;
        }
        this.Description = Description;
        this.Resources = Resources;
    }
    
    public void add(Resource res){
        if(res != null){
            if(Resources == null){
                Resources = new ArrayList<>();
            }
            Resources.add(res);
        }
    }
    
    void set(int i,Resource e){
        if(Resources != null){
            Resources.set(i, e);
        }
    }
    
    void delete(int i){
        if(Resources != null){
            if(i < Resources.size()){
                Resources.remove(i);
            }
        }
    }
    /**
     *@return - количсетво ресурсов 
     */
    public int AmountResources(){
        if(Resources != null){
            return Resources.size();
        }else{
            return 0;
        }
    }
    /**
     * @return - рассчёт стоимости услуги и ресурсов.
     */
    double CostCalculation(){
        double Coast = ServiceCoast;
        if(Resources != null){
            for (Resource Resource : Resources) {
                Coast = Coast + Resource.getCoast();
            }
        }
        return Coast;
    }
    
}
