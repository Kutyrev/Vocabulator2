package com.github.kutyrev.vocabulator.features.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.kutyrev.vocabulator.BuildConfig
import com.github.kutyrev.vocabulator.R

@Composable
fun AboutRoute() {
    Surface() {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std))) {
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
            Row(horizontalArrangement = Arrangement.Center) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.logo_desc),
                    contentScale = ContentScale.FillWidth
                )
            }
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_std)))
            Text(
                text = stringResource(id = R.string.version_description, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}
