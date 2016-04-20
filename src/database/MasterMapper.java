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
                        "\"" + Database.Master.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.Master.id
                )
        );
        rs.next();
        db.commitTransaction();        
        return new Master(rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim());
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
        ArrayList<Master> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Master(rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim()));
        }
        db.commitTransaction();
        return list;
    }

    @Override
    public boolean save(Master e, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Master.Table + "\".\"" + Database.Master.name, e.getName());
        value.put(Database.Master.Table + "\".\"" + Database.Master.phone_number,e.getPhoneNumber());        
        if(e.getID()==0||e.getID()==-1){
            //insert
            int Id = generateIDs(1, db);
            e.setID(Id);
            value.put(Database.Master.id, String.valueOf(Id));            
            flag = db.execute(QueryBilder.insert(Database.Master.Table, value));
        }else{
            //update
            value.put(Database.Master.id, String.valueOf(e.getID()));
            String whereClause = "\"" + Database.Master.Table + "\".\"" + Database.Master.id +"\"=?";
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
        int NewIdCount = 0;
        //цикл по всем элементам для поиска элементов без Id.
        NewIdCount = list.stream().filter((list1) -> (list1.getID() == 0 || list1.getID() == -1)).map((item) -> 1).reduce(NewIdCount, Integer::sum);
        int LastGeneratedId = (NewIdCount > 0) ? generateIDs(NewIdCount, db) : 0;
        int NextId = LastGeneratedId - NewIdCount;
        for (Master list1 : list) {
            ContentValues value = new ContentValues();
            value.put(Database.Master.Table + "\".\"" + Database.Master.name, list1.getName());
            value.put(Database.Master.Table + "\".\"" + Database.Master.phone_number, list1.getPhoneNumber());
            if (list1.getID() == 0 || list1.getID() == -1) {
                //insert
                value.put(Database.Master.id, String.valueOf(NextId));            
                flag = db.execute(QueryBilder.insert(Database.Master.Table, value));
                list1.setID(NextId);
                NextId++;
            } else {
                //update
                value.put(Database.Master.id, String.valueOf(list1.getID()));
                String whereClause = "\"" + Database.Master.Table + "\".\"" + Database.Master.id +"\"=?";
                String[] args = {String.valueOf(list1.getID())};
                flag = db.execute(QueryBilder.update(Database.Master.Table, value, whereClause, args));            
            }
        }
        db.commitTransaction();        
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String whereClause = "\"" + Database.Master.Table + "\".\"" + Database.Master.id +"\"="+String.valueOf(id);
        db.execute(QueryBilder.delete(Database.Master.Table,whereClause));
        db.commitTransaction();
    }

    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( MASTER_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }
    
}
