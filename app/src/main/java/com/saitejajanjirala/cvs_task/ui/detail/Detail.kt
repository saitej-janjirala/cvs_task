package com.saitejajanjirala.cvs_task.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.saitejajanjirala.cvs_task.domain.network.Item
import com.saitejajanjirala.cvs_task.ui.util.AsyncImageWithPlaceholder
import com.saitejajanjirala.cvs_task.util.Util

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    item: Item,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClicked: () -> Unit,
    viewModel: DetailScreenViewModel = hiltViewModel()
){
    LaunchedEffect(key1 = true){
        viewModel.getUri(item.media!!.m!!)
    }
    val context = LocalContext.current
    var shareData by remember { mutableStateOf(false) }
    val uri : Uri? by viewModel.shareUri
    LaunchedEffect (shareData){
        val finalUri = uri
        if(finalUri!=null && shareData){
            shareData(context,item,finalUri)
        }
        shareData = false
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "${item.title}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClicked()

                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareData = true
                    }) {
                        Icon(
                            painterResource(id = android.R.drawable.ic_menu_share),
                            contentDescription = "Share Image"
                        )
                    }
                }
                )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImageWithPlaceholder(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .sharedElement(
                            state = rememberSharedContentState(
                                key = "img-${item.published}"
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 1000)
                            }
                        )
                    ,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Title: ${item.title}", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                HtmlText(
                    html = item.description!!,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Author: ${item.author}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Published: ${Util.formatPublishedDateLegacy(item.published!!)}",
                    style = MaterialTheme.typography.titleMedium,modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

fun shareData(context: Context, item: Item, uri: Uri) {
    val metadata = """
                             Title: ${item.title}
                              Description: ${item.description ?: "No description"}
                              Author: ${item.author}
                              Published: ${Util.formatPublishedDateLegacy(item.published!!)}
                           """.trimIndent()
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, metadata)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Image and Metadata"))
}
@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
    )
}