package com.beloushkin.android.learn.mydictionary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_definition.*

class Definition : AppCompatActivity() {

    public final val DEF_KEY = "myDefinition"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_definition)

        val definition = intent.getStringExtra(DEF_KEY)

        tvDefinition.text = definition
    }

    fun onBackClick(view: View) {
        finish()
    }
}
