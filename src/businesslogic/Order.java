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
public class Order {//Прописать определение CurrentCoast стоимости 
    
    public final static int OPEN = 0x1;//содаём заказ.
    public final static int INPROGRESS = 0x2;// отправляем заказ прорабу.
    public final static int WAITING_ACKNOWLEDGMENT_TAKE = 0x3;// запрос на приём заказа.
    public final static int WAITING_PAY = 0x4;// ожидание оплаты от клиента
    public final static int WAITING_ACKNOWLEDGMENT_PAY = 0x5;// запрос на приём оплаты менеджером.
    public final static int CLOSE = 0x6;//закрытие заказы.
    
    int id; // id заказа.
    int Number;//номер заказа
    int Status;//Статус заказа.
    //определяет открыт или закрыт заказ
    int ClientID;//определяет к какому Клиенту принадлежит заказ.
    int ManagerID;//определяет к какому Менеджеру принадлежит заказ.
    
    double CurrentCoast;
    double TotalCoast;    
    Date Create;
    Date LastUpdate;
    Date End;
    ArrayList<Estimate> EstimateList;
    
    //время брать от сервера базы данных, время не объект бизнес логики
    public Order(Date Create,int Number) {
        this.Number = Number;
        this.Create = Create;
        this.LastUpdate = (Date)Create.clone();
        CurrentCoast = 0;
        TotalCoast = 0;
        Status = OPEN;
    }
    //для инициализации от базы данных
    public Order(int Number, 
            int Status, 
            int ClientID, 
            int ManagerID, 
            double CurrentCoast, 
            double TotalCoast, 
            Date Create, 
            Date LastUpdate, 
            Date End, 
            ArrayList<Estimate> EstimateList) {
        this.Number = Number;
        this.Status = Status;
        this.ClientID = ClientID;
        this.ManagerID = ManagerID;
        this.CurrentCoast = CurrentCoast;
        this.TotalCoast = TotalCoast;
        this.Create = Create;
        this.LastUpdate = LastUpdate;
        this.End = End;
        this.EstimateList = EstimateList;
    }
    
    public void setEstimate(ArrayList<Estimate> EstimateList) {
        this.EstimateList = EstimateList;
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
        if(End != null){//  защита от дурака на всякий случай!
            this.End = End;
            return true;
        }else{
            return false;
        }
        //Старый вариант
        /*if(End != null){//  защита от дурака на всякий случай! 
        //в иделаьном случае все эти проверки осуществляет менеджер.
            if(CurrentCoast == 0){
                Status = CLOSE;//так же повторная запись
                this.End = End;
                return true;
            }
        }
        return false;*/        
    }

    public void clear(){
        EstimateList.clear();
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
    
    public boolean addEstimapte(Estimate e){
        if(e != null){
            if(EstimateList == null){EstimateList = new ArrayList<>();}
            EstimateList.add(e);
            return true;
        }else{
            return false;
        }
    }
    
    void setEstimate(int i,Estimate e){
        if(EstimateList != null){
            EstimateList.set(i, e);
        }
    }
    
    public void deleteEstimate(int i){
        if(EstimateList != null){
            if(i < EstimateList.size()){
                EstimateList.remove(i);
            }
        }
    }
    
    /** 
     * @return Стоимость всего заказа
     */
    public double CoastCalculation(){
        if(EstimateList != null){
            if(!EstimateList.isEmpty()){
                EstimateList.stream().forEach((EpartElem) -> {
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
                Status = WAITING_ACKNOWLEDGMENT_PAY;
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

    public boolean setCurrentCoast(double CurrentCoast) {
        if(CurrentCoast > 0){
            this.CurrentCoast = CurrentCoast;
            return true;
        }else{
            return false;
        }
    }
    
    //текущая задолженность
    public double getCurrentCoast() {
        return CurrentCoast;
    }
    
    boolean isFinish(){
        return EstimateList.stream().noneMatch((Estimate1) -> (!Estimate1.isFinish()));
    }
}
