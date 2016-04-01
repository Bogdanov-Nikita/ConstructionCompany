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
    
    int Id;
    String Name;
    String PhoneNumber;

    public Role(int Id, String Name, String PhoneNumber) {
        this.Id = Id;
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
    }
    
    public int getID() {
        return Id;
    }

    public void setID(int Id) {
        this.Id = Id;
    }    
    
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }
    
    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }
    
}
