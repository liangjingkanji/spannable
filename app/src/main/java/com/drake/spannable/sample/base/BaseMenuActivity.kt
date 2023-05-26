package com.drake.spannable.sample.base

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.drake.spannable.sample.MainActivity
import com.drake.spannable.sample.R
import com.drake.spannable.sample.RichInputActivity

open class BaseMenuActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_input -> startActivity(Intent(this, RichInputActivity::class.java))
            R.id.menu_image_text -> startActivity(Intent(this, MainActivity::class.java))
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}