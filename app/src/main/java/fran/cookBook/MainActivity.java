package fran.cookBook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;

import fran.cookBook.fragments.GridFragment;
import fran.cookBook.model.MainModel;
import fran.cookBook.object.Recipe;

// Activity principal, por defecto siempre mostrará el fragmento GridFragment
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainModel model;
    private MenuItem delete;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views y widgets
        Toolbar toolbar = findViewById(R.id.toolbar); setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);  navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //modelo
        model = ViewModelProviders.of(this).get(MainModel.class);

        //Maneja el evento de click sobre el widget FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Recipe recipe = model.getRecipe().getValue();

                //mirar en qué pantalla estamos
                String screen = model.getScreen().getValue();
                Intent intent = new Intent(getApplicationContext(),AddRecipeActivity.class);
                //en función de la pantalla en la que estemos, adjuntamos estado de pantalla
                if(screen.equals(getString(R.string.screen_2))){

                    intent.putExtra("screen","edit");
                    intent.putExtra("id",recipe.getId());
                    intent.putExtra("title", recipe.getTitle());
                }else{
                    intent.putExtra("screen","add");
                }

                startActivity(intent);
            }
        });

        //ocultar título en la actionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        //cargar datos demo de la base de datos si es necesario
        if(model.needDemo()==true) {
            //cargar datos demo
           model.demoData();

        }


        //cargar recetas
        model.setType(getString(R.string.menu_todo));
        model.LoadItems();

        //cargar fragment GridFragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, new GridFragment()).commit();


    }

    @Override // al volver a iniciar la actividad, volver a cargar los datos
    protected void onRestart() {
        super.onRestart();

        model.LoadItems();
    }

    @Override // gestiona cuando se presiona atrás
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //si el drawer está abierto, lo cierra
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override //infla el menú
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        delete = menu.getItem(0);

        //observador que mira el estado de la pantalla
        Observer<String> observerScreen = new Observer<String>() {
            @Override
            public void onChanged(String s) {

                //cambia los iconos en función de la pantalla
                if(s.equals(getString(R.string.screen_1))){
                    delete.setVisible(false);
                    fab.setImageResource(R.drawable.ic_add);
                }else{
                    delete.setVisible(true);
                    fab.setImageResource(R.drawable.ic_edit);
                }
            }
        };

        model.getScreen().observe(this,observerScreen);

        return true;
    }

    @Override//Gestiona los eventos del menú
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //comprueba el elemento del menú
        if (id == R.id.delete) {

            //quiere eliminar la receta?
            AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
            dialog1.setTitle(R.string.dialog_delete_title);
            dialog1.setMessage(getString(R.string.dialog_delete) +"\n"+ model.getRecipe().getValue().getTitle() + " ?" );
            dialog1.setCancelable(false);
            dialog1.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                    //suprimir receta de la base de datos
                    model.deleteRecipe();

                }
            });
            dialog1.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {

                }
            });
            dialog1.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override //Gestiona los eventos del NavigationView
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_todo) {

            model.setType(getString(R.string.menu_todo));
            model.LoadItems();

        } else if (id == R.id.menu_desayuno) {

            model.setType(getString(R.string.menu_desayuno));
            model.LoadItems();

        } else if (id == R.id.menu_almuerzo) {

            model.setType(getString(R.string.menu_almuerzo));
            model.LoadItems();

        } else if (id == R.id.menu_merienda) {

            model.setType(getString(R.string.menu_merienda));
            model.LoadItems();

        } else if (id == R.id.menu_cena) {

            model.setType(getString(R.string.menu_cena));
            model.LoadItems();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
