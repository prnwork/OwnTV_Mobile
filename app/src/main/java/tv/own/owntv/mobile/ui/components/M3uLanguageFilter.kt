package tv.own.owntv.mobile.ui.components

/**
 * Utility to detect and filter content (Live Channels, Movies, Series, and Categories)
 * based on M3U language and country tags.
 *
 * M3U playlists commonly format tags in category names (group-title), stream names, or tvg-id:
 * - Prefixes: "EN | Movies", "UK: BBC One", "FR - Cinema", "DE | Sky"
 * - Bracketed tags: "[EN]", "(US)", "|ES|", "[Hindi]"
 * - Suffixes: "Movie Title (2024) [EN]"
 * - EPG channel IDs (tvg-id): "CNN.us@SD", "BBCNews.uk@HD", "AajTak.in@SD"
 */
object M3uLanguageFilter {

    private val PREFIX_DELIMITERS = charArrayOf('|', ':', '-', '–', '/', '•')

    // Maps uppercase code/alias to canonical display name
    private val ALIAS_TO_LANGUAGE = mapOf(
        // English
        "EN" to "English",
        "ENG" to "English",
        "US" to "English",
        "USA" to "English",
        "UK" to "English",
        "GB" to "English",
        "CA" to "English",
        "CAN" to "English",
        "AU" to "English",
        "AUS" to "English",
        "NZ" to "English",
        "NZL" to "English",
        "IE" to "English",
        "IRL" to "English",
        "ENGLISH" to "English",

        // Spanish
        "ES" to "Spanish",
        "ESP" to "Spanish",
        "SPA" to "Spanish",
        "SPANISH" to "Spanish",
        "ESPANOL" to "Spanish",
        "ESPA?OL" to "Spanish",
        "MX" to "Spanish",
        "MEX" to "Spanish",
        "ARG" to "Spanish",
        "CL" to "Spanish",
        "CHL" to "Spanish",
        "CO" to "Spanish",
        "COL" to "Spanish",
        "PE" to "Spanish",
        "PER" to "Spanish",
        "VE" to "Spanish",
        "VEN" to "Spanish",
        "UY" to "Spanish",
        "URY" to "Spanish",

        // French
        "FR" to "French",
        "FRA" to "French",
        "FRENCH" to "French",
        "FRANCAIS" to "French",
        "FRAN?AIS" to "French",
        "BE" to "French",
        "BEL" to "French",

        // German
        "DE" to "German",
        "GER" to "German",
        "DEU" to "German",
        "GERMAN" to "German",
        "DEUTSCH" to "German",
        "AT" to "German",
        "AUT" to "German",

        // Italian
        "IT" to "Italian",
        "ITA" to "Italian",
        "ITALIAN" to "Italian",
        "ITALIANO" to "Italian",

        // Portuguese
        "PT" to "Portuguese",
        "POR" to "Portuguese",
        "PRT" to "Portuguese",
        "BR" to "Portuguese",
        "BRA" to "Portuguese",
        "PORTUGUESE" to "Portuguese",
        "PORTUGU?S" to "Portuguese",
        "PORTUGUES" to "Portuguese",

        // Arabic
        "AR" to "Arabic",
        "ARA" to "Arabic",
        "ARABIC" to "Arabic",
        "ARABE" to "Arabic",
        "SA" to "Arabic",
        "SAU" to "Arabic",
        "EG" to "Arabic",
        "EGY" to "Arabic",
        "AE" to "Arabic",
        "ARE" to "Arabic",
        "QA" to "Arabic",
        "QAT" to "Arabic",
        "KW" to "Arabic",
        "KWT" to "Arabic",
        "DZ" to "Arabic",
        "DZA" to "Arabic",
        "MA" to "Arabic",
        "MAR" to "Arabic",
        "IQ" to "Arabic",
        "IRQ" to "Arabic",

        // Hindi & South Asian
        "HI" to "Hindi",
        "HIN" to "Hindi",
        "HINDI" to "Hindi",
        "IN" to "Hindi",
        "IND" to "Hindi",
        "TA" to "Tamil",
        "TAM" to "Tamil",
        "TAMIL" to "Tamil",
        "TE" to "Telugu",
        "TEL" to "Telugu",
        "TELUGU" to "Telugu",
        "ML" to "Malayalam",
        "MAL" to "Malayalam",
        "MALAYALAM" to "Malayalam",
        "KN" to "Kannada",
        "KAN" to "Kannada",
        "KANNADA" to "Kannada",
        "BN" to "Bengali",
        "BEN" to "Bengali",
        "BENGALI" to "Bengali",
        "BD" to "Bengali",
        "BGD" to "Bengali",
        "PA" to "Punjabi",
        "PUN" to "Punjabi",
        "PUNJABI" to "Punjabi",
        "MR" to "Marathi",
        "MAR" to "Marathi",
        "MARATHI" to "Marathi",
        "GU" to "Gujarati",
        "GUJ" to "Gujarati",
        "GUJARATI" to "Gujarati",
        "UR" to "Urdu",
        "URD" to "Urdu",
        "URDU" to "Urdu",
        "PK" to "Urdu",
        "PAK" to "Urdu",

        // Russian & Eastern Europe
        "RU" to "Russian",
        "RUS" to "Russian",
        "RUSSIAN" to "Russian",
        "???????" to "Russian",
        "UKR" to "Ukrainian",
        "UA" to "Ukrainian",
        "UKRAINIAN" to "Ukrainian",
        "PL" to "Polish",
        "POL" to "Polish",
        "POLISH" to "Polish",
        "POLSKI" to "Polish",
        "CS" to "Czech",
        "CZE" to "Czech",
        "CZECH" to "Czech",
        "CZ" to "Czech",
        "RO" to "Romanian",
        "RON" to "Romanian",
        "ROU" to "Romanian",
        "ROMANIAN" to "Romanian",
        "HU" to "Hungarian",
        "HUN" to "Hungarian",
        "HUNGARIAN" to "Hungarian",
        "BG" to "Bulgarian",
        "BUL" to "Bulgarian",
        "BGR" to "Bulgarian",

        // Turkish
        "TR" to "Turkish",
        "TUR" to "Turkish",
        "TURKISH" to "Turkish",
        "TURKCE" to "Turkish",
        "T?RK?E" to "Turkish",

        // Dutch
        "NL" to "Dutch",
        "NLD" to "Dutch",
        "DUTCH" to "Dutch",
        "NEDERLANDS" to "Dutch",

        // Nordic
        "SV" to "Swedish",
        "SWE" to "Swedish",
        "SE" to "Swedish",
        "SWEDISH" to "Swedish",
        "NO" to "Norwegian",
        "NOR" to "Norwegian",
        "NORWEGIAN" to "Norwegian",
        "DA" to "Danish",
        "DAN" to "Danish",
        "DNK" to "Danish",
        "DK" to "Danish",
        "DANISH" to "Danish",
        "FI" to "Finnish",
        "FIN" to "Finnish",
        "FINNISH" to "Finnish",

        // Greek
        "EL" to "Greek",
        "GRE" to "Greek",
        "GRC" to "Greek",
        "GR" to "Greek",
        "GREEK" to "Greek",

        // Asian
        "JA" to "Japanese",
        "JPN" to "Japanese",
        "JP" to "Japanese",
        "JAPANESE" to "Japanese",
        "KO" to "Korean",
        "KOR" to "Korean",
        "KR" to "Korean",
        "KOREAN" to "Korean",
        "ZH" to "Chinese",
        "CHI" to "Chinese",
        "ZHO" to "Chinese",
        "CN" to "Chinese",
        "CHN" to "Chinese",
        "TW" to "Chinese",
        "TWN" to "Chinese",
        "HK" to "Chinese",
        "HKG" to "Chinese",
        "CHINESE" to "Chinese",
        "VI" to "Vietnamese",
        "VIE" to "Vietnamese",
        "VN" to "Vietnamese",
        "VNM" to "Vietnamese",
        "VIETNAMESE" to "Vietnamese",
        "TH" to "Thai",
        "THA" to "Thai",
        "THAI" to "Thai",
        "ID" to "Indonesian",
        "IDN" to "Indonesian",
        "INDONESIAN" to "Indonesian",
        "MS" to "Malay",
        "MSA" to "Malay",
        "MY" to "Malay",
        "MYS" to "Malay",
        "MALAY" to "Malay",

        // Middle Eastern
        "FA" to "Persian",
        "PER" to "Persian",
        "FAS" to "Persian",
        "IR" to "Persian",
        "IRN" to "Persian",
        "PERSIAN" to "Persian",
        "FARSI" to "Persian",
        "HE" to "Hebrew",
        "HEB" to "Hebrew",
        "ISR" to "Hebrew",
        "IL" to "Hebrew",
        "HEBREW" to "Hebrew",
    )

    val COMMON_LANGUAGES: List<String> = listOf(
        "Arabic",
        "Bengali",
        "Chinese",
        "Czech",
        "Danish",
        "Dutch",
        "English",
        "Finnish",
        "French",
        "German",
        "Greek",
        "Gujarati",
        "Hebrew",
        "Hindi",
        "Hungarian",
        "Indonesian",
        "Italian",
        "Japanese",
        "Kannada",
        "Korean",
        "Malay",
        "Malayalam",
        "Marathi",
        "Norwegian",
        "Persian",
        "Polish",
        "Portuguese",
        "Punjabi",
        "Romanian",
        "Russian",
        "Spanish",
        "Swedish",
        "Tamil",
        "Telugu",
        "Thai",
        "Turkish",
        "Ukrainian",
        "Urdu",
        "Vietnamese",
    )

    private val BRACKET_TAG_REGEX = Regex("[\[(\|]([A-Za-z]{2,10})[\])\|]")
    private val EPG_COUNTRY_REGEX = Regex("[\._-]([A-Za-z]{2,3})(@|$)")

    fun parseToken(token: String): String? {
        val clean = token.trim().uppercase()
        return ALIAS_TO_LANGUAGE[clean]
    }

    /**
     * Detects language from category name (first priority), item title, or epgChannelId (tvg-id).
     */
    fun detectLanguage(
        categoryName: String?,
        itemName: String?,
        epgChannelId: String? = null,
    ): String? {
        if (!categoryName.isNullOrBlank()) {
            val fromCat = detectFromText(categoryName)
            if (fromCat != null) return fromCat
        }

        if (!itemName.isNullOrBlank()) {
            val fromItem = detectFromText(itemName)
            if (fromItem != null) return fromItem
        }

        if (!epgChannelId.isNullOrBlank()) {
            val fromEpg = detectFromEpgId(epgChannelId)
            if (fromEpg != null) return fromEpg
        }

        return null
    }

    private fun detectFromText(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null

        // 1. Check for delimiter-separated prefix (e.g. "EN | Movies", "UK: News", "FR - Cinema")
        for (delim in PREFIX_DELIMITERS) {
            val idx = trimmed.indexOf(delim)
            if (idx in 1..12) {
                val candidate = trimmed.substring(0, idx).trim()
                parseToken(candidate)?.let { return it }
            }
        }

        // 2. Check for bracketed tags like [EN], (ES), [Hindi]
        val match = BRACKET_TAG_REGEX.find(trimmed)
        if (match != null) {
            val tag = match.groupValues[1]
            parseToken(tag)?.let { return it }
        }

        // 3. Check words
        val words = trimmed.split(Regex("[\s|:\-/()\[\],]+"))
        for (word in words) {
            if (word.length >= 2) {
                parseToken(word)?.let { return it }
            }
        }

        return null
    }

    private fun detectFromEpgId(epgId: String): String? {
        val trimmed = epgId.trim()
        val match = EPG_COUNTRY_REGEX.find(trimmed)
        if (match != null) {
            val code = match.groupValues[1]
            parseToken(code)?.let { return it }
        }
        return null
    }

    /**
     * Returns true if the item matches the given target language (or true if targetLanguage is null).
     */
    fun matchesLanguage(
        categoryName: String?,
        itemName: String?,
        epgChannelId: String? = null,
        targetLanguage: String?,
    ): Boolean {
        if (targetLanguage == null) return true
        val detected = detectLanguage(categoryName, itemName, epgChannelId) ?: return false
        return detected.equals(targetLanguage, ignoreCase = true)
    }

    fun matchesLanguage(
        categoryName: String?,
        itemName: String?,
        targetLanguage: String?,
    ): Boolean = matchesLanguage(categoryName, itemName, null, targetLanguage)

    /**
     * Extracts all unique languages from a collection of category names.
     * If no language tags are found in category names (e.g. genre-only playlists),
     * falls back to common languages so the user can still filter channels by language.
     */
    fun extractAvailableLanguages(categoryNames: Collection<String>): List<String> {
        val fromCategories = categoryNames
            .mapNotNull { detectLanguage(it, null) }
            .distinct()
            .sorted()
        return if (fromCategories.isNotEmpty()) fromCategories else COMMON_LANGUAGES
    }
}
