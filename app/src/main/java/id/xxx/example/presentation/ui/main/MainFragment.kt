package id.xxx.example.presentation.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import be.tgermain.m3uparser.core.Parser
import id.xxx.example.R
import id.xxx.example.data.repository.M3uRepository
import id.xxx.example.databinding.FragmentMainBinding
import id.xxx.example.presentation.ui.playback.PlaybackActivity
import id.xxx.example.presentation.ui.content.ContentFragmentRecyclerViewAdapter
import id.xxx.example.presentation.ui.group_title.M3uGroupTitleRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {

        const val KEY_URI_M3U = "KEY_URI_M3U"
    }

    private var jobLoadItem: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            return
        requireActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val viewBinding = FragmentMainBinding.bind(view)

        val argUriM3U =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(KEY_URI_M3U, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable(KEY_URI_M3U)
            } ?: throw NullPointerException()

        lifecycleScope.launch {
            val contentResolver = view.context.contentResolver
            val groupTitles = Parser().parse(context, argUriM3U)
            withContext(Dispatchers.Main) {
                val recyclerViewGroupTitle = viewBinding.recyclerViewGroupTitle
                val m3uGroupTitleRecyclerViewAdapter = M3uGroupTitleRecyclerViewAdapter(
                    onItemFocus = { groupTitle ->
                        jobLoadItem?.cancel()
                        jobLoadItem = lifecycleScope.launch(Dispatchers.IO) {
                            val items =
                                M3uRepository.getInstance(view.context).getDataBy(groupTitle)
                            val adapter =
                                viewBinding.recyclerView.adapter as? ContentFragmentRecyclerViewAdapter
                            withContext(Dispatchers.Main) {
                                adapter?.setItem(items)
                            }
                        }
                    }
                )
                recyclerViewGroupTitle.setHasFixedSize(true)
                recyclerViewGroupTitle.layoutManager = LinearLayoutManager(view.context)
                recyclerViewGroupTitle.adapter = m3uGroupTitleRecyclerViewAdapter
                m3uGroupTitleRecyclerViewAdapter.setItem(groupTitles)
                viewBinding.progressBarMain.isVisible = false

                val recyclerView = viewBinding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
                recyclerView.setHasFixedSize(true)
                val contentFragmentRecyclerViewAdapter = ContentFragmentRecyclerViewAdapter(
                    onItemClicked = { data ->
                        val intent = Intent(context, PlaybackActivity::class.java)
                        intent.putExtra(PlaybackActivity.EXTRA_DATA, data)
                        startActivity(intent)
                    }
                )
                recyclerView.adapter = contentFragmentRecyclerViewAdapter
            }
        }
    }
}