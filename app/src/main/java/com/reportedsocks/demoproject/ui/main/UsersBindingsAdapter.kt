package com.reportedsocks.demoproject.ui.main

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.data.User

@BindingAdapter("items")
fun setItems(listView: RecyclerView, users: PagedList<User>?) {
    users?.let {
        (listView.adapter as UsersAdapter).submitList(users)
    }
}

@BindingAdapter("avatar")
fun setAvatar(imageView: ImageView, url: String?) {
    Glide
        .with(imageView)
        .load(url)
        .placeholder(R.drawable.ic_baseline_account_box_24)
        .error(R.drawable.ic_baseline_account_box_24)
        .into(imageView)
}