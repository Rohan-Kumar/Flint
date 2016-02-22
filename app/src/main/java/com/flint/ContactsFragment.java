package com.flint;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ContactsFragment extends Fragment {


    RecyclerView recyclerView;
    MyRecyclerViewAdapter myRecyclerViewAdapter;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        setUpViews(view);

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


        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            public MyHolder(View itemView) {
                super(itemView);
            }
        }

    }

}
