package com.example.boasf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.concurrent.thread

const val URL = "https://avidreaders.ru/s/"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<Button>(R.id.buttonSearch)
        btnSearch.setOnClickListener{
            thread{
                val ex = GetBook()
                ex.start()

            }
        }
    }
}

class GetBook() {
    init {
        start()
    }
    fun start() {
        if (true) {
            val res: Connection.Response = Jsoup
                .connect("https://avidreaders.ru/s/простодушный")
                .cookie("list_view_full_books", "1")
                .method(Connection.Method.GET)
                .execute()
            val doc: Document = res.parse()
        }
    }
}