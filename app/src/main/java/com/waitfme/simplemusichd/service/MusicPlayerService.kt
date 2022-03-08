package com.waitfme.simplemusichd.service

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.waitfme.simplemusichd.utils.ScanMusicUtils
import com.waitfme.simplemusichd.model.SongModel
import java.lang.ref.WeakReference

// 可播放格式：AAC、AMR、FLAC、MP3、MIDI、OGG、PCM
class MusicPlayerService constructor(
    private var seekBar: SeekBar,
    songName: TextView,
    singer: TextView,
    songName2: TextView,
    singer2: TextView,
    songLongNow: TextView,
    songLongAll: TextView
) :
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    SeekBar.OnSeekBarChangeListener {
    private var mHandler: MusicPlayerHelperHandler = MusicPlayerHelperHandler(this)

    // 播放器
    private var player: MediaPlayer = MediaPlayer()

    // 进度条
    //private var seekBar: SeekBar

    // 显示播放信息
    private var songName: TextView
    private var songSinger: TextView
    private var songName2: TextView
    private var songSinger2: TextView
    private var songLongNow: TextView
    private var songLongAll: TextView

    // 当前播放歌曲
    private var songModel: SongModel? = null

    init {
        // 设置媒体流类型
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnBufferingUpdateListener(this)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)

        this.seekBar.setOnSeekBarChangeListener(this)
//        this.seekBar2.setOnSeekBarChangeListener(this)
        this.songName = songName
        this.songSinger = singer
        this.songName2 = songName2
        this.songSinger2 = singer2
        this.songLongNow = songLongNow
        this.songLongAll = songLongAll
    }

    companion object {
        var TAG: String = MusicPlayerService::class.java.simpleName
        private const val MSG_CODE = 0x01
        private const val MSG_TIME = 1_000L

        class MusicPlayerHelperHandler constructor(helper: MusicPlayerService) :
            Handler(Looper.getMainLooper()) {
            var weakReference: WeakReference<MusicPlayerService> = WeakReference(helper)
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_CODE -> {
                        var pos = 0
                        //如果播放且进度条未被按压
                        if (weakReference.get()?.player!!.isPlaying && !weakReference.get()?.seekBar!!.isPressed) {
                            val position: Int = weakReference.get()?.player!!.currentPosition
                            val duration: Int = weakReference.get()?.player!!.duration
                            if (duration > 0) {
                                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                                pos =
                                    (weakReference.get()?.seekBar!!.max * position / duration.toLong()).toInt()
                            }
                            /*weakReference.get()?.songName!!.text =
                                weakReference.get()?.getCurrentPlayingInfo(position, duration)*/

                            weakReference.get()?.songName!!.text =
                                weakReference.get()?.getSongName(position)
                            weakReference.get()?.songSinger!!.text =
                                weakReference.get()?.getSongSinger(position)

                            weakReference.get()?.songName2!!.text =
                                weakReference.get()?.getSongName(position)
                            weakReference.get()?.songSinger2!!.text =
                                weakReference.get()?.getSongSinger(position)

                            weakReference.get()?.songLongNow!!.text =
                                weakReference.get()?.getSongLongNow(position)
                            weakReference.get()?.songLongAll!!.text =
                                weakReference.get()?.getSongLongAll(duration)

                        }
                        weakReference.get()?.seekBar!!.progress = pos
                        sendEmptyMessageDelayed(MSG_CODE, MSG_TIME)
                        /*weakReference.get()?.seekBar2!!.progress = pos
                        sendEmptyMessageDelayed(MSG_CODE, MSG_TIME)*/
                    }
                }
            }
        }
    }

    /**
     * 更新缓冲
     */
    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        seekBar.secondaryProgress = percent
        val currentProgress: Int =
            seekBar.max * player.currentPosition / player.duration
        Log.e(TAG, "$currentProgress% play --> $percent buffer")
    }

    /**
     * 当前 Song 已经准备好
     */
    override fun onPrepared(mp: MediaPlayer?) {
        Log.e(TAG, "onPrepared")
        mp!!.start()
    }

    /**
     * 当前 Song 播放完毕
     */
    override fun onCompletion(mp: MediaPlayer?) {
        Log.e(TAG, "onCompletion")
        mOnCompletionListener?.onCompletion(mp)
    }

    /**
     * 播放
     * @param songModel 播放源
     * @param isRestPlayer true 切换歌曲 false 不切换
     */
    fun playBySongModel(songModel: SongModel, isRestPlayer: Boolean) {
        this.songModel = songModel
        Log.e(TAG, "playBySongModel Url: ${songModel.path}")
        if (isRestPlayer) {
            //重置多媒体
            player.reset()
            // 设置数据源
            songModel.path?.let { player.setDataSource(it) }
            // 准备自动播放 同步加载，阻塞 UI 线程
            // player.prepare()
            // 建议使用异步加载方式，不阻塞 UI 线程
            player.prepareAsync()
        } else {
            player.start()
        }
        //发送更新命令
        mHandler.sendEmptyMessage(MSG_CODE)
    }

    /**
     * 暂停（Lambda 写法）
     *  等同于 var pause:() -> Unit = {if (player.isPlaying) player.pause()}
     */
    var pause = {
        Log.e(TAG, "pause")
        if (player.isPlaying) player.pause()
        //移除更新命令
        mHandler.removeMessages(MSG_CODE)
    }

    // 停止
    fun stop() {
        Log.e(TAG, "stop")
        player.stop()
        seekBar.progress = 0
        songName.text = "停止播放"
        //移除更新命令
        mHandler.removeMessages(MSG_CODE)
    }

    // 是否正在播放
    var isPlaying = { player.isPlaying }

    fun getCurrentPlayingInfo(currentTime: Int, maxTime: Int): String {
        val info = songModel?.let { "${songModel?.title}\t\t" }
        return "$info ${ScanMusicUtils.formatTime(currentTime)} / ${
            ScanMusicUtils.formatTime(
                maxTime
            )
        }"
    }

    fun getSongName(currentTime: Int): String {
        val info = songModel?.let { "${songModel?.title}" }
        return "$info"
    }

    fun getSongSinger(currentTime: Int): String {
        val info = songModel?.let { "${songModel?.artist}" }
        return "$info"
    }

    fun getSongLongNow(currentTime: Int): String {
        return ScanMusicUtils.formatTime(currentTime)
    }

    fun getSongLongAll(maxTime: Int): String {
        return ScanMusicUtils.formatTime(maxTime)
    }

    /**
     * 消亡 必须在 Activity 或者 Frament onDestroy() 调用 以防止内存泄露
     */
    fun destroy() {
        // 释放掉播放器
        player.release()
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 用于监听SeekBar进度值的改变
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    /**
     * 用于监听SeekBar开始拖动
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        mHandler.removeMessages(MSG_CODE)
    }

    /**
     * 用于监听SeekBar停止拖动  SeekBar停止拖动后的事件
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val progress = seekBar!!.progress
        Log.i(TAG, "onStopTrackingTouch $progress")
        // 得到该首歌曲最长秒数
        val musicMax: Int = player.getDuration()
        // SeekBar最大值
        val seekBarMax = seekBar.max
        //计算相对当前播放器歌曲的应播放时间
        val msec: Double = progress / (seekBarMax * 1.0) * musicMax
        // 跳到该曲该秒
        player.seekTo(msec.toInt())
        mHandler.sendEmptyMessageDelayed(MSG_CODE, MSG_TIME)
    }

    private var mOnCompletionListener: OnCompletionListener? = null

    fun setOnCompletionListener(listener: OnCompletionListener) {
        mOnCompletionListener = listener
    }

    interface OnCompletionListener {
        fun onCompletion(mp: MediaPlayer?)
    }
}