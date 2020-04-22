package com.maurict.networktask;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * NetworkResult.java (c) MaurICT 2020
 */
public class NetworkResult {

    //Private variables
    private byte[] data;
    private boolean success;
    private Exception exception;
    private HashMap<String, String> responseHeaders;

    private static final String TAG = NetworkResult.class.getSimpleName();

    /**
     * Create new instance of NetworkResult
     * @param success Indicates if task succeed
     */
    public NetworkResult(boolean success) {
        this.success = success;
        responseHeaders = new HashMap<>();
    }

    /**
     * Create new BinaryData instance
     * @param data Binary data, containing Drawable or String
     * @param success Indicates if task succeed
     */
    public NetworkResult(byte[] data, boolean success) {
        this(success);
        this.data = data;
    }

    public NetworkResult(byte[] data) { this(data, true); }

    /**
     * Convert Drawable to BinaryData
     * @param drawable The drawable
     */
    public NetworkResult(Drawable drawable, boolean success) {
        if(drawable == null){
            this.data = new byte[0];
            Log.e(TAG, "Drawable is NULL");
            return;
        }

        Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        this.data = os.toByteArray();
        responseHeaders = new HashMap<>();
    }

    public NetworkResult(Drawable drawable) { this(drawable, true); }

    /**
     * Convert String to BinaryData
     * @param value The string
     * @param success Indicates if task succeed
     */
    public NetworkResult(String value, boolean success) {
        this.data = value.getBytes();
        this.success = success;
        responseHeaders = new HashMap<>();
    }

    public NetworkResult(String value) { this(value, true); }

    /**
     * Create failing result with exception
     * @param exception The exception
     */
    public NetworkResult(Exception exception) {
        this.data = new byte[0];
        this.success = false;
        this.exception = exception;
        responseHeaders = new HashMap<>();
    }

    /**
     * Get String from inner byte[]
     * @return String
     */
    @Override
    public String toString() {
        InputStream is = new ByteArrayInputStream(this.data);
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Get inner data length
     * @return int
     */
    public int length() { return this.data.length; }

    /**
     * Get drawable from inner byte[]
     * @return Drawable
     */
    public Drawable toDrawable() {
        Log.d(TAG, "Converting to drawable");

        if(data.length == 0){
            Log.e(TAG, "Data size must be greater than 0");
            return null;
        }

        InputStream is = new ByteArrayInputStream(this.data);
        return Drawable.createFromStream(is, "Useless");
    }

    /**
     * Get result as byte[]
     * @return byte[]
     */
    public byte[] toArray() {
        return data;
    }

    /**
     * Indicates if task succeed and the result contains data
     * @return bool
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Get exception if exists
     * @return Exception
     */
    public Exception getError() {
        return exception;
    }

    /**
     * Get the response headers
     * @return HashMap(string, string)
     */
    public HashMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Set response headers
     * @param responseHeaders The response headers the result must contain
     */
    public void setResponseHeaders(HashMap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * Get response header
     * @param key The key
     * @return The value
     */
    public String getHeader(String key) {
        return responseHeaders.get(key);
    }

    /**
     * Set exception. Sets success to false
     * @param exception The exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
        this.success = false;
    }
}
