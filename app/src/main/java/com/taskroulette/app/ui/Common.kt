package com.taskroulette.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskroulette.app.ui.theme.Baloo2
import com.taskroulette.app.ui.theme.TrColors

/** Gold/amber gradient CTA — matches the "Fill the Wheel" / "Claim Done" / "Add" buttons in the mockup. */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Brush.linearGradient(listOf(TrColors.gold, TrColors.amber)))
            .border(3.dp, Color.White, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = TrColors.goldText, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = fontSize)
    }
}

/** Outlined pill/rounded-rect used for secondary actions ("Spin Again", "Cancel"). */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White)
            .border(2.5.dp, TrColors.lilacBorder, shape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = TrColors.titlePurple, fontFamily = Baloo2, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

/** Small circular "✕" dismiss button used on every sheet header. */
@Composable
fun CloseChip(onClick: () -> Unit, modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 26.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(TrColors.hotPinkChipBg)
            .border(2.dp, Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "✕", color = TrColors.hotPink, fontSize = (size.value * 0.5).sp)
    }
}

@Composable
fun PlainTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    background: Color = TrColors.lavenderFill,
    borderColor: Color = TrColors.lilacBorderLight,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    textStyle: TextStyle = TextStyle(color = TrColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    singleLine: Boolean = true,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.5.dp, borderColor, shape)
            .padding(contentPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty() && placeholder.isNotEmpty()) {
            Text(text = placeholder, style = textStyle.copy(color = TrColors.mutedPurple))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            singleLine = singleLine,
            cursorBrush = androidx.compose.ui.graphics.SolidColor(TrColors.hotPink),
        )
    }
}
