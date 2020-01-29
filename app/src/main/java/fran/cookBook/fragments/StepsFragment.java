package fran.cookBook.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fran.cookBook.R;
import fran.cookBook.tools.Utils;
import fran.cookBook.model.AddRecipeModel;

// Fragment que tiene como objetivo guardar los datos de una receta nueva
public class StepsFragment extends Fragment {

    private ViewGroup ly_steps;
    private TextView addStep;
    private ArrayList<String> send_steps =new ArrayList<>();
    private AddRecipeModel model;
    private Button bt_save;
    private ProgressDialog p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //modelo
        model = ViewModelProviders.of(getActivity()).get(AddRecipeModel.class);

        //views
        ly_steps = getView().findViewById(R.id.ly_steps);
        addStep = getView().findViewById(R.id.tx_addStep);
        bt_save = getView().findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //envía datos al model
                send_steps = getTextViews(ly_steps);
                model.setSteps(send_steps);

               //inserta los datos en la BD
               Hilo async = new Hilo();
               async.execute();

            }
        });

        //carga datos del modelo
        ArrayList<String>list = model.getSteps();
        if(!list.isEmpty()){

            int i=0;
            for(String a:list){
                //añadir view con el paso de la lista
                Utils.basic.addItem(R.layout.list_item_step, ly_steps,getActivity());
                View v = ly_steps.getChildAt(i);
                TextView v_child = v.findViewById(R.id.et_item_step);
                v_child.setText(a);
                i++;
            }

        }else{
            //si no hay datos en el modelo, cargar un view vacío
            Utils.basic.addItem(R.layout.list_item_step, ly_steps,getActivity());
        }


        //añade un elemento vacío a la vista al hacer click
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.basic.addItem(R.layout.list_item_step, ly_steps,getActivity());
            }
        });


    }

    @Override//envía los pasos al model
    public void onStop() {
        super.onStop();

        send_steps = getTextViews(ly_steps);
        model.setSteps(send_steps);
    }

    //retorna una lista con los textos de los hijos del viewGroup
    public ArrayList<String> getTextViews(ViewGroup viewParent) {

        //recorre los views hijos y guarda los textos en un array
        int count = viewParent.getChildCount();
        ArrayList<String> list = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            View v = viewParent.getChildAt(a);
            TextView v_child = v.findViewById(R.id.et_item_step);
            String text = v_child.getText().toString();

            list.add(text);
        }

        return list;
    }

    //Hilo asíncrono para insertar una receta en la base de datos a través del modelo
    private class Hilo extends AsyncTask<Bitmap, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //muestra la barra de progreso
            p = new ProgressDialog(getActivity());
            p.setMessage(getString(R.string.progress_loading));
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Long doInBackground(Bitmap... bm) {

            //guardar datos en la base de datos
            return model.insertRecipeDB();

        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);

            //ocultar barra de progreso
            p.hide();

            //si el resultado no ha sido favorable...
            if(result==-1){

                //faltan campos por rellenar
                Toast.makeText(getActivity(),R.string.toast_fields,Toast.LENGTH_SHORT).show();

            //si el resultado ha sido satisfactório...
            }else if(result==1){

                //receta añadida correctamente
                Toast.makeText(getActivity(),R.string.toast_recipeOK,Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }




        }
    }




}
