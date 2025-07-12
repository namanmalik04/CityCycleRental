package com.example.citycycle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.ViewHolder> {
    private Context context;
    private List<Cycle> cycleList;

    public CycleAdapter(Context context, List<Cycle> cycleList) {
        this.context = context;
        this.cycleList = cycleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topcycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cycle cycle = cycleList.get(position);
        Log.d("CycleAdapter", "Binding cycle: " + cycle.getName());

        holder.nameTextView.setText(cycle.getName());
        holder.typeTextView.setText(cycle.getType());
        holder.priceTextView.setText(String.format("Rs. %.2f", cycle.getPrice()));


        byte[] imageBytes = cycle.getBikeImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.cycleImageView.setImageBitmap(bitmap);
        } else {
            holder.cycleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loginimage)); // Placeholder image
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("cycle_id", cycle.getId());
            intent.putExtra("name", cycle.getName());
            intent.putExtra("type", cycle.getType());
            intent.putExtra("price", cycle.getPrice());
            intent.putExtra("available", cycle.getAvailable());
            intent.putExtra("description", cycle.getDescription());
            intent.putExtra("image", cycle.getBikeImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cycleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cycleImageView;
        TextView nameTextView, typeTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cycleImageView = itemView.findViewById(R.id.imgBike);
            nameTextView = itemView.findViewById(R.id.tvName);
            typeTextView = itemView.findViewById(R.id.tvType);
            priceTextView = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}