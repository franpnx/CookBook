package fran.cookBook.object;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

//Clase que represanta un objeto receta
public class Recipe  {

    private int id;
    private String title;
    private Bitmap photo;
    private ArrayList<String> ingredients;
    private ArrayList<String> steps;
    private String type;
    private String time;
    private String people;

//constructor
    public Recipe(int id, String title, Bitmap photo, ArrayList<String> ingredients, ArrayList<String> steps, String type, String time, String people) {
        this.id = id;
        this.title = title;
        this.photo = photo;
        this.ingredients = ingredients;
        this.steps = steps;
        this.type = type;
        this.time = time;
        this.people = people;
    }

    /////////////////////////////////////////  getters and setters  ///////////////////////////////////////////////////////////////

    public int getId(){ return id; }

    public String getTitle() {
        return title;
    }


    public ArrayList<String> getIngredients() {
        return ingredients;
    }


    public Bitmap getPhoto() {
        return photo;
    }


    public ArrayList<String> getSteps() {
        return steps;
    }


    public String getTime() {
        return time;
    }


    public String getPeople() {
        return people;
    }

    public String getType() {
        return type;
    }
}
