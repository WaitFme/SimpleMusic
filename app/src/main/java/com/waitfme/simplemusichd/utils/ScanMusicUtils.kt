package com.waitfme.simplemusichd.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.waitfme.simplemusichd.model.SongModel

open class ScanMusicUtils {
    companion object {
        // 扫描本地的音频文件，返回一个list集合
        @SuppressLint("Range")
        fun getMusicData(context: Context): List<SongModel> {
            val list: MutableList<SongModel> = ArrayList()
            val selectionArgs = arrayOf("%Music%")
            val selection = MediaStore.Audio.Media.DATA + " like ? "
            // 媒体库查询语句（写一个工具类MusicUtils）
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection,
                selectionArgs, MediaStore.Audio.AudioColumns.IS_MUSIC
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val songModel = SongModel()
                    songModel.title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                    songModel.artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    songModel.album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                    songModel.albumId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                    songModel.duration =
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    songModel.path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                    songModel.size =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    if (songModel.size > 1000 * 800) {
                        // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                        /*val name = songModel.name
                        if (name != null && name.contains("-")) {
                            val str: List<String> = name.split(" - ")
                            songModel.singer = str[0]
                            songModel.name = str[1].replace(".mp3", "").replace(".m4a", "").replace(".flac", "")
                        }*/
                        list.add(songModel)
                    }
                }
                // 释放资源
                cursor.close()
            }
            return list
        }

        /**
         * 定义一个方法用来格式化获取到的时间
         */
        fun formatTime(time: Int): String {
            return if (time / 1000 % 60 < 10) {
                (time / 1000 / 60).toString() + ":0" + time / 1000 % 60
            } else {
                (time / 1000 / 60).toString() + ":" + time / 1000 % 60
            }
        }
    }
}