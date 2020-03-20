# Smart Home
El proyecto Smart Home es un proyecto de Internet de las Cosas en el que usando el protocolo MQTT podemos ver información y manejar sensores en tiempo real a través de una aplicación para Android.

-> La aplicación está diseñada de tal forma que si no estamos conectados al broker MQTT(Connection Failed), no nos dejará ingresar a ninguna de las 3 ventanas: Temperatura y Humedad, Luz o Luz Regulable.

-> Los componentes electrónicos que manejamos a través de la aplicación son el sensor DHT 11 ( temperatura y humedad ) y dos leds, uno de ellos tiene su intensidad fija, es decir, solo lo podemos prender y apagar, y el otro, solo podemos regular su intensidad a través de la aplicación.

-> La parte del hardware es manejada utilizando la placa NodeMCU en la que viene integrada la ESP8266 lo que nos permite conectarla a WiFi.

-> Se configuró a la placa NodeMCU con un modo Access Point el cual nos permite configurar a qué red WiFi nos deseamos conectar y elegir el broker MQTT.

# Capturas de Pantalla Smart Home:

<img src="/Screenshots TP Final Android/SplashScreen.jpeg" width="250">  <img src="/Screenshots TP Final Android/Home.jpeg" width="250">  <img src="/Screenshots TP Final Android/Home_error_conexion.jpeg" width="250">
<img src="/Screenshots TP Final Android/Home_tools.jpeg" width="250">  <img src="/Screenshots TP Final Android/Luz.jpeg" width="250">  <img src="/Screenshots TP Final Android/Luz_Regulable.jpeg" width="250">
<img src="/Screenshots TP Final Android/Temp_y_Hum.jpeg" width="250">  <img src="/Screenshots TP Final Android/Ayuda.jpeg" width="250">  <img src="/Screenshots TP Final Android/Configuracion.jpeg" width="250">
<img src="/Screenshots TP Final Android/AccessPoint.jpeg" width="250">

