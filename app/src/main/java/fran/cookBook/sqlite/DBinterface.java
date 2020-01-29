package fran.cookBook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import fran.cookBook.tools.Utils;

import static fran.cookBook.sqlite.DBschema.*;

// Clase que implementa una base de datos SQLITE
public class DBinterface {

    public static final String TAG = "DBInterface";
    public static final String DB_NAME = "DBRecetas";
    public static final int VERSION = 1;


    private Context context;
    private DBHelper helper;
    private SQLiteDatabase db;

    ///////////////////////////////////////////////  Creación de la BD  ////////////////////////////////////////////////////////

    //Constructor
    public DBinterface(Context con ) {
        this.context = con;
        helper = new DBHelper(context);
    }

    //Crea o actualiza la base de datos
    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context con) {
            super(con, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(recipes.CREATE_TABLE);
                db.execSQL(recipes.CREATE_TABLE_USER);

                ContentValues cv = new ContentValues();
                cv.put("demo",0);
                db.insert("user",null,cv);


            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("DB_ERROR","ERROR AL CREAR LA BASE DE DATOS");
            }


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int newVersion, int oldVersion) {
            Log.w(TAG, "Actualizando Base de datos de la versión" + newVersion + " a la versión " + oldVersion + ". Se destruirán todos los datos");
            db.execSQL("DROP TABLE IF EXISTS recetas");
            onCreate(db);
        }
    }


    //Abre la base de datos
    public DBinterface open() throws SQLException {
        db = helper.getWritableDatabase();
        return this;
    }

    //Cierra la base de datos
    public void close() {
        helper.close();
    }

    /////////////////////////////////////  Métodos para modificar la Base de Datos  //////////////////////////////////////////////////////

    //Obtener todas las recetas
    public Cursor loadRecipes() {
        Cursor c = db.query(recipes.TABLENAME, new String[]{recipes.TITLE, recipes.IMAGE, recipes.INGREDIENTS, recipes.STEPS, recipes.TYPE, recipes.TIME, recipes.PEOPLE, recipes.IDRECIPE}, null, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }

    //Obtener recetas por tipo
    public Cursor loadRecipesByType(String typeR) {
        Cursor c = db.query(recipes.TABLENAME, new String[]{recipes.TITLE, recipes.IMAGE, recipes.INGREDIENTS, recipes.STEPS, recipes.TYPE, recipes.TIME, recipes.PEOPLE, recipes.IDRECIPE}, recipes.TYPE + "=" + "'" + typeR + "'", null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }

    //Obtener una receta por id
    public Cursor loadRecipe(int id){
        Cursor c = db.query(recipes.TABLENAME, new String[]{recipes.TITLE, recipes.IMAGE, recipes.INGREDIENTS, recipes.STEPS, recipes.TYPE, recipes.TIME, recipes.PEOPLE, recipes.IDRECIPE}, recipes.IDRECIPE + "=" + id, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;
    }

    //añadir receta
    public long addRecipe(String title, Bitmap photo, String ingr, String steps, String type, String time, String people){

        byte[]blob = Utils.photo.bitmapToBlob(photo);

        ContentValues cv = new ContentValues();

        //insertamos los valores que irán en las columnas
        cv.put(recipes.TITLE, title);
        cv.put(recipes.IMAGE, blob);
        cv.put(recipes.INGREDIENTS,ingr);
        cv.put(recipes.STEPS, steps);
        cv.put(recipes.TYPE, type);
        cv.put(recipes.TIME, time);
        cv.put(recipes.PEOPLE, people);

        return db.insert(recipes.TABLENAME,null,cv);

    }

    //actualizar receta
    public boolean updateRecipe(Integer id, String title, Bitmap photo, String ingr, String steps, String type, String time, String people){

        byte[]blob = Utils.photo.bitmapToBlob(photo);

        ContentValues cv = new ContentValues();

        //insertamos los valores que irán en las columnas
        cv.put(recipes.TITLE, title);
        cv.put(recipes.IMAGE, blob);
        cv.put(recipes.INGREDIENTS,ingr);
        cv.put(recipes.STEPS, steps);
        cv.put(recipes.TYPE, type);
        cv.put(recipes.TIME, time);
        cv.put(recipes.PEOPLE, people);

        return db.update(recipes.TABLENAME,cv,recipes.IDRECIPE + "=" + id,null)>0;
    }


    //obtener los datos de configuración
    public Cursor getDataConf(){

        Cursor c = db.query("user", new String[]{"demo"}, null, null, null, null, null);
        if (c != null) c.moveToFirst();
        return c;

    }

    //editar datos de configuración
    public long setDataConf(int demo){

        ContentValues cv = new ContentValues();
        cv.put("demo",demo);
        return db.insert("user",null,cv);

    }

    //suprimir receta
    public long deleteRecipe(int id){

       return db.delete(recipes.TABLENAME, recipes.IDRECIPE + "=" + id, null);

    }


}
