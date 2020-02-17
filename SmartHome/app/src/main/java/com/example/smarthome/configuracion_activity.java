/*
    En está actividad se ingresan todos los datos del broker al que se conecta el dispositivo móvil.
    Está actividad permite:
    ->  Ingresar los datos del broker:
        ->  Dirección IP o web
        ->  Puerto
    ->  Mostrar los datos del servidor ingresado en Configuración
    ->  Verificar si el servidor MQTT está en la web
    ->  Guardar los datos ingresados de forma permanente utilizando el fichero Shared Preferences
 */

package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class configuracion_activity extends AppCompatActivity
{
    // Variables CONFIGURACION_ACTIVITY
    private EditText broker;
    private EditText port;
    private Button save_data;
    private TextView data;
    private Switch cloud_server;

    // Variables MQTT
    private String text_broker;
    private int text_port;
    private Boolean estado = false;

    // ---------------> Métodos CONFIGURACION_ACTIVITY <---------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        broker = findViewById(R.id.editText_broker);
        port = findViewById(R.id.editText_port);
        save_data = findViewById(R.id.button_save);
        data = findViewById(R.id.textView_data);
        cloud_server = findViewById(R.id.switch_cloud);

        cloud_server.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                {
                    estado = true;
                }
                else
                {
                    estado = false;
                }
            }
        });

        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Para evitar que la actividad se cierre si no hay datos ingresados en los EditTexts
                if( !TextUtils.isEmpty(broker.getText().toString()) || !TextUtils.isEmpty(port.getText().toString()) )
                {
                    saveData();
                }
            }
        });
    }

    protected void onResume()
    {
        super.onResume();
        loadData();
        UpdateViews();
    }
    //Almacenamos el estado del botón en un archivo llamado Shared Preferences
    //que persiste siempre, incluso sin importar si se apaga el celular.
    private void saveData()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if( !TextUtils.isEmpty(broker.getText().toString()) && !TextUtils.isEmpty(port.getText().toString())  )
        {
            editor.putString("texto_broker",broker.getText().toString());
            editor.putInt("texto_port",Integer.parseInt(port.getText().toString()));
            editor.putBoolean("estado_cloud_server",estado);
            editor.apply();
            Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();
            loadData();
            UpdateViews();
        }
        else
        {
            if( TextUtils.isEmpty(broker.getText().toString()) )
            {
                editor.putInt("texto_port",Integer.parseInt(port.getText().toString()));
                editor.putBoolean("estado_cloud_server",estado);
                editor.apply();
                Toast.makeText(this,"Data port saved",Toast.LENGTH_SHORT).show();
                loadData();
                UpdateViews();
            }
            else
            {
                editor.putString("texto_broker",broker.getText().toString());
                editor.putBoolean("estado_cloud_server",estado);
                editor.apply();
                Toast.makeText(this,"Data broker saved",Toast.LENGTH_SHORT).show();
                loadData();
                UpdateViews();
            }
        }
    }
    // Leemos los datos almacenados de las variables text y switch_state del archivo Shared Preferences
    private void loadData()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        text_broker = sharedPreferences.getString("texto_broker","192.168.0.145");  //Default Value
        text_port = sharedPreferences.getInt("texto_port",1883);   //Default Value
        estado = sharedPreferences.getBoolean("estado_cloud_server",false);  //Default Value

    }
    // Actualizamos la el estado de las Views en función de la información leída del archivo Shared Preferences
    private void UpdateViews()
    {
        data.setText("Broker: "+ text_broker + "\n");
        data.append("Port: "+ text_port + "\n");
        data.append("CloudMQTT: " + estado);
        cloud_server.setChecked(estado);

    }

}
