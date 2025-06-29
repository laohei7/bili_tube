package com.laohei.bili_tube.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.app.Route
import kotlinx.coroutines.delay


@Composable
internal fun SMSLoginScreen(
    phoneNumber: String,
    isPhoneNUmberError: Boolean,
    code: String,
    isCodeError: Boolean,
    isSendSMSCode: Boolean,
    selectedCountry: CountryItem,
    countryItems: List<CountryItem>,
    codeInteractionSource: MutableInteractionSource? = null,
    switchLoginType: (Route) -> Unit,
    onChangedCountry: (CountryItem) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onCodeChanged: (String) -> Unit,
    getSMSCode: () -> Unit,
    onLogin: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var countDownTimer by remember { mutableIntStateOf(60) }
    var enabledSendBtn by remember { mutableStateOf(true) }
    LaunchedEffect(isSendSMSCode) {
        if (isSendSMSCode.not()) {
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
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChanged,
            singleLine = true,
            isError = isPhoneNUmberError,
            label = {
                Text(text = stringResource(R.string.str_phone_number))
            },
            leadingIcon = {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { expanded = true }
                    ) {
                        Text(text = "+${selectedCountry.countryId}")
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = Icons.Outlined.ArrowDropDown.name,
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.heightIn(max = 400.dp),
                        onDismissRequest = { expanded = false }
                    ) {
                        countryItems.fastForEach {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            R.string.str_country_name,
                                            it.cname,
                                            it.countryId
                                        )
                                    )
                                },
                                onClick = {
                                    onChangedCountry(it)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChanged,
            isError = isCodeError,
            singleLine = true,
            interactionSource = codeInteractionSource,
            label = {
                Text(text = stringResource(R.string.str_sms_code))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Numbers,
                    contentDescription = Icons.Outlined.Numbers.name,
                )
            },
            trailingIcon = {
                TextButton(
                    enabled = enabledSendBtn,
                    onClick = {
                        getSMSCode.invoke()
                    }) {
                    Text(
                        text = when {
                            enabledSendBtn -> stringResource(R.string.str_get_sms_code)
                            else -> stringResource(R.string.str_resend_sms_code, countDownTimer)
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            Text(text = stringResource(R.string.str_login))
        }
        TextButton(onClick = { switchLoginType(Route.Login.Password) }) {
            Text(text = stringResource(R.string.str_password_login))
        }
        TextButton(onClick = { switchLoginType(Route.Login.QRCode) }) {
            Text(text = stringResource(R.string.str_scan_login))
        }
    }
}