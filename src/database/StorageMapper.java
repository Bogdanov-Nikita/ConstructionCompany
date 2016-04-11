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
//TODO требует завершения работы над ресурсной таблицей
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

    @Override
    public boolean save(Storage e, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.location,e.getLocation());         
        if(e.getId()==0||e.getId()==-1){
            //Get Store Id
            ResultSet set = db.executeQuery("SELECT GEN_ID( STORAGEINFORMATION_ID_GENERATOR, 1 ) FROM RDB$DATABASE;");
            set.next();
            int nextID = (set.getInt(1));
            //запись в StorageInformation Table
            value.put(Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.id, String.valueOf(nextID));
            flag = db.execute(QueryBilder.insert(Database.StorageInformation.Table,value));            
            //запись в Resource Table установить сколько элементов будет добавленно. вместо 0
            int newRes = 0;//количество ресурсов вставляемых в таблицу не update
            for(int i = 0; i < e.getResources().size(); i++){
                if(e.getResource(i).getId() <= 0 ){
                    newRes++;
                }
            }
            ResultSet Rset = db.executeQuery("SELECT GEN_ID( RESOUSE_ID_GENERATOR, " + String.valueOf(newRes) + " ) FROM RDB$DATABASE;"); 
            Rset.next();
            int nextResID = ((Rset.getInt(1)) - newRes) + 1;
            db.commitTransaction();
            //add Resource Table
            new ResourceMapper().saveArray(e.getResources(), db);           
            //add Storage Table
            db.startTransaction();
            for(int i = 0; i <  e.getResources().size(); i++){
                ContentValues ResourceValue = new ContentValues();
                // id - nextID
                // resource_id - (nextResID + i)
                //amount - e.getResource(i).getAmount()
                //insert
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.id,String.valueOf(nextID) );
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.resource_id,String.valueOf(nextResID));
                ResourceValue.put(Database.Storage.Table + "\".\"" + Database.Storage.amount,String.valueOf(e.getResource(i).getAmount()));
                QueryBilder.insert(Database.Storage.Table,ResourceValue);
                nextResID++;              
            }
        }else{
            //update
            value.put(Database.StorageInformation.id, String.valueOf(e.getId()));
            String whereClause = "\"" + Database.StorageInformation.Table + "\".\"" + Database.StorageInformation.id +"\"=?";
            String args[] = {String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.StorageInformation.Table, value, whereClause, args));
            //TODO:продолжение.
        }
        db.commitTransaction();        
        return flag;
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveArray(ArrayList<Storage> list, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
