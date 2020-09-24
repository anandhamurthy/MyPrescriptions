package com.myprescriptions.models

class PrescriptionObjects(
    var Key: String,
    var User_Id: String,
    var Name: String,
    var Timestamp: String,
    var Email_Id: String,
    var Phone_Number: String,
    var Medicine: ArrayList<Medicine>,
    var Details: String,
    var Profile_Image: String
)