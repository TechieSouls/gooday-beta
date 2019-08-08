package com.cenesbeta.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by mandeep on 5/10/17.
 */

public class CenesFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


        if(refreshedToken != null && !refreshedToken.isEmpty()) {
            getSharedPreferences("CenesPrefs", MODE_PRIVATE).edit().putString("FcmToken", refreshedToken).apply();
        }

        System.out.println("onTokenRefresh: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }
}
