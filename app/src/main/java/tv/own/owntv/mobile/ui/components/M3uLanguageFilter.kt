package tv.own.owntv.mobile.ui.components

/**
 * Utility to detect and filter content (Live Channels, Movies, Series, and Categories)
 * based on M3U language and country tags.
 *
 * M3U playlists commonly format tags in category names (group-title) or stream names:
 * - Prefixes: "EN | Movies", "UK: BBC One", "FR - Cinema", "DE | Sky"
 * - Bracketed tags: "[EN]", "(US)", "|ES|", "[Hindi]"
 * - Suffixes: "Movie Title (2024) [EN]"
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
        "ENGLISH" to "English",

        // Spanish
        "ES" to "Spanish",
        "ESP" to "Spanish",
        "SPA" to "Spanish",
        "SPANISH" to "Spanish",
        "ESPANOL" to "Spanish",
        "ESPAÑOL" to "Spanish",

        // French
        "FR" to "French",
        "FRA" to "French",
        "FRENCH" to "French",
        "FRANCAIS" to "French",
        "FRANÇAIS" to "French",

        // German
        "DE" to "German",
        "GER" to "German",
        "DEU" to "German",
        "GERMAN" to "German",
        "DEUTSCH" to "German",

        // Italian
        "IT" to "Italian",
        "ITA" to "Italian",
        "ITALIAN" to "Italian",
        "ITALIANO" to "Italian",

        // Portuguese
        "PT" to "Portuguese",
        "POR" to "Portuguese",
        "BR" to "Portuguese",
        "BRA" to "Portuguese",
        "PORTUGUESE" to "Portuguese",
        "PORTUGUÊS" to "Portuguese",
        "PORTUGUES" to "Portuguese",

        // Arabic
        "AR" to "Arabic",
        "ARA" to "Arabic",
        "ARABIC" to "Arabic",
        "ARABE" to "Arabic",

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

        // Russian & Eastern Europe
        "RU" to "Russian",
        "RUS" to "Russian",
        "RUSSIAN" to "Russian",
        "РУССКИЙ" to "Russian",
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
        "RO" to "Romanian",
        "RON" to "Romanian",
        "ROMANIAN" to "Romanian",
        "HU" to "Hungarian",
        "HUN" to "Hungarian",
        "HUNGARIAN" to "Hungarian",
        "BG" to "Bulgarian",
        "BUL" to "Bulgarian",

        // Turkish
        "TR" to "Turkish",
        "TUR" to "Turkish",
        "TURKISH" to "Turkish",
        "TURKCE" to "Turkish",
        "TÜRKÇE" to "Turkish",

        // Dutch
        "NL" to "Dutch",
        "NLD" to "Dutch",
        "DUTCH" to "Dutch",
        "NEDERLANDS" to "Dutch",

        // Nordic
        "SV" to "Swedish",
        "SWE" to "Swedish",
        "SWEDISH" to "Swedish",
        "NO" to "Norwegian",
        "NOR" to "Norwegian",
        "NORWEGIAN" to "Norwegian",
        "DA" to "Danish",
        "DAN" to "Danish",
        "DANISH" to "Danish",
        "FI" to "Finnish",
        "FIN" to "Finnish",
        "FINNISH" to "Finnish",

        // Greek
        "EL" to "Greek",
        "GRE" to "Greek",
        "GRC" to "Greek",
        "GREEK" to "Greek",

        // Asian
        "JA" to "Japanese",
        "JPN" to "Japanese",
        "JAPANESE" to "Japanese",
        "KO" to "Korean",
        "KOR" to "Korean",
        "KOREAN" to "Korean",
        "ZH" to "Chinese",
        "CHI" to "Chinese",
        "ZHO" to "Chinese",
        "CN" to "Chinese",
        "CHINESE" to "Chinese",
        "VI" to "Vietnamese",
        "VIE" to "Vietnamese",
        "VIETNAMESE" to "Vietnamese",
        "TH" to "Thai",
        "THA" to "Thai",
        "THAI" to "Thai",
        "ID" to "Indonesian",
        "INDONESIAN" to "Indonesian",
        "MS" to "Malay",
        "MSA" to "Malay",
        "MALAY" to "Malay",

        // Middle Eastern
        "FA" to "Persian",
        "PER" to "Persian",
        "FAS" to "Persian",
        "IR" to "Persian",
        "PERSIAN" to "Persian",
        "FARSI" to "Persian",
        "HE" to "Hebrew",
        "HEB" to "Hebrew",
        "ISR" to "Hebrew",
        "IL" to "Hebrew",
        "HEBREW" to "Hebrew",
    )

    private val BRACKET_TAG_REGEX = Regex("[\\[(\\|]([A-Za-z]{2,10})[\\])\\|]")

    fun parseToken(token: String): String? {
        val clean = token.trim().uppercase()
        return ALIAS_TO_LANGUAGE[clean]
    }

    /**
     * Detects language from category name (first priority) or item title.
     */
    fun detectLanguage(categoryName: String?, itemName: String?): String? {
        if (!categoryName.isNullOrBlank()) {
            val fromCat = detectFromText(categoryName)
            if (fromCat != null) return fromCat
        }

        if (!itemName.isNullOrBlank()) {
            val fromItem = detectFromText(itemName)
            if (fromItem != null) return fromItem
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
        val words = trimmed.split(Regex("[\\s|:\\-/()\\[\\],]+"))
        for (word in words) {
            if (word.length >= 2) {
                parseToken(word)?.let { return it }
            }
        }

        return null
    }

    /**
     * Returns true if the item matches the given target language (or true if targetLanguage is null).
     */
    fun matchesLanguage(categoryName: String?, itemName: String?, targetLanguage: String?): Boolean {
        if (targetLanguage == null) return true
        val detected = detectLanguage(categoryName, itemName) ?: return false
        return detected.equals(targetLanguage, ignoreCase = true)
    }

    /**
     * Extracts all unique languages from a collection of category names.
     */
    fun extractAvailableLanguages(categoryNames: Collection<String>): List<String> {
        return categoryNames
            .mapNotNull { detectLanguage(it, null) }
            .distinct()
            .sorted()
    }
}
