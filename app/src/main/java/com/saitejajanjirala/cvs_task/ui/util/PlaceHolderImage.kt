package com.saitejajanjirala.cvs_task.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import coil.compose.AsyncImage

import com.saitejajanjirala.cvs_task.R
import com.saitejajanjirala.cvs_task.domain.network.Item


@Composable
fun AsyncImageWithPlaceholder(item: Item,modifier: Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Render the AsyncImage
        AsyncImage(
            model = item.media!!.m,
            contentDescription = item.description,
            modifier = modifier,
            contentScale = ContentScale.Crop ,
            placeholder = painterResource(id = R.drawable.img),
            error = painterResource(id = android.R.drawable.stat_notify_error),
        )
    }
}

