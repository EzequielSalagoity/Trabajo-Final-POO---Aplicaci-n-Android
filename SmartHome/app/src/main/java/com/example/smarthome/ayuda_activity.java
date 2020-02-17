/*
    Está actividad muestra los pasos a seguir en caso de no poder conectarse al servidor
    MQTT elegido.
 */

package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ayuda_activity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
    }
}
