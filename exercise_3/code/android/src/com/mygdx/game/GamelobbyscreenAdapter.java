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
    private Context context;
    private JoingameInterface joingameInterface;

    GamelobbyscreenAdapter(Context context, List<String> data,JoingameInterface joinInterface)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.joingameInterface = joinInterface;
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

        TextView textGamename;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textGamename = itemView.findViewById(R.id.textviewGamename);
            imageView = itemView.findViewById(R.id.imageViewMarioIconGamelobby);
            imageView.setImageResource(R.drawable.mario_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    joinGame(textGamename.getText().toString());
                }
            });
        }
    }

    public void joinGame(String gameID)
    {
        joingameInterface.joinGame(gameID);
    }
}
