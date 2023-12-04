package com.cmpt362.cinebon.data.enums

import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.ChatListScreenDestination
import com.cmpt362.cinebon.ui.destinations.DirectionDestination
import com.cmpt362.cinebon.ui.destinations.ListsScreenDestination
import com.cmpt362.cinebon.ui.destinations.MoviesScreenDestination

enum class DashboardNavItems(val icon: Int, val destination: DirectionDestination) {
    Movies(R.drawable.movie, MoviesScreenDestination),
    Lists(R.drawable.lists, ListsScreenDestination),
    Profile(R.drawable.profile, ChatListScreenDestination)
}