package com.walt4771.a080callcheckin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MainListAdapter (val context: Context, val ContactsList: ArrayList<Contacts>) : BaseAdapter() {

    // xml 파일의 View와 데이터를 연결하는 핵심 역할을 하는 메소드이다.
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View = LayoutInflater.from(context).inflate(R.layout.main_lv_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val photo = view.findViewById<ImageView>(R.id.profile)
        val name = view.findViewById<TextView>(R.id.name)
        val contactlist = view.findViewById<TextView>(R.id.contact)

        /* ArrayList<Contacts>의 변수의 이미지와 데이터를 View에 담는다. */
        val contact = ContactsList[position]
        // val resourceId = context.resources.getIdentifier(contact.photo, "drawable", context.packageName)
        // photo.setImageResource(resourceId)
        name.text = contact.name
        contactlist.text = contact.contact
        return view
    }

    // 해당 위치의 item을 메소드이다. Int 형식으로 된 position을 파라미터로 갖는다.
    // 예를 들어 1번째 item을 선택하고 싶으면 코드에서 getItem(0)과 같이 쓸 수 있을 것이다.
    override fun getItem(position: Int): Any {
        return ContactsList[position]
    }

    // ListView에 속한 item의 전체 수를 반환한다.
    override fun getCount(): Int {
        return ContactsList.size
    }

    // 해당 위치의 item id를 반환하는 메소드이다. 이 예제에서는 실질적으로 id가 필요하지 않아서 0을 반환하도록 설정했다.
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}