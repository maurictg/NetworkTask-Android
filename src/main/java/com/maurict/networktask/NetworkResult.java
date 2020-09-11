package com.maurict.networktask;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Scanner;

/**
 * BinaryData.java (c) MaurICT 2020
 */
public class NetworkResult {

    //Private variables
    private byte[] data;
    private boolean success;
    private int status;

    private static final String TAG = NetworkResult.class.getSimpleName();

    /**
     * Create new instance of NetworkResult
     * @param status Indicates the HTTP status
     */
    public NetworkResult(int status) {
        this.status = status;
        this.success = (status >= 200 && status < 300);
    }

    /**
     * Create new BinaryData instance
     * @param data Binary data, containing Drawable or String
     * @param status Indicates the HTTP status
     */
    public NetworkResult(byte[] data, int status) {
        this(status);
        this.data = data;
    }

    public NetworkResult(byte[] data) { this(data, 200); }

    /**
     * Convert Drawable to BinaryData
     * @param drawable The drawable
     */
    public NetworkResult(Drawable drawable) {
        if(drawable == null){
            this.data = new byte[0];
            Log.e(TAG, "Drawable is NULL");
            return;
        }

        Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        this.data = os.toByteArray();
    }

    /**
     * Convert String to BinaryData
     * @param value The string
     * @param success Indicates if task succeed
     */
    public NetworkResult(String value, boolean success) {
        this.data = value.getBytes();
        this.success = success;
    }

    public NetworkResult(String value) { this(value, true); }

    /**
     * Get String from inner byte[]
     * @return String
     */
    @NonNull
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
     * Get http status
     * @return int with HTTP status code
     */
    public int getStatus() {
        return status;
    }
}
