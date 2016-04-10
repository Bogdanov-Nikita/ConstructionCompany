/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class ResourceMapper extends Mapper<Resource, DatabaseManager>{
    
    @Override
    public Resource load(int Id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        String columns[] = {
            Database.Resource.id,
            Database.Resource.type,
            Database.Resource.name,
            Database.Resource.coast
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Resource.Table,
                columns,
                "\"" + Database.Resource.id + "\"=?",
                new String[]{String.valueOf(Id)},
                null,
                Database.Resource.id)
        );
        rs.next();
        db.commitTransaction();
        return new Resource(rs.getInt(1),0, rs.getInt(2), rs.getDouble(4), rs.getString(3).trim());
    }
    
    @Override
    public ArrayList<Resource> loadList(DatabaseManager db) throws SQLException{
        db.startTransaction();
        String columns[] = {
            Database.Resource.id,
            Database.Resource.type,
            Database.Resource.name,
            Database.Resource.coast
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Resource.Table,
                columns,
                null,
                null,
                null,
                Database.Resource.id)
        );
        ArrayList<Resource> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Resource(rs.getInt(1),0, rs.getInt(2), rs.getDouble(4), rs.getString(3).trim()));
        }
        db.commitTransaction();
        return list;
    }
    
    @Override
    public boolean save(Resource e, DatabaseManager db) throws SQLException{        
        boolean flag;
        db.startTransaction();
        ContentValues value = new ContentValues();
        value.put(Database.Resource.Table + "\".\"" + Database.Resource.type,String.valueOf(e.getType()));
        value.put(Database.Resource.Table + "\".\"" + Database.Resource.name, e.getName());        
        value.put(Database.Resource.Table + "\".\"" + Database.Resource.coast,String.valueOf(e.getCoast()));        
        if(e.getId()==0||e.getId()==-1){
            //insert
            value.put(Database.Resource.id, "0");            
            flag = db.execute(QueryBilder.insert(Database.Resource.Table, value));
        }else{
            //update
            value.put(Database.Resource.id, String.valueOf(e.getId()));
            String whereClause = "\"" + Database.Resource.Table + "\".\"" + Database.Resource.id +"\"=?";
            String args[] = {String.valueOf(e.getId())};
            flag = db.execute(QueryBilder.update(Database.Resource.Table, value, whereClause, args));            
        }
        db.commitTransaction();
        return flag;
    }
    
    @Override
    public boolean saveArray(ArrayList<Resource> list, DatabaseManager db) throws SQLException{
        db.startTransaction();
        boolean flag = false;
        for (Resource list1 : list) {
            ContentValues value = new ContentValues();
            value.put(Database.Resource.Table + "\".\"" + Database.Resource.type, String.valueOf(list1.getType()));
            value.put(Database.Resource.Table + "\".\"" + Database.Resource.name, list1.getName());
            value.put(Database.Resource.Table + "\".\"" + Database.Resource.coast,String.valueOf(list1.getCoast()));  
            if (list1.getId() == 0 || list1.getId() == -1) {
                //insert
                value.put(Database.Resource.id, "null");            
                flag = db.execute(QueryBilder.insert(Database.Resource.Table, value));
            } else {
                //update
                String whereClause = "\"" + Database.Resource.Table + "\".\"" + Database.Resource.id +"\"=?";
                String[] args = {String.valueOf(list1.getId())};
                flag = db.execute(QueryBilder.update(Database.Resource.Table, value, whereClause, args));            
            }
        }
        db.commitTransaction();
        return flag;
    }

    @Override
    public void delete(int id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String whereClause = "\"" + Database.Resource.Table + "\".\"" + Database.Resource.id +"\"="+String.valueOf(id);
        db.execute(QueryBilder.delete(Database.Resource.Table,whereClause));
        db.commitTransaction();
    }
    
}
