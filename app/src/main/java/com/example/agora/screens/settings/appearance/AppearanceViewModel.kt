import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode(val value: Int) {
    SYSTEM(0), LIGHT(1), DARK(2);

    companion object {
        fun fromValue(value: Int): ThemeMode = values().firstOrNull { it.value == value } ?: SYSTEM
    }
}

class AppearanceViewModel(context: Context) : ViewModel() {
    private val dataStore = context.dataStore
    private val themeKey = intPreferencesKey("theme_mode")

    private val _text = MutableLiveData<String>().apply {
        value = "Appearance"
    }
    val text: LiveData<String> = _text
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    init {
        loadTheme()
    }

    private fun loadTheme() {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                ThemeMode.fromValue(preferences[themeKey] ?: ThemeMode.SYSTEM.value)
            }.collect { theme ->
                _themeMode.value = theme
            }
        }
    }

    fun setThemeMode(theme: ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[themeKey] = theme.value
            }
        }
    }
}
