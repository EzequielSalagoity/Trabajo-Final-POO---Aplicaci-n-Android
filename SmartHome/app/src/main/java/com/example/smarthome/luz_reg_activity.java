/*
    Está actividad permite regular la intensidad un LED a través de una barra ya sea pulsando el valor
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class luz_reg_activity extends AppCompatActivity
{
    // Variables LUZ_REG_ACTIVITY
    private TextView intensidad;
    private SeekBar seekBar;
    private String text;
    private int progress;

    // Variables MQTT
    private MQTTClass mqttClass;
    private String server; //"tcp://192.168.0.145:1883";
    private final String pub_topic = "casa/luz_reg";
    private Boolean state;

    // ---------------> Métodos LUZ_REG_ACTIVITY <---------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luz_reg);

        intensidad = findViewById(R.id.textView_intensidad_porcentaje);
        seekBar = findViewById(R.id.seekBar_luz);

        // Defino rangos del SeekBar
        seekBar.setProgress(0);  // Estado Inicial
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                intensidad.setText(progress+"%");
                if( mqttClass.isConnected() )
                {
                    mqttClass.Publish(getApplicationContext(),pub_topic,String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        state = getCloudMQTT();
        server = getServer();
        mqttClass = new MQTTClass( getApplicationContext(), server);
        mqttClass.Connect( getApplicationContext(),state);

    }

    protected void onPause()
    {
        super.onPause();
        saveData();
    }

    protected void onResume()
    {
        super.onResume();
        loadData();
        UpdateViews();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("estado_porcentaje_text",intensidad.getText().toString());
        editor.putInt("estado_porcentaje",seekBar.getProgress());

        editor.apply();
        //Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();

    }
    // Leemos los datos almacenados de las variables text y switch_state del archivo Shared Preferences
    private void loadData()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        text = sharedPreferences.getString("estado_porcentaje_text","Porcentaje");  //Default Value
        progress =sharedPreferences.getInt("estado_porcentaje",0);   //Default Value

    }
    // Actualizamos la el estado de las Views en función de la información leída del archivo Shared Preferences
    private void UpdateViews()
    {
        intensidad.setText(text);
        seekBar.setProgress(progress);
    }
    // Verificamos si el servidor MQTT está en la web
    private Boolean getCloudMQTT()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean estado;

        estado = sharedPreferences.getBoolean("estado_cloud_server",false);

        return estado;
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

}
