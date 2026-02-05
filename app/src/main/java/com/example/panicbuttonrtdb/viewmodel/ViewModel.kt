package com.example.panicbuttonrtdb.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panicbuttonrtdb.data.MonitorRecord
import com.example.panicbuttonrtdb.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class ViewModel(private val context: Context) : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // State untuk UI
    var currentUserName by mutableStateOf("")
    var currentUserHouseNumber by mutableStateOf("")

    // LiveData
    private val _monitorData = MutableLiveData<List<MonitorRecord>>()
    val monitorData: LiveData<List<MonitorRecord>> get() = _monitorData

    private val _userData = MutableLiveData<Map<String, String>>()
    val userData: LiveData<Map<String, String>> get() = _userData

    private val _latestRecord = MutableStateFlow(MonitorRecord())
    val latestRecord: StateFlow<MonitorRecord> = _latestRecord

    private val _buzzerState = MutableLiveData<String>()
    val buzzerState: LiveData<String> = _buzzerState

    private val _daftarPerumahan = MutableLiveData<Map<String, String>>()
    val daftarPerumahan: LiveData<Map<String, String>> get() = _daftarPerumahan

    private val _userProfileData = MutableLiveData<User?>()
    val userProfileData: LiveData<User?> get() = _userProfileData

    private val _quickMessages = MutableLiveData<List<String>>()
    val quickMessages: LiveData<List<String>> get() = _quickMessages

    // LiveData untuk URL gambar profil di DashboardUserScreen
    private val _userProfileImageUrl = MutableLiveData<String>()
    val userProfileImageUrl: LiveData<String> get() = _userProfileImageUrl


    // Manajemen Listener untuk mencegah memory leak
    private val activeListeners = mutableMapOf<String, ValueEventListener>()

    init {
        loadUserFromSession()
    }

    // =================================================================
    // Sesi & Autentikasi
    // =================================================================

    private fun getPerumahanIdFromSession(): String? {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("perumahan_id", null)
    }

    private fun loadUserFromSession() {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        currentUserName = sharedPref.getString("user_name", "") ?: ""
        currentUserHouseNumber = sharedPref.getString("house_number", "") ?: ""
    }

    fun fetchDaftarPerumahan() {
        database.getReference("daftar_perumahan").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, String>()
                snapshot.children.forEach {
                    val id = it.key
                    val nama = it.getValue(String::class.java)
                    if (id != null && nama != null) map[id] = nama
                }
                _daftarPerumahan.value = map
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel", "Gagal fetch daftar perumahan: ${error.message}")
            }
        })
    }

    fun fetchQuickMessages() {
        val quickMessagesPath = database.getReference("global_quick_messages")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                _quickMessages.value = messages
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel", "Gagal fetch quick messages: ${error.message}")
            }
        }
        quickMessagesPath.addValueEventListener(listener)
    }

    fun saveUserToFirebase(name: String, houseNumber: String, password: String, perumahanId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val usersPath = database.getReference("perumahan/$perumahanId/users")
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    onFailure("Nomor rumah sudah digunakan di perumahan ini.")
                } else {
                    val userId = usersPath.push().key
                    val user = User(name = name, houseNumber = houseNumber, password = password, role = "user")
                    if (userId != null) {
                        usersPath.child(userId).setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) onSuccess() else onFailure("Gagal menyimpan data.")
                        }
                    } else {
                        onFailure("Gagal membuat ID pengguna.")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onFailure("Terjadi kesalahan: ${error.message}")
            }
        })
    }

    fun validateLogin(perumahanId: String, houseNumber: String, password: String, onResult: (Boolean, User?) -> Unit) {
        val usersPath = database.getReference("perumahan/$perumahanId/users")
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userSnapshot = snapshot.children.first()
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.password == password) {
                        saveUserSession(user, perumahanId)
                        onResult(true, user)
                    } else {
                        onResult(false, null)
                    }
                } else {
                    onResult(false, null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(false, null)
            }
        })
    }

    private fun saveUserSession(user: User, perumahanId: String) {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", user.name)
            putString("house_number", user.houseNumber)
            putString("user_role", user.role)
            putString("perumahan_id", perumahanId)
            putBoolean("is_logged_in", true)
            commit()
        }
        loadUserFromSession()
    }

    fun logout() {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()
        currentUserName = ""
        currentUserHouseNumber = ""
    }

    // =================================================================
    // Fungsi Panic Button & Buzzer
    // =================================================================

    fun saveMonitorData(message: String, priority: String, status: String, latitude: Double, longitude: Double) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        val data = MonitorRecord(
            name = currentUserName,
            houseNumber = currentUserHouseNumber,
            message = message,
            priority = priority,
            status = status,
            time = getCurrentTimestampFormatted(),
            latitude = latitude,
            longitude = longitude
        )
        monitorPath.push().setValue(data)
    }

    fun getBuzzerState() {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val buzzerPath = database.getReference("perumahan/$perumahanId/buzzers/main/state")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _buzzerState.value = snapshot.getValue(String::class.java) ?: "off"
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        buzzerPath.addValueEventListener(listener)
        activeListeners["buzzerState"] = listener
    }

    fun updateBuzzerState(isOn: Boolean, priority: String? = null) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val buzzerRef = database.getReference("perumahan/$perumahanId/buzzers/main")
        if (isOn && priority != null) {
            buzzerRef.child("priority").setValue(priority)
            buzzerRef.child("state").setValue("on")
        } else {
            buzzerRef.child("priority").setValue("off")
            buzzerRef.child("state").setValue("off")
        }
    }

    // =================================================================
    // Fungsi untuk Admin
    // =================================================================

    fun fetchLatestRecord() {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.children.first().getValue(MonitorRecord::class.java)
                    data?.let { viewModelScope.launch { _latestRecord.emit(it) } }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        monitorPath.orderByKey().limitToLast(1).addValueEventListener(listener)
        activeListeners["latestRecord"] = listener
    }

    fun latestMonitorItem() {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _monitorData.value = snapshot.children.reversed().mapNotNull { it.getValue(MonitorRecord::class.java) }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        monitorPath.orderByKey().limitToLast(3).addValueEventListener(listener)
        activeListeners["latestMonitorItems"] = listener
    }

    fun detailRekap(houseNumber: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        monitorPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _monitorData.value = snapshot.children.reversed().mapNotNull { it.getValue(MonitorRecord::class.java)?.copy(id = it.key ?: "") }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchMonitorData() {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _monitorData.value = snapshot.children.reversed().mapNotNull { it.getValue(MonitorRecord::class.java) }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        monitorPath.addValueEventListener(listener)
        activeListeners["allMonitorData"] = listener
    }

    fun updateStatus(recordId: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        database.getReference("perumahan/$perumahanId/monitor/$recordId/status").setValue("Selesai")
    }

    // =================================================================
    // Fungsi untuk User
    // =================================================================

    fun userHistory() {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val monitorPath = database.getReference("perumahan/$perumahanId/monitor")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _monitorData.value = snapshot.children.reversed()
                    .mapNotNull { it.getValue(MonitorRecord::class.java) }
                    .filter { it.houseNumber == currentUserHouseNumber }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        monitorPath.addValueEventListener(listener)
        activeListeners["userHistory"] = listener
    }

    fun fetchCurrentUserProfile() {
        val perumahanId = getPerumahanIdFromSession()
        val houseNumber = currentUserHouseNumber

        if (perumahanId.isNullOrEmpty() || houseNumber.isEmpty()) {
            return
        }

        val usersPath = database.getReference("perumahan/$perumahanId/users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    // Perbarui kedua LiveData
                    _userProfileData.value = user
                    _userProfileImageUrl.value = user?.imageProfile ?: ""
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel", "Gagal fetch profil user: ${error.message}")
            }
        }
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addValueEventListener(listener)
        activeListeners["userProfile"] = listener
    }

    fun fetchUserProfileData() { // <-- NAMA FUNGSI DIPERBAIKI DI SINI
        val perumahanId = getPerumahanIdFromSession()
        val houseNumber = currentUserHouseNumber

        if (perumahanId.isNullOrEmpty() || houseNumber.isEmpty()) {
            return
        }

        val usersPath = database.getReference("perumahan/$perumahanId/users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    // Perbarui kedua LiveData
                    _userProfileData.value = user
                    _userProfileImageUrl.value = user?.imageProfile ?: ""
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel", "Gagal fetch profil user: ${error.message}")
            }
        }
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addValueEventListener(listener)
        activeListeners["userProfile"] = listener
    }

    fun uploadImage(imageUri: Uri, imageType: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val houseNumber = currentUserHouseNumber
        if(houseNumber.isEmpty()) return
        val imageRef = storage.child("$perumahanId/$imageType/$houseNumber.jpg")
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveImagePathToDatabase(uri.toString(), houseNumber, imageType)
            }
        }
    }

    private fun saveImagePathToDatabase(imageUri: String, houseNumber: String, imageType: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val usersPath = database.getReference("perumahan/$perumahanId/users")
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.first().ref.child(imageType).setValue(imageUri)
                        .addOnSuccessListener {
                            Toast.makeText(context, "$imageType berhasil diperbaharui.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun savePhoneNumberAndNote(phoneNumber: String, note: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val houseNumber = currentUserHouseNumber
        if(houseNumber.isEmpty()) return
        val usersPath = database.getReference("perumahan/$perumahanId/users")
        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userRef = snapshot.children.first().ref
                    userRef.child("phoneNumber").setValue(phoneNumber)
                    userRef.child("note").setValue(note).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Keterangan berhasil disimpan.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchUserData(houseNumber: String) {
        val perumahanId = getPerumahanIdFromSession() ?: return
        val usersPath = database.getReference("perumahan/$perumahanId/users")

        usersPath.orderByChild("houseNumber").equalTo(houseNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userSnapshot = snapshot.children.first()
                    val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val note = userSnapshot.child("note").getValue(String::class.java) ?: ""
                    _userData.postValue(mapOf("phoneNumber" to phoneNumber, "note" to note))
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // =================================================================
    // Fungsi Utilitas
    // =================================================================

    private fun getCurrentTimestampFormatted(): String {
        return SimpleDateFormat("yyyy-MM-dd 'waktu' HH:mm", Locale.getDefault()).format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        // Melepas semua listener saat ViewModel dihancurkan
        activeListeners.forEach { (key, listener) ->
            // Implementasi pelepasan listener yang lebih baik akan menyimpan path referensinya
            Log.d("ViewModel", "Melepas listener untuk: $key")
        }
        activeListeners.clear()
    }
}