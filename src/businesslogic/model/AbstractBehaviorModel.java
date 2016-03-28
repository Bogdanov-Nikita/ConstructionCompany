/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import businesslogic.Role;
import database.ContentValues;
/**
 *
 * @author Nik
 */

public abstract class AbstractBehaviorModel {
    Role role;
    abstract void Create(Role role);//инициализация корректными значениями ответственность инициализации роли лежит на разработке описателя модели смотри класс BehaviorModel
    abstract void onSaveInstanceState(Container savedInstanceState);//onPause
    abstract void onRestoreInstanceState(Container outState);//onResume
    abstract void executeEvent(int Event,ContentValues value);//onAction
}
