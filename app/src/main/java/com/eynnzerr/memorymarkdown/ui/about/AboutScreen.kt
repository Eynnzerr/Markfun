package com.eynnzerr.memorymarkdown.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.eynnzerr.memorymarkdown.R

@ExperimentalMaterial3Api
@Composable
fun AboutScreen(
    navigateBack: () -> Unit,
    viewModel: AboutViewModel
) {

    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val scrollState = rememberScrollState()
    var browseLibrary by remember { mutableStateOf(false) }

    if (browseLibrary) {
        AlertDialog(
            onDismissRequest = { browseLibrary = false },
            text = {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            setTextColor(textColor)
                            viewModel.markwon.setMarkdown(this, context.resources.getString(R.string.library_credits))
                        }
                    },
                    modifier = Modifier.verticalScroll(scrollState)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        browseLibrary = false
                    },
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(id = R.string.setting_confirm),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.setting_confirm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = stringResource(id = R.string.setting_about),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                     ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start
        ) {
            AboutItem(
                painter = painterResource(id = R.drawable.info),
                title = stringResource(id = R.string.about_version),
                description = versionName
            ) {
                // TODO Checking updates
                Toast.makeText(context, context.resources.getString(R.string.version_hint), Toast.LENGTH_SHORT).show()
            }
            AboutItem(
                painter = painterResource(id = R.drawable.personal),
                title = stringResource(id = R.string.about_author),
                description = stringResource(id = R.string.author_name)
            ) {
                startBrowser("eynnzerr.life", context)
            }
            AboutItem(
                painter = painterResource(id = R.drawable.github),
                title = stringResource(id = R.string.about_github),
                description = stringResource(id = R.string.github_url)
            ) {
                startBrowser(context.resources.getString(R.string.github_url), context)
            }
            AboutItem(
                painter = painterResource(id = R.drawable.library),
                title = stringResource(id = R.string.about_libraries),
                description = stringResource(id = R.string.libraries_description)
            ) {
                browseLibrary = true
            }
        }
    }
}

private fun startBrowser(url: String, context: Context) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}