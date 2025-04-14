package com.laohei.bili_tube.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.laohei.bili_sdk.module_v2.search.SearchResultItemType
import com.laohei.bili_sdk.search.SearchRequest
import com.laohei.bili_tube.model.UIModel
import com.laohei.bili_tube.repository.BiliSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val KeywordPattern = "<em class=\"keyword\">(.*?)</em>".toRegex()


class SearchViewModel(
    private val biliSearchRepository: BiliSearchRepository
) : ViewModel() {

    companion object {
        private val TAG = SearchViewModel::class.simpleName
        private const val DBG = true
    }

    private val _mState = MutableStateFlow(SearchState())
    val state = _mState.onStart {
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _mState.value
        )

    private var mLastKeyword: String = ""

//    var results: Flow<PagingData<UIModel<out Any?>>> =
//        getSearchResultPager(SearchRequest.Companion.SearchType.None)
//        private set

    fun onKeywordChanged(value: String) {
        _mState.update { it.copy(keyword = value) }
    }

    fun onExpandedChanged(value: Boolean) {
        _mState.update { it.copy(expanded = value) }
    }

    fun onSearch() {
        if (mLastKeyword == _mState.value.keyword) {
            return
        }
        mLastKeyword = _mState.value.keyword
        viewModelScope.launch {
            _mState.update { it.copy(isSearching = true) }
            launch {
                val results = getSearchResultPager(SearchRequest.Companion.SearchType.All)
                _mState.update { it.copy(results = results) }
            }
            launch {
                val videos = getSearchResultPager(SearchRequest.Companion.SearchType.Video)
                _mState.update { it.copy(videos = videos) }
            }
            launch {
                val bangumis = getSearchResultPager(SearchRequest.Companion.SearchType.Bangumi)
                _mState.update { it.copy(bangumis = bangumis) }
            }
            launch {
                val fts = getSearchResultPager(SearchRequest.Companion.SearchType.FT)
                _mState.update { it.copy(fts = fts) }
            }
        }
    }

    fun updateState(state: SearchState) {
        _mState.update { state }
    }

    private fun getSearchResultPager(type: SearchRequest.Companion.SearchType): Flow<PagingData<UIModel<out Any?>>> {
        return biliSearchRepository.onSearch(_mState.value.keyword, type)
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
                        return@insertSeparators when {
                            beforeType == null && afterType != null -> UIModel.Header(afterType)
                            beforeType != afterType -> UIModel.Header(afterType)

                            else -> null
                        }
                    }
            }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
            .cachedIn(viewModelScope)
    }

    private fun String.replaceKeyword(): String {
        val match = KeywordPattern.find(this)
        return match?.groupValues?.get(1)?.let {
            this.replace(KeywordPattern, it)
        } ?: this
    }
}