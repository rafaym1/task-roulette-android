package com.taskroulette.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskroulette.app.model.TaskItem
import com.taskroulette.app.model.TaskList
import com.taskroulette.app.ui.theme.Baloo2
import com.taskroulette.app.ui.theme.Nunito
import com.taskroulette.app.ui.theme.TrColors

@Composable
private fun noRippleInteractionSource() = remember { MutableInteractionSource() }

@Composable
private fun composedNoRipple(onClick: () -> Unit): Modifier =
    Modifier.clickable(interactionSource = noRippleInteractionSource(), indication = null, onClick = onClick)

/** Full-screen scrim + a rounded-top sheet that slides up from the bottom, matching the mockup's `sheetUp` sheets. */
@Composable
fun BottomSheetScrim(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    scrimAlpha: Float = 0.4f,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrColors.titlePurple.copy(alpha = scrimAlpha))
                .then(composedNoRipple(onDismiss)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                Box(modifier = composedNoRipple {}) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun SheetSurface(
    modifier: Modifier = Modifier,
    borderColor: Color = TrColors.lilacBorder,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TrColors.cream, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .border(3.dp, borderColor, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
    ) {
        content()
    }
}

@Composable
private fun SheetHeader(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TrColors.textPrimary)
        CloseChip(onClick = onClose, size = 28.dp)
    }
}

@Composable
fun ResultSheet(
    visible: Boolean,
    resultText: String,
    onDismiss: () -> Unit,
    onClaimDone: () -> Unit,
    onRespin: () -> Unit,
) {
    BottomSheetScrim(visible = visible, onDismiss = onDismiss, scrimAlpha = 0.35f) {
        SheetSurface {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp, 30.dp, 24.dp, 36.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "WHEEL SAYS...",
                        fontFamily = Baloo2,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = TrColors.hotPink,
                    )
                    CloseChip(onClick = onDismiss)
                }
                Spacer(Modifier.size(16.dp))
                Text(
                    text = resultText,
                    fontFamily = Baloo2,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    color = TrColors.textPrimary,
                )
                Spacer(Modifier.size(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoldButton(text = "Claim Done", onClick = onClaimDone, modifier = Modifier.weight(1f))
                    OutlineButton(text = "Spin Again", onClick = onRespin, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AccountSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onHowItWorks: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    BottomSheetScrim(visible = visible, onDismiss = onDismiss) {
        SheetSurface {
            SheetHeader(title = "Manage Account", onClose = onDismiss)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AccountRow(icon = "❓", label = "How It Works", bg = TrColors.lavenderFill, border = TrColors.lilacBorderLight, textColor = TrColors.textPrimary, onClick = onHowItWorks)
                AccountRow(icon = "🔒", label = "Privacy Policy", bg = TrColors.lavenderFill, border = TrColors.lilacBorderLight, textColor = TrColors.textPrimary, onClick = onPrivacyPolicy)
                AccountRow(icon = "🗑️", label = "Delete Account", bg = TrColors.destructiveBg, border = TrColors.destructiveBorder, textColor = TrColors.destructiveText, onClick = onDeleteAccount)
            }
        }
    }
}

@Composable
private fun AccountRow(icon: String, label: String, bg: Color, border: Color, textColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(2.5.dp, border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 18.sp)
        Text(text = label, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
    }
}

@Composable
fun HowItWorksSheet(visible: Boolean, onDismiss: () -> Unit) {
    BottomSheetScrim(visible = visible, onDismiss = onDismiss) {
        SheetSurface {
            Column(modifier = Modifier.fillMaxWidth().padding(22.dp, 20.dp, 22.dp, 30.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "How It Works", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TrColors.textPrimary)
                    CloseChip(onClick = onDismiss, size = 28.dp)
                }
                Spacer(Modifier.size(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "1. Build a list of tasks you need to do.",
                        "2. Pick that list as your active wheel.",
                        "3. Hit SPIN — the wheel lands on one task at random.",
                        "4. Reveal it, then claim it done or spin again.",
                    ).forEach {
                        Text(text = it, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, lineHeight = 20.sp, color = TrColors.editText)
                    }
                }
            }
        }
    }
}

private data class PolicySection(val heading: String, val body: String)

private val PRIVACY_POLICY_SECTIONS = listOf(
    PolicySection(
        "The short version",
        "Task Roulette doesn't collect, transmit, or share any of your data. Everything you enter stays on your device.",
    ),
    PolicySection(
        "What the app stores",
        "Your lists and tasks, and which ones you've marked done, are saved using Android's on-device storage. That's the only data the app handles — it never leaves your phone, because Task Roulette has no server and contains no networking code at all.",
    ),
    PolicySection(
        "Permissions",
        "Vibrate — used only for a short haptic buzz when the wheel spins and lands. It has no access to any personal information.",
    ),
    PolicySection(
        "Accounts",
        "There's no real sign-in or authentication. \"Delete Account\" just clears your locally stored lists from this device — there's nothing stored anywhere else to delete.",
    ),
    PolicySection(
        "Deleting your data",
        "Use Delete Account in this menu, or uninstall the app, or clear its storage from Android Settings → Apps → Task Roulette → Storage.",
    ),
    PolicySection(
        "Children's privacy",
        "Because Task Roulette collects no data of any kind from anyone, it does not knowingly collect information from children or any other user.",
    ),
)

@Composable
fun PrivacyPolicySheet(visible: Boolean, onDismiss: () -> Unit) {
    BottomSheetScrim(visible = visible, onDismiss = onDismiss) {
        SheetSurface {
            SheetHeader(title = "Privacy Policy", onClose = onDismiss)
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).padding(horizontal = 22.dp).padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(PRIVACY_POLICY_SECTIONS) { section ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = section.heading, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TrColors.titlePurple)
                        Text(text = section.body, fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 19.sp, color = TrColors.editText)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(visible: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TrColors.titlePurple.copy(alpha = 0.45f))
                .then(composedNoRipple(onDismiss))
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(TrColors.cream)
                    .border(3.dp, TrColors.destructiveBorder, RoundedCornerShape(22.dp))
                    .then(composedNoRipple {})
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(text = "Delete your account?", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TrColors.textPrimary)
                Text(
                    text = "This removes all your lists and tasks. This can't be undone.",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TrColors.bodyMuted,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineButton(text = "Cancel", onClick = onDismiss, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(TrColors.destructiveButton)
                            .border(2.5.dp, Color.White, RoundedCornerShape(14.dp))
                            .clickable(onClick = onConfirm)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Delete", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ListsSheet(
    visible: Boolean,
    lists: List<TaskList>,
    activeListId: String?,
    expandedListId: String?,
    addingList: Boolean,
    newListName: String,
    taskDraft: String,
    editingTaskId: String?,
    editingText: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onRemoveList: (String) -> Unit,
    onToggleTaskDone: (listId: String, taskId: String) -> Unit,
    onStartEditTask: (taskId: String, text: String) -> Unit,
    onEditingTextChange: (String) -> Unit,
    onSaveEdit: (listId: String) -> Unit,
    onRemoveTask: (listId: String, taskId: String) -> Unit,
    onTaskDraftChange: (String) -> Unit,
    onAddTask: (listId: String) -> Unit,
    onStartAddList: () -> Unit,
    onNewListNameChange: (String) -> Unit,
    onConfirmAddList: () -> Unit,
) {
    BottomSheetScrim(visible = visible, onDismiss = onDismiss) {
        SheetSurface {
            SheetHeader(title = "Your Lists", onClose = onDismiss)
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp).padding(horizontal = 18.dp).padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(lists, key = { it.id }) { list ->
                    ListCard(
                        list = list,
                        isActive = list.id == activeListId,
                        expanded = list.id == expandedListId,
                        taskDraft = taskDraft,
                        editingTaskId = editingTaskId,
                        editingText = editingText,
                        onSelect = { onSelect(list.id) },
                        onToggleExpand = { onToggleExpand(list.id) },
                        onRemoveList = { onRemoveList(list.id) },
                        onToggleTaskDone = { taskId -> onToggleTaskDone(list.id, taskId) },
                        onStartEditTask = onStartEditTask,
                        onEditingTextChange = onEditingTextChange,
                        onSaveEdit = { onSaveEdit(list.id) },
                        onRemoveTask = { taskId -> onRemoveTask(list.id, taskId) },
                        onTaskDraftChange = onTaskDraftChange,
                        onAddTask = { onAddTask(list.id) },
                    )
                }
                item {
                    if (addingList) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PlainTextField(
                                value = newListName,
                                onValueChange = onNewListNameChange,
                                placeholder = "List name…",
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                textStyle = TextStyle(color = TrColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                            )
                            GoldButton(text = "Save", onClick = onConfirmAddList, contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TrColors.lavenderFill, RoundedCornerShape(12.dp))
                                .dashedRoundedBorder(color = TrColors.lilacBorder, cornerRadius = 12.dp)
                                .clickable(onClick = onStartAddList)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "+ New List", fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TrColors.mutedPurple2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListCard(
    list: TaskList,
    isActive: Boolean,
    expanded: Boolean,
    taskDraft: String,
    editingTaskId: String?,
    editingText: String,
    onSelect: () -> Unit,
    onToggleExpand: () -> Unit,
    onRemoveList: () -> Unit,
    onToggleTaskDone: (String) -> Unit,
    onStartEditTask: (taskId: String, text: String) -> Unit,
    onEditingTextChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onRemoveTask: (String) -> Unit,
    onTaskDraftChange: (String) -> Unit,
    onAddTask: () -> Unit,
) {
    val doneCount = list.tasks.count { it.done }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(2.5.dp, TrColors.lilacBorderLight, RoundedCornerShape(18.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isActive) TrColors.hotPink else Color.Transparent)
                    .border(2.5.dp, if (isActive) TrColors.hotPink else TrColors.lilacBorder, CircleShape)
                    .clickable(onClick = onSelect),
            )
            Column(modifier = Modifier.weight(1f).clickable(onClick = onToggleExpand)) {
                Text(text = list.name, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TrColors.textPrimary)
                Text(text = "${list.tasks.size} tasks · $doneCount done", fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TrColors.mutedPurple)
            }
            Text(text = "✕", color = TrColors.removeIcon, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onRemoveList).padding(4.dp))
            Text(
                text = "▾",
                color = TrColors.mutedPurple,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable(onClick = onToggleExpand)
                    .padding(4.dp)
                    .rotate(if (expanded) 180f else 0f),
            )
        }
        if (expanded) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                list.tasks.forEach { task ->
                    TaskRow(
                        task = task,
                        isEditing = editingTaskId == task.id,
                        editingText = editingText,
                        onToggleDone = { onToggleTaskDone(task.id) },
                        onStartEdit = { onStartEditTask(task.id, task.text) },
                        onEditingTextChange = onEditingTextChange,
                        onSaveEdit = onSaveEdit,
                        onRemove = { onRemoveTask(task.id) },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PlainTextField(
                        value = taskDraft,
                        onValueChange = onTaskDraftChange,
                        placeholder = "Add a task…",
                        modifier = Modifier.weight(1f),
                    )
                    GoldButton(text = "Add", onClick = onAddTask, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskItem,
    isEditing: Boolean,
    editingText: String,
    onToggleDone: () -> Unit,
    onStartEdit: () -> Unit,
    onEditingTextChange: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TrColors.lavenderFill)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(if (task.done) TrColors.hotPink else Color.Transparent)
                .border(2.dp, if (task.done) TrColors.hotPink else TrColors.lilacBorder, RoundedCornerShape(5.dp))
                .clickable(onClick = onToggleDone),
            contentAlignment = Alignment.Center,
        ) {
            if (task.done) Text(text = "✓", color = Color.White, fontSize = 10.sp)
        }
        if (isEditing) {
            PlainTextField(
                value = editingText,
                onValueChange = onEditingTextChange,
                modifier = Modifier.weight(1f),
                background = Color.White,
                borderColor = TrColors.lilacBorder,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
            )
            Text(text = "✓", color = TrColors.hotPink, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onSaveEdit).padding(4.dp))
        } else {
            Text(
                text = task.text,
                modifier = Modifier.weight(1f).clickable(onClick = onStartEdit),
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (task.done) TrColors.mutedPurple else TrColors.textPrimary,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
            )
        }
        Text(text = "✕", color = TrColors.removeIcon, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onRemove))
    }
}

/** A dashed rounded-rect outline (Compose's built-in `border` only draws solid strokes). */
private fun Modifier.dashedRoundedBorder(color: Color, cornerRadius: Dp, strokeWidth: Dp = 2.5.dp): Modifier = drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)),
    )
    val inset = strokeWidth.toPx() / 2
    drawRoundRect(
        color = color,
        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
        size = androidx.compose.ui.geometry.Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
        style = stroke,
    )
}
