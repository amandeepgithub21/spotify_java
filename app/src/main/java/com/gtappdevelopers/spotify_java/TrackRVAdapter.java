package com.gtappdevelopers.spotify_java;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class TrackRVAdapter extends RecyclerView.Adapter<TrackRVAdapter.ViewHolder> {
    // creating variables for list and context
    private ArrayList<TrackRVModal> trackRVModals;
    private Context context;
    private MediaPlayer mediaPlayer;

    // creating constructor on below line.
    public TrackRVAdapter(ArrayList<TrackRVModal> trackRVModals, Context context) {
        this.trackRVModals = trackRVModals;
        this.context = context;
    }

    @NonNull
    @Override
    public TrackRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating layout on below line.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_rv_item, parent, false);
        mediaPlayer = new MediaPlayer();

        // Set up the audio attributes for the media player
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackRVAdapter.ViewHolder holder, int position) {
        // setting data to text views.
        TrackRVModal trackRVModal = trackRVModals.get(position);
        holder.trackNameTV.setText(trackRVModal.getTrackName());
        holder.trackArtistTV.setText(trackRVModal.getTrackArtist());
        
        // adding click listener for track item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackUrl = "https://open.spotify.com/track/" + trackRVModal.getId();

                Uri uri = Uri.parse(trackUrl); // missing 'http://' will cause crashed
               /* playSong(String.valueOf(uri));
                Toast.makeText(context, ""+trackUrl, Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onClickurl: "+trackUrl);*/
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra("albumUrl", trackRVModal.external_urls);
                context.startActivity(intent);
            }
        });
    }
    private void playSong(String songUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return trackRVModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating and initializing variables for text views.
        private TextView trackNameTV, trackArtistTV;
        private ImageView songimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameTV = itemView.findViewById(R.id.idTVTrackName);
            trackArtistTV = itemView.findViewById(R.id.idTVTrackArtist);
            songimage = itemView.findViewById(R.id.songimageview);
        }
    }
}
