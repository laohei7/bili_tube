package com.laohei.bili_tube.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_tube.core.KeywordPattern
import com.laohei.bili_tube.features.search.data.repository.BiliSearchRepository
import com.laohei.bili_tube.features.search.data.repository.SearchHistoryRepository
import com.laohei.bili_tube.model.SearchResults
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.room.entity.SearchHistory
import com.laohei.bili_tube.utill.withRefreshing
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val biliSearchRepository: BiliSearchRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    companion object {
        private val TAG = SearchViewModel::class.simpleName
        private const val DBG = true
    }

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _uiState.value
    )

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            val historyPager = searchHistoryRepository.searchHistoryPager()
            _uiState.update { it.copy(searchHistories = historyPager.cachedIn(viewModelScope)) }
        }
    }

    private var _lastestKeyword: String = ""

    fun onKeywordChange(value: String) {
        _uiState.update { it.copy(keyword = value, expanded = true) }
    }

    fun onExpandedChange(value: Boolean) {
        _uiState.update { it.copy(expanded = value) }
    }

    fun onSearch() {
        viewModelScope.launch {
            val keyword = _uiState.value.keyword
            if (_lastestKeyword == keyword) {
                withRefreshing {
                    _uiState.update { it.copy(isSearching = true, expanded = false) }
                }
                _uiState.update { it.copy(isSearching = false) }
                return@launch
            }
            _lastestKeyword = keyword
            val historyItem = SearchHistory(keyword = keyword)
            searchHistoryRepository.addSearchHistoryItem(historyItem)
            _uiState.update { it.copy(isSearching = true, expanded = false) }
            withRefreshing {
                val results = coroutineScope {
                    val all =
                        async {
                            getSearchResultPager(
                                keyword,
                                SearchRequest.Companion.SearchType.All
                            )
                        }
                    val videos =
                        async {
                            getSearchResultPager(
                                keyword,
                                SearchRequest.Companion.SearchType.Video
                            )
                        }
                    val bangumis =
                        async {
                            getSearchResultPager(
                                keyword,
                                SearchRequest.Companion.SearchType.Bangumi
                            )
                        }
                    val movies =
                        async {
                            getSearchResultPager(
                                keyword,
                                SearchRequest.Companion.SearchType.FT
                            )
                        }

                    SearchResults(
                        all = all.await(),
                        videos = videos.await(),
                        bangumis = bangumis.await(),
                        movies = movies.await()
                    )
                }
                _uiState.update {
                    it.copy(
                        results = results.all,
                        videos = results.videos,
                        bangumis = results.bangumis,
                        fts = results.movies,
                    )
                }
            }
            _uiState.update { it.copy(isSearching = false) }
        }
    }

    private fun getSearchResultPager(
        keyword: String,
        type: SearchRequest.Companion.SearchType
    ): Flow<PagingData<UIModel<out Any?>>> {
        return biliSearchRepository.onSearch(keyword, type)
            .map { pagingData ->
                pagingData
                    .filter { (it is SearchResultItemType.UnknownItem).not() }
                    .map {
                        when (it) {
                            is SearchResultItemType.MediaBangumiItem -> {
                                it.copy(title = it.title.replaceKeyword())
                            }

                            is SearchResultItemType.MediaFTItem -> {
                                it.copy(title = it.title.replaceKeyword())
                            }

                            is SearchResultItemType.VideoItem -> {
                                it.copy(title = it.title.replaceKeyword())
                            }

                            else -> it
                        }
                    }
                    .map { UIModel.Item(it) }
                    .insertSeparators { before, after ->
                        val beforeType = before?.item?.getType()
                        val afterType = after?.item?.getType()
                        if (type == SearchRequest.Companion.SearchType.All) {
                            return@insertSeparators when {
                                beforeType == null
                                        && afterType != null -> UIModel.Header(afterType)

                                beforeType != afterType -> UIModel.Header(afterType)

                                else -> null
                            }
                        } else {
                            return@insertSeparators null
                        }

                    }
            }.cachedIn(viewModelScope)
    }

    private fun String.replaceKeyword(): String {
        val match = KeywordPattern.find(this)
        return match?.groupValues?.get(1)?.let {
            this.replace(KeywordPattern, it)
        } ?: this
    }
}