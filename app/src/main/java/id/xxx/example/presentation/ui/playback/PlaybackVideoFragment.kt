package id.xxx.example.presentation.ui.playback

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import id.xxx.example.data.source.local.M3uEntity

/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    companion object {

        const val EXTRA_DATA = "EXTRA_DATA"
    }

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val m3uEntity =
            activity?.intent?.getSerializableExtra(EXTRA_DATA) as? M3uEntity
        if (m3uEntity != null) {
            val glueHost = VideoSupportFragmentGlueHost(this)
            val playerAdapter = MediaPlayerAdapter(activity)
            playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

            mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
            mTransportControlGlue.host = glueHost
            mTransportControlGlue.title = m3uEntity.tvgName
            mTransportControlGlue.subtitle = m3uEntity.channelName
            mTransportControlGlue.playWhenPrepared()
            playerAdapter.setDataSource(m3uEntity.url?.toUri())
        } else {
            requireActivity().finishAfterTransition()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }
}