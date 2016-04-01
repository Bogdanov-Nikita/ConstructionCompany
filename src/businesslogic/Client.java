/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

/**
 *
 * @author Nik
 */

public class Client extends Role{
    
    public final static int PHYSICAL = 0x1;
    public final static int LEGAL = 0x2;
    
    int type;
    String Addres;

    public Client(int type, String Addres, int Id, String Name, String PhoneNumber) {
        super(Id, Name, PhoneNumber);
        this.type = type;
        this.Addres = Addres;
    }
    
    //Не учитывает требование заказчика о добавлении нового только проверяет что заказ был выполнен.
    public boolean TakeWork(Order ord){
            return ord.isFinish();
    }
    
    //процесс по оплате должен быть подтверждён менеджером
    public void PayEstimateFull(Order ord){
        ord.ClientPay(ord.getCurrentCoast());
    }
    
    //процесс по оплате должен быть подтверждён менеджером
    public boolean PayEstimatePart(Order ord,double pay){
        return ord.ClientPay(pay);
    }

    public void setAddres(String Addres) {
        this.Addres = Addres;
    }

    public String getAddres() {
        return Addres;
    }

    public int getType() {
        return type;
    }
    
}
