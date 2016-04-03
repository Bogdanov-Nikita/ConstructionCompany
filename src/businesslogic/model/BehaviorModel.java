/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic.model;

import Resources.R;
import businesslogic.Role;
import database.ContentValues;
/**
 *
 * @author Nik
 */
// размещается глобально в main и передаётся во врейм работает внутри внутри фреймов "окон".
//TODO: расписать требуемые состояния если нужно и описать контейнер. 
public class BehaviorModel {//Originator 
    AbstractBehaviorModel model;
    //Часть паттерна Хранитель
    //Contener - Memento - "Хранилище" ,
    //BehaviorModel - Originator - "Создатель"
    //TODO разместить в main Caretaker - "Опекун" и так же запись данных если потербуется.

    public BehaviorModel() {
        model = new DefaultModel();/*Dimmy*/
    }
    
    public BehaviorModel(int ModelType,Role role) {
        switch(ModelType){
            case R.ModelType.DefaultModel: model = new DefaultModel();break;    /*Dimmy*/
            case R.ModelType.ClientModel:  model = new ClientModel();break;     /*Бизнес логика Заказчика*/
            case R.ModelType.MasterModel:  model = new MasterModel();break;     /*Бизнес логика Прораба*/
            case R.ModelType.ManagerModel: model = new ManagerModel();break;    /*Бизнес логика Менеджера*/
        }
    }
    
    /* get и set для тестирования, в остальных слуаях использование сомнительно,
    возможно, за исключением появления дополнительных моделей поведения*/
    
    public void setModel(AbstractBehaviorModel model) {
        this.model = model;
    }

    public AbstractBehaviorModel getModel() {
        return model;
    }
    
    public Container SaveInstanseState(){
        Container container = new Container(null);//тоже создаём элементы для сохранения если нужно
        model.onSaveInstanceState(container);//onPause//сохранение данных
        return container;
    }
    
    public void RestoreInstanceState(Container container){//onResume//загрузка воостановление состояния данных
        model.onRestoreInstanceState(container);
    }
    /**
     * TODO:требуется полная документация по аргументу Value для использоавния в модели поведения.
     * @param Event - код события, события указываются в Resources.R.ModelEvent
     * @param Value - значение неких сведений передаваемых вместе с указанием события,
     * описание в документации.
    */
    public void executeEvent(int Event,ContentValues Value){
        model.executeEvent(Event,Value);
    }
    
}
