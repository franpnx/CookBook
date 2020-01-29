package fran.cookBook.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import fran.cookBook.R;
import fran.cookBook.model.AddRecipeModel;

// Fragment para añadir datos a una receta como título, imagen y personas
public class TitleFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    //variables
    private String[] people = {"1","2","4","6"};
    private Spinner addPeople, addTime;
    private String[] time = {"5 min","15 min","30 min","45 min","50 min","60 min"};
    private AddRecipeModel model;
    private EditText addTitle;
    private ImageView addImage;
    private String send_title, send_time, send_people;
    private int send_type;
    private RadioGroup rb_group;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title, container, false);


    }

    @Override//inicializa todos los componentes
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //modelo
        model = ViewModelProviders.of(getActivity()).get(AddRecipeModel.class);

        //views
        addPeople =getView().findViewById(R.id.sp_addPeople);
        addPeople.setOnItemSelectedListener(this);
        addTime = getView().findViewById(R.id.sp_addTime);
        addTime.setOnItemSelectedListener(this);
        addTitle = getView().findViewById(R.id.tx_addTitle);
        addImage = getView().findViewById(R.id.addImage);
        rb_group = getView().findViewById(R.id.rb_group);

        //adapters para los spinner
        ArrayAdapter ad_per = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, people);
        ad_per.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addPeople.setAdapter(ad_per);

        ArrayAdapter ad_ti = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, time);
        ad_ti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addTime.setAdapter(ad_ti);

        //carga datos del modelo
        chargeModel();

        //observador que mira si el bitmap ha sido actualizado
        final Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {

                //asignar el bitmap al view cada vez que cambie
                addImage.setImageBitmap(bitmap);

            }
        };
        model.getPhoto().observe(this,observer);

    }


    @Override //carga el texto del elemento seleccionado
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        if(parent.getId()==R.id.sp_addPeople){
            send_people =  parent.getItemAtPosition(pos).toString();

        }else if(parent.getId()==R.id.sp_addTime){
            send_time = parent.getItemAtPosition(pos).toString();
        }
    }

    @Override //carga el texto por defecto
    public void onNothingSelected(AdapterView<?> parent) {

        send_people = parent.getItemAtPosition(0).toString();
        send_time = parent.getItemAtPosition(0).toString();


    }

    @Override //envía los datos al modelo
    public void onStop() {
        super.onStop();

         send_title = addTitle.getText().toString();

         // get radiobutton ID
         int i = rb_group.getCheckedRadioButtonId();

         if(i!=-1){
             send_type = i;
         }

         model.setAll(send_title, send_time, send_people, send_type);


    }

    //carga datos del modelo
    public void chargeModel(){

        if(model.getTitle()!=null){
            addTitle.setText(model.getTitle());
        }


        if(model.getPeople()!=null){
            String item=model.getPeople();
            int i=0;
            for(String a: people){

                if(a.equals(item)){
                    addPeople.setSelection(i);
                }
                i++;
            }
        }

        if(model.getTime()!=null){
            String item=model.getTime();
            int i=0;
            for(String a: time){

                if(a.equals(item)){
                    addTime.setSelection(i);
                }
                i++;
            }
        }

        if(model.getType()!=-1){
          rb_group.check(model.getType());
        }

    }


}
