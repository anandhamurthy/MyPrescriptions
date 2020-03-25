package com.myprescriptions;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myprescriptions.QRActivity;
import com.myprescriptions.QRScannerActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myprescriptions.Adapter.PrescriptionsAdapter;
import com.myprescriptions.models.Prescriptions;
import com.myprescriptions.models.Users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PrescriptionsAdapter.SearchAdapterListener{

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference mUsersDatabase;
    private RecyclerView mPrescriptions;
    private FloatingActionButton mProfile, mAddPrescription, mQR;
    private PrescriptionsAdapter prescriptionsAdapter;
    private List<Prescriptions> prescriptionsList;
    private DatabaseReference mMyPrescriptions;
    private RelativeLayout mNoPrescriptons;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfile = findViewById(R.id.profile);
        mAddPrescription = findViewById(R.id.add_prescription);
        mQR = findViewById(R.id.qr);
        mPrescriptions=findViewById(R.id.prescription_list);

        mNoPrescriptons = findViewById(R.id.no_prescriptions);



        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            mCurrentUserId = mAuth.getCurrentUser().getUid();
            mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
            mUsersDatabase.keepSynced(true);

            mMyPrescriptions = FirebaseDatabase.getInstance().getReference().child("Prescriptions").child(mCurrentUserId);
            mMyPrescriptions.keepSynced(true);

            mPrescriptions.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mPrescriptions.setLayoutManager(mLayoutManager);
            prescriptionsList = new ArrayList<>();
            mPrescriptions.setItemAnimator(new DefaultItemAnimator());

            prescriptionsAdapter = new PrescriptionsAdapter(this, prescriptionsList, this);
            mPrescriptions.setAdapter(prescriptionsAdapter);

            readMyPrescriptions();

            mProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setupIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    setupIntent.putExtra("access","true");
                    startActivity(setupIntent);
                }
            });


            mQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isNetworkConnected()){
                        Intent intent = new Intent(MainActivity.this, QRActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mAddPrescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkConnected()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Scan your Prescription from QR").setTitle("Scan");
                        builder.setMessage("Click to scan your Prescription QR")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(MainActivity.this, QRScannerActivity.class);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("QR Scan");
                        alert.show();
                    }else{
                        Toast.makeText(MainActivity.this, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    private void readMyPrescriptions() {
        mMyPrescriptions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mPrescriptions.setVisibility(View.VISIBLE);
                    mNoPrescriptons.setVisibility(View.GONE);
                    prescriptionsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Prescriptions prescriptions = snapshot.getValue(Prescriptions.class);
                        prescriptionsList.add(prescriptions);
                    }
                    Collections.reverse(prescriptionsList);
                    prescriptionsAdapter.notifyDataSetChanged();

                } else {
                    mNoPrescriptons.setVisibility(View.VISIBLE);
                    mPrescriptions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                prescriptionsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                prescriptionsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                return true;
            case R.id.source:
                Intent intent1 = new Intent(MainActivity.this, SourceActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser == null) {

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();

        }
        else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user.getVerified().equals("false")) {
                        Intent setupIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        setupIntent.putExtra("access","false");
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(setupIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onSearchSelected(Prescriptions prescription) {

    }
}


