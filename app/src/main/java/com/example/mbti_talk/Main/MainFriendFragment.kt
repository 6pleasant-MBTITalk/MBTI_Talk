package com.example.mbti_talk.Main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mbti_talk.Adapter.UserAdapter
import com.example.mbti_talk.UserData
import com.example.mbti_talk.databinding.FragmentMainFriendBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// 이 fragment 는 앱 메인 화면에서 친구 목록을 표시. fragment 사용 시 화면 모듈화하고 관리하기 쉬움.

class MainFriendFragment : Fragment() {

    private lateinit var binding: FragmentMainFriendBinding
    private lateinit var friendadapter: UserAdapter
    private val friendList: MutableList<UserData> = mutableListOf()
    private lateinit var friendDB: DatabaseReference

    // onCreateView 함수는 Fragment가 생성될 때 호출. Fragment의 사용자 인터페이스 레이아웃을 초기화
    override fun onCreateView(
        /* inflater 매개변수는 XML 레이아웃 파일을 Fragment의 레이아웃으로 확장
        * container 매개변수는 Fragment가 표시될 부모 뷰 그룹*/
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 레이아웃을 화면에 그리기 위해 바인딩 객체 생성
        binding = FragmentMainFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    /* onCreateView 이후 호출
    * onCreateView에서 inflate 된 레이아웃에 대한 추가 작업을 수행
    * ex) view 에 data 채우거나 event 처리
    * super.onViewCreated(view, savedInstanceState)는 상위 클래스(부모 Fragment 또는 AppCompatActivity)의 onViewCreated 함수 호출.
    * 여기서 view는 onCreateView에서 반환한 뷰입니다
    * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 친구 데이터 목록 및 RDB 초기화
        friendDB = Firebase.database.reference.child("Users") // RDB 에 대한 ref 초기화하고 "Users" 노드로부터 친구 목록 데이터 가져옴
        friendadapter = UserAdapter(requireContext(), friendList) // Rv 에 사용될 어댑터 초기화

        // RecyclerView에 어댑터 설정
        binding.friendMainRv.adapter = friendadapter
        binding.friendMainRv.layoutManager = LinearLayoutManager(requireContext())

        // 사용자 데이터를 RDB 에서 가져오기
        friendDB.limitToFirst(30).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Data 가져오기 성공 시 실행
                for (userSnapshot in snapshot.children) {
                    // 각 유저 정보를 UserData 객체로 받아오기
                    val user = userSnapshot.getValue(UserData::class.java)
                    if (user != null) {
                        friendList.add(user) // user data 목록에 추가
                    }
                }
                friendadapter.notifyDataSetChanged() // 어댑터에게 데이터 변경을 알림
            }

            override fun onCancelled(error: DatabaseError) {
                // 처리 중 오류 발생 시 토스트 표시
                Toast.makeText(requireContext(), "데이터를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}