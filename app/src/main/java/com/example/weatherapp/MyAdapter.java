package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Weather[] mDataset;

    public void setDataset(Weather[] mDataset) {
        this.mDataset = mDataset;
    }



//    public MyAdapter(Weather[] myDataset) {
//        mDataset = myDataset;
//    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.dateText.setText(mDataset[position].getDate());
        holder.descriptionText.setText(mDataset[position].getDescription());
        holder.temperatureText.setText(mDataset[position].getTemperature());
        Picasso.get().load(mDataset[position].getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        final TextView dateText;
        final ImageView imageView;
        final TextView descriptionText;
        final TextView temperatureText;

        public MyViewHolder(View v) {
            super(v);

            dateText = v.findViewById(R.id.date);
            imageView = v.findViewById(R.id.imageView2);
            descriptionText = v.findViewById(R.id.description2);
            temperatureText = v.findViewById(R.id.temperature);

        }
    }


}
