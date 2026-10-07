package me.weishu.kernelsu.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import me.weishu.kernelsu.ui.component.material.ExpressiveScaffold
import me.weishu.kernelsu.ui.component.material.SegmentedColumn
import me.weishu.kernelsu.ui.component.material.SegmentedListItem
import me.weishu.kernelsu.ui.component.material.TonalCard
import me.weishu.kernelsu.ui.component.material.expressiveTopAppBarColors
import me.weishu.kernelsu.ui.component.rebootlistpopup.RebootListPopup
import me.weishu.kernelsu.ui.component.statustag.StatusTag
import me.weishu.kernelsu.ui.theme.isInDarkTheme

@Composable
fun HomePagerMaterial(state: HomeUiState, actions: HomeActions, bottomInnerPadding: Dp) {
    ExpressiveScaffold(
        topBar = { TopBar() },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
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

@Composable
private fun TopBar() {
    TopAppBar(
        title = { BrandHeader() },
        actions = { RebootListPopup() },
        colors = expressiveTopAppBarColors(),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    )
}

@Composable
private fun BrandHeader(modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(
            painter = painterResource(R.drawable.ghost_su_logo),
            contentDescription = stringResource(R.string.ghost_app_name),
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
        )
        Column {
            Text(stringResource(R.string.ghost_app_name), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.ghostsu_home_brand_subtitle),
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OverviewHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(R.string.ghostsu_home_overview), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
        Text(
            stringResource(R.string.ghostsu_home_overview_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CompatibilityNotices(state: HomeUiState, actions: HomeActions) {
    if (state.showManagerPrBuildWarning) {
        WarningCard(stringResource(R.string.home_pr_build_warning), WarningLevel.Notice)
    } else if (state.showKernelPrBuildWarning) {
        WarningCard(stringResource(R.string.home_pr_kernel_warning), WarningLevel.Notice)
    }
    if (state.showGkiWarning) WarningCard(stringResource(R.string.home_gki_warning), WarningLevel.Notice)
    if (state.requiresNewKernel) {
        WarningCard(
            stringResource(if (state.canInstallKernelUpdate) R.string.require_kernel_version else R.string.require_kernel_version_gki),
            onClick = if (state.canInstallKernelUpdate) actions.onInstallClick else null,
        )
    }
    // Compatibility warning stays visible; only the application-update banner is removed.
    if (state.requiresNewManager) WarningCard(stringResource(R.string.require_manager_version))
    if (state.showLkmUpdate) {
        WarningCard(stringResource(R.string.home_lkm_update_available), WarningLevel.Notice, actions.onInstallClick)
    }
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
        installed -> MaterialTheme.colorScheme.tertiaryContainer
        notInstalled -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (rootActive) {
        if (dark) Color.White else Color(0xFF251648)
    } else MaterialTheme.colorScheme.contentColorFor(containerColor)
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(28.dp),
        onClick = { if (!state.isLateLoadMode) actions.onInstallClick() },
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 19.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(if (rootActive) R.string.ghostsu_home_root_access else R.string.ghostsu_home_system_status).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (rootActive) StatusTag(label = stringResource(R.string.ghostsu_home_active), backgroundColor = GhostHomeActiveDark, contentColor = GhostHomeActive)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(9.dp))
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(summary, style = MaterialTheme.typography.bodyLarge, color = contentColor.copy(alpha = 0.86f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (rootActive) StatusTag(label = stringResource(R.string.ghostsu_home_root_available), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (mode != null) StatusTag(label = mode, backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.isSafeMode) StatusTag(label = stringResource(R.string.safe_mode), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.isLateLoadMode) StatusTag(label = stringResource(R.string.home_jailbreak), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
                if (state.showCustomLkmBadge) StatusTag(label = stringResource(R.string.home_lkm_custom), backgroundColor = contentColor.copy(alpha = 0.14f), contentColor = contentColor)
            }
        }
    }
}

@Composable
private fun RebootNotice() {
    TonalCard(containerColor = MaterialTheme.colorScheme.surfaceVariant, content = {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            androidx.compose.material3.Icon(Icons.Filled.Info, contentDescription = null, tint = GhostHomeViolet)
            Text(
                stringResource(R.string.ghostsu_home_reboot_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    })
}

@Composable
private fun DeviceCard(state: HomeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeading(stringResource(R.string.ghostsu_home_device), state.systemInfo.deviceModel)
        SegmentedColumn(modifier = Modifier.fillMaxWidth()) {
            item { HomeInfoItem(Icons.Filled.Smartphone, stringResource(R.string.ghostsu_home_model), state.systemInfo.deviceModel) }
            item { HomeInfoItem(Icons.Filled.DeveloperBoard, stringResource(R.string.ghostsu_home_kernel), state.kernelVersion.homeDisplayValue()) }
        }
    }
}

@Composable
private fun HomeInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, content: String) {
    SegmentedListItem(
        headlineContent = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = { Text(content, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface) },
        leadingContent = { Icon(icon, contentDescription = label) },
    )
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
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeading(stringResource(R.string.ghostsu_home_security), stringResource(R.string.ghostsu_home_current_state))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecurityValue(Modifier.weight(1f), stringResource(R.string.ghostsu_home_selinux), selinux)
            SecurityValue(Modifier.weight(1f), stringResource(R.string.ghostsu_home_seccomp), seccomp)
        }
    }
}

@Composable
private fun SecurityValue(modifier: Modifier, label: String, value: String) {
    TonalCard(modifier = modifier, containerColor = MaterialTheme.colorScheme.surfaceVariant, content = {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    })
}

@Composable
private fun SectionHeading(title: String, trailing: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(trailing, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun WarningCard(message: String, level: WarningLevel = WarningLevel.Error, onClick: (() -> Unit)? = null) {
    val containerColor = when (level) {
        WarningLevel.Error -> MaterialTheme.colorScheme.errorContainer
        WarningLevel.Notice -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val content: @Composable () -> Unit = {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.contentColorFor(containerColor))
        }
    }
    if (onClick != null) TonalCard(containerColor = containerColor, onClick = onClick, content = content)
    else TonalCard(containerColor = containerColor, content = content)
}
