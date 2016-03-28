/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

/**
 *
 * @author Nik
 * абстрактная роль с телефоном ID и именем(Ф.И.О.)
 */
public abstract class Role {
    
    int ID;
    String Name;
    String PhoneNumpber;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }    
    
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhoneNumpber() {
        return PhoneNumpber;
    }
    
    public void setPhoneNumpber(String PhoneNumpber) {
        this.PhoneNumpber = PhoneNumpber;
    }
    
}
