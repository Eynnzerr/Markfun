package com.eynnzerr.memorymarkdown.ui.write

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun WriteScreen(
    navController: NavHostController,
    editor: MarkwonEditor,
    uiState: Markdown,
    optionList: List<WriteViewModel.MarkdownOption>,
    onStateChange: (Markdown) -> Unit,
    saveCraft: () -> Unit,
    removeCraft: () -> Unit,
    saveFile: () -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var isContentFocused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
                        saveCraft()
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
                        removeCraft()
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
                        saveFile()
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
                onValueChange = { newInput -> onStateChange(Markdown(title = newInput)) },
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
                                    onStateChange(Markdown(content = s.toString()))
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
