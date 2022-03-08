package com.waitfme.simplemusichd.model

/**
 * Describe:
 * <p>歌曲实体模型</p>
 *
 * @author zhouhuan
 * @Date 2020/11/19
 */
class SongModel() {
    /**
     * 歌曲名字
     */
    var title: String? = null

    /**
     * 作家
     */
    var artist: String? = null

    /**
     * 专辑
     */
    var album: String? = null

    /**
     * 专辑ID
     */
    var albumId: String? = null

    /**
     * 时长
     */
    var duration: Int = 0

    /**
     * 歌曲路径
     */
    var path: String? = null

    /**
     * 歌曲文件大小
     */
    var size: Long = 0

    /**
     * 歌曲照片
     */
    var imagePath: String? = null

    /**
     * 是否正在播放
     */
    var isPlaying: Boolean = false

    fun SongModel() {

    }
    @JvmName("getName1")
    fun getTitle(): String? {
        return title
    }

    @JvmName("setName1")
    fun setTitle(title: String?) {
        this.title = title
    }

    @JvmName("getImagePath1")
    fun getImagePath(): String? {
        return imagePath
    }

    @JvmName("setImagePath1")
    fun setImagePath(imagePath: String?) {
        this.imagePath = imagePath
    }

    @JvmName("getSinger1")
    fun getSinger(): String? {
        return artist
    }

    @JvmName("setSinger1")
    fun setSinger(singer: String?) {
        this.artist = singer
    }

    @JvmName("getPath1")
    fun getPath(): String? {
        return path
    }

    @JvmName("setPath1")
    fun setPath(path: String?) {
        this.path = path
    }

    @JvmName("getDuration1")
    fun getDuration(): Int {
        return duration
    }

    @JvmName("setDuration1")
    fun setDuration(duration: Int) {
        this.duration = duration
    }

    @JvmName("getSize1")
    fun getSize(): Long {
        return size
    }

    @JvmName("setSize1")
    fun setSize(size: Long) {
        this.size = size
    }

    fun getPlaying(): Boolean {
        return isPlaying
    }

    @JvmName("setPlaying1")
    fun setPlaying(playing: Boolean) {
        isPlaying = playing
    }

}