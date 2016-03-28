/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

/**
 *
 * @author Nik
 */

public  final class Databse {
        
    public final static class All_Keywords{
        public final static String Table = "All_Keywords";
        public final static String id = "id";
        public final static String event_id_1 = "event_id_1";
        public final static String event_id_2 = "event_id_2";
        public final static String connection = "connection";
        public final static String author_id = "author_id";
        public final static String privacy = "privacy";
        public final static String association_type = "association_type";
    }
    
    public final static class Author{
        public final static String Table = "Author";
        public final static String id = "id";
        public final static String author = "author";
    }
    
    public final static class Author_Keyword{
        public final static String Table = "Author_Keyword";
        public final static String Author = "Author";
        public final static String Keyword = "Keyword";
        public final static String Table_N = "Table_N";
    }
    
    public final static class Category{
        public final static String Table = "Category";
        public final static String id = "id";
        public final static String name = "name";
    }
    
    public final static class Category_Keyword{
        public final static String Table = "Category_Keyword";
        public final static String Category = "Category";
        public final static String Keyword = "Keyword";
        public final static String Table_N = "Table_N";
    }
    
    public final static class City{
        public final static String Table = "City";
        public final static String id = "id";
        public final static String City = "City";
    }
    
    public final static class Context{
        public final static String Table = "Context";
        public final static String id = "id";
        public final static String geo = "geo";
        public final static String author = "author";
        public final static String persona = "persona";
        public final static String category = "category";
        public final static String timeline = "timeline";
    }
    
    public final static class Continent{
        public final static String Table = "Continent";
        public final static String id = "id";
        public final static String Continent = "Continent";
    }
    
    public final static class Country{
        public final static String Table = "Country";
        public final static String id = "id";
        public final static String Country = "Country";
    }
    
    public final static class Event{
        public final static String Table = "Event";
        public final static String id = "id";
        public final static String geo_id = "geo_id";
        public final static String body = "body";
        public final static String title = "title";
        public final static String start_date = "start_date";
        public final static String end_date = "end_date";
        public final static String author_id = "author_id";
        public final static String category_id = "category_id";
    }
    
    public final static class Event_and_Person{
        public final static String Table = "Event_and_Person";
        public final static String Persona = "Persona";
        public final static String Persona_Event = "Persona_Event";
    }
    
    public final static class EventList{
        public final static String Table = "EventList";
        public final static String TimeList_id = "TimeList_id";
        public final static String Event_id = "Event_id";
        public final static String x = "x";
        public final static String y = "y";
    }
    
    public final static class Geo{
        public final static String Table = "Geo";
        public final static String id = "id";
        public final static String Coordinates = "Coordinates";
        public final static String Nationality_id = "Nationality_id";
        public final static String Continent_id = "Continent_id";
        public final static String Country_id = "Country_id";
        public final static String Sity_id = "Sity_id";
        public final static String Street_id = "Street_id";
        public final static String home = "home";
    }
    
    public final static class GeoList{
        public final static String Table = "GeoList";
        public final static String id = "id";
        public final static String Nationality_id = "Nationality_id";
        public final static String Continents_id = "Continents_id";
        public final static String Sitys_id = "Sitys_id";
        public final static String Countrys_id = "Countrys_id";
        public final static String Streets_id = "Streets_id";
    }
    
    public final static class Links{
        public final static String Table = "Links";
        public final static String id = "id";
        public final static String link = "link";
    }
    
    public final static class Mark{
        public final static String Table = "Mark";
        public final static String Keyword_id = "Keyword_id";
        public final static String Table_N = "Table_N";
        public final static String event_id = "event_id";
        public final static String TimeLine_id = "TimeLine_id";
    }
    
    public final static class Nationality{
        public final static String Table = "Nationality";
        public final static String id = "id";
        public final static String Nationality = "Nationality";
    }
    
    public final static class Person{
        public final static String Table = "Person";
        public final static String id = "id";
        public final static String start_date = "start_date";
        public final static String end_date = "end_date";
    }
    
    public final static class Person_geo{
        public final static String Table = "Person_geo";
        public final static String persona_id = "persona_id";
        public final static String start_date = "start_date";
        public final static String end_date = "end_date";
        public final static String geo_id = "geo_id";
    }
    
    public final static class Person_Keyword{
        public final static String Table = "Person_Keyword";
        public final static String Persona = "Persona";
        public final static String Keyword = "Keyword";
        public final static String Table_N = "Table_N";
    }
    
    public final static class Street{
        public final static String Table = "Street";
        public final static String id = "id";
        public final static String Street = "Street";
    }
    
    public final static class Synonym{
        public final static String Table = "Synonym";
        public final static String Keyword_id = "Keyword_id";
        public final static String Synonym_keyword_id = "Synonym_keyword_id";
        public final static String table_id = "table_id";
    }
    
    public final static class TimeLine{
        public final static String Table = "TimeLine";
        public final static String id = "id";
        public final static String description = "description";
    }
    
    public final static class TimeLists{
        public final static String Table = "TimeLists";
        public final static String id = "id";
        public final static String title = "title";
        public final static String about = "about";
        public final static String color = "color";
        public final static String visable = "visable";
    }
    
}
