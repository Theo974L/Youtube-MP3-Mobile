package com.example.theolaforgeeval.navhost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.theolaforgeeval.player.ui.component.MiniPlayer
import com.example.theolaforgeeval.player.ui.screen.player.FullPlayerScreen
import com.example.theolaforgeeval.player.ui.screen.library.LibraryScreen
import com.example.theolaforgeeval.player.ui.screen.library.LibraryViewModel
import com.example.theolaforgeeval.player.ui.screen.playlist.PlaylistDetailScreen
import com.example.theolaforgeeval.player.ui.screen.playlist.PlaylistsScreen
import com.example.theolaforgeeval.player.ui.screen.search.SearchScreen
import com.example.theolaforgeeval.player.ui.screen.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

@Composable
fun AppNavHost(navController: NavHostController) {

    val items = listOf(
        NavItem(Screen.Library, "Bibliothèque", Icons.Filled.LibraryMusic),
        NavItem(Screen.Playlists, "Playlists", Icons.Filled.QueueMusic),
        NavItem(Screen.Search, "Ajouter", Icons.Filled.AddCircle),
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Masquée sur le lecteur plein écran
            if (currentRoute != Screen.Player.route) {
                Column {
                    MiniPlayer(onExpand = { navController.navigate(Screen.Player.route) })
                    NavigationBar {
                        items.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.screen.route,
                                onClick = {
                                    if (currentRoute != item.screen.route) {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(Screen.Library.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Library.route) {
                val viewModel: LibraryViewModel = koinViewModel()
                LibraryScreen(viewModel = viewModel)
            }
            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = koinViewModel()
                SearchScreen(viewModel = viewModel)
            }
            composable(Screen.Playlists.route) {
                PlaylistsScreen(
                    onOpenPlaylist = { id ->
                        navController.navigate(Screen.PlaylistDetail.createRoute(id))
                    },
                )
            }
            composable(
                route = Screen.PlaylistDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                PlaylistDetailScreen(
                    playlistId = id,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Screen.Player.route) {
                FullPlayerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
