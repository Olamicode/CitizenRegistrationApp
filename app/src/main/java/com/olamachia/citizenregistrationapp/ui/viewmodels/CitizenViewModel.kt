package com.olamachia.citizenregistrationapp.ui.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.olamachia.citizenregistrationapp.data.Citizen
import com.olamachia.citizenregistrationapp.data.Hospital
import com.olamachia.citizenregistrationapp.ui.auth.LoginFragment
import com.olamachia.citizenregistrationapp.ui.auth.RegisterFragment
import com.olamachia.citizenregistrationapp.ui.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CitizenViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val database: DatabaseReference = Firebase.database.reference

    private var authMutableLiveData = MutableLiveData<Boolean>()
    val authLiveData: LiveData<Boolean> get() = authMutableLiveData

    private var loginMutableLiveData = MutableLiveData<Hospital?>()
    val loginLiveData: LiveData<Hospital?> get() = loginMutableLiveData

    private var citizenDetailsMutableLiveData = MutableLiveData<Boolean>()
    val citizenDetailsLiveData: LiveData<Boolean> get() = citizenDetailsMutableLiveData

    private var citizenDataMutableLiveData = MutableLiveData<List<Citizen>?>()
    val citizenDataLiveData : MutableLiveData<List<Citizen>?> get() = citizenDataMutableLiveData


    fun authenticateWithFirebase(
        activity: Activity,
        hospitalName: String,
        email: String,
        password: String
    ) {

        val hospital = Hospital(name = hospitalName, email = email)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid!!

                    saveHospitalDetailsToFirebase(uid, hospital)

                    authMutableLiveData.value = true

                } else {
                    val message = task.exception?.localizedMessage ?: "message"
                    val stackError = task.exception?.stackTrace ?: "stackTrace"

                    authMutableLiveData.value = false

                    Log.d(RegisterFragment.REGISTER_FRAGMENT_TAG, message)
                    Log.d(RegisterFragment.REGISTER_FRAGMENT_TAG, stackError.toString())

                }
            }
    }


    fun loginWithFirebaseAuth(context: Context, email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null) {

                        val uid = auth.currentUser!!.uid

                        SessionManager.save(
                            context,
                            UID,
                            uid
                        )

                        getHospitalDetailsFromFirebase(context, uid)

                    } else {

                        loginMutableLiveData.value = null

                        Log.w(
                            LoginFragment.LOGIN_FRAGMENT_TAG,
                            "authPost:onCancelled"
                        )
                    }
                }
            }

    }

    private fun getHospitalDetailsFromFirebase(context: Context, uid: String) {
        FirebaseDatabase.getInstance().reference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {

                        for (childSnapShot in dataSnapshot.children) {

                            if (uid == childSnapShot.key) {
                                val details =
                                    childSnapShot.getValue(Hospital::class.java)

                                val hospital = TypeConverter.convertPojoToString(details)

                                SessionManager.save(
                                    context,
                                    HOSPITAL_DATA,
                                    hospital
                                )

                                Log.d(LoginFragment.LOGIN_FRAGMENT_TAG, details.toString())

                                loginMutableLiveData.value = details

                            }
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message

                    loginMutableLiveData.value = null

                    Log.w(
                        LoginFragment.LOGIN_FRAGMENT_TAG,
                        "loadPost:onCancelled",
                        databaseError.toException()
                    )

                }
            })
    }


    private fun saveHospitalDetailsToFirebase(uid: String, hospital: Hospital) {
        database.child(uid).setValue(hospital)
    }

    fun saveCitizenDetailsToFirebase(context: Context, citizen: Citizen) {
        val uuid = UUID.randomUUID().toString()
        SessionManager.save(
            context,
            USER_UID,
            uuid
        )
        database.child(uuid).setValue(citizen).addOnCompleteListener {
            citizenDetailsMutableLiveData.value = it.isSuccessful
        }
    }

    fun getDetailsFromFirebase(context: Context) {
        FirebaseDatabase.getInstance().reference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {

                        val citizenData = mutableListOf<Citizen>()
                        val hospital = provideHospital(context)

                        for (childSnapShot in dataSnapshot.children) {

                            val details =
                                childSnapShot.getValue(Citizen::class.java)

                            if (details != null && details.hospital?.email == hospital.email) {
                                citizenData.add(details)
                            }

                        }

                        citizenDataMutableLiveData.value = citizenData

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message

                    citizenDataMutableLiveData.value = null

                    Log.w(
                        LoginFragment.LOGIN_FRAGMENT_TAG,
                        "loadPost:onCancelled",
                        databaseError.toException()
                    )

                }
            })
    }

}