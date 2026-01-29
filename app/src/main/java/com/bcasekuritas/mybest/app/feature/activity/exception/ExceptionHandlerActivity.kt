package com.bcasekuritas.mybest.app.feature.activity.exception

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bcasekuritas.mybest.app.feature.activity.main.MainActivity
import com.bcasekuritas.mybest.databinding.ActivityExceptionHandlerBinding
import com.bcasekuritas.mybest.ext.view.setSafeOnClickListener

class ExceptionHandlerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExceptionHandlerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExceptionHandlerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupComponent()
    }

    private fun setupComponent() {
            binding.btnHome.setSafeOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }
    }
}