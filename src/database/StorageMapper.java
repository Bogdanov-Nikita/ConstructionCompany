/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Resource;
import businesslogic.Storage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class StorageMapper extends Mapper<Storage, DatabaseManager>{

    @Override
    public Storage load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String collums[] = {
            Database.StorageView.id,
            Database.StorageView.location,
            Database.StorageView.resource_id,
            Database.StorageView.type,
            Database.StorageView.name,
            Database.StorageView.coast,
            Database.StorageView.amount
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.StorageView.View,
                        collums,
                        "\""+Database.StorageView.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.StorageView.id + "\",\"" + Database.StorageView.resource_id)
        );
        rs.next();        
        int storeId = rs.getInt(1);
        String location = rs.getString(2).trim();
        ArrayList<Resource> list = new ArrayList<>();
        list.add(new Resource(rs.getInt(3), rs.getInt(7), rs.getInt(4), rs.getDouble(6), rs.getString(5).trim()));
        while(rs.next()){
            //3 - res_id, 7 - amount, 4 - type, 6 - coast,5 - name  
            list.add(new Resource(rs.getInt(3), rs.getInt(7), rs.getInt(4), rs.getDouble(6), rs.getString(5).trim()));
        }
        Storage store = new Storage(storeId, location, list);        
        db.commitTransaction();
        return store;     
    }

    @Override
    public ArrayList<Storage> loadList(DatabaseManager db) throws SQLException {        
        db.startTransaction();
        String collums[] = {
            Database.StorageView.id,
            Database.StorageView.location,
            Database.StorageView.resource_id,
            Database.StorageView.type,
            Database.StorageView.name,
            Database.StorageView.coast,
            Database.StorageView.amount
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.StorageView.View,
                        collums,
                        null,
                        null,
                        null,
                        Database.StorageView.id + "\",\"" + Database.StorageView.resource_id)
        );     
        
        ArrayList<Storage> StoreList = new ArrayList<>();
        ArrayList<ArrayList<Resource>> listList = new ArrayList<>();
        String CurrentLocation;
        String LastLocation = null;
        int currentId;
        int lastId = 0;        
        int i = 0;
        
        while(rs.next()){
            currentId = rs.getInt(1);
            CurrentLocation = rs.getString(2).trim();            
            if(currentId != lastId){
                if(lastId != 0){
                    StoreList.add(new Storage(lastId, LastLocation,listList.get(i)));
                    i++;
                }                                
                lastId = currentId;
                LastLocation = CurrentLocation;                 
                listList.add(new ArrayList<>());                
            }
            listList.get(i).add(new Resource(rs.getInt(3), rs.getInt(7), rs.getInt(4), rs.getDouble(6), rs.getString(5).trim()));
        }
        StoreList.add(new Storage(lastId, LastLocation,listList.get(i)));        
        db.commitTransaction();
        return StoreList;
    }

    //e.getResources() - должен содержать хотябы один элемент!
    @Override
    public boolean save(Storage e, DatabaseManager db) throws SQLException {
        boolean flag;
        ArrayList<Integer> NewElement = new ArrayList<>();
        //создаём список добавляемых ресурсов.
        for(int j = 0; j < e.getResources().size(); j++){
            int id = e.getResource(j).getId();
            if(id == 0 || id == -1){
                NewElement.add(j);
            }
        }
        
        //insert/update Resource Table
        new ResourceMapper().saveArray(e.getResources(), db);
        //insert/update  StorageInformation Table
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.location,e.getLocation());         
        if(e.getId() == 0||e.getId() == -1){
            //Get Store Id
            int nextID = generateIDs(1, db);
            e.setId(nextID);
            //запись в StorageInformation Table
            value.put(Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.id, String.valueOf(nextID));
            flag = db.execute(QueryBilder.insert(Database.StorageInformation.Table,value));            
            db.commitTransaction();
            //insert в Storage Table
            db.startTransaction();
            for(int i = 0; i <  e.getResources().size(); i++){
                ContentValues ResourceValue = new ContentValues();
                // id - nextID
                // resource_id - (nextResID + i)
                //amount - e.getResource(i).getAmount()
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.id,String.valueOf(nextID) );
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.resource_id,String.valueOf(e.getResource(i).getId()));
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.amount,String.valueOf(e.getResource(i).getAmount()));
                flag = db.execute(QueryBilder.insert(Database.Storage.Table,ResourceValue));              
            }
        }else{
            //update
            value.put(Database.StorageInformation.id, String.valueOf(e.getId()));
            String whereClause = "\"" + Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.id +"\"=?";
            String args[] = {String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.StorageInformation.Table, value, whereClause, args));
            db.commitTransaction();
            //insert/update в Storage Table
            db.startTransaction();
            int index = 0;
            for(int i = 0; i <  e.getResources().size(); i++){
                ContentValues ResourceValue = new ContentValues();
                // id - nextID
                // resource_id - (nextResID + i)
                //amount - e.getResource(i).getAmount()
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.id,String.valueOf(e.getId()) );
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.resource_id,String.valueOf(e.getResource(i).getId()));
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.amount,String.valueOf(e.getResource(i).getAmount()));
                if(!NewElement.isEmpty() && (NewElement.get(index) == e.getResource(i).getId())){
                    //insert
                    flag = db.execute(QueryBilder.insert(Database.Storage.Table,ResourceValue));
                    index++;
                }else{
                    //update
                    String whereClauseR = "\"" + Database.Storage.Table + "\".\"" + Database.Storage.id +"\"=? AND " + 
                    "\"" + Database.Storage.Table + "\".\"" + Database.Storage.resource_id +"\"=?";
                    String argsR[] = {String.valueOf(e.getId()),String.valueOf(e.getResource(i).getId())};
                    flag = db.execute(QueryBilder.update(Database.Storage.Table,ResourceValue,whereClauseR,argsR));
                }
            }
        }
        db.commitTransaction();        
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Storage> list, DatabaseManager db) throws SQLException {
        boolean flag = false;
        for (Storage list1 : list) {
            flag = save(list1, db);
        }
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        clearStorage(id, db);
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.StorageInformation.Table, value));
        db.commitTransaction();
    }

    public void clearStorage(int id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put("\"" + Database.Storage.Table + "\".\"" + Database.Storage.id +"\"", String.valueOf(id));
        db.execute(QueryBilder.delete(Database.Storage.Table, value));
        db.commitTransaction();
    }

    @Override
    public int generateIDs(int size, DatabaseManager db) throws SQLException {
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( STORAGEINFORMATION_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }

}
