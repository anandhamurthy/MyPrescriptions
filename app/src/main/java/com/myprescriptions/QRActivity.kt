package com.myprescriptions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.myprescriptions.R
import com.myprescriptions.helper.EncryptionHelper
import com.myprescriptions.helper.QRCodeHelper
import com.myprescriptions.models.ProfileObjects
import kotlinx.android.synthetic.main.activity_qr.*
import kotlinx.android.synthetic.main.activity_qrcode.*
class QRActivity : AppCompatActivity() {

    companion object {

        fun getGenerateQrCodeActivity(callingClassContext: Context) = Intent(callingClassContext, QRActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        val mAuth = FirebaseAuth.getInstance()
        val mCurrentUser = mAuth.getCurrentUser()
        val mCurrentUserId = mCurrentUser!!.uid
        val database = FirebaseDatabase.getInstance()
        val mUsersDatabase = database.getReference("Users").child(mCurrentUserId)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val profile_image = dataSnapshot.child("profile_image").value!!.toString()
                    val name = dataSnapshot.child("name").value!!.toString()
                    val email = dataSnapshot.child("email_id").value!!.toString()
                    val phone_number = dataSnapshot.child("phone_number").value!!.toString()
                    val gender = dataSnapshot.child("gender").value!!.toString()
                    val age = dataSnapshot.child("age").value!!.toString()

                    val address = dataSnapshot.child("address").value!!.toString()
                    val state = dataSnapshot.child("state").value!!.toString()
                    val city = dataSnapshot.child("city").value!!.toString()
                    val country = dataSnapshot.child("country").value!!.toString()

                    Glide.with(this@QRActivity).load(profile_image).into(qr_image);

                    qr_name.setText(name)
                    qr_email.setText(email)
                    qr_gender.setText(gender)
                    qr_age.setText(age)
                    qr_gender.setText(gender)
                    qr_address.setText(address+", "+city+", "+state+", "+country)

                    val user = ProfileObjects(
                        profile_image,
                        name,
                        email,
                        phone_number,
                        gender,
                        age,
                        state,
                        city,
                        address,
                        country,
                        mCurrentUserId
                    )
                    val serializeString = Gson().toJson(user)
                    val encryptedString = EncryptionHelper.getInstance().encryptionString(serializeString).encryptMsg()
                    get_qrcode.setOnClickListener {
                        setImageBitmap(encryptedString)
                    }
                }else{
                    Toast.makeText(this@QRActivity, "Update Your Profile", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        mUsersDatabase.addValueEventListener(postListener)

    }

    private fun setImageBitmap(encryptedString: String?) {
        val bitmap = QRCodeHelper.newInstance(this).setContent(encryptedString).setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(2).qrcOde
        val mDialogView = LayoutInflater.from(this@QRActivity).inflate(R.layout.activity_qrcode, null)
        val mBuilder = AlertDialog.Builder(this@QRActivity)
            .setView(mDialogView)
            .setTitle("Your QR Code")
        val  mAlertDialog = mBuilder.show()
        mAlertDialog.qrcode.setImageBitmap(bitmap)
    }

}