package com.cmpt362.cinebon.data.enums

import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.destinations.DirectionDestination
import com.cmpt362.cinebon.destinations.ListsScreenDestination
import com.cmpt362.cinebon.destinations.MoviesScreenDestination
import com.cmpt362.cinebon.destinations.ProfileScreenDestination

enum class DashboardNavItems(val icon: Int, val destination: DirectionDestination) {
    Movies(R.drawable.movie, MoviesScreenDestination),
    Lists(R.drawable.list_alt_add, ListsScreenDestination),
    Profile(R.drawable.person, ProfileScreenDestination)
}