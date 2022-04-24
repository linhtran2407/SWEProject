package edu.brynmawr.volunteerapp;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    //stretch updates
    Context context;
    List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    // Usually involves inflating a layout from XML and returning the holder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("EventsAdapter", "OnCreateViewHolder");
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(eventView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "OnBindViewHolder" + position);
        // get the movie at the passed in position
        Event event = events.get(position);
        // bind the movie data into the VH
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Event event) {
            tvTitle.setText(event.name);
            tvOverview.setText(event.desc);

        }
    }
}