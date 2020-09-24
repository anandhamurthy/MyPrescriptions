package com.myprescriptions;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class Login extends AppCompatActivity{

    private CountryCodePicker Login_Country_Code;
    private EditText Login_Phone_Number;

    private String  Code="+91";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.login_bg);

        Login_Phone_Number = findViewById(R.id.login_phone_number);
        Login_Country_Code = findViewById(R.id.login_country_code);


        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        Login_Country_Code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Code=Login_Country_Code.getSelectedCountryCodeWithPlus();

            }
        });

        findViewById(R.id.login_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               final String number = Login_Phone_Number.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    Login_Phone_Number.setError("Valid number is required");
                    Login_Phone_Number.requestFocus();
                    return;
                }
                Intent intent = new Intent(Login.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", Code+number);
                startActivity(intent);

            }
        });
    }

}
