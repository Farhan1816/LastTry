package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class home extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView noMusicTextView;
    NotificationManager notificationManagerOffline;
    private ArrayList<AudioModel> songsList;

    public home() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);


        songsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        CenterScrollListener centerScrollListener = new CenterScrollListener(layoutManager);
        recyclerView.addOnScrollListener(centerScrollListener);


        noMusicTextView = view.findViewById(R.id.no_songs_text);
        createChannelOffline();


        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = requireActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }
        if(songsList.size()==0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(new MusicListAdapter(songsList,requireContext().getApplicationContext()));
        }

        return view;
    }

    private void createChannelOffline() {
        Log.d("debug", "creatingChannel");
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CreateNotificationOffline.CHANNELID, "Farhan", NotificationManager.IMPORTANCE_DEFAULT);
        }

        notificationManagerOffline = (NotificationManager)requireContext().getSystemService(NotificationManager.class);
        if (notificationManagerOffline != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManagerOffline.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,requireContext().getApplicationContext()));
        }
    }


    @Override
    public void onClick(View view) {

    }
}
