package fran.cookBook.model;

import android.app.Application;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import fran.cookBook.R;
import fran.cookBook.object.Recipe;
import fran.cookBook.tools.Utils;
import fran.cookBook.sqlite.DBinterface;

// Modelo para cargar recetas de la base de datos
public class MainModel extends AndroidViewModel {
    public MainModel(@NonNull Application application) {
        super(application);
    }

    //variables
    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDelete = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Recipe>> recipes = new MutableLiveData<>();
    private MutableLiveData<String> screen = new MutableLiveData<>();
    private String type;


    //retorna una lista co las recetas cargadas de la base de datos
    public void LoadItems(){

        ArrayList<Recipe> list = new ArrayList<>();

        //valores demo
        DBinterface db= new DBinterface(getApplication());
        db.open();

        //miramos el tipo seleccionado

        Cursor c = null;



        if(type.equals("Todo")){

            c = db.loadRecipes();
        }else{


            c = db.loadRecipesByType(type);
        }

        if (c.getCount() != 0) {

            c.moveToFirst();

            while (!c.isAfterLast()) {

                String title =c.getString(0);
                byte[] blob = c.getBlob(1);
                Bitmap photo = Utils.photo.blobToBitmap(blob);
                String ingredients = c.getString(2);
                //transforma el texto en una ArrayList
                ArrayList<String>ingrList = Utils.basic.getList(ingredients);
                String steps = c.getString(3);
                //transforma el texto en una ArrayList
                ArrayList<String> stepsList = Utils.basic.getList(steps);
                String type = c.getString(4);
                String time = c.getString(5);
                String people = c.getString(6);
                int id = c.getInt(7);

                list.add(new Recipe(id, title, photo,ingrList, stepsList, type, time, people));

                c.moveToNext();
            }

        } else {

            Log.e("ERROR","DATABASE DATA NOT LOADED");
        }
        db.close();

        //actualiza la lista de recetas y la receta
        recipes.setValue(list);
        if(list.size()>0) recipe.setValue(list.get(0));

    }

    // inserta en la database todos los datos de demostración
    public void demoData(){

        DBinterface db = new DBinterface(getApplication());
        db.open();

        if (insertData(db, R.string.d_01_title,R.drawable.d_01,R.array.d_01_ingredients,R.array.d_01_steps,R.string.d_01_type,R.string.d_01_time,R.string.d_01_people)!=-1){

            if (insertData(db,R.string.d_02_title,R.drawable.d_02,R.array.d_02_ingredients,R.array.d_02_steps,R.string.d_02_type,R.string.d_02_time,R.string.d_02_people)!=-1){

                if (insertData(db,R.string.d_03_title,R.drawable.d_03,R.array.d_03_ingredients,R.array.d_03_steps,R.string.d_03_type,R.string.d_03_time,R.string.d_03_people)!=-1){

                    if (insertData(db,R.string.d_04_title,R.drawable.d_04,R.array.d_04_ingredients,R.array.d_04_steps,R.string.d_04_type,R.string.d_04_time,R.string.d_04_people)!=-1){

                        if (insertData(db,R.string.d_05_title,R.drawable.d_05,R.array.d_05_ingredients,R.array.d_05_steps,R.string.d_05_type,R.string.d_05_time,R.string.d_05_people)!=-1){

                            if (insertData(db,R.string.d_06_title,R.drawable.d_06,R.array.d_06_ingredients,R.array.d_06_steps,R.string.d_06_type,R.string.d_06_time,R.string.d_06_people)!=-1){

                                //cambia la configuración de inicio, ya no necesita cargar datos demo
                                db.setDataConf(1);
                            }
                        }
                    }
                }
            }
        }
        db.close();


    }

    //inserta en la database una receta de demostración
    public long insertData(DBinterface db, int titleID,  int imageID, int ingrID, int stepsID, int typeID, int timeID, int peopleID ){


        Bitmap image = BitmapFactory.decodeResource(getApplication().getResources(), imageID);
        Bitmap resized = Utils.photo.resizeImage(image);
        String[]ingr = getApplication().getResources().getStringArray(ingrID);
        String[]stp = getApplication().getResources().getStringArray(stepsID);
        String ingredients = Utils.basic.getStringfromArray(ingr);
        String steps = Utils.basic.getStringfromArray(stp);

        return db.addRecipe(getApplication().getString(titleID), resized, ingredients,
                steps, getApplication().getString(typeID), getApplication().getString(timeID), getApplication().getString(peopleID)) ;


    }

    //retorna true si hay que cargar datos demo por primera vez y false si ya se cargaron anteriormente
    public Boolean needDemo(){

        DBinterface db = new DBinterface(getApplication());
        db.open();

        int demo=0 ;
        Boolean need = true;

        Cursor c = db.getDataConf();
        if (c.getCount() != 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {

                demo =c.getInt(0);
                c.moveToNext();
            }
        } else {

            Log.e("ERROR","DATABASE NOT LOAD");
        }

        db.close();

        if(demo==0){

            need = true;

        }else{
            need = false;
        }

        return need;
    }

    //borra una receta de la base de datos
    public void deleteRecipe(){

        DBinterface db = new DBinterface(getApplication());

        db.open();
        //borra la receta
        if(db.deleteRecipe(recipe.getValue().getId())==-1){
            Log.e("ERROR DB","Error deleting row");
        }
        db.close();

        //actualiza la lista de recetas
        LoadItems();




    }


    /////////////////////////////////  getters and setters  ////////////////////////////////////////////////////

    public void setScreen(String text){
        screen.setValue(text);
    }

    public MutableLiveData<String> getScreen() {
        return screen;
    }

    public void setRecipe(Recipe item){
        recipe.setValue(item);
    }

    public MutableLiveData<Recipe> getRecipe(){
        return recipe;
    }

    public MutableLiveData<ArrayList<Recipe>> getRecipes(){
        return recipes;
    }

    public MutableLiveData<Boolean>  getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete.setValue(delete);
    }

    public void setType(String type) {
        this.type = type;
    }
}
