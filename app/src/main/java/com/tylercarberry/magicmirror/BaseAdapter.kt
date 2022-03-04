package com.tylercarberry.magicmirror

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<ITEM, VIEW_HOLDER: RecyclerView.ViewHolder>(
    var items: List<ITEM>
): RecyclerView.Adapter<VIEW_HOLDER>() {

    override fun getItemCount() = items.size

}