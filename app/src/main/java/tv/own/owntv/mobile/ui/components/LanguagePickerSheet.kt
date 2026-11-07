package tv.own.owntv.mobile.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tv.own.owntv.mobile.R
import tv.own.owntv.mobile.ui.stage.StageMenuItem
import tv.own.owntv.mobile.ui.stage.StageSearchField
import tv.own.owntv.mobile.ui.stage.StageSheetHeader

/**
 * Bottom sheet to pick an M3U-detected language for Live TV or Library (Movies / Series).
 */
@Composable
fun LanguagePickerSheet(
    languages: List<String>,
    selectedLanguage: String?,
    onSelectLanguage: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val matches = remember(languages, query) {
        if (query.isBlank()) languages
        else languages.filter { it.contains(query.trim(), ignoreCase = true) }
    }
    val title = stringResource(R.string.settings_language)
    MobileBottomSheet(onDismissRequest = onDismiss, title = title) {
        StageSheetHeader(
            title = title,
            subtitle = stringResource(R.string.content_sort_applies_hint),
        )
        if (languages.size > 8) {
            StageSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.common_search),
                modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 8.dp),
            )
        }
        LazyColumn(Modifier.heightIn(max = sheetListHeight())) {
            if (query.isBlank()) {
                item(key = -1) {
                    val current = selectedLanguage == null
                    StageMenuItem(
                        label = stringResource(R.string.sports_any),
                        icon = MobileIcons.Translate,
                        onClick = {
                            onSelectLanguage(null)
                            onDismiss()
                        },
                        filled = current,
                        checked = current,
                    )
                }
            }
            itemsIndexed(matches, key = { _, lang -> lang }) { _, lang ->
                val current = selectedLanguage != null && selectedLanguage.equals(lang, ignoreCase = true)
                StageMenuItem(
                    label = lang,
                    icon = MobileIcons.Translate,
                    onClick = {
                        onSelectLanguage(lang)
                        onDismiss()
                    },
                    filled = current,
                    checked = current,
                )
            }
        }
    }
}
