/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Master;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class MasterMapper extends Mapper<Master, DatabaseManager>{

    @Override
    public Master load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Master.id,
            Database.Master.name,
            Database.Master.phone_number
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Master.Table,
                        columns,
                        Database.Master.id + "=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.Master.id
                )
        );
        db.commitTransaction();
        return new Master(rs.getInt(1), rs.getString(2), rs.getString(3));
    }

    @Override
    public ArrayList<Master> loadList(DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Master.id,
            Database.Master.name,
            Database.Master.phone_number
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Master.Table,
                        columns,
                        null,
                        null,
                        null,
                        Database.Master.id
                )
        );
        db.commitTransaction();
        ArrayList<Master> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Master(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return list;
    }

    @Override
    public boolean save(Master e, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Master.name, e.getName());
        value.put(Database.Master.phone_number,e.getPhoneNumber());
        
        if(e.getID()==0||e.getID()==-1){
            //insert
            value.put(Database.Master.id, "null");            
            flag = db.execute(QueryBilder.insert(Database.Master.Table, value));
        }else{
            //update
            value.put(Database.Master.id, String.valueOf(e.getID()));
            String whereClause = Database.Master.Table + "." + Database.Master.id +"=?";
            String args[] = {String.valueOf(e.getID())};
            flag = db.execute(QueryBilder.update(Database.Master.Table, value, whereClause, args));            
        }
        db.commitTransaction();
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Master> list, DatabaseManager db) throws SQLException {
        db.startTransaction();
        boolean flag = false;
        for (Master list1 : list) {
            ContentValues value = new ContentValues();
            value.put(Database.Master.name, list1.getName());
            value.put(Database.Master.phone_number, list1.getPhoneNumber());
            if (list1.getID() == 0 || list1.getID() == -1) {
                //insert
                value.put(Database.Master.id, "null");            
                flag = db.execute(QueryBilder.insert(Database.Master.Table, value));
            } else {
                //update
                value.put(Database.Master.id, String.valueOf(list1.getID()));
                String whereClause = Database.Master.Table + "." + Database.Master.id +"=?";
                String[] args = {String.valueOf(list1.getID())};
                flag = db.execute(QueryBilder.update(Database.Master.Table, value, whereClause, args));            
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
