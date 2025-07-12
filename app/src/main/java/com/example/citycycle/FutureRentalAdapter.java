package com.example.citycycle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class FutureRentalAdapter extends RecyclerView.Adapter<FutureRentalAdapter.ViewHolder> {
    private Context context;
    private List<Rental> rentalList;
    private DBHelper dbHelper;

    public FutureRentalAdapter(Context context, List<Rental> rentalList) {
        this.context = context;
        this.rentalList = rentalList;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listcycle_future_rental, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rental rental = rentalList.get(position);

        holder.tvName.setText(rental.getName());
        holder.tvType.setText(rental.getType());
        holder.tvTotalPrice.setText("Total: $" + rental.getTotalPrice());

        if (rental.isDayBased()) {
            holder.tvStartDate.setText("Start Date: " + rental.getStart());
            holder.tvEndDate.setText("End Date: " + rental.getEnd());
        } else {
            holder.tvStartDate.setText("Start Time: " + rental.getStart());
            holder.tvEndDate.setText("End Time: " + rental.getEnd());
        }

        if (rental.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rental.getImage(), 0, rental.getImage().length);
            holder.imgBike.setImageBitmap(bitmap);
        }


        holder.btnCancel.setOnClickListener(v -> showCancelConfirmation(holder.getAdapterPosition(), rental));
    }

    private void showCancelConfirmation(int position, Rental rental) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_cancel, null);
        LottieAnimationView animationView = view.findViewById(R.id.lottieCancel);
        animationView.setAnimation(R.raw.cancel_animation);
        animationView.playAnimation();

        builder.setView(view)
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelRental(position, rental))
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();

    }

    private void cancelRental(int position, Rental rental) {
        boolean isDeleted;

        if (rental.isDayBased()) {
            isDeleted = dbHelper.deleteDayRental(rental.getId());
        } else {
            isDeleted = dbHelper.deleteTimeRental(rental.getId());
        }

        if (isDeleted) {

            dbHelper.updateAvailableCount(rental.getCycleId(), dbHelper.getAvailableCount(rental.getCycleId()) + rental.getCount());


            rentalList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, rentalList.size());

            Toast.makeText(context, "Rental Canceled Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to Cancel Rental!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBike;
        TextView tvName, tvType, tvStartDate, tvEndDate, tvTotalPrice;
        Button btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBike = itemView.findViewById(R.id.imgBike);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvStartDate = itemView.findViewById(R.id.tvStartDateOrTime);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
