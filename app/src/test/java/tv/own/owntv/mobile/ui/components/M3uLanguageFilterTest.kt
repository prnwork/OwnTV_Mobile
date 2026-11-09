package tv.own.owntv.mobile.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class M3uLanguageFilterTest {

    @Test
    fun detectsLanguageFromCategoryPrefix() {
        assertEquals("English", M3uLanguageFilter.detectLanguage("EN | Movies 4K", "Inception"))
        assertEquals("English", M3uLanguageFilter.detectLanguage("UK: Entertainment", "BBC One"))
        assertEquals("English", M3uLanguageFilter.detectLanguage("US - News", "CNN"))
        assertEquals("Spanish", M3uLanguageFilter.detectLanguage("ES | Deportes", "Real Madrid TV"))
        assertEquals("French", M3uLanguageFilter.detectLanguage("FR | Cinema", "Canal+"))
        assertEquals("German", M3uLanguageFilter.detectLanguage("DE | Sky Cinema", "Sky Cinema Action"))
        assertEquals("Hindi", M3uLanguageFilter.detectLanguage("HINDI | Movies", "Star Gold"))
        assertEquals("Arabic", M3uLanguageFilter.detectLanguage("AR | Drama", "MBC 1"))
    }

    @Test
    fun detectsLanguageFromItemNameWhenCategoryIsGeneric() {
        assertEquals("English", M3uLanguageFilter.detectLanguage("Movies", "[EN] The Dark Knight"))
        assertEquals("French", M3uLanguageFilter.detectLanguage("All Channels", "TF1 [FR]"))
        assertEquals("Spanish", M3uLanguageFilter.detectLanguage(null, "(ES) Telecinco"))
        assertEquals("Hindi", M3uLanguageFilter.detectLanguage("Action", "Sholay [Hindi]"))
    }

    @Test
    fun matchesLanguageCorrectly() {
        assertTrue(M3uLanguageFilter.matchesLanguage("EN | Action", "Die Hard", "English"))
        assertFalse(M3uLanguageFilter.matchesLanguage("EN | Action", "Die Hard", "French"))
        // Target null matches everything
        assertTrue(M3uLanguageFilter.matchesLanguage("EN | Action", "Die Hard", null))
        assertTrue(M3uLanguageFilter.matchesLanguage("FR | Action", "Taxi", null))
    }

    @Test
    fun extractsAvailableLanguagesFromCategories() {
        val categories = listOf(
            "EN | Movies",
            "EN | Series",
            "FR | Cinema",
            "ES | Peliculas",
            "Unknown Category",
            "UK: Sports",
        )
        val extracted = M3uLanguageFilter.extractAvailableLanguages(categories)
        assertEquals(listOf("English", "French", "Spanish"), extracted)
    }

    @Test
    fun returnsNullWhenNoLanguageDetected() {
        assertNull(M3uLanguageFilter.detectLanguage("Favorites", "Channel 1"))
        assertNull(M3uLanguageFilter.detectLanguage(null, null))
    }

    @Test
    fun detectsLanguageFromEpgChannelIdWhenCategoryAndNameAreGeneric() {
        assertEquals("English", M3uLanguageFilter.detectLanguage("News", "CNN", "CNN.us@SD"))
        assertEquals("English", M3uLanguageFilter.detectLanguage("Entertainment", "BBC One", "BBC.uk@HD"))
        assertEquals("Hindi", M3uLanguageFilter.detectLanguage("News", "Aaj Tak", "AajTak.in@SD"))
        assertEquals("French", M3uLanguageFilter.detectLanguage("News", "France 24", "France24.fr@SD"))
        assertEquals("Spanish", M3uLanguageFilter.detectLanguage("General", "12tv", "12tv.es@SD"))
        assertEquals("German", M3uLanguageFilter.detectLanguage("General", "ZDF", "ZDF.de@SD"))
        assertEquals("Italian", M3uLanguageFilter.detectLanguage("General", "Rai 1", "Rai1.it@SD"))
    }

    @Test
    fun matchesLanguageWithEpgChannelId() {
        assertTrue(M3uLanguageFilter.matchesLanguage("News", "CNN", "CNN.us@SD", "English"))
        assertFalse(M3uLanguageFilter.matchesLanguage("News", "CNN", "CNN.us@SD", "French"))
        assertTrue(M3uLanguageFilter.matchesLanguage("News", "Aaj Tak", "AajTak.in@SD", "Hindi"))
        assertFalse(M3uLanguageFilter.matchesLanguage("News", "Aaj Tak", "AajTak.in@SD", "English"))
    }

    @Test
    fun fallsBackToCommonLanguagesWhenCategoriesAreGeneric() {
        val genericCategories = listOf("News", "Movies", "Sports", "General")
        val extracted = M3uLanguageFilter.extractAvailableLanguages(genericCategories)
        assertTrue(extracted.contains("English"))
        assertTrue(extracted.contains("Hindi"))
        assertTrue(extracted.contains("Spanish"))
    }
}
