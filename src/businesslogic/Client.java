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
enum ClientType {PHYSICAL, LEGAL}
public class Client extends Role{
    int ClientType;
    double Balanse;//количество денег на счету
    String Addres;
    public boolean TakeWork(){
        return true;
    }
    
    public void PayEstimate(){
    }
}
