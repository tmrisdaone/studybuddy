package com.tmrisdaone.studybuddy.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class ProotScraper {
    companion object {
        const val TAG = "ProotScraper"
        const val DISTRO = "ubuntu"
    }

    suspend fun fetchText(url: String): String = withContext(Dispatchers.IO) {
        val encoded = URLEncoder.encode(url, "UTF-8")
        val cmd = """curl -sL "https://r.jina.ai/$encoded" | head -c 16000"""
        execute(cmd)
    }

    suspend fun fetchYoutube(videoId: String): String = withContext(Dispatchers.IO) {
        val cmd = """yt-dlp --print "TITLE:%(title)s\nDESC:%(description)s" "https://youtube.com/watch?v=$videoId" 2>/dev/null || echo "FAIL"` 
        execute(cmd)
    }

    suspend fun fetchPdfText(filePath: String): String = withContext(Dispatchers.IO) {
        val cmd = """pdftotext "$filePath" - 2>/dev/null || echo "PDF_PARSE_FAIL""""
        execute(cmd)
    }

    private fun execute(command: String): String {
        val p = ProcessBuilder("proot-distro", "login", DISTRO, "--", "sh", "-lc", command)
            .redirectErrorStream(true)
            .start()

        val out = p.inputStream.bufferedReader().use { it.readText() }
        p.waitFor()
        return if (out.isBlank()) "EMPTY_RESULT" else out
    }
}
