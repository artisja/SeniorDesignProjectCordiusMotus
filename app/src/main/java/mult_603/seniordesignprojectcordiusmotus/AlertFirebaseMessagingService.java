package mult_603.seniordesignprojectcordiusmotus;

import android.util.Log;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by artisja on 10/18/2016.
 */

public class AlertFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyContact";


    public void onMessageRecieved(RemoteMessage remoteMessage){
        if (remoteMessage.getNotification()!=null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size()>0){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
    }
}
