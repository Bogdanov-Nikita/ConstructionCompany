/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Manager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class ManagerMapper extends Mapper <Manager, DatabaseManager>{

    @Override
    public Manager load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Manager.id,
            Database.Manager.name,
            Database.Manager.phone_number,
            Database.Manager.office_address    
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Manager.Table,
                        columns,
                        Database.Manager.id + "=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.Manager.id
                )
        );
        db.commitTransaction();
        return new Manager(rs.getString(4), rs.getInt(1), rs.getString(2), rs.getString(3));
    }

    @Override
    public ArrayList<Manager> loadList(DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Manager.id,
            Database.Manager.name,
            Database.Manager.phone_number,
            Database.Manager.office_address,
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Manager.Table,
                        columns,
                        null,
                        null,
                        null,
                        Database.Manager.id)
        );
        db.commitTransaction();
        ArrayList<Manager> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Manager(rs.getString(4), rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return list;
    }

    @Override
    public boolean save(Manager e, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Manager.name, e.getName());
        value.put(Database.Manager.phone_number,e.getPhoneNumber());
        value.put(Database.Manager.office_address,e.getOfficeAddress());        
        if(e.getID()==0||e.getID()==-1){
            //insert
            value.put(Database.Manager.id, "null");            
            flag = db.execute(QueryBilder.insert(Database.Manager.Table, value));
        }else{
            //update
            value.put(Database.Manager.id, String.valueOf(e.getID()));
            String whereClause = Database.Manager.Table + "." + Database.Manager.id +"=?";
            String args[] = {String.valueOf(e.getID())};
            flag = db.execute(QueryBilder.update(Database.Manager.Table, value, whereClause, args));            
        }
        db.commitTransaction();
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Manager> list, DatabaseManager db) throws SQLException {
        db.startTransaction();
        boolean flag = false;
        for (Manager list1 : list) {
            ContentValues value = new ContentValues();
            value.put(Database.Manager.name, list1.getName());
            value.put(Database.Manager.phone_number, list1.getPhoneNumber());
            value.put(Database.Manager.office_address,list1.getOfficeAddress());  
            if (list1.getID() == 0 || list1.getID() == -1) {
                //insert
                value.put(Database.Manager.id, "null");            
                flag = db.execute(QueryBilder.insert(Database.Manager.Table, value));
            } else {
                //update
                value.put(Database.Manager.id, String.valueOf(list1.getID()));
                String whereClause = Database.Manager.Table + "." + Database.Manager.id +"=?";
                String[] args = {String.valueOf(list1.getID())};
                flag = db.execute(QueryBilder.update(Database.Manager.Table, value, whereClause, args));            
            }
            if(!flag){
                db.rollbackTransaction();
                break;
            }
        }
        
        if(flag){
            db.commitTransaction();
        }        
        return flag;
    }
    
}
