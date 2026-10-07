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