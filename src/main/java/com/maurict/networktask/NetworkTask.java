package com.maurict.networktask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * NetworkTask.java (c) MaurICT 2020
 */
public class NetworkTask {

    private String requestUrl;
    private RequestMethod requestMethod;
    private HashMap<String, String> headers;
    private HashMap<String, String> parameters;
    private HashMap<String, String> formData;

    private static final String TAG = NetworkTask.class.getSimpleName();


    /**
     * Create new instance of NetworkTask
     * @param url The url that should be called
     * @param requestMethod The HTTP method. Leave empty for HTTP GET
     */
    public NetworkTask(String url, RequestMethod requestMethod) {
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.formData = new HashMap<>();
        this.requestUrl = url;
        this.requestMethod = requestMethod;
    }

    /**
     * Create new HTTP-get task
     * @param url The url
     */
    public NetworkTask(String url) {
        this(url, RequestMethod.GET);
    }

    /**
     * Static method to create NetworkTask for chaining
     * @param url The url
     * @return NetworkTask
     */
    public static NetworkTask fromUrl(String url) {
        return new NetworkTask(url);
    }

    /**
     * Create NetworkTask from url and requestMethod for chaining
     * @param url The url to be called
     * @param method The method
     * @return NetworkTask
     */
    public static NetworkTask fromUrl(String url, RequestMethod method) {
        return new NetworkTask(url).withRequestMethod(method);
    }

    /**
     * Set request method
     *
     * @param requestMethod HTTP method like GET, POST e.d.
     */
    public void setRequestMethod(RequestMethod requestMethod) { this.requestMethod = requestMethod; }

    /**
     * Set request URL
     * @param url The url
     */
    public void setUrl(String url) { this.requestUrl = url; }

    //Add headers, parameters or HTTP form data
    public void addHeader(String key, String value) { this.headers.put(key, value); }
    public void addParameter(String key, String value) { this.parameters.put(key, value); }
    public void addFormData(String key, String value) { this.formData.put(key, value); }

    //Chaining methods for header, parameter, formdata and request methods
    public NetworkTask withHeader(String key, String value) {
        this.addHeader(key, value);
        return this;
    }

    public NetworkTask withParameter(String key, String value) {
        this.addParameter(key, value);
        return this;
    }

    public NetworkTask withFormData(String key, String value) {
        this.addFormData(key, value);
        return this;
    }

    public NetworkTask withRequestMethod(RequestMethod method) {
        this.requestMethod = method;
        return this;
    }

    /**
     * Execute task and get result via callback. Use Lambda function for callback
     * @param callback The callback
     */
    public void execute(NetworkCallback callback) {
        new InnerTask(callback).execute();
    }

    /**
     * This is the inner task that will be executed.
     */
    private class InnerTask extends AsyncTask<String, Void, NetworkResult> {

        private final NetworkCallback callback;
        public InnerTask(NetworkCallback callback) { this.callback = callback; }


        @Override
        protected NetworkResult doInBackground(String... strings) {
            StringBuilder url = new StringBuilder(requestUrl);
            if (parameters.size() > 0) {
                parameters.forEach((k, v) -> {
                    try {
                        url.append("&").append(URLEncoder.encode(k, "UTF-8")).append("=").append(URLEncoder.encode(v, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Failed to encode URL data");
                        e.printStackTrace();
                    }
                });
            }

            StringBuilder form_data = new StringBuilder();
            if (formData.size() > 0) {
                formData.forEach((k, v) -> {
                    try {
                        form_data.append("&").append(URLEncoder.encode(k, "UTF-8")).append("=").append(URLEncoder.encode(v, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Failed to URLEncode data");
                        e.printStackTrace();
                    }
                });
            }

            try {
                System.out.println("Opening connection to given url (" + url + ") HTTP-" + requestMethod);
                HttpURLConnection connection = (HttpURLConnection) new URL(url.toString().replaceFirst("&", "?")).openConnection();

                //Add headers to connection
                headers.forEach(connection::addRequestProperty);
                connection.setRequestMethod(requestMethod.method);

                //Write request if needed
                if (formData.size() > 0) {
                    byte[] postData = new NetworkResult(form_data.toString().replaceFirst("&", "")).toArray();
                    connection.setDoOutput(true);
                    connection.setInstanceFollowRedirects(false);
                    connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.addRequestProperty("charset", "utf-8");
                    connection.addRequestProperty("Content-Length", Integer.toString(postData.length));
                    connection.setUseCaches(false);

                    //Write data using OutputStream
                    try (DataOutputStream ws = new DataOutputStream(connection.getOutputStream())) {
                        ws.write(postData);
                    }
                }

                //Read response
                Log.d(TAG, "Reading response: "+connection.getResponseCode());
                Log.d(TAG, "Response message: "+connection.getResponseMessage());

                InputStream is = connection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                Log.d(TAG, "Flushed binary data. Returning BinaryData object");

                HashMap<String, String> responseHeaders = new HashMap<>();
                connection.getHeaderFields().forEach((k,v) -> {
                    Log.d(TAG, "k: "+k+ " - v: "+v);
                    responseHeaders.put(k, connection.getHeaderField(k));
                });

                //Create and return new NetworkResult object. See BinaryData.java
                NetworkResult result = new NetworkResult(data);
                result.setResponseHeaders(responseHeaders);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to get data: " + e.getMessage());

                //Return error
                return new NetworkResult(e);
            }
        }

        @Override
        protected void onPostExecute(NetworkResult networkResult) {
            super.onPostExecute(networkResult);
            callback.callback(networkResult);
        }
    }
}

