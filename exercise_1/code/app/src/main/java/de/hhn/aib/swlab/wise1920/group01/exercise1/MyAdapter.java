package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter {
    private LiveData<List<Timer>> timerLiveData;
    private List<Timer> mTimer;
    private OnLongClickListener onLongClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View viewGroup;
        public final TextView tvDescription;
        public Switch switch1;

        public MyViewHolder(View v) {
            super(v);
            viewGroup = v;
            tvDescription = viewGroup.findViewById(R.id.tv_description);
            switch1 = viewGroup.findViewById(R.id.switch1);
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
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, int position) {
        final Timer timer = mTimer.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timer.getTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        ((MyViewHolder) holder).tvDescription.setText(hour + ":" + minute);
        if(timer.isActive())
        {
            ((MyViewHolder) holder).switch1.setChecked(true);
        }

        else if(timer.isActive()==false){
            ((MyViewHolder) holder).switch1.setChecked(false);
        }
        Log.e("timerId", String.valueOf(timer.getId()));
        ((MyViewHolder) holder).tvDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onLongClickListener != null) {
                    onLongClickListener.onLongClick(timer);
                    Log.e("MyAdapter", "longPressCalled");
                }
                return true;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mTimer != null) {
            return mTimer.size();
        }
        return 0;
    }

    public interface OnLongClickListener {
        void onLongClick(Timer timer);
    }


    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
