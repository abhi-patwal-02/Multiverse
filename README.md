# Multiverse Explorer

## Architecture Overview

The application follows the MVVM architecture with a clear separation between UI, presentation logic, and data access.

### Architecture Flow

    Jetpack Compose UI
            ↓
        ViewModel
            ↓
        Repository
            ↓
        PagingSource
            ↓
        Retrofit
            ↓
    Rick and Morty API

### Main Components

- **UI:** Jetpack Compose screens for character listing and character details.
- **ViewModel:** Manages search, filters, UI state, and coordinates data loading.
- **Repository:** Provides a clean interface between the ViewModel and data sources.
- **PagingSource:** Handles paginated character loading using Paging 3.
- **Retrofit API:** Handles communication with the Rick and Morty API.
- **DTOs:** Represent API response models.
- **Domain Models:** Represent application-level data used by the UI.
- **Mappers:** Convert API DTOs into domain models.
- **Manual DI:** Provides dependencies such as the API and repository.

## Key Decisions and Trade-offs

### MVVM

MVVM was chosen to separate UI code from application and data-loading logic. This keeps the Compose screens focused on displaying state and handling user interactions while the ViewModels manage application state.

### Paging 3

Paging 3 was used for infinite scrolling instead of implementing a custom pagination system.

It provides built-in handling for:

- Page loading
- Refresh
- Append loading
- Retry
- Loading and error states
- Integration with Jetpack Compose

### Search and Filters

Search and filters are combined into a single filter state.

Search input uses a **500 ms debounce** so that typing quickly does not trigger a network request for every keystroke.

`distinctUntilChanged()` prevents unnecessary requests when the effective filter combination has not changed.

`flatMapLatest()` ensures that when the active search or filter combination changes, the previous paging flow is no longer used.

### Paging Flow Cache

Paging flows are cached using the active search and filter combination as the cache key.

This allows previously used filter combinations to reuse their existing paging flow instead of unnecessarily creating another one.

### Episode Fetching

Character responses contain episode URLs instead of complete episode information.

The application extracts episode IDs from these URLs and uses the API's multiple-episode endpoint to fetch multiple episodes in a single request where possible.

This reduces unnecessary network requests.

### Manual Dependency Injection

Manual dependency injection was chosen because the application has a relatively small dependency graph.

The API and repository are provided through a simple application container without introducing an additional dependency-injection framework.

## What I Would Do With More Time

- Add more automated tests for refresh, filtering, pagination, and error states.
- Handle `Retry-After` information for API rate-limit responses.
- Improve loading, empty, and error-state UI.
- Improve image loading with additional placeholders and failure handling.
- Add persistent local caching for better offline support.
- Further improve accessibility and UI polish.

## Time Spent

Approximately **12 hours** were spent on the assignment, including implementation, UI development, testing, debugging, and documentation.

## Testing

### What We Tested

The test suite focuses on the core logic that controls networking, pagination, filtering, search behaviour, and data transformation.

#### CharacterPagingSource

`CharacterPagingSource` is tested using **MockWebServer** and the real Retrofit API interface. MockWebServer allows us to simulate API responses without depending on the live Rick and Morty API.

We test:

- Loading the first page and verifying that characters are returned and the correct next page is provided.
- Loading the final page and verifying that pagination correctly stops when there is no next page.
- Handling HTTP errors and returning a `LoadResult.Error`.
- Verifying that search, status, and gender filters are correctly sent as API query parameters.
- Verifying that the correct page number is requested during pagination.

#### CharacterListViewModel

The ViewModel is tested using a **fake repository** and Kotlin's coroutine testing utilities.

`StandardTestDispatcher` is used to control coroutine execution and virtual time, allowing the debounce behaviour to be tested deterministically.

We test:

- Rapid search input is debounced so that intermediate values do not immediately trigger repository requests.
- Entering the same effective search query does not create another request because of `distinctUntilChanged()`.
- Previously used search/filter combinations reuse the cached paging flow.
- Search, status, and gender filters are passed together to the repository.
- Different search and filter combinations correctly create/use their corresponding paging flows.

#### CharacterRepository

The repository is tested using **MockWebServer** to verify the actual Retrofit request and response mapping.

We test:

- Multiple episode IDs are combined into a single batch API request.
- The returned episode DTOs are correctly mapped into `EpisodeModel` objects.

### What We Decided Not to Test

#### Compose UI Tests

We did not add Compose UI tests. The UI was manually verified for the required flows, including search, filtering, pagination, loading states, empty results, error states, retry, pull-to-refresh, and navigation to the detail screen.

The core behaviour behind these interactions is covered through ViewModel, PagingSource, and Repository tests.

#### End-to-End Tests

We did not add full end-to-end tests against the live Rick and Morty API. Such tests would depend on an external network service and could become unreliable because of network availability or API rate limits.

Instead, network behaviour is tested in isolation using MockWebServer.

#### Live API Integration Tests

We did not make automated tests depend on the real API. MockWebServer provides deterministic responses, making the tests faster and reliable when run on a clean checkout.