package com.myprescriptions.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.myprescriptions.R;
import com.myprescriptions.models.Med;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ImageViewHolder> {


    private Context mContext;
    private List<Med> DetailsList;
    private FirebaseAuth mAuth;

    public DetailsAdapter(Context context, List<Med> list) {
        mContext = context;
        DetailsList = list;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_prescription_row, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        Med med = DetailsList.get(position);
        holder.Name.setText(med.getName());
        holder.AfterNoon.setText(med.getAft());
        holder.Evening.setText(med.getEve());
        holder.Morning.setText(med.getMor());
        holder.Night.setText(med.getNig());
        holder.Quantity.setText(med.getQuantity());


    }

    @Override
    public int getItemCount() {
        return DetailsList.size();
    }



    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView Name, Quantity, Morning, AfterNoon, Evening, Night;

        public ImageViewHolder(View itemView) {
            super(itemView);

            Name=itemView.findViewById(R.id.name);
            Quantity=itemView.findViewById(R.id.quantity);
            Morning=itemView.findViewById(R.id.morning);
            AfterNoon=itemView.findViewById(R.id.afternoon);
            Evening=itemView.findViewById(R.id.evening);
            Night=itemView.findViewById(R.id.night);

        }
    }
}

