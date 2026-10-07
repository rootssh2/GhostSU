package me.weishu.kernelsu.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.component.WarningLevel
import me.weishu.kernelsu.ui.component.miuix.WarningCard
import me.weishu.kernelsu.ui.component.rebootlistpopup.RebootListPopupMiuix
import me.weishu.kernelsu.ui.component.statustag.StatusTag
import me.weishu.kernelsu.ui.theme.LocalEnableBlur
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.BlurredBar
import me.weishu.kernelsu.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.isDynamicColor
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomePagerMiuix(state: HomeUiState, actions: HomeActions, bottomInnerPadding: Dp) {
    val scrollBehavior = MiuixScrollBehavior()
    val backdrop = rememberBlurBackdrop(LocalEnableBlur.current)
    val barColor = if (backdrop != null) Color.Transparent else colorScheme.surface
    Scaffold(
        topBar = { TopBar(scrollBehavior, backdrop, barColor) },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = innerPadding,
                overscrollEffect = null,
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        BrandHeader()
                        OverviewHeader()
                        CompatibilityNotices(state, actions)
                        StatusCard(state, actions)
                        RebootNotice()
                        DeviceCard(state)
                        SecurityCard(state.systemInfo)
                        Spacer(Modifier.height(bottomInnerPadding + if (!Natives.isFullFeatured())
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(scrollBehavior: ScrollBehavior, backdrop: LayerBackdrop?, barColor: Color) {
    BlurredBar(backdrop) {
        TopAppBar(
            color = barColor,
            title = "",
            actions = { RebootListPopupMiuix() },
            scrollBehavior = scrollBehavior,
        )
    }
}

@Composable
private fun BrandHeader(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(
            painter = painterResource(R.drawable.ghost_su_logo),
            contentDescription = stringResource(R.string.ghost_app_name),
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.ghost_app_name), fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onBackground)
            Text(
                stringResource(R.string.ghostsu_home_brand_subtitle),
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                color = colorScheme.onSurfaceVariantSummary,
            )
        }
    }
}

@Composable
private fun OverviewHeader() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.ghostsu_home_overview), fontSize = 38.sp, fontWeight = FontWeight.SemiBold, color = colorScheme.onBackground)
        Text(
            stringResource(R.string.ghostsu_home_overview_subtitle),
            fontSize = MiuixTheme.textStyles.body1.fontSize,
            color = colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun CompatibilityNotices(state: HomeUiState, actions: HomeActions) {
    if (state.showManagerPrBuildWarning) WarningCard(stringResource(R.string.home_pr_build_warning), level = WarningLevel.Notice)
    else if (state.showKernelPrBuildWarning) WarningCard(stringResource(R.string.home_pr_kernel_warning), level = WarningLevel.Notice)
    if (state.showGkiWarning) WarningCard(stringResource(R.string.home_gki_warning), level = WarningLevel.Notice)
    if (state.requiresNewKernel) {
        WarningCard(
            stringResource(if (state.canInstallKernelUpdate) R.string.require_kernel_version else R.string.require_kernel_version_gki),
            onClick = if (state.canInstallKernelUpdate) actions.onInstallClick else null,
        )
    }
    // Compatibility warning stays visible; only the application-update banner is removed.
    if (state.requiresNewManager) WarningCard(stringResource(R.string.require_manager_version))
    if (state.showLkmUpdate) WarningCard(stringResource(R.string.home_lkm_update_available), level = WarningLevel.Notice, onClick = actions.onInstallClick)
    if (state.showRootWarning) WarningCard(stringResource(R.string.grant_root_failed))
}

@Composable
private fun StatusCard(state: HomeUiState, actions: HomeActions) {
    val installed = state.ksuVersion != null
    val rootActive = installed && state.isRootAvailable
    val notInstalled = !installed && state.kernelVersion.isGKI()
    val dark = isInDarkTheme()
    val containerColor = when {
        rootActive -> if (dark) GhostHomeVioletDark else GhostHomeVioletLight
        installed && isDynamicColor -> colorScheme.secondaryContainer
        else -> colorScheme.surface
    }
    val contentColor = if (rootActive) {
        if (dark) Color.White else Color(0xFF251648)
    } else colorScheme.onBackground
    val icon = when {
        rootActive -> Icons.Rounded.CheckCircle
        notInstalled -> Icons.Rounded.Warning
        else -> Icons.Rounded.Block
    }
    val title = when {
        rootActive -> stringResource(R.string.ghostsu_home_root_active)
        installed -> stringResource(R.string.ghostsu_home_installed)
        notInstalled -> stringResource(R.string.home_not_installed)
        else -> stringResource(R.string.home_unsupported)
    }
    val summary = when {
        rootActive -> stringResource(R.string.ghostsu_home_root_active_summary)
        installed -> stringResource(R.string.ghostsu_home_root_unavailable_summary)
        notInstalled -> stringResource(R.string.home_click_to_install)
        else -> stringResource(R.string.home_unsupported_reason)
    }
    val mode = when (state.lkmMode) { true -> "LKM"; false -> "GKI"; null -> null }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = containerColor),
        onClick = { if (!state.isLateLoadMode) actions.onInstallClick() },
        showIndication = !state.isLateLoadMode,
        pressFeedbackType = PressFeedbackType.Tilt,
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 19.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.ghostsu_home_system_status).uppercase(),
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
                if (rootActive) StatusTag(label = stringResource(R.string.ghostsu_home_active), backgroundColor = GhostHomeActiveDark, contentColor = GhostHomeActive)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                top.yukonga.miuix.kmp.basic.Icon(imageVector = icon, contentDescription = title, modifier = Modifier.padding(end = 8.dp), tint = contentColor)
                Text(title, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
            }
            Text(summary, fontSize = MiuixTheme.textStyles.body1.fontSize, color = contentColor.copy(alpha = 0.86f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (rootActive) StatusTag(label = stringResource(R.string.ghostsu_home_root_available), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (mode != null) StatusTag(label = mode, backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.isSafeMode) StatusTag(label = stringResource(R.string.safe_mode), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.isLateLoadMode) StatusTag(label = stringResource(R.string.home_jailbreak), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.showCustomLkmBadge) StatusTag(label = stringResource(R.string.home_lkm_custom), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
            }
            if (notInstalled && state.isSELinuxPermissive) {
                TextButton(text = stringResource(R.string.home_jailbreak), onClick = actions.onJailbreakClick)
            }
        }
    }
}

@Composable
private fun RebootNotice() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.defaultColors(color = colorScheme.surface)) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            top.yukonga.miuix.kmp.basic.Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = GhostHomeViolet)
            Text(stringResource(R.string.ghostsu_home_reboot_hint), fontSize = MiuixTheme.textStyles.body2.fontSize, color = colorScheme.onSurfaceVariantSummary)
        }
    }
}

@Composable
private fun DeviceCard(state: HomeUiState) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeading(stringResource(R.string.ghostsu_home_device), state.systemInfo.deviceModel)
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.defaultColors(color = colorScheme.surface)) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(19.dp)) {
                InfoText(Icons.Filled.Smartphone, stringResource(R.string.ghostsu_home_model), state.systemInfo.deviceModel)
                InfoText(Icons.Filled.DeveloperBoard, stringResource(R.string.ghostsu_home_kernel), state.kernelVersion.homeDisplayValue())
            }
        }
    }
}

@Composable
private fun InfoText(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        top.yukonga.miuix.kmp.basic.Icon(imageVector = icon, contentDescription = title, modifier = Modifier.padding(end = 12.dp), tint = GhostHomeViolet)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = MiuixTheme.textStyles.body1.fontSize, color = colorScheme.onSurfaceVariantSummary)
            Text(content, fontSize = MiuixTheme.textStyles.headline1.fontSize, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
        }
    }
}

@Composable
private fun SecurityCard(systemInfo: SystemInfo) {
    val selinux = when (systemInfo.selinuxStatus) {
        "Enforcing" -> stringResource(R.string.selinux_status_enforcing)
        "Permissive" -> stringResource(R.string.selinux_status_permissive)
        "Disabled" -> stringResource(R.string.selinux_status_disabled)
        else -> stringResource(R.string.selinux_status_unknown)
    }
    val seccomp = when (systemInfo.seccompStatus) {
        -1 -> stringResource(R.string.seccomp_status_not_supported)
        0 -> stringResource(R.string.seccomp_status_disabled)
        1 -> stringResource(R.string.seccomp_status_strict)
        2 -> stringResource(R.string.seccomp_status_filter)
        else -> stringResource(R.string.seccomp_status_unknown)
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeading(stringResource(R.string.ghostsu_home_security), stringResource(R.string.ghostsu_home_current_state))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecurityValue(Modifier.weight(1f), stringResource(R.string.ghostsu_home_selinux), selinux)
            SecurityValue(Modifier.weight(1f), stringResource(R.string.ghostsu_home_seccomp), seccomp)
        }
    }
}

@Composable
private fun SecurityValue(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier, colors = CardDefaults.defaultColors(color = colorScheme.surface)) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(label, fontSize = MiuixTheme.textStyles.body2.fontSize, color = colorScheme.onSurfaceVariantSummary)
            Text(value, fontSize = MiuixTheme.textStyles.headline2.fontSize, fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface)
        }
    }
}

@Composable
private fun SectionHeading(title: String, trailing: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Text(title, fontSize = MiuixTheme.textStyles.headline2.fontSize, fontWeight = FontWeight.SemiBold, color = colorScheme.onBackground)
        Text(trailing, fontSize = MiuixTheme.textStyles.body2.fontSize, color = colorScheme.onSurfaceVariantSummary)
    }
}
