package com.example.mbti_talk.post

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.mbti_talk.UserData
import com.example.mbti_talk.databinding.ActivityPostWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PostWriteActivity : AppCompatActivity() {


    lateinit var binding: ActivityPostWriteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    val storage = Firebase.storage("gs://mbti-talk-f2a04.appspot.com")


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permission33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO,
    )

    private val permission = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnBack = binding.postbackarrow
        btnBack.setOnClickListener {
            finish()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference

        binding.postImageSelect.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(permission33, 100)
                else {
                    galleryLauncher.launch("image/*")
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(permission, 100)
                } else {
                    galleryLauncher.launch("image/*")
                }

            }
        }

        binding.postSave.setOnClickListener {
            val title = binding.postTitle.text.toString()
            val content = binding.postEtContent.text.toString()
            val time = getTime2()
            val uri = binding.postImageSelect.tag as? Uri
            if (uri != null) {
                Log.d("vec", "Selected Image Uri: $uri")
                uploadImage(uri) {
                    if (it != null) {
                        val user = PostData(title, content, time, it)
                        addItem(user)
                        Toast.makeText(this@PostWriteActivity, "게시글 입력 완료", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    } else {
                        Toast.makeText(this@PostWriteActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this@PostWriteActivity, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        val editText = binding.postEtContent

        val filter = object : InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
                val inputText = dest.toString() + source.toString()
                if (inputText.length <= 500) {
                    return null
                } else {
                    showToast("500자를 초과할 수 없습니다.")
                    return source?.subSequence(0, 500 - dest.toString().length)
                }
            }
        }

        editText.filters = arrayOf(filter)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                100 -> {
                    Toast.makeText(
                        this@PostWriteActivity,
                        "권한 허용됨",
                        Toast.LENGTH_SHORT
                    ).show()
                    galleryLauncher.launch("image/*")

                }
            }
        }
    }

    fun getTime(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat =
            SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(currentDateTime)

        return dateFormat
    }
    fun getTime2(): String {
        val currentDateTime = Calendar.getInstance().time
        val dateFormat =
            SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

        return dateFormat
    }

    fun addItem(user: PostData): String {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        user.user_uid = userUid ?: ""

        val userRef = FirebaseData.database.getReference("Users").child(userUid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserData::class.java)
                if (userData != null) {
                    val user_nickName = userData.user_nickName
                    val user_profile = userData.user_profile
                    val user_mbti = userData.user_mbti
                    val user_gender = userData.user_gender
                    val user_age = userData.user_age


                    if (user_nickName != null && user_profile != null) {
                        user.apply {
                            this.user_nickName = user_nickName
                            this.user_profile = user_profile
                            this.user_mbti = user_mbti
                            this.user_gender = user_gender
                            this.user_age = user_age
                        }
                        FirebaseData.mydata.child(userUid!! + getTime())
                            .setValue(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PostWriteActivity, "닉네임과 프로필 사진 받기 실패", Toast.LENGTH_SHORT)
                    .show()
            }
        })
        return userUid

    }

    //이미지 갤러리 불러오기
    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        binding.postImageSelect.tag = uri
        binding.postImage.setImageURI(uri)
    }

    fun uploadImage(uri: Uri, callback: (String?) -> Unit) {
        val fullPath = makeFilePath("images", "temp", uri)
        val imageRef = storage.getReference(fullPath)
        val uploadTask = imageRef.putFile(uri)

// 업로드 실행 및 결과 확인
        uploadTask.addOnFailureListener {
            Log.d("Storage", "Fail -> ${it.message}")
            callback(null)
        }.addOnSuccessListener { taskSnapshot ->
            Log.d(
                "Storage",
                "Success Address -> ${taskSnapshot.metadata?.name}"
            )
            callback(taskSnapshot.metadata?.name)
        }
    }

    fun makeFilePath(path: String, userId: String, uri: Uri): String {
        val mimeType = contentResolver.getType(uri) ?: "/none" // MIME 타입 ex) images/jpeg
        val ext = mimeType.split("/")[1] // 확장자 ex) jpeg
        val timeSuffix = System.currentTimeMillis() // 시간값 ex) 1235421532
        val filename = "${path}/${userId}_${timeSuffix}.${ext}" // 완성!
        return filename
    }
}

class PostData {
    var user_uid: String = "" // 게시물 고유 식별자
    var title: String = ""
    var content: String = ""
    var time: String = ""
    var image: String = ""
    var user_nickName: String = ""
    var user_profile: String = ""
    var user_mbti: String = ""
    var user_gender: String = ""
    var user_age: Int = 0
    var likeCount: Int = 0
    var likeByUser: Boolean = false

    constructor()
    constructor(title: String, content: String, time: String, image: String) {
        this.title = title
        this.content = content
        this.time = time
        this.image = image
    }
}
