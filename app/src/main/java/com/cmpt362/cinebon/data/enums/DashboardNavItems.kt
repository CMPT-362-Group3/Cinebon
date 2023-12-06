package com.cmpt362.cinebon.data.enums

import com.cmpt362.cinebon.R
import com.cmpt362.cinebon.ui.destinations.DirectionDestination
import com.cmpt362.cinebon.ui.destinations.ListsScreenDestination
import com.cmpt362.cinebon.ui.destinations.MoviesScreenDestination
import com.cmpt362.cinebon.ui.destinations.SocialScreenDestination

// Handy enum class for the dashboard navigation items
enum class DashboardNavItems(val icon: Int, val destination: DirectionDestination) {
    Movies(R.drawable.movie, MoviesScreenDestination),
    Lists(R.drawable.lists, ListsScreenDestination),
    Socials(R.drawable.socials, SocialScreenDestination),
}