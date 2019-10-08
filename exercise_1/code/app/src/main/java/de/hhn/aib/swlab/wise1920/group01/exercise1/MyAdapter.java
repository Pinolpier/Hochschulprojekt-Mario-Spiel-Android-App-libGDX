package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private TodoRepository todoRepository;
    private List<Todo> list = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewGroup viewGroup;
        private final TextView tvDescription;
        private final Switch switch1;

        public MyViewHolder(ViewGroup v) {
            super(v);
            viewGroup = v;
            tvDescription = viewGroup.findViewById(R.id.tv_description);
            switch1 = viewGroup.findViewById(R.id.switch1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
        todoRepository.getTodos().observeForever(new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                list = todos;
                notifyDataSetChanged();
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_timelist, parent, false);

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDescription.setText(list.get(position).getDescription());
        System.out.println(holder.tvDescription.getText());
        System.out.println(holder.switch1.isChecked());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}
