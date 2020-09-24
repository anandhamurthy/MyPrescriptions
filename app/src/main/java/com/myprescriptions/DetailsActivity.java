package com.myprescriptions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myprescriptions.Adapter.DetailsAdapter;
import com.myprescriptions.models.Prescriptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {

    private TextView Details_Name, Details_Phone_Number, Details_Email, Details_Details, Details_Timestamp;
    private CircleImageView Details_Profile_Image;
    private RecyclerView Medicine_List;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        Prescriptions prescription = (Prescriptions) bundle.getSerializable("prescription");


        Details_Name=findViewById(R.id.details_name);
        Details_Email=findViewById(R.id.details_email);
        Details_Phone_Number=findViewById(R.id.details_phone_number);
        Details_Details=findViewById(R.id.details_details);
        Details_Timestamp=findViewById(R.id.details_timestamp);
        Details_Profile_Image=findViewById(R.id.details_profile_image);
        Medicine_List=findViewById(R.id.prescription_list);

        Details_Name.setText(prescription.getName());
        Details_Email.setText(prescription.getEmail());
        Details_Phone_Number.setText(prescription.getPhone_number());
        Details_Details.setText(prescription.getDetails());
        Details_Timestamp.setText(prescription.getTimestamp());

        Glide.with(getApplicationContext()).load(prescription.getProfile_image()).
                placeholder(R.drawable.profile_placeholder).into(Details_Profile_Image);

        Medicine_List.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        Medicine_List.setLayoutManager(mLayoutManager);
        DetailsAdapter myStatusAdapter = new DetailsAdapter(DetailsActivity.this, prescription.getMedicine());
        Medicine_List.setAdapter(myStatusAdapter);

    }

}