package com.mygdx.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GamelobbyscreenAdapter extends RecyclerView.Adapter<GamelobbyscreenAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<String> data;

    GamelobbyscreenAdapter(Context context, List<String> data)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = data.get(position);
        holder.textGamename.setText(title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textGamename,textDescription;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textGamename = itemView.findViewById(R.id.textviewGamename);
            textDescription = itemView.findViewById(R.id.textviewDescription);
            imageView = itemView.findViewById(R.id.imageViewMarioIconGamelobby);
            imageView.setImageResource(R.drawable.mario_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Item Clicked " + textGamename.getText());
                }
            });
        }
    }
}
