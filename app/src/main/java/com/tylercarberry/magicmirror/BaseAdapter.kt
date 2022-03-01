package com.tylercarberry.magicmirror

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<ITEM, BINDING: ViewBinding, VIEW_HOLDER: RecyclerView.ViewHolder>(
    var items: List<ITEM>
): RecyclerView.Adapter<VIEW_HOLDER>() {

    protected lateinit var binding: BINDING

    override fun getItemCount() = items.size

}