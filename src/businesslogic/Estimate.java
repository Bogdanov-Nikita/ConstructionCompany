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
//Смета
public class Estimate {
    
    public final static int MAIN = 0x1;
    public final static int ADDITIONAL = 0x2;
    
    boolean Paid;
    int Type;
    double Coast;
    ArrayList<Work> WorkList;

    public Estimate(boolean Paid, int Type, double Coast, ArrayList<Work> WorkList) {
        this.Paid = Paid;
        this.Type = Type;
        this.Coast = Coast;
        this.WorkList = WorkList;
    }    
    
    public Estimate(int Type,ArrayList<Work> WorkList) {
        this.Paid = false;
        this.Type = Type;
        this.Coast = 0;
        this.WorkList = WorkList;
    }
    
    /** 
     * @return Стоимость сметы
     */
    public double CostCalculation(){
        if(WorkList != null){
            if(!WorkList.isEmpty()){
                WorkList.stream().forEach((WorkListElem) -> {
                    Coast = Coast + WorkListElem.CoastCalculation();
                });
            }
        }
        return Coast;
    }
    
    public void add(Work e){
        if(e != null){
            if(WorkList == null){WorkList = new ArrayList<>();}
            WorkList.add(e);
        }
    }
    
    public void set(int i,Work e){
        if(WorkList != null){
            WorkList.set(i, e);
        }
    }
    
    public void delete(int i){
        if(WorkList != null){
            if(i < WorkList.size()){
                WorkList.remove(i);
            }
        }
    }

    public void setCoast(double Coast) {
        this.Coast = Coast;
    }    
    
    public double getCoast() {
        return Coast;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getType() {
        return Type;
    }

    public void setPaid(boolean Paid) {
        this.Paid = Paid;
    }

    public boolean isPaid() {
        return Paid;
    }
    
    public void setWorkList(ArrayList<Work> WorkList) {
        this.WorkList = WorkList;
    }
    
    public ArrayList<Work> getWorkList() {
        return WorkList;
    }
    
    public boolean isFinish(){//проверка есть ли ещё невыполнкенная работа
        return WorkList.stream().noneMatch((WorkList1) -> (!WorkList1.isFinish()));
    }
    
}
