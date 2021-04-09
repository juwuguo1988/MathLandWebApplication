package com.codemao.land.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codemao.land.R
import com.codemao.land.utils.permission.RxPermissionUtils
import kotlinx.android.synthetic.main.activity_launch.*


class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if ((intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish()
            return
        }
        setContentView(R.layout.activity_launch)
        setListener()
    }

    private fun setListener() {
        btnFreeCreation.setOnClickListener(onClickListener)
        btnTeacherLesson.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnFreeCreation -> {
                RxPermissionUtils.getInstance(this).getPowerStatus(
                    {
                        val intent = Intent(this, FreeCreationActivity::class.java)
                        startActivity(intent)
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            R.id.btnTeacherLesson -> {
                RxPermissionUtils.getInstance(this).getPowerStatus(
                    {
                        val intent = Intent(this, TeacherLessonActivity::class.java)
                        startActivity(intent)
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
        }
    }


}