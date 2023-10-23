package com.example.mbti_talk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.mbti_talk.Adapter.UserAdapter
import com.example.mbti_talk.databinding.ActivityDetailBinding
import com.example.mbti_talk.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Constants
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    // DetailActivity 클래스의 멤버 변수들을 선언
    lateinit var binding: ActivityDetailBinding // 뷰바인딩 초기화

    lateinit var detailDB: DatabaseReference // FB Realtime DB와 연동하기 위한 레퍼런스를 초기화

    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var mbtiTextView: TextView

    private lateinit var userList: MutableList<UserData> // 유저 목록을 저장 위한 MutableList를 초기화. 유저 데이터는 RecyclerView 에 표시됨.

    private lateinit var userDB: DatabaseReference // FB Realtime DB와 연동하기 위한 레퍼런스를 초기화

//    private lateinit var dataList: MutableList<UserData> // 유저 목록을 저장 위한 MutableList를 초기화. 유저 데이터는 RecyclerView 에 표시됨.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화, 해당 바인딩을 현재 액티비티 레이아웃으로 설정
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TextView 초기화
        nameTextView = binding.DetailTxtNickname
        ageTextView = binding.DetailTxtAge
        mbtiTextView = binding.DetailTxtMbti

        // 인증, DB, 리스트 adapter 초기화
        userList = mutableListOf() // 사용자 데이터 목록 초기화 (이 데이터는 FB 에서 가져옴)
        userDB = Firebase.database.reference.child("Users") // FB Realtime DB 초기화하고 "Users" 레퍼런스 가져오기

        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    // 각 사용자의 데이터를 UserData 객체로 가져와 목록 추가
                    val user = userSnapshot.getValue(UserData::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 처리 중 오류 발생 시 토스트 표시
                Toast.makeText(this@DetailActivity, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

//        // Intent 에서 클릭한 user Data 가져오기
//        val userDetail = intent.getParcelableExtra<UserData>("userDetail") as UserData
//
//        if (userDetail != null) {
//            val databaseReference = Firebase.database.reference.child("Users").child(userDetail.uid)
//
//            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val name = snapshot.child("user_nickName").getValue(String::class.java)
//                        val age = snapshot.child("user_age").getValue(Int::class.java)
//                        val mbti = snapshot.child("user_mbti").getValue(String::class.java)
//
//                        // 가져온 data 를 TextView 에 표시
//                        nameTextView.text = name
//                        ageTextView.text = age.toString()
//                        mbtiTextView.text = mbti
//                    } else {
//                        Toast.makeText(this@DetailActivity, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@DetailActivity, "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            Toast.makeText(this, "userDetail null", Toast.LENGTH_SHORT).show()
//        }
    }
}