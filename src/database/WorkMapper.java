/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Work;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class WorkMapper extends Mapper<Work, DatabaseManager>{

    @Override
    public Work load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Work.id,
            Database.Work.description,
            Database.Work.service_coast,
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Work.Table,
                columns,
                Database.Work.id+"=?",
                new String[]{String.valueOf(Id)},
                null,
                Database.Work.id)
        );
        rs.next();
        db.commitTransaction();
        return new Work(null, rs.getDouble(2), rs.getString(3));
    }

    @Override
    public ArrayList<Work> loadList(DatabaseManager db) throws SQLException {
        /*db.startTransaction();
        String columns[] = {
            Database.Work.id,
            Database.Work.description,
            Database.Work.service_coast,
        };
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                Database.Work.Table,
                columns,
                null,
                null,
                null,
                Database.Work.id)
        );
        rs.next();
        db.commitTransaction();
        new Work(null, rs.getDouble(2), rs.getString(3));*/
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean save(Work e, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean saveArray(ArrayList<Work> list, DatabaseManager db) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
