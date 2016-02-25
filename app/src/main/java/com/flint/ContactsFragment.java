package com.flint;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragment extends Fragment {


    RecyclerView recyclerView;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    String Response = "";

    ArrayList<Contactsdata> contactsdataArrayList = new ArrayList<>();

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        setUpViews(view);

        new LoadContactsTask().execute();

        return view;
    }

    private void setUpViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }


    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new MyHolder(LayoutInflater.from(getActivity()).inflate(R.layout.single_contact_view, parent, false));

        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            holder.FullName.setText(contactsdataArrayList.get(position).fname);
            holder.Tag.setText("@"+contactsdataArrayList.get(position).uname);

        }

        @Override
        public int getItemCount() {
            return contactsdataArrayList.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            TextView FullName,Tag;
            CircleImageView profileImage;

            public MyHolder(View itemView) {
                super(itemView);

                FullName = (TextView) itemView.findViewById(R.id.userName);
                Tag = (TextView) itemView.findViewById(R.id.nameTag);
                profileImage = (CircleImageView) itemView.findViewById(R.id.imageView);

            }
        }

    }

    class LoadContactsTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("http://84.200.84.218:3001/getAllContacts");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder();

                builder.appendQueryParameter("uname", "abhishekoriok");

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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parseData(Response);
            myRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void parseData(String response) {

        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Contactsdata contactsdata = new Contactsdata();

                contactsdata.id = jsonObject.getString("_id");
                contactsdata.uname = jsonObject.getString("uname");
                contactsdata.fname = jsonObject.getString("fname");
                contactsdata.mobile = jsonObject.getString("mobilenumber");
                JSONArray jsonArray1 = jsonObject.getJSONArray("connections");
                for (int j = 0; j < jsonArray1.length(); j++) {
                    contactsdata.connections.add(jsonArray1.getString(j));
                }
//                contactsdata.emailId = jsonObject.getString("emailid");
                contactsdataArrayList.add(contactsdata);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Contactsdata contactsdata : contactsdataArrayList) {
            Log.d("Testing", contactsdata.fname);
        }
    }

    class Contactsdata {

        String id, uname, fname, mobile, emailId;
        ArrayList<String> connections;

        public Contactsdata() {
            connections = new ArrayList<>();
        }

    }

}


