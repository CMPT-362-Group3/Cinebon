package com.cmpt362.cinebon.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.data.DummyMovieData.getListByIndex
import com.cmpt362.cinebon.data.MovieEntity
import com.cmpt362.cinebon.utils.SetStatusBarColor
import com.cmpt362.cinebon.utils.shimmerBrush
import com.ramcosta.composedestinations.annotation.Destination

@DashboardNavGraph
@Destination
@Composable
fun MovieInfoScreen(listIndex: Int = 0, movieIndex: Int = 0) {

    SetStatusBarColor(statusBarColor = MaterialTheme.colorScheme.surface)

    val movie = getListByIndex(listIndex)[movieIndex]

    Box {
        AsyncImage(
            model = movie.image,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xB0000000),
                            Color.Black
                        )
                    )
                )
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
        )

        MovieBookmarkIcon(movie = movie, modifier = Modifier.align(alignment = Alignment.TopEnd))

        Column(modifier = Modifier.align(alignment = Alignment.BottomCenter)) {
            Text(
                movie.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 2.dp)
            ) {
                Text(
                    "${movie.releaseDate} • ${movie.length} • ${movie.language}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                OutlinedCard(
                    modifier = Modifier.padding(0.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface)
                ) {
                    Text(movie.ageRating, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.inverseOnSurface, modifier = Modifier.padding(8.dp))
                }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 12.dp)
            ) {
                for (star in 1..5) {
                    Image(
                        painter = rememberVectorPainter(Icons.Filled.Star),
                        contentDescription = "Star",
                        colorFilter = ColorFilter.tint(if (star <= movie.review) Color(0xFFE6BF41) else Color.Gray),
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                }
            }

            Text(
                movie.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MovieImage(movie: MovieEntity, onClick: () -> Unit = {}) {
    val showShimmer = remember { mutableStateOf(true) }
    AsyncImage(
        model = movie.image,
        contentDescription = movie.title,
        contentScale = ContentScale.FillWidth,
        onSuccess = { showShimmer.value = false },
        modifier = Modifier
            .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value))
            .heightIn(min = 100.dp)
            .fillMaxSize()
            .clickable {
                onClick()
            }
    )
}

@Composable
fun MovieBookmarkIcon(movie: MovieEntity, modifier: Modifier) {

    val isBookmarked by movie.bookmarked.collectAsState()
    val rememberVectorDrawablePainter = rememberVectorPainter(
        if (isBookmarked)
            ImageVector.vectorResource(id = R.drawable.bookmarked)
        else
            ImageVector.vectorResource(id = R.drawable.bookmark_border)
    )

    Surface(
        modifier = modifier
            .zIndex(2f)
            .padding(16.dp),
        shape = MaterialTheme.shapes.small
    ) {
        IconButton(
            onClick = {
                movie.bookmarked.value = !isBookmarked
            },
        ) {
            Image(
                painter = rememberVectorDrawablePainter,
                contentDescription = "Add to watchlist",
                colorFilter = ColorFilter.tint(if (!isBookmarked) MaterialTheme.colorScheme.onSurface else Color(0xFFE6BF41)),
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
            )
        }
    }
}

@Preview
@Composable
fun MovieInfoScreenPreview() {
    MovieInfoScreen(0, 2)
}
