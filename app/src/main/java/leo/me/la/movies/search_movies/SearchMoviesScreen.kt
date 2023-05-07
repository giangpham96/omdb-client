package leo.me.la.movies.search_movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import leo.me.la.movies.R
import leo.me.la.presentation.DataState.Failure
import leo.me.la.presentation.DataState.Idle
import leo.me.la.presentation.DataState.Loading
import leo.me.la.presentation.DataState.Success
import leo.me.la.presentation.MovieNotFoundException
import leo.me.la.presentation.SearchViewState
import leo.me.la.presentation.SearchViewState.SearchUi
import leo.me.la.presentation.SearchViewState.SearchUi.LoadingNextPage
import leo.me.la.presentation.SearchViewState.SearchUi.Movie
import leo.me.la.presentation.SearchViewState.SearchUi.ReloadNextPage
import leo.me.la.presentation.SearchViewState.SearchUi.SearchItem

@Composable
fun SearchMoviesScreen(
    viewState: SearchViewState,
    onQueryChange: (String) -> Unit,
    onLoadingNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color(0xFF121212))
            .imePadding(),
    ) {
        SearchBar(
            value = viewState.keyword,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
        )
        when (val state = viewState.searchState) {
            is Failure -> {
                if (state.error is MovieNotFoundException) {
                    InfoBox(
                        painter = painterResource(id = R.drawable.not_found),
                        text = stringResource(id = R.string.not_found),
                        color = Color(0xffcc0000),
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    InfoBox(
                        painter = painterResource(id = R.drawable.unknown),
                        text = "",
                        color = Color(0xffcc0000),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Idle -> {
                InfoBox(
                    painter = painterResource(id = R.drawable.cinema),
                    text = stringResource(id = R.string.search_movie),
                    color = Color.White,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Loading -> LoadingBox(Modifier.fillMaxSize())

            is Success -> {
                MovieList(
                    items = state.data.listItems,
                    onLoadingNextPage = onLoadingNextPage,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun MovieItem(
    name: String,
    modifier: Modifier = Modifier,
    image: String? = null,
) {
    val fallbackImage = ColorPainter(Color(0xff34353e))
    Column(
        modifier = modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        AsyncImage(
            modifier = modifier
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(24.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(image ?: fallbackImage)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = fallbackImage,
            error = fallbackImage,
        )
        Text(
            text = name,
            style = MaterialTheme.typography.subtitle2,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun LoadingItem(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RetryItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.icon_retry),
                contentDescription = "retry",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun MovieList(
    items: List<SearchItem>,
    onLoadingNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(key1 = Unit) {
        snapshotFlow {
            val itemLeft =
                gridState.layoutInfo.totalItemsCount - 1 - gridState.firstVisibleItemIndex
            itemLeft <= 4 || !gridState.canScrollForward
        }.collect {
            onLoadingNextPage()
        }
    }
    LazyVerticalGrid(
        modifier = modifier,
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        columns = GridCells.Fixed(2)
    ) {
        items(
            key = {
                when (val item = items[it]) {
                    is Movie -> item.imdbId
                    LoadingNextPage -> LoadingNextPage.javaClass.name
                    ReloadNextPage -> ReloadNextPage.javaClass.name
                }
            },
            count = items.size,
            span = { index ->
                GridItemSpan(if (items[index] is Movie) 1 else maxLineSpan)
            }
        ) {
            when (val item = items[it]) {
                is Movie -> MovieItem(name = item.title, image = item.poster)
                LoadingNextPage -> LoadingItem()
                ReloadNextPage -> RetryItem(onLoadingNextPage)
            }
        }
    }
}

@Composable
private fun LoadingBox(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun InfoBox(
    text: String,
    painter: Painter,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painter, contentDescription = null, tint = color)
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                color = color,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier.statusBarsPadding(),
        backgroundColor = Color(0xff1c1c27),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    painter = rememberVectorPainter(image = Icons.TwoTone.Search),
                    contentDescription = null,
                    tint = Color.LightGray
                )
            },
            singleLine = true,
            placeholder = { Text(text = "Search movie...", color = Color.LightGray) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xff34353e),
                focusedIndicatorColor = Color.Transparent,
                textColor = Color.White,
                cursorColor = Color.White,
            ),
            shape = RoundedCornerShape(50, 50, 50, 50)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xff1c1c27, showBackground = true, showSystemUi = true)
private fun Preview_MovieList() {
    MovieList(
        (1..10).map {
            Movie(
                title = "Thor: Love and Thunder",
                poster = "https://lumiere-a.akamaihd.net/v1/images/01fe70e80a0ac867c9a9470641df6848_2764x4096_86b89bf5.jpeg?region=0,0,2764,4096",
                imdbId = it.toString()
            )
        },
        {}
    )
}

@Composable
@Preview(backgroundColor = 0xff1c1c27, showBackground = true, showSystemUi = true)
private fun Preview_MovieList_Retry() {
    MovieList(
        (1..3).map {
            Movie(
                title = "Thor: Love and Thunder",
                poster = "https://lumiere-a.akamaihd.net/v1/images/01fe70e80a0ac867c9a9470641df6848_2764x4096_86b89bf5.jpeg?region=0,0,2764,4096",
                imdbId = it.toString()
            )
        } + ReloadNextPage,
        {}
    )
}

@Composable
@Preview
private fun Preview_Searchbar() {
    SearchBar(value = "", {})
}

@Composable
@Preview
private fun Preview_SearchMoviesScreen_MovieList() {
    SearchMoviesScreen(
        SearchViewState(
            searchState = Success(
                SearchUi(
                    keyword = "Thor",
                    movies = (1..3).map {
                        Movie(
                            title = "Thor: Love and Thunder",
                            poster = "https://lumiere-a.akamaihd.net/v1/images/01fe70e80a0ac867c9a9470641df6848_2764x4096_86b89bf5.jpeg?region=0,0,2764,4096",
                            imdbId = it.toString()
                        )
                    },
                    footer = LoadingNextPage,
                    page = 1,
                    totalPages = 10,
                )
            ),
            keyword = "Thor",
        ),
        onQueryChange = {},
        onLoadingNextPage = {}
    )
}

@Composable
@Preview
private fun Preview_SearchMoviesScreen_Idle() {
    SearchMoviesScreen(
        SearchViewState(
            searchState = Idle,
            keyword = "Thor",
        ),
        onQueryChange = {},
        onLoadingNextPage = {}
    )
}

@Composable
@Preview
private fun Preview_SearchMoviesScreen_Error() {
    SearchMoviesScreen(
        SearchViewState(
            searchState = Failure(MovieNotFoundException),
            keyword = "Thor",
        ),
        onQueryChange = {},
        onLoadingNextPage = {}
    )
}
