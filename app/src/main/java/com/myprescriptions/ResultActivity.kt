package com.myprescriptions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myprescriptions.Adapter.MedicineAdapter
import com.myprescriptions.models.Medicine
import com.myprescriptions.models.PrescriptionObjects
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.myprescriptions.R
import com.myprescriptions.helper.EncryptionHelper
import kotlinx.android.synthetic.main.activity_result.*
import java.lang.RuntimeException

class ResultActivity : AppCompatActivity() {

    companion object {
        private const val SCANNED_STRING: String = "scanned_string"
        fun getScannedActivity(callingClassContext: Context, encryptedString: String): Intent {
            return Intent(callingClassContext, ResultActivity::class.java)
                .putExtra(SCANNED_STRING, encryptedString)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        if (intent.getSerializableExtra(SCANNED_STRING) == null)
            throw RuntimeException("No encrypted String found in intent")
        val decryptedString = EncryptionHelper.getInstance().getDecryptionString(intent.getStringExtra(SCANNED_STRING))
        val userObject = Gson().fromJson(decryptedString, PrescriptionObjects::class.java)
        result_name.text = userObject.Name
        result_email.text = userObject.Email_Id

        var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
        var mCurrentUserId = mAuth.getCurrentUser()?.getUid().toString()

        var mPrescriptionsDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Prescriptions").
        child(mCurrentUserId)
        mPrescriptionsDatabase.keepSynced(true)

        val recyclerView = findViewById(R.id.prescription_list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val users = userObject.Medicine
        val adapter = MedicineAdapter(users)
        recyclerView.adapter = adapter

        val submit = findViewById(R.id.result_approve) as FloatingActionButton
        submit.setOnClickListener {
            var hashMap: HashMap<String, String> = HashMap<String, String>()
            hashMap.put("user_id", userObject.User_Id)
            hashMap.put("name", userObject.Name)
            hashMap.put("email", userObject.Email_Id)
            hashMap.put("details", userObject.Details)
            hashMap.put("timestamp", userObject.Timestamp)
            hashMap.put("profile_image", userObject.Profile_Image)
            hashMap.put("key", userObject.Key)

            val key: String = mPrescriptionsDatabase.push().getKey().toString()

            mPrescriptionsDatabase.child(key).setValue(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var hashMap1: HashMap<String, ArrayList<Medicine>> =
                        HashMap<String, ArrayList<Medicine>>()
                    hashMap1.put("medicine", users)
                    mPrescriptionsDatabase.child(key)
                        .updateChildren(hashMap1 as Map<String, Any>)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                finish()
                            }
                        }
                }
            }
        }
    }
}
