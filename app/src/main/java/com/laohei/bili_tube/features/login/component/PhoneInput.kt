package com.laohei.bili_tube.features.login.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.constraintlayout.compose.layoutId
import com.laohei.bili_sdk.module_v2.location.CountryItem
import com.laohei.bili_tube.R
import com.laohei.bili_tube.ui.theme.ExtremeSmallPadding
import com.laohei.bili_tube.ui.theme.LargePadding
import com.laohei.bili_tube.ui.theme.SmallPadding


@Composable
internal fun PhoneInput(
    modifier: Modifier= Modifier,
    phone: String,
    isPhoneError: Boolean,
    selectedCountryId: String,
    countries: List<CountryItem>,
    onPhoneChange: (String) -> Unit,
    onCountryChange: (CountryItem) -> Unit,
) {
    Log.d("TAG", "PhoneInput: $isPhoneError")
    val density = LocalDensity.current
    val shape = RoundedCornerShape(SmallPadding)
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .layoutId("phone_input")
            .then(modifier)
    ) {
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = LargePadding * 2)
                .shadow(5.dp, shape),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Smartphone,
                    contentDescription = Icons.Rounded.Smartphone.name
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.str_phone_number),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(shape)
                            .clickable { expanded = true }
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = SmallPadding, vertical = ExtremeSmallPadding)
                    ) {
                        Text(text = "+${selectedCountryId}")
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = Icons.Outlined.ArrowDropDown.name,
                        )
                    }
                    if (expanded) {
                        Popup(
                            offset = IntOffset(
                                0, with(density) { (36).dp.toPx().toInt() },
                            ),
                            onDismissRequest = { expanded = false }
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(end = LargePadding)
                                    .heightIn(max = 400.dp)
                                    .widthIn(max = 200.dp),
                                shape = shape,
                                shadowElevation = 5.dp
                            ) {
                                LazyColumn {
                                    items(countries) {
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
                                                onCountryChange(it)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            shape = shape,
            singleLine = true,
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
        if (isPhoneError) {
            Text(
                modifier = Modifier
                    .padding(horizontal = LargePadding * 2)
                    .padding(top = SmallPadding),
                text = stringResource(R.string.str_phone_number_error),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

}