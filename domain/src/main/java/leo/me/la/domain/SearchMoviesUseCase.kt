package leo.me.la.domain

import leo.me.la.common.annotation.KotlinTestOpen
import leo.me.la.common.model.MovieSearchResult
import leo.me.la.domain.repository.SearchRepository

@KotlinTestOpen
class SearchMoviesUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun execute(
        keyword: String,
        page: Int
    ): MovieSearchResult {
        return searchRepository.searchMoviesByKeyword(keyword, page)
    }
}
