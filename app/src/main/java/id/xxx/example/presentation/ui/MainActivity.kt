package id.xxx.example.presentation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import id.xxx.example.presentation.ui.main.MainFragment

class MainActivity : FragmentActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                if (uri.lastPathSegment?.contains(".m3u") == true) {
                    val bundle = bundleOf(MainFragment.KEY_URI_M3U to uri)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(android.R.id.content, MainFragment::class.java, bundle)
                        .commit()
                } else {
                    Toast.makeText(this, "Not supported", Toast.LENGTH_LONG).show()
                    finishAfterTransition()
                }
            } else {
                finishAfterTransition()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher.launch(arrayOf("*/*"))
    }
}