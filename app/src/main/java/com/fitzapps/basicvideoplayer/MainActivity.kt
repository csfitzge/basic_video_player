package com.fitzapps.basicvideoplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.sprylab.android.widget.TextureVideoView

class MainActivity : AppCompatActivity() {

    private var videoPlayer: TextureVideoView? = null
    private var rootView: View? = null
    private var errorLayout: LinearLayout? = null
    private var progressBar: ProgressBar? = null
    private var tryAgainButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootView = findViewById(R.id.root)
        errorLayout = findViewById(R.id.error_state)
        progressBar = findViewById(R.id.progress_bar)
        tryAgainButton = findViewById(R.id.try_again_button)

        //setup views
        val videoPosition = savedInstanceState?.get(VIDEO_POSITION_KEY) as? Int
        setUpVideoPlayer(videoPosition)
        setUpTryAgainButton()
    }

    /**
     * Allows the video position to be maintained if the device is rotated
     */
    override fun onSaveInstanceState(outState: Bundle) {
        videoPlayer?.currentPosition?.let {
            outState.putInt(VIDEO_POSITION_KEY, it)
            Log.d(TAG, "onSaveInstanceState: saving video position: $it")
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * handle video loaded, ability to reload video, restarting when video is over
     */
    private fun setUpVideoPlayer(videoPosition: Int?) {
        videoPlayer = findViewById<TextureVideoView>(R.id.videoPlayer).also {
            it.setVideoPath(VIDEO_URL)
            it.setOnPreparedListener { _ ->
                hideProgressBar()
                it.setMediaController(MediaController(this))
                it.start()
                Log.d(TAG, "setUpVideoPlayer: video prepared and started")
                if (videoPosition != null) {
                    Log.d(TAG, "setUpVideoPlayer: video seeking saved position: $videoPosition")
                    it.seekTo(videoPosition)
                }
            }

            it.setOnErrorListener { _, whatErrorCode, extraErrorCode ->
                hideProgressBar()
                Log.d(TAG, "setUpVideoPlayer: error loading video. Error code $whatErrorCode, with extra code $extraErrorCode")
                errorLayout?.visibility = View.VISIBLE
                false
            }
            it.setOnCompletionListener { _ ->
                hideProgressBar()
                Log.d(TAG, "setUpVideoPlayer: video completed. Restarting")
                it.start()
            }

        }
    }

    private fun setUpTryAgainButton() {
        tryAgainButton?.setOnClickListener{
            errorLayout?.visibility = View.GONE
            progressBar?.visibility = View.VISIBLE
            videoPlayer?.resume()
        }
    }

    private fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    companion object {
        private const val TAG = "TAG_VID"
        private const val VIDEO_POSITION_KEY = "VIDEO_POSITION_KEY"
        private const val VIDEO_URL = "https://soniczone.net/Downloads/Video/AoSTH/E03.mp4"
    }
}