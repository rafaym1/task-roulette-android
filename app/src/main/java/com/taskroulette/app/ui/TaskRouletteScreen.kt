package com.taskroulette.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskroulette.app.ui.theme.Baloo2
import com.taskroulette.app.ui.theme.Nunito
import com.taskroulette.app.ui.theme.TrColors
import com.taskroulette.app.viewmodel.TaskRouletteViewModel

@Composable
fun TaskRouletteScreen(viewModel: TaskRouletteViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val activeList = state.activeList
    val pool = state.activePool
    val n = pool.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(TrColors.bgMint, TrColors.bgSky, TrColors.bgLilac, TrColors.bgPink),
                    start = Offset(0f, 0f),
                    end = Offset(300f, 1200f),
                )
            ),
    ) {
        BackgroundDecor()

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                appName = viewModel.appName,
                activeListName = activeList?.name ?: "No list",
                onClick = viewModel::openModal,
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (n > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        WheelView(
                            pool = pool,
                            rotationTarget = state.rotation,
                            spinning = state.spinning,
                            onSpin = viewModel::spin,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.White)
                                .border(2.dp, TrColors.lilacBorder, RoundedCornerShape(999.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = "$n " + if (n == 1) "round loaded" else "rounds loaded",
                                fontFamily = Baloo2,
                                fontSize = 13.sp,
                                color = TrColors.titlePurple,
                                letterSpacing = 0.3.sp,
                            )
                        }
                    }
                } else {
                    EmptyState(
                        emptyMessage = emptyMessageFor(activeList),
                        onFillWheel = viewModel::openModal,
                    )
                }
            }

            BottomBar(
                activeListName = activeList?.name ?: "No list",
                onAccount = viewModel::openAccount,
                onAddList = viewModel::startAddListQuick,
            )
        }

        ResultSheet(
            visible = state.showResult,
            resultText = state.result?.text.orEmpty(),
            onDismiss = viewModel::closeResult,
            onClaimDone = viewModel::markResultDone,
            onRespin = viewModel::respin,
        )

        AccountSheet(
            visible = state.accountOpen,
            onDismiss = viewModel::closeAccount,
            onHowItWorks = viewModel::openHowItWorks,
            onPrivacyPolicy = viewModel::openPrivacyPolicy,
            onDeleteAccount = viewModel::confirmDeleteAccount,
        )

        HowItWorksSheet(visible = state.howItWorksOpen, onDismiss = viewModel::closeHowItWorks)

        PrivacyPolicySheet(visible = state.privacyPolicyOpen, onDismiss = viewModel::closePrivacyPolicy)

        DeleteConfirmDialog(
            visible = state.deleteConfirmOpen,
            onDismiss = viewModel::closeDeleteConfirm,
            onConfirm = viewModel::deleteAccount,
        )

        ListsSheet(
            visible = state.modalOpen,
            lists = state.lists,
            activeListId = state.activeListId,
            expandedListId = state.expandedListId,
            addingList = state.addingList,
            newListName = state.newListName,
            taskDraft = state.taskDraft,
            editingTaskId = state.editingTaskId,
            editingText = state.editingText,
            onDismiss = viewModel::closeModal,
            onSelect = viewModel::selectList,
            onToggleExpand = viewModel::toggleExpand,
            onRemoveList = viewModel::removeList,
            onToggleTaskDone = viewModel::toggleTaskDone,
            onStartEditTask = viewModel::startEditTask,
            onEditingTextChange = viewModel::onEditingTextChange,
            onSaveEdit = viewModel::saveEdit,
            onRemoveTask = viewModel::removeTask,
            onTaskDraftChange = viewModel::onTaskDraftChange,
            onAddTask = viewModel::addTask,
            onStartAddList = viewModel::startAddList,
            onNewListNameChange = viewModel::onNewListNameChange,
            onConfirmAddList = viewModel::confirmAddList,
        )
    }
}

private fun emptyMessageFor(activeList: com.taskroulette.app.model.TaskList?): String = when {
    activeList == null -> "No lists yet. Create one to get started."
    activeList.tasks.isEmpty() -> "This list is empty. Add tasks to fill the wheel."
    else -> "All tasks in this list are done. Add more, or reopen one."
}

@Composable
private fun Header(appName: String, activeListName: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 22.dp, end = 22.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = appName,
            fontFamily = Baloo2,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            letterSpacing = 0.5.sp,
            color = TrColors.titlePurple,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White)
                .border(2.5.dp, TrColors.lilacBorder, RoundedCornerShape(999.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = activeListName, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TrColors.titlePurple)
            Text(text = "▾", fontSize = 10.sp, color = TrColors.titlePurple.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun EmptyState(emptyMessage: String, onFillWheel: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .background(Color.White, CircleShape)
                .dashedCircle(TrColors.lilacBorder),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "no tasks loaded",
                fontFamily = Baloo2,
                fontSize = 13.sp,
                color = TrColors.mutedPurple,
                letterSpacing = 0.3.sp,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        Text(
            text = emptyMessage,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TrColors.bodyMuted,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 220.dp),
        )
        GoldButton(text = "Fill the Wheel", onClick = onFillWheel)
    }
}

@Composable
private fun BottomBar(activeListName: String, onAccount: () -> Unit, onAddList: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .topBorder(TrColors.lilacBorderLight, 3.dp)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3ECFB))
                .border(2.5.dp, TrColors.lilacBorder, CircleShape)
                .clickable(onClick = onAccount),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "☰", fontSize = 16.sp, color = TrColors.titlePurple)
        }
        Text(
            text = activeListName,
            fontFamily = Baloo2,
            fontSize = 12.sp,
            color = TrColors.mutedPurple2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 160.dp),
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(TrColors.gold, TrColors.amber)))
                .border(2.5.dp, Color.White, CircleShape)
                .clickable(onClick = onAddList),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "+", fontSize = 22.sp, color = TrColors.goldText)
        }
    }
}

@Composable
private fun BackgroundDecor() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 18.dp, y = 60.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(TrColors.blobYellow.copy(alpha = 0.55f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-10).dp, y = 140.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(TrColors.blobBlue.copy(alpha = 0.5f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = (-120).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(TrColors.blobPink.copy(alpha = 0.4f)),
        )
        TwinkleStar(sizeSp = 16, periodMs = 2200, delayMs = 0, modifier = Modifier.align(Alignment.TopStart).offset(x = 120.dp, y = 78.dp))
        TwinkleStar(sizeSp = 12, periodMs = 2600, delayMs = 400, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = 100.dp))
        TwinkleStar(sizeSp = 14, periodMs = 2400, delayMs = 800, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-24).dp, y = (-150).dp))
    }
}

private fun Modifier.dashedCircle(color: Color): Modifier = this.drawBehind {
    drawCircle(
        color = color,
        style = Stroke(
            width = 4.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
        ),
    )
}

private fun Modifier.topBorder(color: Color, width: Dp): Modifier = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, width.toPx() / 2),
        end = Offset(size.width, width.toPx() / 2),
        strokeWidth = width.toPx(),
    )
}
