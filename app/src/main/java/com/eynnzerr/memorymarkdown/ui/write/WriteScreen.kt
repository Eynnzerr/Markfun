package com.eynnzerr.memorymarkdown.ui.write

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.utils.UriUtils
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.ui.write.markdown.MarkdownOption
import com.eynnzerr.memorymarkdown.ui.write.markdown.addOption
import com.eynnzerr.memorymarkdown.ui.write.markdown.optionList
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun WriteScreen(
    navController: NavHostController,
    viewModel: WriteViewModel
) {
    // viewModel
    val uiState by viewModel.uiState.collectAsState()
    val editor = viewModel.getEditor()
    val markwon = viewModel.getMarkwon()
    val optionList = optionList

    // Dialogs
    var contentChanged by remember { mutableStateOf(false) }
    var insertImage by remember { mutableStateOf(false) }
    var insertFromUrl by remember { mutableStateOf(false) }
    var isDialogOpen by remember { mutableStateOf(false) }

    // Others
    var optionId by remember { mutableStateOf(0) }
    var imageUrl by remember { mutableStateOf("") }
    var imageName by remember { mutableStateOf("") }
    var isContentFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val textColor = if (isSystemInDarkTheme()) Color.White.toArgb() else Color.Black.toArgb()

    // AndroidView softInput management
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken = LocalView.current.windowToken

    val saveFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        Log.d(TAG, "WriteScreen: returned uri: $uri")
        uri?.let { viewModel.saveFileAs(it) }
        // TODO Severe bug to be fixed, which will lead to coroutine cancellation.
        navController.navigateTo(Destinations.HOME_ROUTE)
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // get file path from picture uri
        it.data?.data?.let { uri ->
            var imagePath = ""
            if (DocumentsContract.isDocumentUri(context, uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                if (uri.authority == "com.android.providers.media.documents") {
                    val id = docId.split(":")[1]
                    val selection = MediaStore.Images.Media._ID + "=" + id
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context)
                }
                else if (uri.authority == "com.android.providers.downloads.documents") {
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                    imagePath = getImagePath(contentUri, null, context)
                }
            }
            else if (uri.scheme == "content") imagePath = getImagePath(uri, null, context)
            else if (uri.scheme == "file") imagePath = uri.path!!
            Log.d(TAG, "WriteScreen: path is $imagePath")
            viewModel.updateContent(uiState.content.plus("\n![$imageName]($imagePath)\n"))
        }
    }

    BackHandler {
        UriUtils.run {
            if (isUriValid) {
                uri = null
                isUriValid = false
            }
        }
        if (contentChanged) isDialogOpen = true // 本地新建文件且内容改变时才提示是否保存草稿
        else navController.popBackStack()
    }

    if (insertFromUrl) {
        AlertDialog(
            onDismissRequest = { insertFromUrl = false },
            title = { },
            text = {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.write_img_url_label),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.write_img_url_holder),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateContent(uiState.content.plus("\n![$imageName]($imageUrl)\n"))
                        insertFromUrl = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_confirm),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        insertFromUrl = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_cancel),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }

    if (insertImage) {
        AlertDialog(
            onDismissRequest = { insertImage = false },
            title = { },
            text = {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = R.string.write_insert_image_title))
                    OutlinedTextField(
                        value = imageName,
                        onValueChange = { imageName = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.write_img_name_label),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.write_img_name_holder),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                insertImage = false
                                insertFromUrl = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Link,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(id = R.string.write_img_url),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                  Toast.makeText(context, "This feature will be ready very soon!", Toast.LENGTH_SHORT).show()
//                                val intent = Intent("android.intent.action.GET_CONTENT").apply {
//                                    type = "image/*"
//                                }
//                                selectPicture.launch(intent)
//                                insertImage = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                text = stringResource(id = R.string.write_img_gallery),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = {},
            text = {
                Text(
                    text = stringResource(id = R.string.write_exit_notice),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // saveCraft()
                        viewModel.saveCraft()
                        isDialogOpen = false
                        UriUtils.clearUri()
                        navController.popBackStack()
                        keyboard?.hide()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_confirm),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // removeCraft()
                        viewModel.emptyCraft()
                        isDialogOpen = false
                        UriUtils.clearUri()
                        navController.popBackStack()
                        keyboard?.hide()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = null
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(id = R.string.write_cancel),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (contentChanged) isDialogOpen = true
                        else navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(visible = !uiState.isReadOnly) {
                        Row {
                            IconButton(onClick = {
                                // Save as
                                saveFile.launch(viewModel.uiState.value.title + ".md")
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.SaveAs,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = {
                                // Save/Stash to private folder
                                viewModel.stashFile()
                                viewModel.saveMarkdown()
                                navController.popBackStack()
                                keyboard?.hide()
                                inputManager.hideSoftInputFromWindow(windowToken, 0)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !uiState.isReadOnly,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Surface(
                    shadowElevation = 10.dp,
                    modifier = Modifier.autoImePadding(isContentFocused)
                ) {
                    LazyRow(
                        modifier = Modifier.navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        item {
                            IconButton(
                                onClick = { insertImage = true },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.option_image),
                                    contentDescription = null
                                )
                            }
                        }

                        items(optionList) { option ->
                            IconButton(
                                onClick = { optionId = option },
                            ) {
                                Icon(
                                    painter = painterResource(id = option),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.autoNavigationPadding(uiState.isReadOnly),
                onClick = {
                    viewModel.updateMode()
                    inputManager.hideSoftInputFromWindow(windowToken, 0)
                },
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = if (uiState.isReadOnly) R.drawable.edit else R.drawable.eye_open),
                    contentDescription = null,
                    // tint = IconColor
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            TextField(
                value = uiState.title,
                onValueChange = { newInput -> viewModel.updateTitle(newInput) },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.write_title_holder),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                maxLines = 1,
                textStyle = MaterialTheme.typography.headlineLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                readOnly = uiState.isReadOnly
            )

            if (uiState.isReadOnly) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            gravity = Gravity.TOP.or(Gravity.START)
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.5f)
                            setTextColor(textColor)
                            markwon.setMarkdown(this, uiState.content)
                        }
                    },
                    modifier = Modifier.padding(top = 23.dp, start = 17.dp, bottom = 13.dp, end = 13.dp),
                    update = { textView ->
                        markwon.setMarkdown(textView, uiState.content)
                    }
                )
            }
            else {
                AndroidView(
                    factory = { context ->
                        EditText(context).apply {
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            gravity = Gravity.TOP.or(Gravity.START)
                            setHintTextColor(textColor)
                            setTextColor(textColor)
                            setText(uiState.content)
                            hint = "Enjoy your MarkDown now!"
                            background = null
                            addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                                editor,
                                Executors.newCachedThreadPool(),
                                this
                            ))
                            addTextChangedListener(object: TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }

                                override fun afterTextChanged(s: Editable?) {
                                    //if (editText.text.toString() != uiState.content) // avoid dead loop
                                    viewModel.updateContent(s.toString())
                                    Log.d(TAG, "afterTextChanged: content changed to ${viewModel.uiState.value.content}")
                                    contentChanged = true
                                }

                            })
                            setOnFocusChangeListener { _, hasFocus -> isContentFocused = hasFocus }
                        }
                    },
                    modifier = Modifier.padding(13.dp),
                    update = { editText ->
                        if (optionId != 0) {
                            val option = when (optionId) {
                                R.drawable.option_header -> MarkdownOption.HEADER
                                R.drawable.option_bold -> MarkdownOption.BOLD
                                R.drawable.option_italic -> MarkdownOption.ITALIC
                                R.drawable.option_delete_line -> MarkdownOption.STRIKETHROUGH
                                R.drawable.option_code_inline -> MarkdownOption.CODEINLINE
                                R.drawable.option_code_block -> MarkdownOption.CODEBLOCK
                                R.drawable.option_quote -> MarkdownOption.QUOTE
                                R.drawable.option_divider -> MarkdownOption.DIVIDER
                                R.drawable.option_hyperlink -> MarkdownOption.HYPERLINK
                                R.drawable.option_task_list -> MarkdownOption.TASKLIST
                                else -> MarkdownOption.NONE
                            }
                            optionId = 0 // Consume option event to avoid dead loop: click -> update -> addOption -> textChanged -> updateContent -> update
                            editText.addOption(option) // will call onTextChanged()
                        }
                    }
                )
            }
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.autoImePadding(isFocused: Boolean): Modifier = if (isFocused) Modifier.imePadding() else Modifier

// When isReadOnly, add navigationPadding for FAB
@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.autoNavigationPadding(isReadOnly: Boolean): Modifier = if (isReadOnly) Modifier.navigationBarsPadding() else Modifier

@SuppressLint("Range", "Recycle")
private fun getImagePath(uri: Uri, selection: String?, context: Context): String {
    var path = ""
    context.contentResolver.query(uri, null, selection, null, null)?.run {
        if (moveToFirst()) path = getString(getColumnIndex(MediaStore.Images.Media.DATA))
        close()
    }
    return path
}

private const val TAG = "WriteScreen"
