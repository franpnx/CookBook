package fran.cookBook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import fran.cookBook.R;

// Activity de presentación que muestra el logo de la aplicación
public class splashScreenActivity extends AppCompatActivity {

    private Handler hand;
    private Runnable r;
    private long time = 2000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Abre la activity MainActivity automáticamente después de haber pasado unos segundos
        hand = new Handler();
        r = new Runnable() {
           @Override
           public void run() {
               startActivity(new Intent(getApplicationContext(), MainActivity.class));
           }
       };

        hand.postDelayed(r, time);

    }

    @Override //cierra la aplicación
    protected void onRestart() {
        super.onRestart();

        finish();
    }

}
