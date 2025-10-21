Volkszähler App - Architektur-Dokumentation

Überblick

Die Volkszähler Android App folgt den Android Architecture Best Practices und implementiert eine Clean Architecture mit klarer Trennung der Verantwortlichkeiten.

Architektur-Muster

MVVM (Model-View-ViewModel)

┌─────────────────────────────────────────────────┐
│                      UI Layer                          │
│  ┌───────────────────────────────────────────┐  │
│  │          Composable Screens              │  │
│  │   (ChannelListScreen, ChartScreen)     │  │
│  └─────────────────────┬────────────────────┘  │
│                         │ observes                     │
│  ┌────────────────────┴────────────────────┐  │
│  │             ViewModels                 │  │
│  │  (ChannelListViewModel, ChartViewModel) │  │
│  └───────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────┘
│ uses
┌───────────────────────┴─────────────────────────┐
│                   Domain Layer                       │
│  ┌───────────────────────────────────────────┐  │
│  │           Repository                  │  │
│  │    (VolkszaehlerRepository)           │  │
│  └───────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────┘
│ coordinates
┌───────────────────────┴─────────────────────────┐
│                    Data Layer                        │
│  ┌────────────────────┐  ┌────────────────────┐  │
│  │   Remote Data      │  │   Local Data       │  │
│  │   (API Service)    │  │   (Room DB)        │  │
│  └────────────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────┘


Layer-Beschreibung

UI Layer (Presentation)
Verantwortlichkeit Darstellung der Benutzeroberfläche und Interaktion

Komponenten:
Screens (Composables)
ChannelListScreen.kt: Zeigt Liste aller Kanäle
ChartScreen.kt: Visualisiert Messdaten als Diagramm
ViewModels
ChannelListViewModel.kt: Verwaltet UI-State für Kanal-Liste
ChartViewModel.kt: Verwaltet UI-State für Diagramm
Navigation
Navigation.kt: Definiert App-Navigation mit Navigation Compose
Theme
Theme.kt: Material Design 3 Theme-Konfiguration
Type.kt: Typografie-Definitionen

Datenfluss:

// Screen observiert ViewModel State
@Composable
fun ChannelListScreen(viewModel: ChannelListViewModel) {
val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingIndicator()
        uiState.error != null -> ErrorMessage()
        else -> ChannelList(uiState.channels)
    }
}

// ViewModel exponiert UI State als Flow
class ChannelListViewModel : ViewModel() {
private val \_uiState = MutableStateFlow(ChannelListUiState())
val uiState: StateFlow<ChannelListUiState> = \_uiState.asStateFlow()
}


Domain Layer (Business Logic)
Verantwortlichkeit Geschäftslogik und Datenkoordination

Komponenten:
Repository
VolkszaehlerRepository.kt: Zentrale Datenquelle, koordiniert Remote und Local Data
Models
Channel.kt: Kanal-Datenmodell
ChannelData.kt: Messdaten-Modell
DataTuple.kt: Einzelner Messpunkt

Repository Pattern:

class VolkszaehlerRepository @Inject constructor(
private val apiService: VolkszaehlerApiService,
private val channelDao: ChannelDao
) {
// Single Source of Truth
fun getChannels(): Flow<NetworkResult<List<Channel>>> = flow {
emit(NetworkResult.Loading())

        // 1. Emit cached data first
        val cachedChannels = channelDao.getAllChannels().first()
        if (cachedChannels.isNotEmpty()) {
            emit(NetworkResult.Success(cachedChannels))
        }
        
        // 2. Fetch from network
        try {
            val response = apiService.getChannels()
            if (response.isSuccessful) {
                response.body()?.let { channels ->
                    channelDao.insertAll(channels)
                    emit(NetworkResult.Success(channels))
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message))
        }
    }
}


Data Layer
Verantwortlichkeit Datenbeschaffung und -speicherung

Remote Data Source
API Service
interface VolkszaehlerApiService {
@GET("entity.json")
suspend fun getChannels(): Response<List<Channel>>

    @GET("data/{uuid}.json")
    suspend fun getChannelData(
        @Path("uuid") uuid: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): Response<ChannelData>
}

Technologien
Retrofit: REST API Client
OkHttp: HTTP Client mit Interceptors
Moshi: JSON Serialisierung

Local Data Source
Room Database
@Database(
entities = [Channel::class],
version = 1,
exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
abstract fun channelDao(): ChannelDao
}

@Dao
interface ChannelDao {
@Query("SELECT * FROM channels")
fun getAllChannels(): Flow<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(channels: List<Channel>)
}


Dependency Injection (Hilt)

Module-Struktur

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://demo.volkszaehler.org/middleware.php/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): VolkszaehlerApiService {
        return retrofit.create(VolkszaehlerApiService::class.java)
    }
}


Dependency Graph

VolkszaehlerApplication (@HiltAndroidApp)
│
├── MainActivity (@AndroidEntryPoint)
│       │
│       ├── ChannelListViewModel (@HiltViewModel)
│       │       └── VolkszaehlerRepository
│       │               ├── VolkszaehlerApiService
│       │               └── ChannelDao
│       │
│       └── ChartViewModel (@HiltViewModel)
│               └── VolkszaehlerRepository
│
├── NetworkModule (provides API, OkHttp, Retrofit)
└── DatabaseModule (provides Room DB, DAO)


State Management

UI State Pattern

data class ChannelListUiState(
val channels: List<Channel> = emptyList(),
val isLoading: Boolean = false,
val error: String? = null,
val searchQuery: String = ""
)

class ChannelListViewModel @Inject constructor(
private val repository: VolkszaehlerRepository
) : ViewModel() {

    private val \_uiState = MutableStateFlow(ChannelListUiState())
    val uiState: StateFlow<ChannelListUiState> = \_uiState.asStateFlow()
    
    init {
        loadChannels()
    }
    
    fun loadChannels() {
        viewModelScope.launch {
            repository.getChannels().collect { result ->
                \_uiState.update { currentState ->
                    when (result) {
                        is NetworkResult.Loading -> currentState.copy(isLoading = true)
                        is NetworkResult.Success -> currentState.copy(
                            channels = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                        is NetworkResult.Error -> currentState.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}


Reactive Data Flow

User Action → ViewModel → Repository → Data Source
↑                      ↓
└───── Flow ←─────────┘
│
↓
UI State Update
│
↓
Recomposition


Error Handling

NetworkResult Sealed Class

sealed class NetworkResult<T>(
val data: T? = null,
val message: String? = null
) {
class Success<T>(data: T) : NetworkResult<T>(data)
class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)
class Loading<T> : NetworkResult<T>()
}


Error Propagation

// Repository Level
try {
val response = apiService.getChannels()
if (response.isSuccessful) {
emit(NetworkResult.Success(response.body()))
} else {
emit(NetworkResult.Error("HTTP ${response.code()}"))
}
} catch (e: IOException) {
emit(NetworkResult.Error("Network error: ${e.message}"))
} catch (e: Exception) {
emit(NetworkResult.Error("Unknown error: ${e.message}"))
}

// ViewModel Level
repository.getChannels().collect { result ->
when (result) {
is NetworkResult.Error -> \_uiState.update {
it.copy(error = result.message)
}
}
}

// UI Level
if (uiState.error != null) {
ErrorMessage(
message = uiState.error,
onRetry = { viewModel.loadChannels() }
)
}


Threading & Coroutines

Dispatcher Strategy

// IO Operations (Network, Database)
viewModelScope.launch(Dispatchers.IO) {
val data = apiService.getChannels()
}

// Main Thread (UI Updates)
withContext(Dispatchers.Main) {
\_uiState.value = newState
}

// Default (CPU-intensive)
withContext(Dispatchers.Default) {
val processed = heavyComputation(data)
}


Flow Operators

repository.getChannels()
.flowOn(Dispatchers.IO)  // Upstream on IO
.catch { e -> emit(NetworkResult.Error(e.message)) }
.onStart { emit(NetworkResult.Loading()) }
.collect { result -> / Handle result / }


Testing Strategy

Unit Tests

@Test
fun loadChannels emits success state() = runTest {
// Given
val mockChannels = listOf(Channel(uuid = "123", title = "Test"))
coEvery { repository.getChannels() } returns flowOf(
NetworkResult.Success(mockChannels)
)

    // When
    viewModel.loadChannels()
    
    // Then
    assertEquals(mockChannels, viewModel.uiState.value.channels)
    assertFalse(viewModel.uiState.value.isLoading)
}


Integration Tests

@Test
fun repository fetches from API and caches in DB() = runTest {
// Test full data flow from API to DB
}


Performance Optimizations

Lazy Loading
// Only load data when needed
LazyColumn {
items(channels) { channel ->
ChannelItem(channel)
}
}


Caching Strategy
// Cache-first approach
val cachedData = dao.getData().first()
if (cachedData.isNotEmpty()) {
emit(NetworkResult.Success(cachedData))
}
// Then fetch fresh data


Debouncing Search
val searchQuery = MutableStateFlow("")
searchQuery
.debounce(300)
.distinctUntilChanged()
.collectLatest { query ->
performSearch(query)
}


Security Considerations

Network Security
<!-- network\security\config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">volkszaehler.org</domain>
    </domain-config>
</network-security-config>


Data Encryption
// Use encrypted SharedPreferences
EncryptedSharedPreferences.create(
context,
"secure\_prefs",
masterKey,
EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256\_SIV,
EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256\_GCM
)


Scalability

Die Architektur ist darauf ausgelegt, einfach erweiterbar zu sein:

Neue Features: Einfach neue Screens/ViewModels hinzufügen
Neue Datenquellen: Repository-Pattern ermöglicht einfache Integration
Neue APIs: Retrofit-Interface erweitern
Offline-First: Room DB bietet vollständigen Offline-Support

Best Practices

Single Responsibility: Jede Klasse hat eine klare Verantwortung
Dependency Inversion: Abhängigkeiten zeigen nach innen
Immutable State: UI State ist immutable
Reactive Streams: Flow für reaktive Daten
Error Handling: Konsistente Fehlerbehandlung auf allen Ebenen   