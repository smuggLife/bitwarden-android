package com.bitwarden.authenticator.ui.platform.components.listitem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitwarden.authenticator.ui.platform.components.listitem.model.VaultDropdownMenuAction
import com.bitwarden.authenticator.ui.platform.components.listitem.model.VerificationCodeDisplayItem
import com.bitwarden.core.util.persistentListOfNotNull
import com.bitwarden.ui.platform.base.util.cardStyle
import com.bitwarden.ui.platform.components.animation.AnimateNullableContentVisibility
import com.bitwarden.ui.platform.components.appbar.action.BitwardenOverflowActionItem
import com.bitwarden.ui.platform.components.appbar.model.OverflowMenuItemData
import com.bitwarden.ui.platform.components.button.BitwardenStandardIconButton
import com.bitwarden.ui.platform.components.icon.BitwardenIcon
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.indicator.BitwardenCircularCountdownIndicator
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme
import kotlinx.coroutines.delay

/**
 * The verification code item displayed to the user.
 *
 * @param displayItem he model containing all relevant data to be displayed.
 * @param onItemClick The lambda function to be invoked when the item is clicked.
 * @param onDropdownMenuClick A lambda function invoked when a dropdown menu action is clicked.
 * @param cardStyle The card style to be applied to this item.
 * @param modifier The modifier for the item.
 */
@Composable
fun VaultVerificationCodeItem(
    displayItem: VerificationCodeDisplayItem,
    onItemClick: () -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    if (displayItem.isCodeHidden) {
        HiddenVaultVerificationCodeItem(
            displayItem = displayItem,
            onItemClick = onItemClick,
            onDropdownMenuClick = onDropdownMenuClick,
            cardStyle = cardStyle,
            modifier = modifier,
        )
        return
    }
    VaultVerificationCodeItem(
        authCode = displayItem.authCode,
        nextAuthCode = displayItem.nextAuthCode,
        primaryLabel = displayItem.title,
        secondaryLabel = displayItem.subtitle,
        periodSeconds = displayItem.periodSeconds,
        timeLeftSeconds = displayItem.timeLeftSeconds,
        alertThresholdSeconds = displayItem.alertThresholdSeconds,
        startIcon = displayItem.startIcon,
        onItemClick = onItemClick,
        onDropdownMenuClick = onDropdownMenuClick,
        showOverflow = displayItem.showOverflow,
        showMoveToBitwarden = displayItem.showMoveToBitwarden,
        cardStyle = cardStyle,
        modifier = modifier,
    )
}

/**
 * The verification code item displayed to the user.
 *
 * @param authCode The code for the item.
 * @param primaryLabel The label for the item. Represents the OTP issuer.
 * @param secondaryLabel The supporting label for the item. Represents the OTP account name.
 * @param periodSeconds The times span where the code is valid.
 * @param timeLeftSeconds The seconds remaining until a new code is needed.
 * @param alertThresholdSeconds The time threshold in seconds to display an expiration warning.
 * @param startIcon The leading icon for the item.
 * @param onItemClick The lambda function to be invoked when the item is clicked.
 * @param onDropdownMenuClick A lambda function invoked when a dropdown menu action is clicked.
 * @param showOverflow Whether overflow menu should be available or not.
 * @param showMoveToBitwarden Whether the option to move the item to Bitwarden is displayed.
 * @param cardStyle The card style to be applied to this item.
 * @param nextAuthCode The next verification code to preview when nearing expiration.
 * @param modifier The modifier for the item.
 */
@Suppress("LongMethod", "MagicNumber")
@Composable
fun VaultVerificationCodeItem(
    authCode: String,
    primaryLabel: String?,
    secondaryLabel: String?,
    periodSeconds: Int,
    timeLeftSeconds: Int,
    alertThresholdSeconds: Int,
    startIcon: IconData,
    onItemClick: () -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction) -> Unit,
    showOverflow: Boolean,
    showMoveToBitwarden: Boolean,
    cardStyle: CardStyle,
    nextAuthCode: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .testTag(tag = "Item")
            .defaultMinSize(minHeight = 60.dp)
            .cardStyle(
                cardStyle = cardStyle,
                onClick = onItemClick,
                role = Role.Button,
                paddingStart = 16.dp,
                paddingEnd = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        BitwardenIcon(
            iconData = startIcon,
            tint = BitwardenTheme.colorScheme.icon.primary,
            modifier = Modifier.size(size = 24.dp),
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(weight = 1f),
        ) {
            if (!primaryLabel.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.testTag(tag = "Name"),
                    text = primaryLabel,
                    style = BitwardenTheme.typography.bodyLarge,
                    color = BitwardenTheme.colorScheme.text.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (!secondaryLabel.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.testTag(tag = "Username"),
                    text = secondaryLabel,
                    style = BitwardenTheme.typography.bodyMedium,
                    color = BitwardenTheme.colorScheme.text.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        BitwardenCircularCountdownIndicator(
            modifier = Modifier.testTag(tag = "CircularCountDown"),
            timeLeftSeconds = timeLeftSeconds,
            periodSeconds = periodSeconds,
            alertThresholdSeconds = alertThresholdSeconds,
        )

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                modifier = Modifier.testTag(tag = "AuthCode"),
                text = authCode
                    .chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }
                    .joinToString(separator = " "),
                style = BitwardenTheme.typography.sensitiveInfoSmall.copy(
                    // Force LTR text direction so TOTP digit groups
                    // retain correct order in RTL locales
                    textDirection = TextDirection.Ltr,
                ),
                color = BitwardenTheme.colorScheme.text.primary,
            )

            AnimateNullableContentVisibility(
                targetState = nextAuthCode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                label = "AnimateNextCode",
            ) { code ->
                val nextCodeDescription = stringResource(
                    id = BitwardenString.next_code_x,
                    formatArgs = arrayOf(code),
                )
                Text(
                    modifier = Modifier
                        .testTag(tag = "NextAuthCode")
                        .semantics {
                            contentDescription = nextCodeDescription
                        },
                    text = code
                        .chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }
                        .joinToString(separator = " "),
                    style = BitwardenTheme.typography.sensitiveInfoSmall.copy(
                        // Force LTR text direction so TOTP digit groups
                        // retain correct order in RTL locales
                        textDirection = TextDirection.Ltr,
                    ),
                    color = BitwardenTheme.colorScheme.text.secondary,
                )
            }
        }

        if (showOverflow) {
            BitwardenOverflowActionItem(
                menuItemDataList = persistentListOfNotNull(
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.copy),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.COPY_CODE) },
                    ),
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.edit),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.EDIT) },
                    ),
                    if (showMoveToBitwarden) {
                        OverflowMenuItemData(
                            text = stringResource(id = BitwardenString.copy_to_bitwarden_vault),
                            onClick = {
                                onDropdownMenuClick(VaultDropdownMenuAction.COPY_TO_BITWARDEN)
                            },
                        )
                    } else {
                        null
                    },
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.delete_item),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.DELETE) },
                    ),
                ),
                vectorIconRes = BitwardenDrawable.ic_ellipsis_horizontal,
                testTag = "Options",
            )
        } else {
            BitwardenStandardIconButton(
                vectorIconRes = BitwardenDrawable.ic_copy,
                contentDescription = stringResource(id = BitwardenString.copy),
                onClick = onItemClick,
            )
        }
    }
}

/**
 * How long a revealed code stays visible before it is hidden again.
 */
private const val REVEAL_TIMEOUT_MILLIS = 30_000L

/**
 * Holds the id of the one item whose code is currently revealed, so revealing a code hides the
 * previously revealed one. Provided per list via [ProvideRevealedCodeState].
 */
private val LocalRevealedCodeId = staticCompositionLocalOf<MutableState<String?>?> { null }

/**
 * Lets all hidden-code items inside [content] share one revealed item: opening a code closes
 * the previously opened one.
 */
@Composable
fun ProvideRevealedCodeState(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRevealedCodeId provides remember { mutableStateOf<String?>(null) },
        content = content,
    )
}

/**
 * A compact verification code item whose code is hidden until the item is tapped.
 *
 * Collapsed, it shows a single line: issuer on the left, account name on the right.
 * Tapping reveals a second line with the countdown, the code in a large font and, if enabled,
 * the next code in a smaller font. Tapping a revealed item copies the code. The code is hidden
 * again after [REVEAL_TIMEOUT_MILLIS].
 */
@Suppress("LongMethod", "MagicNumber")
@Composable
private fun HiddenVaultVerificationCodeItem(
    displayItem: VerificationCodeDisplayItem,
    onItemClick: () -> Unit,
    onDropdownMenuClick: (VaultDropdownMenuAction) -> Unit,
    cardStyle: CardStyle,
    modifier: Modifier = Modifier,
) {
    val revealedCodeId = LocalRevealedCodeId.current
        ?: remember { mutableStateOf<String?>(null) }
    val isRevealed = revealedCodeId.value == displayItem.id
    if (isRevealed) {
        LaunchedEffect(Unit) {
            delay(REVEAL_TIMEOUT_MILLIS)
            if (revealedCodeId.value == displayItem.id) revealedCodeId.value = null
        }
    }

    Row(
        modifier = modifier
            .testTag(tag = "Item")
            .defaultMinSize(minHeight = 48.dp)
            .cardStyle(
                cardStyle = cardStyle,
                onClick = {
                    if (isRevealed) {
                        onItemClick()
                    } else {
                        revealedCodeId.value = displayItem.id
                    }
                },
                role = Role.Button,
                paddingStart = 16.dp,
                paddingTop = 4.dp,
                paddingEnd = 4.dp,
                paddingBottom = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            ) {
                Text(
                    modifier = Modifier
                        .testTag(tag = "Name")
                        .weight(weight = 1f),
                    text = displayItem.title,
                    style = BitwardenTheme.typography.bodyLarge,
                    color = BitwardenTheme.colorScheme.text.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = displayItem.subtitle
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier
                            .testTag(tag = "Username")
                            .widthIn(max = 160.dp),
                        text = subtitle,
                        style = BitwardenTheme.typography.bodyMedium,
                        color = BitwardenTheme.colorScheme.text.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            AnimatedVisibility(
                visible = isRevealed,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                label = "AnimateRevealCode",
            ) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                ) {
                    BitwardenCircularCountdownIndicator(
                        modifier = Modifier.testTag(tag = "CircularCountDown"),
                        timeLeftSeconds = displayItem.timeLeftSeconds,
                        periodSeconds = displayItem.periodSeconds,
                        alertThresholdSeconds = displayItem.alertThresholdSeconds,
                    )
                    Text(
                        modifier = Modifier.testTag(tag = "AuthCode"),
                        text = displayItem.authCode.toGroupedCode(),
                        style = BitwardenTheme.typography.sensitiveInfoMedium.copy(
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            textDirection = TextDirection.Ltr,
                        ),
                        color = BitwardenTheme.colorScheme.text.primary,
                    )
                    val nextCode = displayItem.nextAuthCode
                    if (nextCode != null) {
                        val nextCodeDescription = stringResource(
                            id = BitwardenString.next_code_x,
                            formatArgs = arrayOf(nextCode),
                        )
                        Text(
                            modifier = Modifier
                                .testTag(tag = "NextAuthCode")
                                .semantics { contentDescription = nextCodeDescription },
                            text = nextCode.toGroupedCode(),
                            style = BitwardenTheme.typography.sensitiveInfoSmall.copy(
                                textDirection = TextDirection.Ltr,
                            ),
                            color = BitwardenTheme.colorScheme.text.secondary,
                        )
                    }
                }
            }
        }

        if (displayItem.showOverflow) {
            BitwardenOverflowActionItem(
                menuItemDataList = persistentListOfNotNull(
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.copy),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.COPY_CODE) },
                    ),
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.edit),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.EDIT) },
                    ),
                    OverflowMenuItemData(
                        text = stringResource(
                            id = if (displayItem.favorite) {
                                BitwardenString.unfavorite
                            } else {
                                BitwardenString.favorite
                            },
                        ),
                        onClick = {
                            onDropdownMenuClick(VaultDropdownMenuAction.TOGGLE_FAVORITE)
                        },
                    ),
                    if (displayItem.showMoveToBitwarden) {
                        OverflowMenuItemData(
                            text = stringResource(id = BitwardenString.copy_to_bitwarden_vault),
                            onClick = {
                                onDropdownMenuClick(VaultDropdownMenuAction.COPY_TO_BITWARDEN)
                            },
                        )
                    } else {
                        null
                    },
                    OverflowMenuItemData(
                        text = stringResource(id = BitwardenString.delete_item),
                        onClick = { onDropdownMenuClick(VaultDropdownMenuAction.DELETE) },
                    ),
                ),
                vectorIconRes = BitwardenDrawable.ic_ellipsis_horizontal,
                testTag = "Options",
            )
        } else {
            BitwardenStandardIconButton(
                vectorIconRes = BitwardenDrawable.ic_copy,
                contentDescription = stringResource(id = BitwardenString.copy),
                onClick = onItemClick,
            )
        }
    }
}

/**
 * Groups a TOTP code into blocks of three digits, e.g. "123456" -> "123 456".
 */
private fun String.toGroupedCode(): String =
    chunked(size = 3) { it.padEnd(length = 3, padChar = ' ') }.joinToString(separator = " ")

@Suppress("MagicNumber")
@Preview(showBackground = true)
@Composable
private fun VerificationCodeItem_preview() {
    BitwardenTheme {
        VaultVerificationCodeItem(
            authCode = "123456",
            primaryLabel = "Issuer, AKA Name",
            secondaryLabel = "username@bitwarden.com",
            periodSeconds = 30,
            timeLeftSeconds = 15,
            alertThresholdSeconds = 7,
            startIcon = IconData.Local(BitwardenDrawable.ic_login_item),
            onItemClick = {},
            onDropdownMenuClick = {},
            showOverflow = true,
            modifier = Modifier.padding(horizontal = 16.dp),
            showMoveToBitwarden = true,
            cardStyle = CardStyle.Full,
            nextAuthCode = null,
        )
    }
}
