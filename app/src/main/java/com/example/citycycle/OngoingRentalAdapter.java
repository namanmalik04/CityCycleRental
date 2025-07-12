package com.example.citycycle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OngoingRentalAdapter extends RecyclerView.Adapter<OngoingRentalAdapter.ViewHolder> {
    private Context context;
    private List<Rental> rentalList;

    public OngoingRentalAdapter(Context context, List<Rental> rentalList) {
        this.context = context;
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listcycle_ongoing, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rental rental = rentalList.get(position);

        holder.tvName.setText(rental.getName());
        holder.tvType.setText(rental.getType());
        holder.tvTotalPrice.setText("Total: $" + rental.getTotalPrice());
        holder.tvStartDateOrTime.setText("Start: " + rental.getStart());

        if (rental.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(rental.getImage(), 0, rental.getImage().length);
            holder.imgBike.setImageBitmap(bitmap);
        } else {
            holder.imgBike.setImageResource(R.drawable.loginimage);
        }

        Log.d("AdapterDebug", "🔍 Binding Data: " + rental.getName() + " | End: " + rental.getEnd());


        startCountdownTimer(rental.getEnd(), holder.tvTimer, rental.isDayBased());

    }

    private void startCountdownTimer(String endTime, TextView tvTimer, boolean isDayBased) {
        if (endTime == null || endTime.trim().isEmpty()) {
            tvTimer.setText("Error: No End Time");
            Log.e("TimerDebug", " Error: EndTime is NULL or Empty");
            return;
        }

        try {
            // ✅ Handle different possible date formats
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date endDate = sdf.parse(endTime);

            if (endDate == null) {
                tvTimer.setText("Error: Invalid Date");
                Log.e("TimerDebug", " Error: Parsed endDate is NULL");
                return;
            }

            long endMillis = endDate.getTime();
            long currentMillis = System.currentTimeMillis();
            long remainingMillis = endMillis - currentMillis;

            Log.d("TimerDebug", "⏳ Timer Set - End: " + endMillis + " | Current: " + currentMillis + " | Remaining: " + remainingMillis);

            if (remainingMillis > 0) {
                new CountDownTimer(remainingMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24;
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                        String timeLeft;
                        if (isDayBased) {
                            timeLeft = String.format(Locale.getDefault(), "%02dD %02dH %02dM", days, hours, minutes);
                        } else {
                            timeLeft = String.format(Locale.getDefault(), "%02dH %02dM %02dS", hours, minutes, seconds);
                        }

                        tvTimer.setText(timeLeft);
                    }

                    @Override
                    public void onFinish() {
                        tvTimer.setText("Expired");
                        Log.d("TimerDebug", " Timer Expired");
                    }
                }.start();
            } else {
                tvTimer.setText("Expired");
                Log.d("TimerDebug", " Already Expired!");
            }
        } catch (Exception e) {
            tvTimer.setText("Error");
            Log.e("TimerDebug", " Exception: " + e.getMessage());
        }
    }





    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBike;
        TextView tvName, tvType, tvStartDateOrTime, tvTimer, tvTotalPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBike = itemView.findViewById(R.id.imgBike);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvStartDateOrTime = itemView.findViewById(R.id.tvStartDateOrTime);
            tvTimer = itemView.findViewById(R.id.tvTimer);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
