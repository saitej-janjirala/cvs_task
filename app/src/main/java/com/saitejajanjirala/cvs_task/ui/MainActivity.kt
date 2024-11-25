package com.saitejajanjirala.cvs_task.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.ui.detail.DetailScreen
import com.saitejajanjirala.cvs_task.ui.home.HomeScreen
import com.saitejajanjirala.cvs_task.ui.theme.Cvs_taskTheme
import com.saitejajanjirala.cvs_task.ui.util.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.saitejajanjirala.cvs_task.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cvs_taskTheme {

                val snackbarHostState = remember { SnackbarHostState()}
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Spacer(modifier = Modifier.padding(innerPadding))
                    NavGraph(navController = rememberNavController())

                }
            }
        }
    }
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun NavGraph(navController: NavHostController){
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var sharedItem by remember { mutableStateOf<Item?>(null) }

        LaunchedEffect(
        key1 = sharedItem,
        ) {
            val item = sharedItem
            if (item != null) {
                urlToBitmap(
                    scope,
                    item.media!!.m!!,
                    context,
                    onSuccess = {b->
                        b?.let { bitmap->
                            val metadata = """
                                                Title: ${item.title}
                                                Description: ${item.description ?: "No description"}
                                                Author: ${item.author}
                                                Published: ${Util.formatPublishedDateLegacy(item.published!!)}
                                            """.trimIndent()
                            scope.launch {
                                shareBitmapWithMetadata(context, bitmap, metadata)
                            }
                        }
                    },
                    onError = {

                    },)
            }
        }

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
                        DetailScreen(animatedVisibilityScope = this, item = item,
                            onBackClicked = {
                            navController.navigateUp()
                            },
                            onShareClicked = { item->
                                sharedItem = item
                            }
                        )
                    }
                }

            }
        }
    }


    private fun urlToBitmap(
        scope: CoroutineScope,
        imageURL: String,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (error: Throwable) -> Unit
    ) {
        var bitmap: Bitmap? = null
        val loadBitmap = scope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageURL)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                bitmap = (result.drawable as BitmapDrawable).bitmap
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
            }
        }
        loadBitmap.invokeOnCompletion { throwable ->
            bitmap?.let {
                onSuccess(it)
            } ?: throwable?.let {
                onError(it)
            } ?: onError(Throwable("Undefined Error"))
        }
    }




    private suspend fun shareBitmapWithMetadata(context: Context, bitmap: Bitmap, metadata: String) {
        val fileUri = withContext(Dispatchers.IO) {
            val cacheDir = File(context.cacheDir, "shared_images")
            cacheDir.mkdirs()

            val file = File(cacheDir, "image${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }

        withContext(Dispatchers.Main) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_TEXT, metadata)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Image and Metadata"))
        }
    }

}

