package fran.cookBook.sqlite;

// Clase esquema de creación de la base de datos
public class DBschema {

    public static final class recipes {

        public static final String TABLENAME = "recipes";
        public static final String IDRECIPE = "idrecipe";
        public static final String TITLE = "title";
        public static final String IMAGE = "image";
        public static final String INGREDIENTS = "ingredients";
        public static final String STEPS = "steps";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String PEOPLE = "people";

        //sentencia sql para crear tabla
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLENAME + " ( "
                + IDRECIPE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL, "
                + INGREDIENTS + " TEXT NOT NULL, "
                + STEPS + " TEXT NOT NULL, "
                + TYPE + " TEXT NOT NULL, "
                + TIME + " TEXT NOT NULL, "
                + PEOPLE + " TEXT NOT NULL );";

        //sentencia sql para crear tabla
        public static final String CREATE_TABLE_USER = "CREATE TABLE user ( demo INTEGER NOT NULL);";
    }

}
