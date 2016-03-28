/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Nik
 *///Заказ
public class Estimate {
    
    boolean Open;
    int Number;
    double CurrentCoast;
    double TotalCoast;
    Date Create;
    Date LastUpdate;
    Date End;
    ArrayList<EstimatePart> Epart;
    
    //время брать от сервера базы данных, время не объект бизнес логики
    public Estimate(Date Create,int Number) {
        this.Number = Number;
        this.Create = Create;
        this.LastUpdate = (Date)Create.clone();
        CurrentCoast = 0;
        TotalCoast = 0;
        Open = true;
    }

    public void setEstimate(ArrayList<EstimatePart> Epart) {
        this.Epart = Epart;
    }

    public void setLastUpdate(Date LastUpdate) {
        if(LastUpdate != null){
            this.LastUpdate = LastUpdate;
        }
    }

    public Date getCreate() {
        return Create;
    }

    public Date getLastUpdate() {
        return LastUpdate;
    }

    public Date getEnd() {
        return End;
    }
    
    //закрытие заказа не означает уничтожение документа!
    public boolean CloseEstimate(Date End) {
        if(End != null){
            if(CurrentCoast == 0){
                Open = false;
                this.End = End;
                return true;
            }
        }
        return false;        
    }

    public boolean isOpen() {
        return Open;
    }
    
    
    public void addEstimaptePart(EstimatePart e){
        if(e != null){
            if(Epart == null){Epart = new ArrayList<>();}
            Epart.add(e);
        }
    }
    
    void set(int i,EstimatePart e){
        if(Epart != null){
            Epart.set(i, e);
        }
    }
    
    public void deleteEstimatePart(int i){
        if(Epart != null){
            if(i < Epart.size()){
                Epart.remove(i);
            }
        }
    }
    
    /** 
     * @return Стоимость всего заказа
     */
    public double CostCalculation(){
        if(Epart != null){
            if(!Epart.isEmpty()){
                Epart.stream().forEach((EpartElem) -> {
                    TotalCoast = TotalCoast + EpartElem.CostCalculation();
                });
            }
        }
        return TotalCoast;
    }
    
    /**
     * пополнение счёта клиентом
     * @param pay - количество денежных единиц внесённых клиентом.
     * @return true в случе успеха иначе false
     */
    public boolean ClientPay(double pay){
        if(pay > 0){
            double tempCoast = CurrentCoast - pay;
            if(tempCoast  == 0){
                CurrentCoast = 0;
                Open = false;
                return true;
            }else{
                if(tempCoast > 0){
                    CurrentCoast = tempCoast;
                    return true;
                }
            }
        }
        return false;
    }
    //текущая задолженность
    public double getCurrentCoast() {
        return CurrentCoast;
    }
    
}
