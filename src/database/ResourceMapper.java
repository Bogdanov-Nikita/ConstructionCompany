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
        String columns[] = {
            Databse.Resource.id,
            Databse.Resource.type,
            Databse.Resource.name,
            Databse.Resource.amount,
            Databse.Resource.coast
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Databse.Resource.Table,
                columns,
                Databse.Resource.id+"=?",
                new String[]{String.valueOf(Id)},
                null,
                Databse.Resource.id)
        );
        rs.next();
        return new Resource(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4));
    }
    
    @Override
    public ArrayList<Resource> loadList(DatabaseManager db) throws SQLException{
        String columns[] = {
                    Databse.Resource.id,
                    Databse.Resource.type,
                    Databse.Resource.name,
                    Databse.Resource.amount,
                    Databse.Resource.coast
                };        
                ResultSet rs = db.executeQuery(
                        QueryBilder.select(
                        Databse.Resource.Table,
                        columns,
                        null,
                        null,
                        null,
                        Databse.Resource.id)
                );
                ArrayList<Resource> list = new ArrayList<>();
                while(rs.next()){ 
                    list.add(new Resource(rs.getInt(1), rs.getInt(2), rs.getDouble(3), rs.getString(4)));
                }
                return list;
    }
    
    @Override
    public boolean save(Resource res){
       return false; 
    }
    
    @Override
    public boolean saveArray(ArrayList<Resource> list){
        return false;
    }
    
}
