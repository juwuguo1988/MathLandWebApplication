package com.codemao.land.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codemao.land.R
import com.codemao.land.utils.file.FileUtils
import com.codemao.land.utils.global.AppConstant
import com.codemao.land.utils.loading.LoadingDialog
import com.codemao.land.utils.okhttp.OkHttpUtils
import com.codemao.land.utils.okhttp.callback.FileCallBack
import com.codemao.land.utils.permission.RxPermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Request
import java.io.File


class MainActivity : AppCompatActivity() {
    private var dialog: LoadingDialog? = null
    private var mRandomInt: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.c_858e99)
        mRandomInt = (Math.random() * 10000).toInt()
        setListener()
    }

    private fun setListener() {
        btnSaveJS.setOnClickListener(onClickListener)
        btnSaveFile.setOnClickListener(onClickListener)
        btnSkip.setOnClickListener(onClickListener)
        btnSaveJson.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnSaveJson -> {
                RxPermissionUtils.getInstance(this).getPowerStatus(
                    {
                        downloadJsonFile()
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            R.id.btnSaveJS -> {
                RxPermissionUtils.getInstance(this).getPowerStatus(
                    {
                        downloadJSFile()
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            R.id.btnSaveFile -> {
                RxPermissionUtils.getInstance(this).getPowerStatus(
                    {
                        downloadFile()
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            R.id.btnSkip -> {
                val sampleDir: String = Environment.getExternalStorageDirectory()
                    .toString() + "/" + "CodeMaoFile/MathLand" + "/web-mobile/"
                val url = "file://" + sampleDir + "index.html"
                val localFile = localJsonFile();
                if (localFile.exists()) {
                    val intent = Intent(this, SelfJavaScriptActivity::class.java).apply {
                        putExtra(SelfJavaScriptActivity.WEB_VIEW_URL, url)
                        putExtra(SelfJavaScriptActivity.WEB_JSON_PATH_URL, localFile.path)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "请先下载配置文件", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun localJsonFile(): File {
        val fileName = "lessonConfig"
        val tmpDir: String = FileUtils.createTmpDir(this)
        val destFileDir = "$tmpDir/MathLand"
        val destFileName = "$fileName.json"
        return File(destFileDir, destFileName)
    }

    private fun downloadJsonFile() {
        val fileName = "lessonConfig"
        val tmpDir: String = FileUtils.createTmpDir(this)
        val destFileDir = "$tmpDir/MathLand"
        val destFileName = "$fileName.json"
        val file = File(destFileDir, destFileName)
        if (file.exists()) {
            return
        }
        val stringBufferURl = StringBuffer()
            .append(AppConstant.getBaseUrl())
            .append(destFileName)
            .append("?v=")
            .append(mRandomInt)
            .toString()
        OkHttpUtils
            .get()
            .url(stringBufferURl)
            .build()
            .execute(object : FileCallBack(destFileDir, destFileName) {
                override fun onBefore(request: Request, id: Int) {
                    super.onBefore(request, id)
                    showProgressDialog()
                }

                override fun onCompleted(json: File?, id: Int) {

                }

                override fun onAfter(id: Int) {
                    super.onAfter(id)
                    dismissProgressDialog()
                }

                override fun onError(call: Call, e: Exception?, id: Int) {
                }
            })
    }


    private fun downloadJSFile() {
        val fileName = "cocos2d-js"
        val tmpDir: String = FileUtils.createTmpDir(this)
        val destFileDir = "$tmpDir/MathLand"
        val destFileName = "$fileName.js"
        val file = File(destFileDir, destFileName)
        if (file.exists()) {
            return
        }
        val stringBufferURl = StringBuffer()
            .append(AppConstant.getBaseUrl())
            .append(destFileName)
            .append("?v=")
            .append(mRandomInt)
            .toString()
        OkHttpUtils
            .get()
            .url(stringBufferURl)
            .build()
            .execute(object : FileCallBack(destFileDir, destFileName) {
                override fun onBefore(request: Request, id: Int) {
                    super.onBefore(request, id)
                    showProgressDialog()
                }

                override fun onCompleted(json: File?, id: Int) {

                }

                override fun onAfter(id: Int) {
                    super.onAfter(id)
                    dismissProgressDialog()
                }

                override fun onError(call: Call, e: Exception?, id: Int) {
                }
            })
    }


    private fun downloadFile() {
        val fileName = "web-mobile"
        val tmpDir: String = FileUtils.createTmpDir(this)
        val destFileDir = "$tmpDir/MathLand"
        val destFileName = "$fileName.zip"
        val file = File(destFileDir, destFileName)
        if (file.exists()) {
            return
        }
        val stringBufferURl = StringBuffer()
            .append(AppConstant.getBaseUrl())
            .append(destFileName)
            .append("?v=")
            .append(mRandomInt)
            .toString()
        OkHttpUtils
            .get()
            .url(stringBufferURl)
            .build()
            .execute(object : FileCallBack(destFileDir, destFileName) {
                override fun onBefore(request: Request, id: Int) {
                    super.onBefore(request, id)
                    showProgressDialog()
                }

                override fun onCompleted(json: File?, id: Int) {

                }

                override fun inProgress(progress: Float, total: Long, id: Int) {
                    if (progress == 1.0f) {//开始解压
                        try {
                            FileUtils.unZipFolder(file, "$destFileDir/$fileName")
                        } catch (e: Exception) {
                            Log.d("============", "======解压失败！！！======");
                        }
                    }
                }

                override fun onAfter(id: Int) {
                    super.onAfter(id)
                    dismissProgressDialog()
                }

                override fun onError(call: Call, e: Exception?, id: Int) {
                }
            })
    }


    private fun showProgressDialog() {
        dismissProgressDialog()
        dialog = LoadingDialog(this, false).apply {
            setCancelable(false)
            show()
        }
    }


    private fun dismissProgressDialog() {
        dialog?.run {
            dismiss();
        }
    }
}