package com.example.mbti_talk.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mbti_talk.DetailActivity
import com.example.mbti_talk.Profile
import com.example.mbti_talk.UserData
import com.example.mbti_talk.databinding.UserListBinding

// 이 어댑터는 RecyclerView에 사용됨. 각 아이템은 사용자 정보를 나타내며, 유저가 리스트 클릭 시, 해당 유저의 프로필 화면으로 이동 기능 추가

class UserAdapter(private val userList: List<UserData>) : RecyclerView.Adapter<UserAdapter.Holder>() {


    // onCreateViewHolder 함수는 ViewHolder 객체를 생성, 초기화
    // ItemBinding.inflate() 함수를 통해 XML 레이아웃 파일에서 뷰를 inflate, 그 뷰를 사용하여 Holder 객체를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = UserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val user_list = binding.clListItem // 유저 리스트 바인딩

        // user list 클릭 시 DetailActivity 로 이동
        user_list.setOnClickListener {
            // startActivity 함수를 사용하여 User Detail 로 이동하는 인텐트를 생성하고 실행. 이 경우 DetailActivity::class.java로 지정된 Profile 로 이동
            val intent = Intent(parent.context, DetailActivity::class.java)

            // 클릭한 user data 를 DetailActivity 로 전달
            val userPosition = Intent(parent.context, DetailActivity::class.java)
            intent.putExtra("userPosition", userPosition)

            startActivity(parent.context, intent, null)
        }
        // 클릭 이벤트를 처리한 후, Holder(binding)을 반환
        return Holder(binding)
    }

    // RecyclerView 에 표시할 항목의 총 수를 반환
    override fun getItemCount(): Int {
        // userList 에 있는 항목의 수를 반환
        return userList.size
    }

    // onBindViewHolder 는 RecyclerView 가 화면에 표시할 때 호출.
    // position 은 현재 ViewHolder 가 Binding 될 항목의 위치를 나타냄
    // userList[position] 을 사용하여 해당 위치의 UserData 가져옴
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = userList[position]

        // 아이템 데이터 설정
        holder.user_nickname.text = user.user_nickName
        holder.user_age.text = user.user_age.toString()
        holder.user_gender.text = user.user_gender
        holder.user_mbti.text = user.user_mbti
        holder.user_profile.setImageResource(user.user_profile)

    }

    /* onBindViewHolder는 RecyclerV가 화면에 Item 표시 or Item 이 화면에 스크롤되거나 새 아이템 나타날 때 호출
    holder 는 RV,ViewHolder 객체. Item View 의 레이아웃 내부의 서브 뷰 접근할 수 있다.
    position 은 현재 Item 위치 나타내는 Index. 이 위치에 해당하는 Data 를 가져와 Item View 에 표시

    onBindViewHolder 내에서는 주로 position 매개변수 사용하여 Data 를 가져와서 ViewHolder 내부 View 에 설정
    * */

    // Holder 클래스는 ViewHolder 패턴을 구현
    // XML 레이아웃에서 정의한 뷰들을 멤버 변수로 가진다.
    // 이 뷰들에 유저 데이터 설정, 아이템 이벤트를 처리
    inner class Holder(binding: UserListBinding) : RecyclerView.ViewHolder(binding.root) {

        val user_nickname = binding.etNickName // 닉네임 텍스트뷰
        val user_age = binding.etAge // 나이 텍스트뷰
        val user_gender = binding.etGender // 성별 텍스트뷰
        val user_mbti = binding.etMbti // mbti 텍스트뷰
        val user_profile = binding.ivProfile // 프로필 이미지뷰
    }
}