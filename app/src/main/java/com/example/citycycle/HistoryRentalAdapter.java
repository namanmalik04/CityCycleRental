package com.example.citycycle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryRentalAdapter extends RecyclerView.Adapter<HistoryRentalAdapter.ViewHolder> {
    private Context context;
    private List<Rental> rentalList;

    public HistoryRentalAdapter(Context context, List<Rental> rentalList) {
        this.context = context;
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listcycle_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rental rental = rentalList.get(position);

        holder.tvName.setText(rental.getName());
        holder.tvType.setText(rental.getType());
        holder.tvTotalPrice.setText("Total: $" + rental.getTotalPrice());
        holder.textDateOrTime.setText(rental.isDayBased() ? "End Date: " + rental.getEnd() : "End Time: " + rental.getEnd());

        if (rental.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rental.getImage(), 0, rental.getImage().length);
            holder.imgBike.setImageBitmap(bitmap);
        } else {
            holder.imgBike.setImageResource(R.drawable.loginimage);
        }

        Log.d("HistoryAdapter", " Binding Past Rental: " + rental.getName() + " | End: " + rental.getEnd());
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBike;
        TextView tvName, tvType, textDateOrTime, tvTotalPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBike = itemView.findViewById(R.id.imgBike);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            textDateOrTime = itemView.findViewById(R.id.textDateOrTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
