package com.codemao.land.activity

import android.content.Context
import androidx.multidex.MultiDex
import kotlin.properties.Delegates
import androidx.multidex.MultiDexApplication

public class BCMApplication : MultiDexApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        //初始化上下文
        context = applicationContext
        getScreenSize()
    }

    /**
     * 获取屏幕分辨率方法
     */
    private fun getScreenSize() {
        val dm = applicationContext.resources.displayMetrics
        screenDensity = dm.density
        scaledDensity = dm.scaledDensity
        verticalScreenWidth = dm.widthPixels
        verticalScreenHeight = dm.heightPixels
        horizontalScreenWidth = verticalScreenHeight
        horizontalScreenHeight = verticalScreenWidth
    }

    companion object {
        var verticalScreenWidth: Int = 0      //竖屏时屏幕的宽度
        var verticalScreenHeight: Int = 0     //竖屏时屏幕的高度
        var horizontalScreenWidth: Int = 0    //横屏时屏幕的宽度
        var horizontalScreenHeight: Int = 0   //横屏时屏幕的高度
        var screenDensity: Float = 0.toFloat()          //密度
        var scaledDensity: Float = 0.toFloat()
        var context: Context by Delegates.notNull()
            private set
    }
}