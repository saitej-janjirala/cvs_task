package com.saitejajanjirala.cvs_task.ui.util

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.saitejajanjirala.cvs_task.R
import com.saitejajanjirala.cvs_task.domain.network.Item

@Composable
fun AsyncImageWithPlaceholder(item: Item,modifier: Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {


        AsyncImage(
            model = item.media!!.m,
            contentDescription = item.description,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            error = painterResource(id = android.R.drawable.stat_notify_error),

        )
    }
}
