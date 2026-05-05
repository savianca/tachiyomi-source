package eu.kanade.tachiyomi.extension.en.manganelo

import eu.kanade.tachiyomi.multisrc.mangabox.MangaBox
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Manganato :
    MangaBox(
        "Manganato",
        arrayOf(
            "www.natomanga.com",
            "www.nelomanga.com",
            "www.manganato.gg",
        ),
        "en",
    ) {

    override val id: Long = 1024627298672457456

    override fun pageListParse(document: Document): List<Page> =
        document.select("div.container-chapter-reader img").mapIndexed { i, element ->
            val url = element.attr("abs:src").ifEmpty { element.attr("abs:data-src") }
            Page(i, "", url)
        }

    override fun chapterListSelector() = "div.row-content-chapter li.a-h"

    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        element.selectFirst("a.chapter-name")!!.let {
            url = it.attr("href").substringAfter(baseUrl)
            name = it.text()
        }
        date_upload = element.selectFirst("span.chapter-time")?.text()?.let {
            super.parseChapterDate(it) // Added 'super.' here
        } ?: 0L
    }

    override fun mangaDetailsRequest(manga: SManga): Request {
        if (LEGACY_DOMAINS.any { manga.url.startsWith(it) }) {
            throw Exception(MIGRATE_MESSAGE)
        }
        return super.mangaDetailsRequest(manga)
    }

    companion object {
        private val LEGACY_DOMAINS = arrayOf(
            "https://chapmanganato.to/",
            "https://manganato.com/",
            "https://readmanganato.com/",
        )
        private const val MIGRATE_MESSAGE = "Migrate this entry from \"Manganato\" to \"Manganato\" to continue reading"
    }
}
