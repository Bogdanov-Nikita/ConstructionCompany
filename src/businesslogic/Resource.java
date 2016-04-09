/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

/**
 *
 * @author Nik
 * в основном данные для заполнения берутся из базы для склада,
 * или задаётся Amount пользователем для сметы.
 */
public class Resource {
    
    int Id;         //Идентификатор
    int Amount;     //Колиество данного ресурса.
    int Type;       //Тип ресурса.
    double Coast;   //Цена.
    String Name;    //Название ресурса.

    /**
     * Создание ресурса
     * Тип ресурса и Название связанны и имеют прямое соответствие.
     * @param Id - Идентификатор 0 если ещё не известен
     * @param Amount - Колиество данного ресурса.
     * @param Type - Тип ресурса.
     * @param Сoast - Цена ресурса. 
     * @param Name - Название ресурса.
     */
    public Resource(int Id,int Amount, int Type,double Сoast,String Name) {
        if(Id < 0){
            this.Id = 0;
        }else{
            this.Id = Id;
        }
        if(Amount < 0){
            this.Amount = 0;
        }else{
            this.Amount = Amount;
        }
        if(Сoast < 0){
            this.Coast = 0;
        }else{
            this.Coast = Сoast;
        }if(Type < 0){
            this.Type = 0;
        }else{
            this.Type = Type;
        }
        this.Name = Name;
    }

    public int getAmount() {
        return Amount;
    }

    public String getName() {
        return Name;
    }

    public int getType() {
        return Type;
    }

    public double getCoast() {
        return Coast;
    }

    /**
     * @param Amount - значение Amount должно быть > 0.
     * @return Вернёт true в случае успеха иначе false.
     */
    public boolean setAmount(int Amount) {
        if(Amount < 0 ){
            this.Amount = 0; 
            return false;
        }else{
            this.Amount = Amount; 
            return true;
        }
    }

    /**
     * @param Coast - значение Coast должно быть > 0.
     * @return Вернёт true в случае успеха иначе false.
     */
    public boolean setCoast(double Coast){
        if(Coast < 0){
            this.Coast = 0;
            return false;
        }else{
            this.Coast = Coast;
            return true;
        }
    }
    
    public void setName(String Name) {
        this.Name = Name;
    }

    public boolean setType(int Type) {
        if(Type < 0 ){
            this.Type = 0; 
            return false;
        }else{
            this.Type = Type; 
            return true;
        }
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }
    
}
