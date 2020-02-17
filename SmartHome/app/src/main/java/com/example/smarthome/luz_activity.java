/*
    Está actividad permite encender o apagar un LED a través de un botón ya sea pulsándolo
    ó deslizándolo.
    Está actividad permite:
    ->  Conectarse al servidor MQTT elegido en Configuración
    ->  Desconectarse del servidor MQTT
    ->  Enviar los datos del estado del botón al tópico elegido
    ->  Actualizar, guardar y leer el estado de las Views en función de los datos almacenados en el fichero Shared Preferences
        -> SaveData,loadData y UpdateViews
    ->  Obtener los datos del servidor ingresado en Configuración
    ->  Verificar si el servidor MQTT está en la web
 */

package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class luz_activity extends AppCompatActivity
{
    // Variables LUZ_ACTIVITY
    private TextView estado;
    private Switch estado_switch;
    private String text;
    private Boolean switch_state;

    // Variables MQTT
    private MQTTClass mqttClass;
    private String server; //"tcp://192.168.0.145:1883";
    private final String pub_topic = "casa/luz";
    private Boolean state;

    // ---------------> Métodos LUZ_ACTIVITY <---------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luz);

        estado = findViewById(R.id.textView_estado_luz);
        estado_switch = findViewById(R.id.switch_luz);

        estado_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                {
                    estado.setText("Encendida");
                    if( mqttClass.isConnected() )
                    {
                        mqttClass.Publish(getApplicationContext(),pub_topic,"on");
                    }
                }
                else
                {
                    estado.setText("Apagada");
                    if( mqttClass.isConnected() )
                    {
                        mqttClass.Publish(getApplicationContext(),pub_topic,"off");
                    }
                }
            }
        });

        state = getCloudMQTT();
        server = getServer();
        mqttClass = new MQTTClass( getApplicationContext(), server);
        mqttClass.Connect( getApplicationContext(),state);
    }


    protected void onResume()
    {
        super.onResume();
        loadData();
        UpdateViews();
    }

    protected void onPause()
    {
        super.onPause();
        saveData();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if( mqttClass.isConnected() )
        {
            mqttClass.Disconnect(getApplicationContext());
        }
    }

    //Almacenamos el estado del botón en un archivo llamado Shared Preferences
    //que persiste siempre, incluso sin importar si se apaga el celular.
    private void saveData()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("estado_luz_text",estado.getText().toString());
        editor.putBoolean("estado_luz_switch",estado_switch.isChecked());

        editor.apply();
        //Toast.makeText(this,"Data luz saved",Toast.LENGTH_SHORT).show();

    }

    // Leemos los datos almacenados de las variables text y switch_state del archivo Shared Preferences
    private void loadData()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        text = sharedPreferences.getString("estado_luz_text","Apagada");  //Default Value
        switch_state = sharedPreferences.getBoolean("estado_luz_switch",false);   //Default Value
    }
    // Actualizamos la el estado de las Views en función de la información leída del archivo Shared Preferences
    private void UpdateViews()
    {
        estado.setText(text);
        estado_switch.setChecked(switch_state);
    }
    //  Obtenemos los datos del servidor MQTT:
    //  -> Dirección IP o link
    //  -> Puerto utilizado
    private String getServer()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String text_broker;
        int text_port;

        text_broker = sharedPreferences.getString("texto_broker","192.168.0.145");  //Default Value
        text_port = sharedPreferences.getInt("texto_port",1883);   //Default Value

        return "tcp://"+text_broker+":"+text_port;
    }
    // Verificamos si el servidor MQTT está en la web
    private Boolean getCloudMQTT()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean estado;

        estado = sharedPreferences.getBoolean("estado_cloud_server",false);

        return estado;
    }
}
