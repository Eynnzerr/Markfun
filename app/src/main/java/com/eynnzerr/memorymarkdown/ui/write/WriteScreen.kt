package com.eynnzerr.memorymarkdown.ui.write

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.eynnzerr.memorymarkdown.ui.write.markdown.*
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var optionId by remember { mutableStateOf(0) }
    var imageUrl by remember { mutableStateOf("") }
    var imageName by remember { mutableStateOf("") }
    var isContentFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface

    // AndroidView softInput management
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val windowToken = LocalView.current.windowToken

    val saveFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/markdown")) { uri ->
        Log.d(TAG, "WriteScreen: returned uri: $uri")
        uri?.let { viewModel.saveFileAs(it) }
        // TODO Severe bug to be fixed, which will lead to coroutine cancellation.
        navController.navigateTo(Destinations.HOME_ROUTE)
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri.let {
            picUri = it.toString()
            optionId = R.drawable.option_image // Trigger recomposition
        }
    }

    BackHandler {
        // Save markdown to database anyway since loading markdown from SAF/DeepLink.
        if (UriUtils.isUriValid && viewModel.targetId == -1)
            viewModel.saveMarkdown()
        UriUtils.clearUri()

        if (contentChanged) isDialogOpen = true
        else navController.popBackStack()
    }

    if (insertFromUrl) {
        AlertDialog(
            onDismissRequest = { insertFromUrl = false },
            title = { },
            text = {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                    },
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
                        picUri = imageUrl
                        insertFromUrl = false
                        optionId = R.drawable.option_image // Trigger recomposition
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
                        onValueChange = {
                            imageName = it
                        },
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
                                picName = imageName
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
                                insertImage = false
                                picName = imageName
                                selectPicture.launch("image/*")
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
                        // Save markdown to database anyway since loading markdown from SAF/DeepLink.
                        if (UriUtils.isUriValid && viewModel.targetId == -1)
                            viewModel.saveMarkdown()
                        UriUtils.clearUri()

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
                    if (uiState.isReadOnly) {
                        IconButton(onClick = {
                            // share
                            if (viewModel.targetId == -1) {
                                // already passed uri validation test
                                if (!UriUtils.isUriValid) {
                                    // craft
                                    Toast.makeText(
                                        context,
                                        "Please fisrt store the file via saveAs or stash.",
                                        Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Log.d(TAG, "WriteScreen: Shared Uri: ${UriUtils.uri}")
                                    // Some apps cannot recognize uri returned from uri. However this isn't my fault ;)
                                    // e.g. content://com.android.providers.downloads.documents/document/442
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_STREAM, UriUtils.uri)
                                        type = "text/*"
                                    }
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                                    context.startActivity(Intent.createChooser(shareIntent, "Share your thoughts!"))
                                }
                            }
                            else {
                                // find if uri exists.
                                scope.launch {
                                    val uri = withContext(Dispatchers.IO) {
                                        viewModel.getUri()
                                    }
                                    if (uri == null) {
                                        Toast.makeText(
                                            context,
                                            "Please fisrt store the file via saveAs or stash.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Log.d(TAG, "WriteScreen: Shared Uri: $uri")
                                        // 这样提供的uri是file类型
                                        // e.g. file:///storage/emulated/0/Android/data/com.eynnzerr.memorymarkdown/fileshi.md
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            type = "text/*"
                                        }
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                                        context.startActivity(Intent.createChooser(shareIntent, "Share your thoughts!"))
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = null
                            )
                        }
                    }
                    else {
                        Row {
                            IconButton(onClick = {
                                // Save as
                                saveFile.launch(viewModel.uiState.value.title + ".md")
                                viewModel.saveMarkdown()
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
                                UriUtils.clearUri()

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
                                onClick = {
                                    insertImage = true
                                },
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
                            setTextColor(textColor.toArgb())
                            markwon.setMarkdown(this, uiState.content)
                        }
                    },
                    modifier = Modifier
                        .padding(top = 23.dp, start = 17.dp, bottom = 13.dp, end = 13.dp)
                        .verticalScroll(scrollState),
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
                            setHintTextColor(textColor.copy(alpha=0.4f).toArgb())
                            setTextColor(textColor.toArgb())
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

                            requestFocus()
                        }
                    },
                    modifier = Modifier
                        .padding(13.dp)
                        .verticalScroll(scrollState),
                    update = { editText ->
                        // observe optionId
                        if (optionId != 0) {
                            val option = when (optionId) {
                                R.drawable.option_image -> MarkdownOption.IMAGE
                                R.drawable.option_header -> MarkdownOption.HEADER
                                R.drawable.option_bold -> MarkdownOption.BOLD
                                R.drawable.option_italic -> MarkdownOption.ITALIC
                                R.drawable.option_delete_line -> MarkdownOption.STRIKETHROUGH
                                R.drawable.option_code_inline -> MarkdownOption.CODEINLINE
                                R.drawable.option_code_block -> MarkdownOption.CODEBLOCK
                                R.drawable.option_ordered_list -> MarkdownOption.ORDEREDLIST
                                R.drawable.option_unordered_list -> MarkdownOption.UNORDEREDLIST
                                R.drawable.option_quote -> MarkdownOption.QUOTE
                                R.drawable.option_divider -> MarkdownOption.DIVIDER
                                R.drawable.option_hyperlink -> MarkdownOption.HYPERLINK
                                R.drawable.option_task_list -> MarkdownOption.TASKLIST
                                R.drawable.option_arrow_left -> MarkdownOption.LEFT
                                R.drawable.option_arrow_right -> MarkdownOption.RIGHT
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
