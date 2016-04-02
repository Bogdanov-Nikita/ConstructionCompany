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
public class Master extends Role{

    public Master(int Id, String Name, String PhoneNumber) {
        super(Id, Name, PhoneNumber);
    }
    
    void MakeWork(Order ord){
        ord.setStatus(Order.WAITING_ACKNOWLEDGMENT_TAKE);
    }
    void CreateWorkList(){}
}
