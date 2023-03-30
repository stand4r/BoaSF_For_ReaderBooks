package com.example.boasf

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Gravity.*
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.concurrent.thread


const val URL = "https://avidreaders.ru/s/"
const val URL2 = "https://avidreaders.ru/api/get.php?"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<Button>(R.id.buttonSearch)
        btnSearch.setOnClickListener {
            try {
                val scrollLayout = findViewById<LinearLayout>(R.id.Lay1)
                scrollLayout.removeAllViews()
            } catch (e: java.lang.Exception) { }
            val name = findViewById<TextInputEditText>(R.id.bookInput).text.toString()
            thread { getBooks(name) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        }
    }
        fun getBooks(name: String) {
        if (name != "") {
            val res: Connection.Response = Jsoup
                .connect(URL + name)
                .cookie("list_view_full_books", "1")
                .method(Connection.Method.GET)
                .execute()
            val doc: Document = res.parse()
            val divs = doc.select("div.card_info")
            if (divs.size!=0) {
                for (i in 0 until divs.size) {
                    runOnUiThread {
                        val nameBook = divs[i].select("div.book_name").select("a").text().toString()
                        val urlBook = divs[i].select("a.btn").attr("href").toString()
                        var genreBook = ""
                        if (divs[i].select("a.genre").size != 0) {
                            genreBook = divs[i].select("a.genre").text().toString()
                        } else {
                            genreBook = divs[i].select("span")[0].text().toString()
                        }
                        addCard(
                            nameBook,
                            urlBook,
                            genreBook
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addCard(name: String, url: String, author: String) {
        val parentLayout = findViewById<LinearLayout>(R.id.Lay1)
        val card = CardView(this)
        val linear = LinearLayout(this)
        val txt = TextView(this)
        val btn = Button(this)
        val txtGenre = TextView(this)
        val linearParent = LinearLayout(this)
        val params = LinearLayout.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT,
        )
        params.setMargins(40,20,40,20)
        params.gravity = CENTER
        card.layoutParams = params
        card.radius = 40.0F

        val params3 = LinearLayout.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT)
        linearParent.layoutParams = params3
        linearParent.orientation = LinearLayout.VERTICAL
        linearParent.setBackgroundColor(Color.parseColor("#3B5A8B"))

        val params2 = LinearLayout.LayoutParams(
            MATCH_PARENT,
            100)
        params2.setMargins(15,0,0, 10)
        linear.layoutParams = params2
        linear.orientation = LinearLayout.HORIZONTAL
        linear.setBackgroundColor(Color.parseColor("#3B5A8B"))


        txt.textSize = 15.0F
        txt.setTextColor(Color.WHITE)
        txt.text = name
        txt.maxLines = 2
        txt.textAlignment = TEXT_ALIGNMENT_TEXT_START
        txt.width = 400
        txt.gravity = CENTER_VERTICAL and CENTER_HORIZONTAL


        btn.width = 250
        btn.height = WRAP_CONTENT
        btn.text = "Скачать"
        btn.setBackgroundColor(Color.parseColor("#3A4D6F"))
        btn.setTextColor(Color.WHITE)
        btn.textSize = 12.0F
        btn.setOnClickListener {
            downloadBook(url)
        }

        txtGenre.textSize = 14.0F
        txtGenre.setTextColor(Color.WHITE)
        txtGenre.text = "     "+author
        txtGenre.maxLines = 1
        txtGenre.textAlignment = TEXT_ALIGNMENT_TEXT_START
        txtGenre.width = 400
        txtGenre.gravity = CENTER_VERTICAL and CENTER_HORIZONTAL


        linear.addView(txt)
        linear.addView(btn)
        linearParent.addView(linear)
        linearParent.addView(txtGenre)
        card.addView(linearParent)
        parentLayout.addView(card)
    }

    private fun downloadBook(url: String) {
        thread {
            val res: Connection.Response = Jsoup
                .connect(url)
                .cookie("list_view_full_books", "1")
                .method(Connection.Method.GET)
                .execute()
            val doc: Document = res.parse()
            val str = doc.select("a.btn")
            val urlDownload = str.attr("href").toString().split("?")[0] + "?f=fb2"
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlDownload))
            startActivity(intent)
        }
    }
}