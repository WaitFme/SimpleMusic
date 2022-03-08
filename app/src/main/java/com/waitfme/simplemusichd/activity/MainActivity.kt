package com.waitfme.simplemusichd.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions
import com.waitfme.simplemusichd.R
import com.waitfme.simplemusichd.adapter.BaseAdapter
import com.waitfme.simplemusichd.adapter.SongAdapter
import com.waitfme.simplemusichd.model.SongModel
import com.waitfme.simplemusichd.service.MusicPlayerService
import com.waitfme.simplemusichd.ui.about.AboutFragment
import com.waitfme.simplemusichd.ui.colect.CollectFragment
import com.waitfme.simplemusichd.ui.local.LocalFragment
import com.waitfme.simplemusichd.ui.setting.SettingsFragment
import com.waitfme.simplemusichd.utils.GetHeightUtils
import com.waitfme.simplemusichd.utils.ScanMusicUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_horizontal.*
import kotlin.random.Random


@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var TAG: String = MainActivity::class.java.simpleName

    lateinit var mAdapter: SongAdapter

    private lateinit var helper: MusicPlayerService

    // 歌曲数据源
    private var songsList: MutableList<SongModel> = ArrayList()
    var songsCollect: MutableList<SongModel> = ArrayList()

    // 当前播放歌曲游标位置
    var mPosition: Int = 0

    var data = false
    var cycle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 适配小白条和ToolBar
        /*UltimateBarX.with(this)
            .fitWindow(false)
            .light(true)
            .colorRes(R.color.white)
            .applyStatusBar()
        UltimateBarX.with(this)
            .fitWindow(false)
            .light(true)
            .applyNavigationBar()*/

        initial()

        begin()
    }

    @SuppressLint("CheckResult")
    private fun initial() {
        // 监控点击事件
        MainConsole.setOnClickListener(this)
        MainIvLast.setOnClickListener(this)
        MainIvPlay.setOnClickListener(this)
        MainIvNext.setOnClickListener(this)
        MainPlayLast.setOnClickListener(this)
        MainPlayPlay.setOnClickListener(this)
        MainPlayNext.setOnClickListener(this)
        MainPLayBack.setOnClickListener(this)
        MainRdbItem0.setOnClickListener(this)
        MainRdbItem1.setOnClickListener(this)
        MainRdbItem2.setOnClickListener(this)
        MainRdbItem3.setOnClickListener(this)
        MainSongCollect.setOnClickListener(this)
        MainSongCycle.setOnClickListener(this)
        MainMIlaireApplication.setOnClickListener(this)
        MainCloseApplication.setOnClickListener(this)

        cycle = getSharedPreferences("key", 0).getInt("cycle", 0)
        when (cycle) {
            0 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_all)
            1 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_only)
            2 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_random)
        }

        val localFragment = LocalFragment()
        supportFragmentManager.beginTransaction().add(R.id.MainContext, localFragment).commit()
        changeFragment()

        // 获取当前控件的布局对象
        val params0 = MainNavigationBarView.layoutParams
        // 设置当前控件布局的高度
        params0.height = GetHeightUtils.getNavigationBarHeight(this)
        // 将设置好的布局参数应用到控件中
        MainNavigationBarView.layoutParams = params0

        MainEtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            val intent = Intent(this, SettingsActivity::class.java)
            val content = MainEtSearch.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (content == "/设置" || content == "/settings" || content == "/Settings" || content == "/SETTINGS") {
                    MainEtSearch.text = null
                    MainEtSearch.clearFocus()
                    startActivity(intent)
                }
                return@OnEditorActionListener true
            }
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (content == "/设置" || content == "/settings" || content == "/Settings" || content == "/SETTINGS") {
                    MainEtSearch.text = null
                    MainEtSearch.clearFocus()
                    startActivity(intent)
                }
                return@OnEditorActionListener true
            }
            true
        })
    }

    @SuppressLint("CheckResult")
    private fun begin() {
        // Init 播放 Helper
        helper = MusicPlayerService(
            MainPlaySeekBar,
            MainPlaySongName,
            MainPlaySongSinger,
            MainConsoleName,
            MainConsoleSinger,
            MainPlaySongLongNow,
            MainPlaySongLongAll
        )
        helper.setOnCompletionListener(object : MusicPlayerService.OnCompletionListener {
            override fun onCompletion(mp: MediaPlayer?) {
                val s = getSharedPreferences("songCollect", 0)
                Log.e(TAG, "next()")
                //下一曲
                next()
                if (s.getBoolean("$mPosition", false)) {
                    MainSongCollect.setImageResource(R.mipmap.ic_song_collect_true)
                } else {
                    MainSongCollect.setImageResource(R.mipmap.ic_song_collect_false)
                }
            }
        })

        // Init Adapter
        mAdapter = SongAdapter(this)
        //添加数据源
        mAdapter.addAll(songsList)
        // RecyclerView 增加适配器
        MainRclSongList.adapter = mAdapter
        // RecyclerView 增加布局管理器
        MainRclSongList.layoutManager = LinearLayoutManager(this)
        // Adapter 增加 Item 监听
        mAdapter.setItemClickListener(object : BaseAdapter.OnItemClickListener<SongModel> {
            override fun onItemClick(e: SongModel, position: Int) {
                mPosition = position
                //播放歌曲
                play(e, true)
                val s = getSharedPreferences("songCollect", 0)
                if (s.getBoolean("$mPosition", false)) {
                    MainSongCollect.setImageResource(R.mipmap.ic_song_collect_true)
                } else {
                    MainSongCollect.setImageResource(R.mipmap.ic_song_collect_false)
                }
            }
        })

        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe { aBoolean: Boolean? ->
            if (!aBoolean!!) {
                showToast("缺少存储权限，将会导致部分功能无法使用")
            } else {
                // 获取到读写权限 进行操作
                val musicData = ScanMusicUtils.getMusicData(this)
                if (musicData.isNotEmpty()) {
                    songsList.addAll(musicData)
                    mAdapter.refresh(songsList)
                    data = true
                } else {
                    showToast("内容未空！")
                }
            }
        }

        Thread {
            val list: MutableList<SongModel> = ArrayList()
            val s = getSharedPreferences("songCollect", 0)
            for (index in 0..songsList.size) {
                if (s.getBoolean("$index", false)) {
                    val songModel = SongModel()
                    songModel.title = songsList[index].title
                    songModel.artist = songsList[index].artist
                    songModel.path = songsList[index].path
                    songModel.duration = songsList[index].duration
                    songModel.size = songsList[index].size
                    songModel.album = songsList[index].album
                    list.add(songModel)
                }
            }
            if (list.isNotEmpty()) {
                songsCollect.addAll(list)
            } else {
                showToast("内容未空！")
            }
        }
    }

    override fun onClick(v: View?) {
        if (data) {
            when (v) {
                MainConsole -> {
                    MainPlaying.visibility = View.VISIBLE
//                overridePendingTransition(R.anim.`in`, R.anim.ou)
                }
                MainPlayLast, MainIvLast -> last()
                MainPlayPlay, MainIvPlay -> {
                    if (MainRdbItem1.isChecked) {
                        play(songsCollect[mPosition], false)
                    } else {
                        play(songsList[mPosition], false)
                    }
                }
                MainPlayNext, MainIvNext -> next()
                MainPLayBack -> MainPlaying.visibility = View.GONE
                /*MainRdbItem0 -> {
                    //添加数据源
                    mAdapter.addAll(songsList)
                    // RecyclerView 增加适配器
                    MainRclSongList.adapter = mAdapter
                    // RecyclerView 增加布局管理器
                    MainRclSongList.layoutManager = LinearLayoutManager(this)
                    // Adapter 增加 Item 监听
                    mAdapter.setItemClickListener(object :
                        BaseAdapter.OnItemClickListener<SongModel> {
                        override fun onItemClick(e: SongModel, position: Int) {
                            mPosition = position
                            //播放歌曲
                            play(e, true)
                            val s = getSharedPreferences("songCollect", 0)
                            if (s.getBoolean("$mPosition", false)) {
                                MainSongCollect.setImageResource(R.mipmap.ic_song_collect_true)
                            } else {
                                MainSongCollect.setImageResource(R.mipmap.ic_song_collect_false)
                            }
                        }
                    })
                    mAdapter.refresh(songsList)
                }
                MainRdbItem1 -> {
                    // Init Adapter
                    mAdapter = SongAdapter(this)
                    //添加数据源
                    mAdapter.addAll(songsCollect)
                    // RecyclerView 增加适配器
                    MainRclSongList.adapter = mAdapter
                    // RecyclerView 增加布局管理器
                    MainRclSongList.layoutManager = LinearLayoutManager(this)
                    // Adapter 增加 Item 监听
                    mAdapter.setItemClickListener(object :
                        BaseAdapter.OnItemClickListener<SongModel> {
                        override fun onItemClick(e: SongModel, position: Int) {
                            mPosition = position
                            //播放歌曲
                            play(e, true)
                            val s = getSharedPreferences("songCollect", 0)
                            if (s.getBoolean("$mPosition", false)) {
                                MainSongCollect.setImageResource(R.mipmap.ic_song_collect_true)
                            } else {
                                MainSongCollect.setImageResource(R.mipmap.ic_song_collect_false)
                            }
                        }
                    })
                    mAdapter.refresh(songsCollect)
                }
                MainRdbItem2 -> showToast("关于软件")
                MainRdbItem3 -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        intent.putExtra("landscape", true)
                    }
                    startActivity(intent)
                }*/
                MainSongCollect -> {
                    val s = getSharedPreferences("songCollect", 0)
                    if (!s.getBoolean("$mPosition", false)) {
                        s.edit().putBoolean("$mPosition", true).apply()
                        MainSongCollect.setImageResource(R.mipmap.ic_song_collect_true)
                    } else {
                        s.edit().putBoolean("$mPosition", false).apply()
                        MainSongCollect.setImageResource(R.mipmap.ic_song_collect_false)
                    }
                }
                MainSongCycle -> {
                    cycle++
                    if (cycle == 3) {
                        cycle = 0
                    }
                    when (cycle) {
                        0 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_all)
                        1 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_only)
                        2 -> MainSongCycle.setImageResource(R.mipmap.ic_song_list_random)
                    }
                    val s = getSharedPreferences("key", 0)
                    s.edit().putInt("cycle", cycle).apply()
                }
                MainMIlaireApplication -> moveTaskToBack(false)
                MainCloseApplication -> finish()
            }
        }
    }

    private fun changeFragment() {
        val radioGroup = findViewById<RadioGroup>(R.id.MainRadioGroup)
        radioGroup.setOnCheckedChangeListener { _, i ->
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val localFragment = LocalFragment()
            val collectFragment = CollectFragment()
            val aboutFragment = AboutFragment()
            val settingsFragment = SettingsFragment()
            when (i) {
                R.id.MainRdbItem0 -> {
                    MainMusicContent.visibility = View.VISIBLE
                    MainFragmentTitle.text = this.getString(R.string.localMusic)
                    transaction.replace(R.id.MainContext, localFragment).commit()
                }
                R.id.MainRdbItem1 -> {
                    MainMusicContent.visibility = View.GONE
                    MainFragmentTitle.text = this.getString(R.string.collectMusic)
                    transaction.replace(R.id.MainContext, collectFragment).commit()
                }
                R.id.MainRdbItem2 -> {
                    MainMusicContent.visibility = View.GONE
                    MainFragmentTitle.text = this.getString(R.string.about)
                    transaction.replace(R.id.MainContext, aboutFragment).commit()
                }
                R.id.MainRdbItem3 -> {
                    MainMusicContent.visibility = View.GONE
                    MainFragmentTitle.text = this.getString(R.string.settings)
                    transaction.replace(R.id.MainContext, settingsFragment).commit()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun play(songModel: SongModel, isRestPlayer: Boolean) {
        if (!TextUtils.isEmpty(songModel.path)) {
            // 当前若是播放，则进行暂停
            if (!isRestPlayer && helper.isPlaying()) {
                MainIvPlay.setImageResource(R.mipmap.ic_bq_play_notify_play)
                MainPlayPlay.setImageResource(R.mipmap.ic_bq_player_action_play)
                pause()
            } else {
                //进行切换歌曲播放
                MainIvPlay.setImageResource(R.mipmap.ic_bq_play_notify_pause)
                MainPlayPlay.setImageResource(R.mipmap.ic_bq_player_action_pause)
                songModel.let {
                    helper.playBySongModel(
                        it,
                        isRestPlayer
                    )
                }
                // 正在播放的列表进行更新哪一首歌曲正在播放 主要是为了更新列表里面的显示
                for (index in 0 until songsList.size) {
                    songsList[index].isPlaying = mPosition == index
                }
                mAdapter.notifyDataSetChanged()
            }
        } else {
            showToast("当前的播放地址无效")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && MainPlaying.visibility.toString() == "0") {
            MainPlaying.visibility = View.GONE
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showToast(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // 上一首
    private fun last() {
        mPosition--
        //如果上一曲小于0则取最后一首
        if (mPosition < 0) {
            mPosition = songsList.size - 1
        }
        if (MainRdbItem1.isChecked) {
            play(songsCollect[mPosition], true)
        } else {
            play(songsList[mPosition], true)
        }
    }

    // 下一首
    fun next() {
        when (cycle) {
            0 -> {
                mPosition++
                if (MainRdbItem1.isChecked) {
                    //如果下一曲大于歌曲数量则取第一首
                    if (mPosition >= songsCollect.size) {
                        mPosition = 0
                    }
                    play(songsCollect[mPosition], true)
                } else {
                    //如果下一曲大于歌曲数量则取第一首
                    if (mPosition >= songsList.size) {
                        mPosition = 0
                    }
                    play(songsList[mPosition], true)
                }
            }
            1 -> {
                if (MainRdbItem1.isChecked) {
                    play(songsCollect[mPosition], true)
                } else {
                    play(songsList[mPosition], true)
                }
            }
            2 -> {
                if (MainRdbItem1.isChecked) {
                    mPosition = Random.nextInt(songsCollect.size)
                    play(songsCollect[mPosition], true)
                } else {
                    mPosition = Random.nextInt(songsList.size)
                    play(songsList[mPosition], true)
                }
            }
        }
    }

    // 暂停播放
    var pause = { helper.pause() }

    // 停止播放
    var stop = { helper.stop() }

    override fun onDestroy() {
        super.onDestroy()
        helper.destroy()
    }
}