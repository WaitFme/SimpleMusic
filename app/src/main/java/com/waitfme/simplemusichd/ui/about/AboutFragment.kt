package com.waitfme.simplemusichd.ui.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.waitfme.simplemusichd.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment:Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_about, container ,false)
        val aboutGithub = view.findViewById<TextView>(R.id.AboutGithub)
        aboutGithub?.text = Html.fromHtml("<a href'https://github.com/WaitFme/Simple-Music'>github主页：https://github.com/WaitFme/Simple-Music</a>")
        aboutGithub?.movementMethod = LinkMovementMethod.getInstance()
        return view
    }
}