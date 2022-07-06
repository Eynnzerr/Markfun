package com.eynnzerr.memorymarkdown.ui.write

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.ui.theme.IconButtonColor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun WriteScreen(
    navController: NavHostController,
    viewModel: WriteViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val editor = viewModel.getEditor()
    val optionList = viewModel.optionList

    var insertImage by remember { mutableStateOf(false) }
    var imageName by remember { mutableStateOf("") }
    var isDialogOpen by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val saveFile = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        // TODO 回调从来没执行过？ 并且这个无参构造都已经被弃用了，我这还能用？
        if (uri == null) Log.d(TAG, "WriteScreen: uri is null.")
        uri?.let { viewModel.saveFileAs(it) }
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
                                // TODO 唤起另一个Dialog
                                insertImage = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onPrimary
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
                                // TODO 打开图库
                                insertImage = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onPrimary
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
                        navController.navigateTo(Destinations.HOME_ROUTE)
                        keyboard?.hide()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
                        navController.navigateTo(Destinations.HOME_ROUTE)
                        keyboard?.hide()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
                        isDialogOpen = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = IconButtonColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Save as
                        saveFile.launch(viewModel.uiState.value.title + ".txt")
                        navController.navigateTo(Destinations.HOME_ROUTE)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.SaveAs,
                            contentDescription = null,
                            tint = IconButtonColor
                        )
                    }
                    IconButton(onClick = {
                        // Save/Stash to private folder
                        viewModel.stashFile()
                        viewModel.saveMarkdown()
                        Toast.makeText(context, "Done.", Toast.LENGTH_SHORT).show()
                        navController.navigateTo(Destinations.HOME_ROUTE)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = IconButtonColor
                        )
                    }
                }
            )
        },
        bottomBar = {
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
                            onClick = option.action,
                        ) {
                            Icon(
                                painter = painterResource(id = option.iconResource),
                                contentDescription = null
                            )
                        }
                    }
                }
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            AndroidView(
                factory = { context ->
                    EditText(context).also { editText ->
                        editText.setText(uiState.content)
                        editText.hint = "Enjoy your MarkDown now!"
                        editText.background = null
                        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                            editor,
                            Executors.newCachedThreadPool(),
                            editText
                        ))
                        editText.addTextChangedListener(object: TextWatcher {
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
                                if (editText.text.toString() != uiState.content) // avoid dead loop
                                    viewModel.updateContent(s.toString())
                            }

                        })
                        editText.setOnFocusChangeListener { _, hasFocus -> isContentFocused = hasFocus }
                    }
                },
                modifier = Modifier.padding(13.dp),
                update = { editText ->
                    if (editText.text.toString() != uiState.content) {
                        // avoid dead loop
                        editText.setText(uiState.content)
                        editText.setSelection(editText.text.length)
                        if (!editText.isFocused) {
                            editText.requestFocus()
                            inputManager.showSoftInput(editText, 0)
                        }
                    }
                }
            )
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.autoImePadding(isFocused: Boolean): Modifier = if (isFocused) Modifier.imePadding() else Modifier

private const val TAG = "WriteScreen"
