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
 */
public class Manager extends Role{
    
    public final static int ESTIMATE_SUCCESS = 0x1;
    public final static int ESTIMATE_CLIENT_NEED_PAY = 0x2;                     //клиент должен оплатить приведущие задолженности.
    public final static int ESTIMATE_ERROR_CAN_NOT_ADD = 0x3;                   //невозможно добавление возможно смета пуста
    public final static int ESTIMATE_ERROR_CAN_NOT_SET_COAST = 0x4;               //Coast < 0
    
    String CompanyAddress;
    
    public Manager(String CompanyAddress, int Id, String Name, String PhoneNumber) {
        super(Id, Name, PhoneNumber);
        this.CompanyAddress = CompanyAddress;
    }
    
    public Order CreateOrder(Date create,int Number,ArrayList<Work> WorkList){    
        Order ord = null;
        if(WorkList != null){
            ord = new Order(create,Number);
            Estimate e = new Estimate(Estimate.MAIN, WorkList);
            ord.addEstimapte(e);
        }
        return ord;
    }
    
    public int CreateEstimate(Order ord,int type,ArrayList<Work> WorkList){
        return (WorkList != null) ? 
                CreateEstimate(ord,(new Estimate(type,WorkList))) : 
                ESTIMATE_ERROR_CAN_NOT_ADD;
    }
    
    public int CreateEstimate(Order ord,Estimate part){
        int flag = 0;
        switch(part.getType()){
            case Estimate.ADDITIONAL:
                //нужно оплатиить 85% или больше от текущей суммы заказа.
                if(ord.getCurrentCoast() <= (ord.CoastCalculation() * 0.15)){
                    if(ord.addEstimapte(part)){
                        ord.setStatus(Order.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                        flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                    }
                }else{
                    ord.setStatus(Order.WAITING_PAY);
                    flag = ESTIMATE_CLIENT_NEED_PAY;
                }
                break;
            case Estimate.MAIN:
                if(ord.addEstimapte(part)){
                    if(ord.setCurrentCoast(ord.CoastCalculation())){
                        ord.setStatus(Order.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_SET_COAST;
                    }
                }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                }
                break;
        }
        return flag;
    }
    
    public void TakeResourseFromStorage(Storage store,ArrayList<Work> WorkList){
        //запрос ресурсов для работ.
        if(store != null){
            if(WorkList != null){
                for(int i = 0; i < WorkList.size(); i++){
                    ArrayList<Resource> res = WorkList.get(i).getResources();
                    for(int j = 0; j < res.size(); i++){
                        int index = store.findResoursePositionByType(res.get(j).getType());
                        if(index != -1){
                            switch(store.TakeResources(index,res.get(j).getAmount())){
                                case Storage.TAKE_RESORSE_SUCCESS://успешное выполнение
                                    break;
                                case Storage.INSUFFICIENTLY_RESORSE://данного товара недостаточно
                                    break;
                                case Storage.RESORSE_EMPTY:// склад пуст
                                    break;
                                case Storage.RESORSE_NOT_FOUND://на складе такой тип ресурс не найден
                                    break;
                                case Storage.STORAGE_EMPTY:// проблема с инициализацией склада
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void SendResourseToStorage(){
        //отправка ресурсов на склад.
    }
    
    public boolean CloseOrder(boolean ClientAceptWork,Order ord,Date End){
        if(ord.getStatus()== Order.WAITING_ACKNOWLEDGMENT_PAY){
            if(ord.getCurrentCoast() == 0){
                ord.setStatus(Order.CLOSE);
                return ord.CloseEstimate(End);
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    public void setCompanyAddress(String CompanyAddress) {
        this.CompanyAddress = CompanyAddress;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }
    
}
