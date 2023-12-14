package org.ks.chan.mi.updater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.ks.chan.mi.updater.MainViewModel.Factory.mainViewModel
import org.ks.chan.mi.updater.ui.nav.MainNav
import org.ks.chan.mi.updater.ui.screen.home.Home
import org.ks.chan.mi.updater.ui.screen.login.Login
import org.ks.chan.mi.updater.ui.screen.login.LoginPreview
import org.ks.chan.mi.updater.ui.theme.MIUpdaterTheme
import org.ks.chan.mi.updater.util.EmptyStr

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MIUpdaterTheme {
                Main()
            }
        }
    }

    @Composable
    private fun Main(
        mainViewModel: MainViewModel = mainViewModel()
    ) {
        val isSetup by mainViewModel.isSetup.collectAsStateWithLifecycle(initialValue = null)

        val startDestination = when (isSetup) {
            true -> { MainNav.Home }
            false -> { MainNav.Setup }
            null -> { EmptyStr }
        }

        if (startDestination.isNotBlank()) {
            val navController: NavHostController = rememberNavController()
            MainContent(
                navController = navController,
                startDestination = startDestination,
            ) {

                composable(
                    route = MainNav.Setup,
                    exitTransition = { fadeOut() }
                ) {
                    Login(
                        exit = {
                            mainViewModel.setupChecked()
                            navController.navigate(route = MainNav.Home) {
                                popUpTo(route = MainNav.Home) {
                                    inclusive = true
                                }
                            }
                        },
                        complete = {
                            mainViewModel.setupChecked()
                            navController.navigate(route = MainNav.Home) {
                                popUpTo(route = MainNav.Home) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable(
                    route = MainNav.Home,
                    enterTransition = { fadeIn() },
                ) {
                    Home()
                }

            }
        }

    }

}

@Composable
private fun MainContent(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = builder
    )
}

@Preview
@Composable
private fun MainPreview() {
    MainContent(
        startDestination = MainNav.DefaultStartDestination
    ) {

        composable(route = MainNav.Login) {
            LoginPreview()
        }

    }
}