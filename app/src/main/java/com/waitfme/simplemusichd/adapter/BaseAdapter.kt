package com.waitfme.simplemusichd.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

abstract class BaseAdapter<E, VH : androidx.recyclerview.widget.RecyclerView.ViewHolder>(private var mContext: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<VH>() {
    private var mList: MutableList<E> = ArrayList()
    private var mItemClickListener: OnItemClickListener<E>? = null
    private var mOnItemLongClickListener: OnItemLongClickListener<E>? = null

    val dataList: List<E>
        get() = mList

    val listData: List<E>
        get() = mList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutId = onBindLayout()
        val view = LayoutInflater.from(mContext).inflate(layoutId, parent, false)
        return onCreateHolder(view)
    }

    //绑定布局文件
    protected abstract fun onBindLayout(): Int

    //创建一个holder
    protected abstract fun onCreateHolder(view: View): VH

    //绑定数据
    protected abstract fun onBindData(holder: VH, e: E, position: Int)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = mList[position]
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener { mItemClickListener!!.onItemClick(e, position) }
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                mOnItemLongClickListener!!.onItemLongClick(
                    e,
                    position
                )
            }
        }
        onBindData(holder, e, position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(list: List<E>?) {
        if (list != null && list.isNotEmpty()) {
            mList.addAll(list)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh(list: List<E>?) {
        mList.clear()
        if (list != null && list.isNotEmpty()) {
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun remove(position: Int) {
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun remove(e: E) {
        mList.remove(e)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(e: E) {
        mList.add(e)
        notifyDataSetChanged()
    }

    fun addLast(e: E) {
        add(e)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFirst(e: E) {
        mList.add(0, e)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener<E>) {
        mItemClickListener = itemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener<E>) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    interface OnItemClickListener<E> {
        fun onItemClick(e: E, position: Int)
    }

    interface OnItemLongClickListener<E> {
        fun onItemLongClick(e: E, position: Int): Boolean
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}
