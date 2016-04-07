/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Client;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class ClientMapper extends Mapper<Client, DatabaseManager>{

    @Override
    public Client load(int Id, DatabaseManager db) throws SQLException {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Client> loadList(DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(Client res, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Databse.Client.name, res.getName());
        value.put(Databse.Client.phone_number,res.getPhoneNumber());
        value.put(Databse.Client.type,String.valueOf(res.getType()));
        
        if(res.getID()==0||res.getID()==-1){
            //insert
            value.put(Databse.Client.id, "null");            
            flag = db.execute(QueryBilder.insert(Databse.Client.Table, value));
        }else{
            //update
            value.put(Databse.Client.id, String.valueOf(res.getID()));
            String whereClause = Databse.Client.Table + "." + Databse.Client.id +"=?";
            String args[] = {String.valueOf(res.getID())};
            flag = db.execute(QueryBilder.update(Databse.Client.Table, value, whereClause, args));            
        }
        db.commitTransaction();
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Client> list,DatabaseManager db) throws SQLException {        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
