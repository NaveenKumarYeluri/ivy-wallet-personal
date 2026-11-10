package com.ivy.search

import com.ivy.data.model.TagId

sealed interface SearchEvent {
    data class Search(val query: String) : SearchEvent
    data class SelectTag(val tagId: TagId?) : SearchEvent
}
