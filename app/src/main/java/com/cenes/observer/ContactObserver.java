package com.cenes.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.cenes.backendManager.UserApiManager;
import com.cenes.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 8/10/18.
 */

public class ContactObserver  extends ContentObserver {

    private Context context;

    public ContactObserver(Handler handler) {
        super(handler);
    }

    public ContactObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange (boolean selfChange,Uri uri)
    {
        /*Cursor cursor = this.context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " Desc");
        if (cursor.moveToNext()) {
            String id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.w("Contact ID", id);
            Log.w("Person Name",name);
        }*/
        fetchDeviceContactList();
    }

    public void fetchDeviceContactList() {

        Map<String, String> contactsArrayMap = new HashMap<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        Log.e("phoneNo : "+phoneNo , "Name : "+name);

                        if (phoneNo.indexOf("\\*") != -1 || phoneNo.indexOf("\\#") != -1 || phoneNo.length() < 7) {
                            continue;
                        }
                        try {
                            String parsedPhone = phoneNo.replaceAll(" ","").replaceAll("-","").replaceAll("\\(","").replaceAll("\\)","");
                            if (parsedPhone.indexOf("+") == -1) {
                                parsedPhone = "+"+parsedPhone;
                            }
                            //contactObject.put(parsedPhone, name);
                            //contactsArray.put(contactObject);
                            if (!contactsArrayMap.containsKey(parsedPhone)) {
                                contactsArrayMap.put(parsedPhone, name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    pCur.close();
                }
            }

        }
        if(cur!=null){
            cur.close();
        }

        JSONArray contactsArray = new JSONArray();
        for (Map.Entry<String, String> entryMap: contactsArrayMap.entrySet()) {
            JSONObject contactObject = new JSONObject();
            try {
                contactObject.put(entryMap.getKey(), entryMap.getValue());
                contactsArray.put(contactObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = context.getSharedPreferences("CenesPrefs", context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        final String authToken = prefs.getString("authToken", null);


        final JSONObject userContact = new JSONObject();
        try {
            userContact.put("userId",userId);
            userContact.put("contacts",contactsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
            try  {
                //Your code goes here
                String response = "";
                    URL obj = new URL("http://ec2-18-216-7-227.us-east-2.compute.amazonaws.com");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    //add request header
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setConnectTimeout(300);
                    con.setRequestProperty("Content-Type", "application/json");

                    if (authToken != null) {
                        con.setRequestProperty("token", authToken);
                    }

                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                    out.write(userContact.toString());
                    out.close();
                    int responseCode = con.getResponseCode();
                    System.out.println("Response Code : " + responseCode);
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    response = br.readLine();

                    JSONObject jObj = new JSONObject(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });

        thread.start();
    }


}
