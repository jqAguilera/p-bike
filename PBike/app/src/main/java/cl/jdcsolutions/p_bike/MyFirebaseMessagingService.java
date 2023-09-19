package cl.jdcsolutions.p_bike;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Aquí puedes manejar los mensajes recibidos
    }

    @Override
    public void onNewToken(String token) {
        // Aquí puedes manejar la generación de un nuevo token de registro
    }
}



