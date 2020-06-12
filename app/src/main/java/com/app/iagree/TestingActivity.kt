package com.app.iagree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jgabrielfreitas.core.BlurImageView

class TestingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        val b = findViewById<BlurImageView>(R.id.blur_image)
        b.setBlur(2)
    }
}
