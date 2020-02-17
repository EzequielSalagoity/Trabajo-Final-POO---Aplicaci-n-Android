/*
    Está actividad es la encargada de manejar la primer actividad de la aplicación que consiste en mostrar una pantalla con
    el nombre y logo de la aplicación por una determinada cantidad de segundos.
    Está acción normalmente se denomina Splash Screen.
    ------>  Se puede elegir el tiempo de duración de la pantalla modificando la variable SPLASH_TIME_OUT <------
*/

package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity
{
    private static int SPLASH_TIME_OUT = 3500;  // 4 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent homeIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
