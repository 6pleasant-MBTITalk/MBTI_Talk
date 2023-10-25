package com.example.mbti_talk.MyProfile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mbti_talk.SignUpMbtiActivity
import com.example.mbti_talk.UserData
import com.example.mbti_talk.databinding.FragmentMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MyProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.ProfileMbtiBtn.setOnClickListener {
            val intent = Intent(requireContext(), SignUpMbtiActivity::class.java)
            startActivity(intent)
        }

        // Firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        // 현재 로그인한 사용자의 UID 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid

            // DB에서 현재 사용자의 데이터 가져오기
            val usersReference = FirebaseDatabase.getInstance().getReference("Users")
            usersReference.child(currentUserId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if (userData != null) {
                            binding.ProfileEmail.text = "${userData.user_email}"
                            binding.ProfileTxtUid.text = "${userData.user_uid}"
                            binding.ProfileNickname.text = "${userData.user_nickName}"
                            binding.ProfileAge.text = "${userData.user_age}"
                            binding.ProfileGender.text = "${userData.user_gender}"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터 가져오기 실패 시 처리
                }
            })
        }

        return view
    }
}

//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ //머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ //머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ //머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ
//머지하고싶은데 안뜸 ㅡㅡ //머지하고싶은데 안뜸 ㅡㅡ //머지하고싶은데 안뜸 ㅡㅡ






