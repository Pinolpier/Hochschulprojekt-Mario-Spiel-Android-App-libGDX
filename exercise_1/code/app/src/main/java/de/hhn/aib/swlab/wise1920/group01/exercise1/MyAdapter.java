package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter {
    private LiveData<List<Timer>> timerLiveData;
    private List<Timer> mTimer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View viewGroup;
        public final TextView tvDescription;

        public MyViewHolder(View v) {
            super(v);
            viewGroup = v;

            tvDescription = viewGroup.findViewById(R.id.tv_description);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LiveData<List<Timer>> timerLiveData) {
        this.timerLiveData = timerLiveData;
        timerLiveData.observeForever(new Observer<List<Timer>>() {
            @Override
            public void onChanged(List<Timer> timers) {
                mTimer = timers;
                notifyDataSetChanged();
                Log.e("AdapterOberverCalled", "" + timers.size());
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_timelist, parent, false);

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.textView.setText(mDataset[position]);
        //TODO hier die Alarme zurück umwandeln
        ((MyViewHolder) holder).tvDescription.setText(""+ mTimer.get(position).getTime());

        //TODO hier longpress für delete einfügen
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTimer.size();
    }

    void setWords(List<Timer> timer) {
        mTimer = timer;
        notifyDataSetChanged();
    }
}
