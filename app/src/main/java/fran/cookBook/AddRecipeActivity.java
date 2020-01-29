package fran.cookBook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;

import fran.cookBook.fragments.IngFragment;
import fran.cookBook.fragments.StepsFragment;
import fran.cookBook.fragments.TitleFragment;
import fran.cookBook.model.MainModel;
import fran.cookBook.object.Recipe;
import fran.cookBook.tools.Utils;
import fran.cookBook.model.AddRecipeModel;

// La activity tiene como finalidad añadir una receta nueva
public class AddRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_title,bt_ing, bt_steps;
    private static final int TAKE_IMAGE = 50;
    private static final int LOAD_IMAGE = 100;
    private Uri photoURI;
    private Bitmap bitmap;
    private AddRecipeModel model;
    private final int color =Color.parseColor("#D0D0D0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        //views
        bt_ing=findViewById(R.id.bt_ing);
        bt_steps =findViewById(R.id.bt_steps);
        bt_title =findViewById(R.id.bt_title);
        bt_title.setBackgroundColor(0);
        bt_steps.setBackgroundColor(color);
        bt_ing.setBackgroundColor(color);

        //listeners
        bt_ing.setOnClickListener(this);
        bt_steps.setOnClickListener(this);
        bt_title.setOnClickListener(this);

        //cargar fragment
        getSupportFragmentManager().beginTransaction().add(R.id.add_container,new TitleFragment()).commit();

        //modelo
        model = ViewModelProviders.of(this).get(AddRecipeModel.class);


        Intent intent = getIntent();
        String screen = intent.getStringExtra("screen");
        model.setScreenMode(screen);

        //comprobar si la pantalla está en modo edición
        if(screen.equals("edit")){

            getSupportActionBar().setTitle(R.string.editRecipe);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF9E4B02));

            //cargar receta de la bd
            model.loadRecipe(intent.getIntExtra("id",0));

        }else{
            getSupportActionBar().setTitle(R.string.addRecipeActivity);
        }

    }

    @Override // Gestiona el evento de los 3 botones disponibles
    public void onClick(View v) {


        if(v== bt_title){

            //cambia el color de los botones
            bt_title.setBackgroundColor(0);
            bt_steps.setBackgroundColor(color);
            bt_ing.setBackgroundColor(color);

            //cambia de fragment en el contenedor
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.add_container,new TitleFragment());
            transaction.commit();

        }else if(v==bt_ing){

            //cambia el color de los botones
            bt_title.setBackgroundColor(color);
            bt_steps.setBackgroundColor(color);
            bt_ing.setBackgroundColor(0);

            //cambia de fragment en el contenedor
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.add_container,new IngFragment());
            transaction.commit();

        }else if(v== bt_steps){

            //cambia el color de los botones
            bt_title.setBackgroundColor(color);
            bt_steps.setBackgroundColor(0);
            bt_ing.setBackgroundColor( color);

            //cambia de fragment en el contenedor
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.add_container,new StepsFragment());
            transaction.commit();

        }

    }

    // Elimina el view del fragment fragment_ing
    public void onDelete(View v ){

        ViewGroup ly_ing = findViewById(R.id.ly_ingr);
        ly_ing.removeView((View) v.getParent());

    }

    // Elimina el view del fragment fragment_steps
    public void onDelete2(View v){

        ViewGroup ly_steps =findViewById(R.id.ly_steps);
        ly_steps.removeView((View) v.getParent());

    }

    // Maneja el botón camera situado en el fragment_title
    public void camera(View v){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //permiso no concedido

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //mostrar petición de permiso
            } else {
                //pedir permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        225);
            }

        } else {
            //cuando el permiso ha sido concecido
            takePhoto();
        }

    }

    //toma una foto con la camera
    public void takePhoto(){

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Título");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción");
            photoURI = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(i, TAKE_IMAGE);

        }
    }

    //maneja el botón gallery situado en el fragment_title
    public void gallery(View v){

        // Añade una imagen desde la galería
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, LOAD_IMAGE);

    }

    @Override //maneja las peticiones de takePhoto y gallery
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si la petición es de tomar una foto...
        if (requestCode == TAKE_IMAGE && resultCode == RESULT_OK) {

            try {
                //obtener bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                //rotar el bitmap
                Bitmap rotatedBitmap = Utils.photo.rotateImage(bitmap,photoURI,this);
                //escalar el bitmap
                bitmap = Utils.photo.resizeImage(rotatedBitmap);
                //añadir bitmap al model
                model.setPhoto(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Si la petición es de obtener foto de la galería...
        }else if(requestCode == LOAD_IMAGE && resultCode == RESULT_OK){

            photoURI = data.getData();
            try {
                //obtener el bitmap
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                //rotar el bitmap
                Bitmap rotatedBitmap = Utils.photo.rotateImage(bitmap,photoURI,this);
                //escalar el bitmap
                bitmap = Utils.photo.resizeImage(rotatedBitmap);
                //añadir bitmap al model
                model.setPhoto(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override//gestiona cuando se pulsa el botón atrás
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {

            // Diálogo que muestra si el usuario desea volver atrás
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.dialog_return_title);
            dialog.setMessage(R.string.dialog_return);
            dialog.setCancelable(false);
            dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    finish();
                }
            });
            dialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            dialog.show();

        } return super.onKeyDown(keyCode, event);

    }

    @Override //maneja la petición de permiso
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 225: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permiso concedido, tomar foto
                    takePhoto();

                } else {
                    // permiso denegado
                }

            }

        }

    }


}
