package com.dnine7.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var selectedUri: Uri? = null

    private lateinit var tvTrack: TextView
    private lateinit var btnPlayPause: Button

    private val pickAudioLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedUri = uri
                tvTrack.text = uri.lastPathSegment ?: "Faixa selecionada"
                prepareAndPlay(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTrack = findViewById(R.id.tvTrack)
        val btnPickAudio: Button = findViewById(R.id.btnPickAudio)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        val btnStop: Button = findViewById(R.id.btnStop)

        btnPickAudio.setOnClickListener {
            pickAudioLauncher.launch("audio/*")
        }

        btnPlayPause.setOnClickListener {
            val player = mediaPlayer
            if (player != null) {
                if (player.isPlaying) {
                    player.pause()
                    btnPlayPause.text = "Play"
                } else {
                    player.start()
                    btnPlayPause.text = "Pause"
                }
            } else if (selectedUri != null) {
                prepareAndPlay(selectedUri!!)
            }
        }

        btnStop.setOnClickListener {
            releasePlayer()
            btnPlayPause.text = "Play"
        }
    }

    private fun prepareAndPlay(uri: Uri) {
        releasePlayer()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()
                start()
                setOnCompletionListener {
                    btnPlayPause.text = "Play"
                }
            }
            btnPlayPause.text = "Pause"
        } catch (_: Exception) {
            tvTrack.text = "Erro ao carregar o áudio"
            btnPlayPause.text = "Play"
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}
