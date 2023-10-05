package be.tgermain.m3uparser.core

import android.content.Context
import android.net.Uri
import id.xxx.example.data.repository.M3uRepository
import id.xxx.example.data.source.local.M3uEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

class Parser {

    companion object {

        private const val M3U_START_MARKER = "#EXTM3U"
        private const val M3U_INFO_MARKER = "#EXTINF:"

        private val DURATION_REGEX =
            Pattern.compile(".*#EXTINF:(.+?) .*", Pattern.CASE_INSENSITIVE)
        private val TVG_ID_REGEX =
            Pattern.compile(".*tvg-id=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_NAME_REGEX =
            Pattern.compile(".*tvg-name=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_LOGO_REGEX =
            Pattern.compile(".*tvg-logo=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val TVG_SHIFT_REGEX =
            Pattern.compile(".*tvg-shift=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val GROUP_TITLE_REGEX =
            Pattern.compile(".*group-title=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val RADIO_REGEX =
            Pattern.compile(".*radio=\"(.?|.+?)\".*", Pattern.CASE_INSENSITIVE)
        private val CHANNEL_NAME_REGEX = Pattern.compile(".*,(.+?)$", Pattern.CASE_INSENSITIVE)
    }

    suspend fun parse(context: Context?, uri: Uri): Collection<String> =
        withContext(Dispatchers.IO) {
            val stream =
                context?.contentResolver?.openInputStream(uri)
                    ?: throw ParsingException(0, "Cannot read stream")
            val entries = linkedSetOf<String>()
            var lineNbr = 0
            var line: String?
            try {
                val buffer = BufferedReader(InputStreamReader(stream))
                line = buffer.readLine()
                lineNbr++
                checkStart(line)
                val globalTvgShif: String? = extract(line, TVG_SHIFT_REGEX)
                var entry: Entry.Builder? = null

                val repo = M3uRepository.getInstance(context)
                repo.clear()
                val m3uEntities = mutableListOf<M3uEntity>()

                while (buffer.readLine().also { line = it } != null) {
                    lineNbr++
                    val lineFinal = line
                    if (!lineFinal.isNullOrBlank()) {
                        if (isExtInfo(lineFinal)) {
                            entry = extractExtInfo(globalTvgShif, lineFinal)
                        } else {
                            if (entry == null) {
                                throw ParsingException(lineNbr, "Missing $M3U_INFO_MARKER")
                            }
                            val entryNew = entry.channelUri(lineFinal).build()
                            if (!entryNew.groupTitle.isNullOrBlank()) {
                                entries.add(entryNew.groupTitle)
                                m3uEntities.add(
                                    M3uEntity(
                                        tvgId = entryNew.tvgId,
                                        tvgName = entryNew.tvgName,
                                        tvgLogo = entryNew.tvgLogo,
                                        groupTitle = entryNew.groupTitle,
                                        channelName = entryNew.channelName,
                                        url = entryNew.channelUri
                                    )
                                )
                            }
                            if (m3uEntities.size == 25_000) {
                                val result = repo.save(m3uEntities)
                                println(result.size)
                                m3uEntities.clear()
                            }
                        }
                    }
                }

                if (m3uEntities.isNotEmpty()) {
                    val result = repo.save(m3uEntities)
                    println(result.size)
                    m3uEntities.clear()
                }
            } catch (e: IOException) {
                throw ParsingException(lineNbr, "Cannot read file", e)
            }
            entries
        }

    private fun checkStart(line: String?) {
        if (line != null) {
            if (!line.contains(M3U_START_MARKER)) {
                throw ParsingException(
                    1, "First line of the file should be $M3U_START_MARKER"
                )
            }
        }
    }

    private fun isExtInfo(line: String): Boolean {
        return line.contains(M3U_INFO_MARKER)
    }

    private fun extractExtInfo(globalTvgShift: String?, line: String): Entry.Builder? {
        val duration: String? = extract(line, DURATION_REGEX)
        val tvgId: String? = extract(line, TVG_ID_REGEX)
        val tvgName: String? = extract(line, TVG_NAME_REGEX)
        var tvgShift: String? = extract(line, TVG_SHIFT_REGEX)
        if (tvgShift == null) {
            tvgShift = globalTvgShift
        }
        val radio: String? = extract(line, RADIO_REGEX)
        val tvgLogo: String? = extract(line, TVG_LOGO_REGEX)
        val groupTitle: String? = extract(line, GROUP_TITLE_REGEX)
        val channelName: String? = extract(line, CHANNEL_NAME_REGEX)
        return Entry.Builder().channelName(channelName)
            .duration(duration)
            .groupTitle(groupTitle)
            .radio(radio)
            .tvgId(tvgId)
            .tvgLogo(tvgLogo)
            .tvgName(tvgName)
            .tvgShift(tvgShift)
    }

    private fun extract(line: String, pattern: Pattern): String? {
        val matcher = pattern.matcher(line)
        return if (matcher.matches()) {
            matcher.group(1)
        } else null
    }
}