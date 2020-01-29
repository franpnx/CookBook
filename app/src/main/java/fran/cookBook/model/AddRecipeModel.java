package fran.cookBook.model;

import android.app.Application;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import fran.cookBook.object.Recipe;
import fran.cookBook.tools.Utils;
import fran.cookBook.R;
import fran.cookBook.sqlite.DBinterface;


//modelo para añadir una receta en la base de datos
public class AddRecipeModel extends AndroidViewModel {
    public AddRecipeModel(@NonNull Application application) {
        super(application);
    }

    //variables
    private String title;
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> steps = new ArrayList<>();
    private int type;
    private String time;
    private String people;
    private int id;
    private MutableLiveData<Bitmap> photo = new MutableLiveData<>();
    private MutableLiveData<String> screenMode = new MutableLiveData<>();


    //inserta receta en base de datos
    public long insertRecipeDB(){

        String send_tipe ="";
        String send_ingr;
        String send_steps;
        Bitmap image;

        //conversiones para la BD
        image = photo.getValue();
        send_ingr = Utils.basic.getStringfromList(ingredients);
        send_steps = Utils.basic.getStringfromList(steps);

        if(type == R.id.rb_lunch){
            send_tipe ="Almuerzo";
        }else if(type ==R.id.rb_dinner){
            send_tipe ="Cena";
        }else if(type ==R.id.rb_breackfast){
            send_tipe ="Desayuno";
        }else if(type ==R.id.rb_snack){
            send_tipe ="Merienda";
        }


        //comprueba si no faltan campos por rellenar
        if(!title.equals("") && image !=null && !send_ingr.equals("") && !send_steps.equals("") && !send_tipe.equals("")){

            //abrir Base de Datos  SQLITE
            DBinterface db = new DBinterface(getApplication());
            db.open();

            String screen = screenMode.getValue();

            if(screen.equals("edit")){
                //actualizar datos
                if(!db.updateRecipe(id,title, image,send_ingr, send_steps, send_tipe, time, people)){
                    Log.e("ERROR_DB","Error al insertar datos en la BD");
                    db.close();
                }


            }else{
                //insertar datos en la BD
                if(db.addRecipe(title, image,send_ingr, send_steps, send_tipe, time, people)==-1){
                    Log.e("ERROR_DB","Error al insertar datos en la BD");
                    db.close();
                }

            }


            db.close();

            return 1;


        }else{

            return -1;
        }

    }

    //obtener receta por id
    public void loadRecipe(int idRecipe){


        //abrir Base de Datos  SQLITE
        DBinterface db = new DBinterface(getApplication());
        db.open();

        Cursor c = db.loadRecipe(idRecipe);
        //comprovar si ha habido algún resultado
        if (c.getCount() != 0) {

            c.moveToFirst();

            while (!c.isAfterLast()) {

                title =c.getString(0);
                byte[] blob = c.getBlob(1);
                photo.setValue(Utils.photo.blobToBitmap(blob));
                String ingrTxt = c.getString(2);
                //transforma el texto en una ArrayList
                ingredients = Utils.basic.getList(ingrTxt);
                String stepsTxt = c.getString(3);
                //transforma el texto en una ArrayList
                steps = Utils.basic.getList(stepsTxt);
                String typeTxt = c.getString(4);

                if(typeTxt.equals("Almuerzo")){
                    type =R.id.rb_lunch;
                }else if(typeTxt.equals("Cena")){
                    type = R.id.rb_dinner;
                }else if(typeTxt.equals("Desayuno")){
                    type = R.id.rb_breackfast;
                }else if(typeTxt.equals("Merienda")){
                    type = R.id.rb_snack;
                }

                time = c.getString(5);
                people = c.getString(6);
                id = c.getInt(7);

                c.moveToNext();
            }


        } else {

            Log.e("ERROR","DATABASE DATA NOT LOADED");
        }
        db.close();


    }

    ////////////////////////////////////  getters and setters  //////////////////////////////////////////////////////////

    public void setAll( String titulo, String tiempo, String personas, int tipo) {
        this.title = titulo;
        this.time = tiempo;
        this.people = personas;
        this.type = tipo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MutableLiveData<Bitmap> getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap item) {
        photo.setValue(item);
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }


    public MutableLiveData<String> getScreenMode() {
        return screenMode;
    }

    public void setScreenMode(String a) {
        screenMode.setValue(a);
    }

    public int getId() {
        return id;
    }
}
