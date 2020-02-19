package com.example.smarthome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity
{
    // Variables MAIN_ACTIVITY
    private ImageView luz;
    private ImageView luz_reg;
    private ImageView temperatura;

    // Variables MQTT
    private static MQTTClass mqttClass;
    protected String server; //"tcp://192.168.0.145:1883";
    protected Boolean state;
    private final String sub_topic = "casa/#"; // Me suscribo a todos los topics

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);  // Oculta nombre de la app en el TOOLBAR


        //Asigno cada botón(imagen) con una variable
        luz = findViewById(R.id.button_luz);
        luz_reg = findViewById(R.id.button_luz_reg);
        temperatura = findViewById(R.id.button_temperatura);


        // Defino el método OnClick para cada botón
        luz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act_luz = new Intent(MainActivity.this,luz_activity.class);
                startActivity(act_luz);
            }
        });

        luz_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act_luz_reg = new Intent(MainActivity.this,luz_reg_activity.class);
                startActivity(act_luz_reg);
            }
        });

        temperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act_temp = new Intent(MainActivity.this,temperatura_activity.class);
                startActivity(act_temp);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent act_conf = new Intent(MainActivity.this,configuracion_activity.class);
            startActivity(act_conf);
        }
        if (id == R.id.action_help) {
            Intent act_ayuda = new Intent(MainActivity.this,ayuda_activity.class);
            startActivity(act_ayuda);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if( mqttClass.isConnected() )
        {
            mqttClass.Disconnect(getApplicationContext());
        }
    }

    protected static MQTTClass getMqttClass()
    {
        return mqttClass;
    }

    protected void onResume()
    {
        super.onResume();
        state = getCloudMQTT();
        server = getServer();
        mqttClass = new MQTTClass( getApplicationContext(), server);
        mqttClass.Connect( getApplicationContext(), sub_topic, state);

    }

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
