package fran.cookBook.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fran.cookBook.R;
import fran.cookBook.model.MainModel;
import fran.cookBook.object.Recipe;

// Fragment que muestra las recetas disponibles en un gridView
public class GridFragment extends Fragment {

    private GridView grid;
    private GridAdapter adapter;
    private MainModel model;
    private ArrayList<Recipe>list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //model
        model = ViewModelProviders.of(getActivity()).get(MainModel.class);

        //actualizar el estado de la pantalla
        model.setScreen(getString(R.string.screen_1));

        //cargar lista de recetas del model
        list = model.getRecipes().getValue();
        Recipe re =model.getRecipe().getValue();
        if(re==null){
            model.setRecipe(list.get(0));
        }

        //inicializar componentes
        grid= getView().findViewById( R.id.gridView);
        adapter=new GridAdapter(getContext(),list);
        grid.setAdapter(adapter);


        // observador que mira si se ha actualizado la lista de recetas
        final Observer<ArrayList<Recipe>> observer1 = new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {

                //carga la lista de recetas en el gridView
                list = model.getRecipes().getValue();
                adapter=new GridAdapter(getContext(),list);
                grid.setAdapter(adapter);
            }
        };

        model.getRecipes().observe(this,observer1);



        // Gestiona el evento de click en el gridView
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                //actualiza la receta seleccionada en el model
                Recipe recipe = (Recipe)adapter.getItem(pos);
                model.setRecipe(recipe);

                //cambia al fragment DetailsFragment
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container,new DetailsFragment());
                    transaction.addToBackStack(null);//para que vuelva al fragment anterior de la pila al darle atrás
                    transaction.commit();

            }
        });
    }


    // Clase que implementa el adaptador del gridView
    class GridAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Recipe> recipes;


        public GridAdapter(Context context, ArrayList<Recipe> recipes) {
            this.context = context;
            this.recipes = recipes;
        }

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Object getItem(int position) {
            return recipes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.grid_item, null);
            }

            TextView txt = view.findViewById(R.id.tx_grid);
            txt.setText(recipes.get(position).getTitle());

            ImageView img = (ImageView) view.findViewById(R.id.im_photo);
            img.setImageBitmap(recipes.get(position).getPhoto());

            return view;
        }
        }
        }
