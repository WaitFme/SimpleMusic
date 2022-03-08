package com.waitfme.simplemusichd.ui.local

import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import com.waitfme.simplemusichd.R
import com.waitfme.simplemusichd.activity.MainActivity
import com.waitfme.simplemusichd.adapter.BaseAdapter
import com.waitfme.simplemusichd.adapter.SongAdapter
import com.waitfme.simplemusichd.model.SongModel
import com.waitfme.simplemusichd.service.MusicPlayerService
import com.waitfme.simplemusichd.utils.ScanMusicUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_horizontal.*

class LocalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_local, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}