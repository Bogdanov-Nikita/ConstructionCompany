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
    int Id;
    boolean finish;                 //Завершенно выполнение услуги или нет.
    double ServiceCoast;            //Стоимось услуги без стоимости ресурсов.
    String Description;             //Описание услуги.
    ArrayList<Resource> resources;  //Ресурсы.
    

    public Work(int id,ArrayList<Resource> Resources,double ServiceCoast,String Description) {
        if(ServiceCoast < 0){
            this.ServiceCoast = 0;
        }else{
            this.ServiceCoast = ServiceCoast;
        }
        this.Description = Description;
        this.resources = Resources;
    }
    
    public void add(Resource res){
        if(res != null){
            if(resources == null){
                resources = new ArrayList<>();
            }
            resources.add(res);
        }
    }
    
    public void set(int i,Resource e){
        if(resources != null){
            resources.set(i, e);
        }
    }
    
    public void delete(int i){
        if(resources != null){
            if(i < resources.size()){
                resources.remove(i);
            }
        }
    }
    
    /**
     * @param type - тип ресурса
     * @return - количсетво ресурсов данного типа.
     */
    public int amountResources(int type){
        if(resources != null){
            for (Resource Resource : resources) {
                if (Resource.getType() == type) {
                    return Resource.getAmount();
                }
            }
            return 0;
        }else{
            return 0;
        }
    }
    
    /**
     * @return - рассчёт стоимости услуги и ресурсов.
     */
    public double CoastCalculation(){
        double Coast = ServiceCoast;
        if(resources != null){
            for (Resource Resource : resources) {
                Coast = Coast + (Resource.Amount * Resource.getCoast());
            }
        }
        return Coast;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public void setResources(ArrayList<Resource> Resources) {
        this.resources = Resources;
    }
    
    public ArrayList<Resource> getResources() {
        return resources;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
    
    public String getDescription() {
        return Description;
    }
    
    public void setServiceCoast(double ServiceCoast) {
        if(ServiceCoast < 0){
            this.ServiceCoast = 0;
        }else{
            this.ServiceCoast = ServiceCoast;
        }
    }

    public double getServiceCoast() {
        return ServiceCoast;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }
    
    
}
