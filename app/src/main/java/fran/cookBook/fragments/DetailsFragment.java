package fran.cookBook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import fran.cookBook.R;
import fran.cookBook.model.MainModel;
import fran.cookBook.object.Recipe;

// El fragment muestra los atributos de una receta al detalle
public class DetailsFragment extends Fragment {

    private MainModel model;
    private ImageView image;
    private TextView title, ingredients, steps, txt_people, txt_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //model
        model = ViewModelProviders.of(getActivity()).get(MainModel.class);

        //actualizar el estado de la pantalla
        model.setScreen(getString(R.string.screen_2));

        //views
        image = getView().findViewById(R.id.im_photo);
        title = getView().findViewById(R.id.tx_title);
        ingredients = getView().findViewById(R.id.tx_ingredients);
        steps = getView().findViewById(R.id.tx_steps);
        txt_people = getView().findViewById(R.id.tx_people);
        txt_time = getView().findViewById(R.id.tx_time);

        //observador que espera a que el model haya actualizado una receta
        final Observer<Recipe> observer = new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {

                //si hay alguna receta...
                if(recipe !=null){

                    //cargar datos del model
                    image.setImageBitmap(recipe.getPhoto());
                    title.setText(recipe.getTitle());

                    ArrayList<String>ingr_list= recipe.getIngredients();
                    String ingr="";
                    for(String a:ingr_list) ingr=ingr + a +"\n";
                    ingredients.setText(ingr);

                    ArrayList<String>pasos_list= recipe.getSteps();
                    String pas="";

                    for(String i:pasos_list) pas=pas + "PASO " + (pasos_list.indexOf(i)+1) + "\n" + i +"\n\n";
                    steps.setText(pas);

                    txt_people.setText(recipe.getPeople() + " personas");
                    txt_time.setText(recipe.getTime());

                    Log.e("people",recipe.getPeople());
                    Log.e("time",recipe.getTime());

                }


            }
        };

        model.getRecipe().observe(this,observer);


    }


}
