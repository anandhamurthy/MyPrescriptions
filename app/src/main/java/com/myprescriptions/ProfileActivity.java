package com.myprescriptions;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.myprescriptions.models.Users;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity  extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private String mCurrentUserId;
    private Uri mImageUri;
    private String Gender="Male";
    private StorageTask mUploadTask;
    private StorageReference mProfileImageStorage;
    private DatabaseReference mUsersDatabase;

    private RadioButton Edit_Profile_Male, Edit_Profile_Female, Edit_Profile_Others;
    private FloatingActionButton Edit_Profile_Save;
    private TextView Edit_Profile_Phone_Number, Edit_Profile_Change_Profile_Image;
    private CircleImageView Edit_Profile_Image;
    private ImageView Edit_Profile_Back;
    private String Name;
    private EditText Edit_Profile_Name, Edit_Profile_Age, Edit_Profile_Email_Id, Edit_Profile_Address,Edit_Profile_City,Edit_Profile_State,Edit_Profile_Country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        Name = intent.getStringExtra("access");


        Edit_Profile_Image = findViewById(R.id.edit_profile_profile_image);
        Edit_Profile_Change_Profile_Image = findViewById(R.id.edit_profile_change_profile_image_text);
        Edit_Profile_Name = findViewById(R.id.edit_profile_name);
        Edit_Profile_Phone_Number = findViewById(R.id.edit_profile_phone_number);
        Edit_Profile_Address = findViewById(R.id.edit_profile_address);
        Edit_Profile_City = findViewById(R.id.edit_profile_city);
        Edit_Profile_State = findViewById(R.id.edit_profile_state);
        Edit_Profile_Country = findViewById(R.id.edit_profile_country);
        Edit_Profile_Email_Id = findViewById(R.id.edit_profile_email_address);
        Edit_Profile_Male = findViewById(R.id.edit_profile_male);
        Edit_Profile_Age = findViewById(R.id.edit_profile_age);
        Edit_Profile_Female = findViewById(R.id.edit_profile_female);
        Edit_Profile_Others = findViewById(R.id.edit_profile_others);
        Edit_Profile_Save = findViewById(R.id.edit_profile_save);
        Edit_Profile_Back = findViewById(R.id.edit_profile_back);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();
        mProfileImageStorage = FirebaseStorage.getInstance().getReference("profile_images");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUserId);
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Edit_Profile_Name.setText(user.getName());
                Edit_Profile_Address.setText(user.getAddress());
                Edit_Profile_City.setText(user.getCity());
                Edit_Profile_Age.setText(user.getAge());
                Edit_Profile_State.setText(user.getState());
                Edit_Profile_Country.setText(user.getCountry());
                Gender = user.getGender();
                if (!Gender.isEmpty()){
                    if(Gender.equals("Male"))
                        Edit_Profile_Male.setChecked(true);
                    else if(Gender.equals("Female"))
                        Edit_Profile_Female.setChecked(true);
                    else
                        Edit_Profile_Others.setChecked(true);
                }

                Edit_Profile_Phone_Number.setText(user.getPhone_number());
                Edit_Profile_Email_Id.setText(user.getEmail_id());

                Glide.with(getApplicationContext()).load(user.getProfile_image()).
                        placeholder(R.drawable.profile_placeholder).into(Edit_Profile_Image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Edit_Profile_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Edit_Profile_Male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Gender="Male";
                }
            }
        });
        Edit_Profile_Female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Gender="Female";
                }
            }
        });
        Edit_Profile_Others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Gender="Others";
                }
            }
        });

        Edit_Profile_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmpty(Edit_Profile_Name.getText().toString(),Edit_Profile_Address.getText().toString(),
                        Edit_Profile_City.getText().toString(),
                        Edit_Profile_State.getText().toString(),
                        Edit_Profile_Country.getText().toString(),
                        Gender, Edit_Profile_Age.getText().toString(),Edit_Profile_Email_Id.getText().toString())) {

                    UpdateProfile(Edit_Profile_Name.getText().toString(),
                            Edit_Profile_Address.getText().toString(),
                            Edit_Profile_City.getText().toString(),
                            Edit_Profile_State.getText().toString(),
                            Edit_Profile_Country.getText().toString(),
                            Gender, Edit_Profile_Age.getText().toString(),Edit_Profile_Email_Id.getText().toString());
                }else{
                    Toast.makeText(ProfileActivity.this, "Complete all details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Edit_Profile_Change_Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }
        });

        Edit_Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);
            }
        });

    }

    private void UpdateProfile(String name, String address,String city, String state, String country, String gender, String age, String email) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("city", city);
        map.put("country", country);
        map.put("state", state);
        map.put("verified", "true");
        map.put("age", age);
        map.put("email_id", email);
        map.put("gender", gender);
        map.put("user_id", mCurrentUserId);

        mUsersDatabase.updateChildren(map);

        Toast.makeText(ProfileActivity.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (mImageUri != null) {
            final StorageReference fileReference = mProfileImageStorage.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("profile_image", "" + miUrlOk);
                        mUsersDatabase.updateChildren(map1);

                        pd.dismiss();

                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage()+"AMMAMAM", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            UploadImage();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(String name, String s, String toString, String string, String s1, String gender, String toString1, String string1) {
        if (name.isEmpty() || s.isEmpty() || toString.isEmpty() || string.isEmpty() || s1.isEmpty() || gender.isEmpty() || toString1.isEmpty() || string1.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Complete All Details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Name.equals("false")) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid().toString());
                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.getValue(Users.class);
                        if (user.getVerified().equals("true")) {
                            Intent setupIntent = new Intent(ProfileActivity.this, MainActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(setupIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(ProfileActivity.this, Login.class));
                finish();
            }
        }


    }
}
