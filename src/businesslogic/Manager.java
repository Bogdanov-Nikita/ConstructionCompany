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
public class Manager extends Role{
    
    String CompanyAddres;
    
    public void setCompanyAddres(String CompanyAddres) {
        this.CompanyAddres = CompanyAddres;
    }

    public String getCompanyAddres() {
        return CompanyAddres;
    }
    
}
