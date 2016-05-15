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
 * абстрактный соединитель данных базы и модели представления.
 * @param <Domain> Обект записываемый в таблицу
 * @param <DatabaseHelper>  помошник управление с базой данных
 */
public abstract class Mapper<Domain,DatabaseHelper>{
    /**
     * загружает значения элемента модели предметной области из базы данных управлемой через db
     * @param id id элемента в таблице.
     * @param db класс управлющий взаимодействием с базой данных.
     * @return возвращает элемент модели предметной области. 
     * @throws java.sql.SQLException 
     */
    public abstract Domain load(int id,DatabaseHelper db)throws SQLException;
    /**
     * загружает значения списка элементов модели предметной области из базы данных управлемой через db
     * @param db класс управлющий взаимодействием с базой данных
     * @return возвращает список элементов модели предметной области 
     * @throws java.sql.SQLException 
     */
    public abstract ArrayList<Domain> loadList(DatabaseHelper db)throws SQLException;
    /**
     * сохраняет элемент модели предметной области в базу данныданных управлемой через db
     * @param e элемент модели предметной области
     * @param db класс управлющий взаимодействием с базой данных
     * @return успешно или не спешно завершилась данная операция
     * @throws java.sql.SQLException 
    */
    public abstract boolean save(Domain e, DatabaseHelper db)throws SQLException;
    /**
     * сохраняет список элементов модели предметной области в базу данныданных управлемой через db
     * @param list список элементов модели предметной области
     * @param db класс управлющий взаимодействием с базой данных
     * @return успешно или не спешно завершилась данная операция
     * @throws java.sql.SQLException 
    */
    public abstract boolean saveArray(ArrayList<Domain> list, DatabaseHelper db)throws SQLException;
    /**
     * Удаляет запись в таблице
     * @param id id элемента для удаления
     * @param db класс управлющий взаимодействием с базой данных
     * @throws java.sql.SQLException 
    */
    public abstract void delete(int id,DatabaseManager db) throws SQLException;
    /**
     * @param size указывает какое количество "строк" (записей) в таблице мы хотим зарезервировать
     * @param db класс управлющий взаимодействием с базой данных
     * @return возвращает последний зарезервированнй id для списка из size элементов
     * @throws java.sql.SQLException 
     */
    public abstract int generateIDs(int size,DatabaseManager db) throws SQLException;
}
