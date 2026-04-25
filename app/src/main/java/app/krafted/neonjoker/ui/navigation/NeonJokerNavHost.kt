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
import app.krafted.neonjoker.ui.OnboardingScreen
import app.krafted.neonjoker.ui.SplashScreen

private const val PREFS_NAME = "neonjoker_prefs"
private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"

object NeonJokerRoutes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
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
        startDestination = NeonJokerRoutes.Splash,
        modifier = modifier
    ) {
        composable(NeonJokerRoutes.Splash) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = androidx.compose.runtime.remember { 
                context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE) 
            }
            SplashScreen(onNavigateNext = {
                val hasSeenOnboarding = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
                if (hasSeenOnboarding) {
                    navController.navigate(NeonJokerRoutes.Home) {
                        popUpTo(NeonJokerRoutes.Splash) { inclusive = true }
                    }
                } else {
                    navController.navigate(NeonJokerRoutes.Onboarding) {
                        popUpTo(NeonJokerRoutes.Splash) { inclusive = true }
                    }
                }
            })
        }
        composable(NeonJokerRoutes.Onboarding) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = androidx.compose.runtime.remember { 
                context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE) 
            }
            OnboardingScreen(onFinish = {
                prefs.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, true).apply()
                navController.navigate(NeonJokerRoutes.Home) {
                    popUpTo(NeonJokerRoutes.Onboarding) { inclusive = true }
                }
            })
        }
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
