package com.example.networkapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val id = numberEditText.text.toString()
            if (id.isNotEmpty()) {
                downloadComic(id)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()

            }
        }
        loadSavedComic()
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url,
                { json ->
                    showComic(json)
                    saveComic(json)
                },
                {
                    Toast.makeText(this, "Failed to download comic", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }


    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        try {
            val title = comicObject.getString("title")
            val description = comicObject.getString("alt")
            val imageUrl = comicObject.getString("img")

            titleTextView.text = title
            descriptionTextView.text = description
            Picasso.get().load(imageUrl).into(comicImageView)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid comic format", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveComic(comicObject: JSONObject) {
        try {
            openFileOutput("comic.json", Context.MODE_PRIVATE).use { stream ->
                stream.write(comicObject.toString().toByteArray())
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save comic", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedComic() {
        try {
            val content = openFileInput("comic.json").bufferedReader().use { it.readText() }
            val json = JSONObject(content)
            showComic(json)
        } catch (e: Exception) {
            // No saved comic or corrupted file
        }
    }
}