package com.flint;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rohan on 2/18/2016.
 */
public class CheckUser extends AsyncTask<String, Void, String> {


    String Response = "";

    String URL;
    ArrayList<paramClass> parameters;

    public CheckUser(String URL, ArrayList<paramClass> parameters) {

        this.URL = URL;
        this.parameters = parameters;

    }

    @Override
    protected String doInBackground(String... params) {


        URL url = null;
        try {
            url = new URL(URL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            Uri.Builder builder = new Uri.Builder();
            for(int i=0;i<parameters.size();i++){
                builder.appendQueryParameter(parameters.get(i).paramName,parameters.get(i).paramValue);
            }



            String query = builder.build().getEncodedQuery();

            OutputStream os = httpURLConnection.getOutputStream();

            BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            mBufferedWriter.write(query);
            mBufferedWriter.flush();
            mBufferedWriter.close();
            os.close();

            httpURLConnection.connect();
            BufferedReader mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inline;
            while ((inline = mBufferedInputStream.readLine()) != null) {
                Response += inline;
            }
            mBufferedInputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Testing", Response);
        return Response;
    }

    public class paramClass{
        String paramName,paramValue;
    }

}
