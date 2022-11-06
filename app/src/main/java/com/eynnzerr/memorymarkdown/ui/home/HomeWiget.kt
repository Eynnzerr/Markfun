package com.eynnzerr.memorymarkdown.ui.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eynnzerr.memorymarkdown.R
import com.eynnzerr.memorymarkdown.navigation.Destinations
import com.eynnzerr.memorymarkdown.navigation.navigateTo
import com.eynnzerr.memorymarkdown.utils.UriUtils
import kotlin.math.absoluteValue

@Composable
fun HomeBarTitle(type: HomeType) {
    val title = when (type) {
        HomeType.CREATED -> stringResource(id = R.string.title_created)
        HomeType.VIEWED -> stringResource(id = R.string.title_viewed)
        HomeType.STARRED -> stringResource(id = R.string.drawer_starred)
        HomeType.ARCHIVED -> stringResource(id = R.string.drawer_archived)
    }
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        // modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@Composable
fun FakeBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)),
        exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomStart
        ) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = onDismiss
                    )
            ) {
                drawRect(Color.Black, alpha = 0.4f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
            ) {
                content()
            }
        }
    }
}

@Composable
fun Logo() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.markdown_line),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = stringResource(id = R.string.home_hint),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PageMenuItem(
    title: String,
    selected: Boolean,
    imageVector: ImageVector,
    onClick: () -> Unit) {

    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    DropdownMenuItem(
        text = {
            Text(
                text = title,
                color = color
            )
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = color
            )
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun OrderMenuItem(title: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title)
                RadioButton(
                    selected = selected,
                    onClick = onClick
                )
            }
        },
        onClick = onClick
    )
}

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val iconTextColor = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.6f)
    val backgroundColor = if (isSelected) colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent

    Surface(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =  imageVector,
                    contentDescription = title,
                    tint = iconTextColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = iconTextColor
                )
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    imageVector: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector =  imageVector,
                contentDescription = title,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}