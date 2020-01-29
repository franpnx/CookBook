package fran.cookBook.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fran.cookBook.R;
import fran.cookBook.tools.Utils;
import fran.cookBook.model.AddRecipeModel;

import java.util.ArrayList;

// Fragment que sirve para añadir ingredientes a una receta
public class IngFragment extends Fragment {

    //variables
    private ViewGroup ly_ing;
    private TextView addIng;
    private ArrayList<String> send_ing=new ArrayList<>();
    private AddRecipeModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ing, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //model
        model = ViewModelProviders.of(getActivity()).get(AddRecipeModel.class);

        //views
        ly_ing = getView().findViewById(R.id.ly_ingr);
        addIng = getView().findViewById(R.id.tx_addIng);

        //carga ingredientes del model
        ArrayList<String>list = model.getIngredients();
        if(!list.isEmpty()){

            int i=0;
            for(String a:list){
                //añadir view con el ingrediente de la lista
                Utils.basic.addItem(R.layout.list_item,ly_ing,getActivity());
                View v = ly_ing.getChildAt(i);
                TextView v_child = v.findViewById(R.id.et_item);
                v_child.setText(a);
                i++;
            }

        }else{
            //si no hay datos en el modelo, cargar un view vacío
            Utils.basic.addItem(R.layout.list_item,ly_ing,getActivity());
        }

        //añade un view vacío a la vista al hacer click
        addIng.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Utils.basic.addItem(R.layout.list_item,ly_ing,getActivity());
            }
        });

    }

    @Override//envía los datos al model
    public void onStop() {
        super.onStop();

        send_ing = getTextViews(ly_ing);
        model.setIngredients(send_ing);
    }

    //retorna una lista con los textos de los hijos del viewGroup
    public ArrayList<String> getTextViews(ViewGroup viewParent) {

        //recorre los views hijos y guarda los textos en la lista
        int count = viewParent.getChildCount();
        ArrayList<String> list = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            View v = viewParent.getChildAt(a);
            TextView v_child = v.findViewById(R.id.et_item);
            String text = v_child.getText().toString();
            list.add(text);
        }
        return list;
    }



}

