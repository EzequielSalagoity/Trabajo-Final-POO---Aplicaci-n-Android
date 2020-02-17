/*
    Esta clase contiene todos los métodos necesarios para realizar una conexión a un servidor MQTT.
    Está clase permite:
    -   Crear un objeto MqttAndroidClient
    -   Conectarse al servidor MQTT de dos formas distintas:
        -> Solo conexión
        -> Conexión y subscripción a un tópico determinado
    -   Sobrescribir el método Callback utilizado para la recepción de mensajes
    -   Desconectarse del servidor MQTT
    -   Suscribirse a un tópico y elegir el QoS(calidad de servicio)
    -   Publicar un mensaje en un cierto tópico
    -   Conocer si nuestro objeto MQTTClass está conectado al servidor MQTT

 */

package com.example.smarthome;

import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MQTTClass
{
    protected MqttAndroidClient client;
    protected MqttConnectOptions options;

    // Datos de CloudMQTT
    protected final String username = "psngmpoh" ;
    protected final String password = "1WwW6kwnBrSk" ;

    protected final int CONNECTION_TIMEOUT = 60;
    protected final int KEEP_ALIVE_INTERVAL = 200;
    protected final String clientId = "Ezequiel";
    protected final int Qos = 1;

    /// Constructor
    public MQTTClass(final Context context, final String server)
    {

        client = new MqttAndroidClient( context, server, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause)
            {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token)
            {
            }
        });
    }

    // MÉTODOS:  -----> ( hacerlos privados con getters ) <-----

    protected void Connect(final Context context,final Boolean cloud_server ){

        if( cloud_server )
        {
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);//seconds
            options.setConnectionTimeout(CONNECTION_TIMEOUT);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(context,
                                "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);//seconds
            options.setConnectionTimeout(CONNECTION_TIMEOUT);
            //options.setUserName(username);
            //options.setPassword(password.toCharArray());

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(context,
                                "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void Connect(final Context context, final String sub_topic, final Boolean cloud_server){
        if( cloud_server )
        {
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);//seconds
            options.setConnectionTimeout(CONNECTION_TIMEOUT);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show();

                        /// ----------> Funcion para suscribirse a los topics <------------
                        Subscribe(context,sub_topic,Qos);

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(context,
                                "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);//seconds
            options.setConnectionTimeout(CONNECTION_TIMEOUT);
            //options.setUserName(username);
            //options.setPassword(password.toCharArray());

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show();

                        /// ----------> Funcion para suscribirse a los topics <------------
                        Subscribe(context,sub_topic,Qos);

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(context,
                                "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    protected void setCallback(MqttCallbackExtended callback) {
        client.setCallback(callback);
    }

    protected void Disconnect(final Context context)
    {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    //Toast.makeText(context,"Disconnected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Toast.makeText(context,
                            "Disonnection Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(context,
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void Subscribe(final Context context, String topic,int QoS)
    {
        try {
            client.subscribe(topic, QoS);
            //Toast.makeText(context, "Client Subscribe", Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void Publish(final Context context,String topic,String message)
    {
        try {
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean isConnected()
    {
        return client.isConnected();
    }

}
