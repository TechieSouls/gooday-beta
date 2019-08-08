package com.cenesbeta.Manager;


import android.util.Log;

import com.cenesbeta.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by puneet on 15/8/17.
 */

public class JsonParsing {

    int TIMEOUT_MILLIS = 20 * 1000;

    // constructor
    public JsonParsing() {}



    public JSONObject httpPost(String url,JSONObject postData,String authToken) {
        String response = "";
        try {
            System.out.println("API : "+url);
            System.out.println("Post Data : "+postData.toString());
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(TIMEOUT_MILLIS);
            con.setRequestProperty("Content-Type", "application/json");

            if (authToken != null) {
                con.setRequestProperty("token", authToken);
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
            out.write(postData.toString());
            out.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            response = br.readLine();

            System.out.println(response.toString());
            JSONObject jObj = new JSONObject(response.toString());
            return jObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject httpPostMultipart(String url, User user, File file) {
        String charset = "UTF-8";
        try {
            MultipartUtility multipart = new MultipartUtility(url,charset,user.getAuthToken());

            Log.e("User Id",String.valueOf(user.getUserId()));
            multipart.addFormField("userId", String.valueOf(user.getUserId()));

            multipart.addFilePart("mediaFile",file);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");
            StringBuffer responseBuffer = new StringBuffer();
            for (String line : response) {
                //Log.e("File Output : ",line);
                responseBuffer.append(line);
            }
            JSONObject jObj = new JSONObject(responseBuffer.toString());
            return jObj;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject httpPostMultipartWithFormData(String url, Map<String, String> formFields, File file, String authToken) {
        String charset = "UTF-8";
        try {
            MultipartUtility multipart = new MultipartUtility(url,charset,authToken);

            for (Map.Entry<String, String> formFieldsMap : formFields.entrySet()) {
                multipart.addFormField(formFieldsMap.getKey(), formFieldsMap.getValue());
            }

            multipart.addFilePart("uploadfile",file);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");
            StringBuffer responseBuffer = new StringBuffer();
            for (String line : response) {
                //Log.e("File Output : ",line);
                responseBuffer.append(line);
            }
            JSONObject jObj = new JSONObject(responseBuffer.toString());
            return jObj;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray httpGet(String url,String authToken) {
        String response = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("GET");
            con.setConnectTimeout(TIMEOUT_MILLIS);
            if (authToken != null) {
                con.setRequestProperty("token", authToken);
            }

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            response = br.readLine();
            System.out.println(response.toString());
            JSONArray jsonArray = new JSONArray(response.toString());
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject httpGetJsonObject(String url,String authToken) {
        StringBuilder sb = new StringBuilder();
        try {
            System.out.println("API : "+url);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("GET");
            con.setConnectTimeout(TIMEOUT_MILLIS);
            if (authToken != null) {
                con.setRequestProperty("token", authToken);
            }

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                System.out.println(sb.toString());
                JSONObject jsonObject = new JSONObject(sb.toString());
                return jsonObject;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject httpPutJsonObject(String url,String authToken) {
        StringBuilder sb = new StringBuilder();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("PUT");
            con.setConnectTimeout(TIMEOUT_MILLIS);
            if (authToken != null) {
                con.setRequestProperty("token", authToken);
            }

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            System.out.println(sb.toString());
            JSONObject jsonObject = new JSONObject(sb.toString());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    class MultipartUtility {
        private String boundary ;
        private static final String LINE_FEED = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        /**
              * This constructor initializes a new HTTP POST request with content type
              * is set to multipart/form-data
              * @param requestURL
              * @param charset
              * @throws IOException
              */
        MultipartUtility(String requestURL, String charset,String token)
                throws IOException {
            this.charset = charset;

            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";

            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(TIMEOUT_MILLIS);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("token",token);
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        /**
         * Adds a form field to the request
         * @param name field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }
        /**
         * Adds a upload file section to the request
         * @param fieldName name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a header field to the request.
         * @param name - name of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }
        /**
         * Completes the request and receives response from the server.
         * @return a list of Strings as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public List<String> finish() throws IOException {
            List<String> response = new ArrayList<>();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }

            return response;
        }
    }
}
