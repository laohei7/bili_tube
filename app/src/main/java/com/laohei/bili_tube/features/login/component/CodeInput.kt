package com.laohei.bili_tube.features.login.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.layoutId
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding
import kotlinx.coroutines.delay


@Composable
internal fun CodeInput(
    modifier: Modifier = Modifier,
    code: String,
    isCodeError: Boolean,
    isCodeSend: Boolean,
    onCodeChange: (String) -> Unit,
    onCodeSend: () -> Unit
) {
    var enabledSendBtn by remember { mutableStateOf(true) }
    var countDownTimer by remember { mutableIntStateOf(60) }
    val shape = RoundedCornerShape(SmallPadding)

    LaunchedEffect(isCodeSend) {
        if (isCodeSend.not()) {
            return@LaunchedEffect
        }
        enabledSendBtn = false
        countDownTimer = 60
        while (countDownTimer > 0) {
            delay(1000L)
            countDownTimer--
        }
        enabledSendBtn = true
    }

    Column(
        modifier = Modifier
            .layoutId("code_input")
            .then(modifier)
    ) {
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            isError = isCodeError,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = LargePadding * 2)
                .shadow(5.dp, shape),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = Icons.Rounded.Security.name
                )
            },
            trailingIcon = {
                Surface(
                    enabled = enabledSendBtn,
                    onClick = {
                        onCodeSend()
                    },
                    shape = shape,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = when {
                            enabledSendBtn -> stringResource(R.string.str_get_sms_code)
                            else -> stringResource(R.string.str_resend_sms_code, countDownTimer)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(SmallPadding),
                        textAlign = TextAlign.Center
                    )
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.str_sms_code),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            singleLine = true,
            shape = shape,
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.background,
                unfocusedBorderColor = MaterialTheme.colorScheme.background,
                errorBorderColor = MaterialTheme.colorScheme.background,
                disabledBorderColor = MaterialTheme.colorScheme.background,
            )
        )

        if (isCodeError) {
            Text(
                modifier = Modifier
                    .padding(horizontal = LargePadding * 2)
                    .padding(top = SmallPadding),
                text = stringResource(R.string.str_sms_code_error),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

}