package com.example.alkmovies.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.alkmovies.data.model.Movie

abstract class BaseViewHolder<T>(itemView: View):RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Movie)
}