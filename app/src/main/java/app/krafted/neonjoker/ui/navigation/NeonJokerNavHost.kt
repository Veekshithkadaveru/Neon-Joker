package app.krafted.neonjoker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.neonjoker.ui.GameRoute
import app.krafted.neonjoker.ui.HomeRoute
import app.krafted.neonjoker.ui.LeaderboardRoute

object NeonJokerRoutes {
    const val Home = "home"
    const val Game = "game"
    const val Leaderboard = "leaderboard"
}

@Composable
fun NeonJokerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NeonJokerRoutes.Home,
        modifier = modifier
    ) {
        composable(NeonJokerRoutes.Home) {
            HomeRoute(
                onNewGame = {
                    navController.navigate(NeonJokerRoutes.Game)
                },
                onContinue = {
                    navController.navigate(NeonJokerRoutes.Game)
                },
                onLeaderboard = {
                    navController.navigate(NeonJokerRoutes.Leaderboard)
                }
            )
        }
        composable(NeonJokerRoutes.Game) {
            GameRoute(
                onHome = {
                    navController.popBackStack(
                        route = NeonJokerRoutes.Home,
                        inclusive = false
                    )
                },
                onLeaderboard = {
                    navController.navigate(NeonJokerRoutes.Leaderboard)
                }
            )
        }
        composable(NeonJokerRoutes.Leaderboard) {
            LeaderboardRoute(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
