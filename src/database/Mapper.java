/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 * абстрактный соединитель данных базы и модели представления по принципу Inheritance Mappers.
 * @param <Domain> - Обект записываемый в таблицу
 * @param <DatabaseHelper> - помошник управление с базой данных
 */
public abstract class Mapper<Domain,DatabaseHelper>{
    public abstract Domain load(int Id,DatabaseHelper db)throws SQLException;
    public abstract ArrayList<Domain> loadList(DatabaseHelper db)throws SQLException;    
    public abstract boolean save(Domain e, DatabaseHelper db)throws SQLException;
    public abstract boolean saveArray(ArrayList<Domain> list, DatabaseHelper db)throws SQLException;
}
