package com.saitejajanjirala.cvs_task.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.ui.detail.DetailScreen
import com.saitejajanjirala.cvs_task.ui.home.HomeScreen
import com.saitejajanjirala.cvs_task.ui.theme.Cvs_taskTheme
import com.saitejajanjirala.cvs_task.ui.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cvs_taskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Spacer(modifier = Modifier.padding(innerPadding))

                    NavGraph(navController = rememberNavController())

                }
            }
        }
    }
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun NavGraph(navController: NavHostController){
        SharedTransitionLayout {
            NavHost(navController, startDestination = Screen.HomeScreen.route) {
                composable(Screen.HomeScreen.route) {
                    HomeScreen(animatedVisibilityScope = this,onImageClicked = {item->
                            navController.currentBackStackEntry?.savedStateHandle?.set("item", item)
                            navController.navigate(Screen.DetailScreen.route)
                        })
                }

                composable(
                    route = Screen.DetailScreen.route,
                ) { backStackEntry ->
                    val item = navController.previousBackStackEntry?.savedStateHandle?.get<Item>("item")
                    if(item!=null) {
                        DetailScreen(animatedVisibilityScope = this, item = item, onBackClicked = {
                            navController.navigateUp()
                        })
                    }
                }

            }
        }
    }
}

