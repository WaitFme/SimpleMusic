package com.waitfme.simplemusichd.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.waitfme.simplemusichd.R
//import com.waitfme.simplemusichd.data.Data
import com.waitfme.simplemusichd.model.SongModel

// 歌曲适配器
class SongAdapter(context: Context) : BaseAdapter<SongModel, SongAdapter.SongViewHolder>(context) {

    override fun onBindLayout(): Int {
        return R.layout.layout_linear_item
    }

    override fun onCreateHolder(view: View): SongViewHolder {
        return SongViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindData(holder: SongViewHolder, e: SongModel, position: Int) {
        holder.songName.text = e.title?.trim()
        holder.songSinger.text = e.artist?.trim()
        holder.songAlbum.text = e.album?.trim()
//        holder.songAlbum.text = " - " + e.album?.trim()

        // holder.ivSongImage.setTag(R.id.itemIvIcon, positon)
        /*if (position == holder.ivSongImage.getTag(R.id.ItemIvIcon) as Int && e.isPlaying) {

            holder.ivSongImage.setImageResource(R.mipmap.playlist_icon_default)
        }else{

            holder.ivSongImage.setImageResource(R.mipmap.playlist_icon_default)
        }
*/
        if (e.isPlaying) {
            val selectColor = "#E94648"
            holder.songName.setTextColor(Color.parseColor(selectColor))
            holder.songSinger.setTextColor(Color.parseColor(selectColor))
            holder.songAlbum.setTextColor(Color.parseColor(selectColor))
        }
    }

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {

//        var ivSongImage: ImageView = view.findViewById(R.id.ItemIvIcon)
        var songName: TextView = view.findViewById(R.id.ItemTvSongName)
        var songSinger: TextView = view.findViewById(R.id.itemTvSinger)
        var songAlbum: TextView = view.findViewById(R.id.itemTvAlbum)
//        var songAlbum: ImageView = view.findViewById(R.id.ItemIvIcon)
    }
}