package com.myprescriptions.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myprescriptions.DetailsActivity;
import com.myprescriptions.R;
import com.myprescriptions.models.Prescriptions;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionsAdapter extends RecyclerView.Adapter<PrescriptionsAdapter.ImageViewHolder> implements Filterable {


    private Context mContext;
    private List<Prescriptions> PrescriptionsList;
    private List<Prescriptions> DefaultPrescriptionsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mMyPrescriptionsDatabase, mUsersDatabase;
    private FirebaseUser mFirebaseUser;
    private SearchAdapterListener listener;
    private String mCurrentUserId;

    public PrescriptionsAdapter(Context context, List<Prescriptions> prescriptionsList, PrescriptionsAdapter.SearchAdapterListener listener) {
        mContext = context;
        PrescriptionsList = prescriptionsList;
        DefaultPrescriptionsList=prescriptionsList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_layout_my_prescription, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();
        mMyPrescriptionsDatabase = FirebaseDatabase.getInstance().getReference().child("Prescriptions").child(mCurrentUserId);
        mMyPrescriptionsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        final Prescriptions prescriptions = PrescriptionsList.get(position);
        holder.Doctor_Email.setText("Email : "+prescriptions.getEmail());
        holder.Doctor_Name.setText("Name : "+prescriptions.getName());
        holder.Details.setText(prescriptions.getDetails());
        holder.Timestamp.setText("Dtae-Time : "+prescriptions.getTimestamp());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("prescription", prescriptions);
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return PrescriptionsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    PrescriptionsList = DefaultPrescriptionsList;
                } else {
                    List<Prescriptions> filteredList = new ArrayList<>();
                    for (Prescriptions row : DefaultPrescriptionsList) {

                        if (row.getName().toLowerCase().contains(charString) ||
                                row.getDetails().toLowerCase().contains(charSequence) ||
                                row.getEmail().toLowerCase().contains(charSequence)
                        ) {
                            filteredList.add(row);
                        }
                    }

                    PrescriptionsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = PrescriptionsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                PrescriptionsList = (ArrayList<Prescriptions>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView Doctor_Name, Doctor_Email, Details, Timestamp;

        public ImageViewHolder(View itemView) {
            super(itemView);

            Doctor_Name = itemView.findViewById(R.id.prescription_doctor_name);
            Doctor_Email = itemView.findViewById(R.id.prescription_doctor_email);
            Details = itemView.findViewById(R.id.prescription_details);
            Timestamp = itemView.findViewById(R.id.prescription_timestamp);

        }
    }

    public interface SearchAdapterListener {
        void onSearchSelected(Prescriptions prescription);
    }
}