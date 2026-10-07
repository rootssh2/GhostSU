package me.weishu.kernelsu.ghost.ui
import me.weishu.kernelsu.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.Card as MaterialCard
import androidx.compose.material3.CardDefaults as MaterialCardDefaults
import androidx.compose.material3.DropdownMenu as MaterialDropdownMenu
import androidx.compose.material3.DropdownMenuItem as MaterialDropdownMenuItem
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.LargeTopAppBar as MaterialLargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton as MaterialOutlinedButton
import androidx.compose.material3.Scaffold as MaterialScaffold
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.TopAppBarDefaults as MaterialTopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.ghost.domain.model.CpuPair
import me.weishu.kernelsu.ghost.domain.model.ExecutionFieldValue
import me.weishu.kernelsu.ghost.domain.model.ProfileFieldNode
import me.weishu.kernelsu.ghost.domain.model.ShizukuStatus
import me.weishu.kernelsu.ghost.domain.model.UserProfileFile
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.theme.MiuixKernelSUTheme
import me.weishu.kernelsu.ui.theme.ThemeController
import me.weishu.kernelsu.ui.util.rootAvailable
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Copy
import top.yukonga.miuix.kmp.nav.core.NavBackStack
import top.yukonga.miuix.kmp.nav.core.NavDisplay
import top.yukonga.miuix.kmp.nav.core.NavDisplayEffects
import top.yukonga.miuix.kmp.nav.core.NavKey
import top.yukonga.miuix.kmp.nav.core.navBackStackOf
import top.yukonga.miuix.kmp.nav.core.rememberNavSystemCornerRadius
import top.yukonga.miuix.kmp.nav.transition.NavSwipeDirection
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlaySpinnerPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

data class GhostlockUiState(
    val deviceName: String = "",
    val kernelRelease: String = "",
    val socName: String = "",
    val manufacturerName: String = "",
    val ramTotal: String = "",
    val ramAvailable: String = "",
    val cpuCores: String = "",
    val architecture: String = "",
    val androidVersion: String = "",
    val kernelSupported: Boolean = false,
    val shizukuEnabled: Boolean = false,
    val shizukuStatus: ShizukuStatus = ShizukuStatus.NOT_REQUIRED,
    val running: Boolean = false,
    val cpuPairLabels: List<String> = emptyList(),
    val cpuPairIndex: Int = 0,
    /** CPU pair from the resolved profile when it differs from the device pick. */
    val customCpuPair: CpuPair? = null,
    val safeModeEnabled: Boolean = false,
    val forceAttackTestEnabled: Boolean = false,
    val tcpRouteEnabled: Boolean = true,
    val compact: Boolean = false,
    val executionSheetVisible: Boolean = false,
    val executionSheetDismissible: Boolean = false,
    val dialogVisible: Boolean = false,
    val dialogType: DialogType = DialogType.NONE,
    val dialogTitleRes: Int = 0,
    val dialogMessage: String = "",
    val dialogMessageRes: Int = 0,
    val dialogItems: List<String> = emptyList(),
    val dialogItemResIds: List<Int> = emptyList(),
    val dialogCurrentItemIndex: Int = -1,
    val dialogInput: String = "",
    val dialogConfirmLabelRes: Int = R.string.ghost_parse_start,
    /** Documentation URL shown as an extra button on a NOTICE dialog. */
    val dialogDocUrl: String? = null,
    val overwriteDialogVisible: Boolean = false,
    val overwriteMessage: String = "",
    val logLines: List<GhostlockLogLine> = emptyList(),
    val executionRelease: String = "",
    val executionHasProfile: Boolean = false,
    val executionFields: List<ExecutionFieldValue> = emptyList(),
    val executionEditing: Map<String, String> = emptyMap(),
    val advancedScreenVisible: Boolean = false,
    val debugExportEnabled: Boolean = true,
    val debugExportLocation: String = "",
    val debugKernelLogEnabled: Boolean = true,
    val aboutVisible: Boolean = false,
    val parametersVisible: Boolean = false,
    val profileOverrideVisible: Boolean = false,
    val advancedOverrideVisible: Boolean = false,
    /** Stored document edited by the open session; null for the builtin. */
    val editTargetName: String? = null,
    val profileOverrideRelease: String = "",
    val profileOverrideRoots: List<ProfileFieldNode> = emptyList(),
    val profileOverrideEditing: Map<String, String> = emptyMap(),
    /** Controller-reported geometry violations, dotted paths. */
    val profileInvalidPaths: Set<String> = emptySet(),
    /** Explicit route from the profile; null means geometry inference. */
    val profileRoute: String? = null,
    /** Declared fallback route; null/"none" means disabled. */
    val profileFallback: String? = null,
    /** Manually selected builtin source; null means automatic matching. */
    val activeBuiltinProfile: String? = null,
    val builtinScreenVisible: Boolean = false,
    /** Unfilled reference templates, listed separately on the builtin picker. */
    val builtinTemplates: List<String> = emptyList(),
    /** Builtin releases sorted by similarity to the device kernel. */
    val builtinProfiles: List<String> = emptyList(),
    val loadConfigVisible: Boolean = false,
    /** Verbatim documents in the user profile folder, newest first. */
    val userProfiles: List<UserProfileFile> = emptyList(),
    /** Loaded user document feeding the imported layer; null means none. */
    val activeUserProfile: String? = null,
    /** File name of the open user-profile detail screen, null when closed. */
    val userProfileDetail: String? = null,
    val userProfileRenameTarget: String? = null,
    val userProfileDeleteTarget: String? = null,
)

enum class DialogType { NONE, LIST, INPUT, CONFIRM, NOTICE }

data class GhostlockLogLine(val text: String, val color: Int)

interface GhostlockActions {
    fun onRun()
    fun onProfileInvalid()
    fun onStatusClick()
    fun onCloseExecutionSheet()
    fun onCopyLogs()
    fun onImportOffsetsHocon()
    fun onImportOffsetsJson()
    fun onDocumentsResult(request: DocumentRequest, uris: List<String>)
    fun onParseOta()
    fun onParseImage()
    fun onCpuPairSelected(index: Int)
    fun onSafeModeChanged(enabled: Boolean)
    fun onForceAttackTestChanged(enabled: Boolean)
    fun onShizukuChanged(enabled: Boolean)
    fun onDialogItemSelected(index: Int)
    fun onDialogInputChange(value: String)
    fun onDialogConfirm(value: String)
    fun onDialogDismiss()
    fun onDialogDismissFinished()
    fun onOverwriteConfirm()
    fun onOverwriteDismiss()
    fun onExecutionFieldChanged(path: String, value: String)
    fun onRouteChanged(index: Int)
    fun onFallbackChanged(index: Int)
    fun onExportProfile()
    fun onSaveProfileEdits()
    fun onSaveProfileAs()
    fun onExportProfileEdits()
    fun onRevertProfileEdits()
    fun onOpenAdvanced()
    fun onCloseAdvanced()
    fun onShowAbout()
    fun onCloseAbout()
    fun onDebugExportChanged(enabled: Boolean)
    fun onDebugExportLocationPick()
    fun onDebugKernelLogChanged(enabled: Boolean)
    fun onOpenParameters()
    fun onCloseParameters()
    fun onOpenLoadConfig()
    fun onCloseLoadConfig()
    fun onOpenUserProfileDetail(name: String)
    fun onCloseUserProfileDetail()
    fun onLoadUserProfile(name: String)
    fun onUnloadUserProfile()
    fun onEditUserProfile(name: String)
    fun onUserProfileRename(name: String)
    fun onUserProfileExport(name: String)
    fun onConvertUserProfile(name: String)
    fun onUserProfileDelete(name: String)
    fun onUserProfileDeleteConfirm()
    fun onUserProfileDeleteDismiss()
    fun onOpenBuiltinProfiles()
    fun onCloseBuiltinProfiles()
    fun onSelectBuiltinProfile(release: String?)
    fun onOpenProfileOverrides()
    fun onCloseProfileOverrides()
    fun onOpenAdvancedOverrides()
    fun onCloseAdvancedOverrides()
    fun onProfileOverrideChanged(path: String, value: String)
}

internal sealed interface GhostlockScreen : NavKey {
    data object Main : GhostlockScreen
    data object Advanced : GhostlockScreen
    data object About : GhostlockScreen
    data object Parameters : GhostlockScreen
    data object LoadConfig : GhostlockScreen
    data object Builtin : GhostlockScreen
    data class UserProfileDetail(val name: String) : GhostlockScreen
    data object ProfileOverride : GhostlockScreen
    data object AdvancedOverride : GhostlockScreen
}

internal fun navigationPath(state: GhostlockUiState): List<GhostlockScreen> {
    val path = mutableListOf<GhostlockScreen>(GhostlockScreen.Main)
    if (!state.advancedScreenVisible) return path
    path += GhostlockScreen.Advanced
    if (state.aboutVisible) {
        path += GhostlockScreen.About
        return path
    }
    if (!state.parametersVisible) return path
    path += GhostlockScreen.Parameters
    if (state.loadConfigVisible) {
        path += GhostlockScreen.LoadConfig
        when {
            state.builtinScreenVisible -> path += GhostlockScreen.Builtin
            state.userProfileDetail != null ->
                path += GhostlockScreen.UserProfileDetail(state.userProfileDetail)
        }
    }
    if (state.profileOverrideVisible) {
        path += GhostlockScreen.ProfileOverride
        if (state.advancedOverrideVisible) path += GhostlockScreen.AdvancedOverride
    }
    return path
}

internal fun syncNavigationPath(backStack: NavBackStack, desired: List<GhostlockScreen>) {
    var common = 0
    while (common < backStack.size && common < desired.size &&
        backStack[common] == desired[common]
    ) {
        common++
    }
    while (backStack.size > common) backStack.removeAt(backStack.lastIndex)
    backStack.addAll(desired.drop(common))
}

private fun closeScreen(screen: GhostlockScreen, actions: GhostlockActions) {
    when (screen) {
        GhostlockScreen.Main -> Unit
        GhostlockScreen.Advanced -> actions.onCloseAdvanced()
        GhostlockScreen.About -> actions.onCloseAbout()
        GhostlockScreen.Parameters -> actions.onCloseParameters()
        GhostlockScreen.LoadConfig -> actions.onCloseLoadConfig()
        GhostlockScreen.Builtin -> actions.onCloseBuiltinProfiles()
        is GhostlockScreen.UserProfileDetail -> actions.onCloseUserProfileDetail()
        GhostlockScreen.ProfileOverride -> actions.onCloseProfileOverrides()
        GhostlockScreen.AdvancedOverride -> actions.onCloseAdvancedOverrides()
    }
}

@Composable
internal fun GhostlockApp(
    state: GhostlockUiState,
    actions: GhostlockActions,
) {
    val uiMode = LocalUiMode.current
    val desiredPath = navigationPath(state)
    val backStack = remember {
        navBackStackOf(GhostlockScreen.Main).apply { addAll(desiredPath.drop(1)) }
    }
    LaunchedEffect(desiredPath) { syncNavigationPath(backStack, desiredPath) }
    val swipeBack = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
        NavSwipeDirection.RightToLeft
    } else {
        NavSwipeDirection.LeftToRight
    }
    /* A completed exploit can leave root available after a soft reboot while
     * the old log is still on screen. Read the native state instead of using
     * the last log line as a proxy for an active session. */
    val rootActive by produceState(
        initialValue = false,
        key1 = state.running,
        key2 = state.executionSheetVisible,
        key3 = state.logLines.size,
    ) {
        value = withContext(Dispatchers.IO) {
            runCatching { Natives.isManager && rootAvailable() }.getOrDefault(false)
        }
    }

    when (uiMode) {
        UiMode.Miuix -> {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GhostlockNavDisplay(
                        state = state,
                        actions = actions,
                        backStack = backStack,
                        swipeBack = swipeBack,
                        uiMode = uiMode,
                        rootActive = rootActive,
                    )
                    GhostlockOverlays(state = state, actions = actions, uiMode = uiMode)
                }
            }
        }

        UiMode.Material -> {
            Box(modifier = Modifier.fillMaxSize()) {
                GhostlockNavDisplay(
                    state = state,
                    actions = actions,
                    backStack = backStack,
                    swipeBack = swipeBack,
                    uiMode = uiMode,
                    rootActive = rootActive,
                )
                /* The existing profile/log overlays remain available in
                 * Material mode; they are scoped with Miuix only while
                 * shown so the Material main screen is not forced through
                 * a Miuix theme. */
                GhostlockOverlays(state = state, actions = actions, uiMode = uiMode)
            }
        }
    }
}

@Composable
private fun GhostlockNavDisplay(
    state: GhostlockUiState,
    actions: GhostlockActions,
    backStack: NavBackStack,
    swipeBack: NavSwipeDirection,
    uiMode: UiMode,
    rootActive: Boolean,
) {
    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        effects = NavDisplayEffects(cornerClipRadius = rememberNavSystemCornerRadius()),
        onBack = {
            when {
                state.executionSheetVisible -> {
                    if (state.executionSheetDismissible) actions.onCloseExecutionSheet()
                }

                state.overwriteDialogVisible -> actions.onOverwriteDismiss()
                state.dialogVisible -> actions.onDialogDismiss()
                state.userProfileDeleteTarget != null -> actions.onUserProfileDeleteDismiss()
                else -> {
                    val screen = backStack.lastOrNull() as? GhostlockScreen
                    if (screen != null && screen != GhostlockScreen.Main) {
                        closeScreen(screen, actions)
                        backStack.removeAt(backStack.lastIndex)
                    }
                }
            }
        },
    ) {
        entry<GhostlockScreen.Main>(swipeDismiss = NavSwipeDirection.None) {
            if (uiMode == UiMode.Material) {
                MaterialMainScreen(state = state, actions = actions, rootActive = rootActive)
            } else {
                MainScreen(state = state, actions = actions, rootActive = rootActive)
            }
        }
        entry<GhostlockScreen.Advanced>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { AdvancedScreen(state = state, actions = actions) }
        }
        entry<GhostlockScreen.About>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { AboutScreen(onBack = actions::onCloseAbout) }
        }
        entry<GhostlockScreen.Parameters>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { ParameterScreen(state = state, actions = actions) }
        }
        entry<GhostlockScreen.LoadConfig>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { LoadConfigScreen(state = state, actions = actions) }
        }
        entry<GhostlockScreen.Builtin>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { BuiltinProfileScreen(state = state, actions = actions) }
        }
        entry<GhostlockScreen.UserProfileDetail>(swipeDismiss = swipeBack) { screen ->
            GhostlockMiuixEntry(uiMode) {
                UserProfileDetailScreen(state = state, actions = actions, name = screen.name)
            }
        }
        entry<GhostlockScreen.ProfileOverride>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { ProfileOverrideScreen(state = state, actions = actions) }
        }
        entry<GhostlockScreen.AdvancedOverride>(swipeDismiss = swipeBack) {
            GhostlockMiuixEntry(uiMode) { AdvancedOverrideScreen(state = state, actions = actions) }
        }
    }
}

@Composable
private fun GhostlockMiuixEntry(uiMode: UiMode, content: @Composable () -> Unit) {
    if (uiMode == UiMode.Material) {
        MiuixKernelSUTheme(appSettings = ThemeController.getAppSettings(), content = content)
    } else {
        content()
    }
}


@Composable
private fun GhostlockOverlays(
    state: GhostlockUiState,
    actions: GhostlockActions,
    uiMode: UiMode,
) {
    GhostlockMiuixEntry(uiMode) {
        GhostlockDialog(state = state, actions = actions)
        GhostlockOverwriteDialog(state = state, actions = actions)
        GhostlockExecutionSheet(state = state, actions = actions)
    }
}

@Composable
private fun MainScreen(
    state: GhostlockUiState,
    actions: GhostlockActions,
    rootActive: Boolean,
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.ghost_app_name),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        MainContent(
        state = state,
        actions = actions,
        rootActive = rootActive,
        scrollBehavior = scrollBehavior,
        scaffoldPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
    )
}
}

@Composable
private fun MaterialMainScreen(
    state: GhostlockUiState,
    actions: GhostlockActions,
    rootActive: Boolean,
) {
    val scrollBehavior = MaterialTopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    MaterialScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MaterialLargeTopAppBar(
                title = { MaterialText(text = stringResource(R.string.ghost_app_name)) },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.ghost_su_logo),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 16.dp).size(34.dp),
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .imePadding(),
            contentPadding = pageContentPadding(scaffoldPadding, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "material-brand") { MaterialBrandHeader(state = state) }
            item(key = "material-status") {
                MaterialStatusCard(
                    state = state,
                    rootActive = rootActive,
                    onParametersClick = actions::onOpenParameters,
                    onShizukuClick = actions::onStatusClick,
                )
            }
            item(key = "material-device") { MaterialDeviceCard(state = state) }
            item(key = "material-controls") {
                MaterialControlsCard(state = state, actions = actions)
            }
            item(key = "material-advanced") {
                MaterialCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = actions::onOpenAdvanced,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MaterialText(
                            text = stringResource(R.string.ghost_advanced_settings),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        MaterialText(
                            text = stringResource(R.string.ghost_advanced_settings_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
            }
            item(key = "material-run") {
                MaterialRunButton(
                    state = state,
                    rootActive = rootActive,
                    onClick = actions::onRun,
                    onBlockedClick = actions::onProfileInvalid,
                )
            }
        }
    }
}

@Composable
private fun MaterialBrandHeader(state: GhostlockUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ghost_su_logo),
            contentDescription = null,
            modifier = Modifier.size(58.dp),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            MaterialText(
                text = stringResource(R.string.ghost_app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            MaterialText(
                text = displayKernelRelease(state.kernelRelease),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MaterialStatusCard(
    state: GhostlockUiState,
    rootActive: Boolean,
    onParametersClick: () -> Unit,
    onShizukuClick: () -> Unit,
) {
    val accessReady = !state.shizukuEnabled || state.shizukuStatus == ShizukuStatus.READY
    val profileReady = state.executionHasProfile && state.profileInvalidPaths.isEmpty()
    val ready = rootActive || (state.kernelSupported && profileReady && accessReady)
    val missing = !state.kernelSupported || !state.executionHasProfile
    val container = when {
        ready -> if (rootActive) Color(0xFFDFFAE4) else MaterialTheme.colorScheme.primaryContainer
        missing -> MaterialTheme.colorScheme.errorContainer
        else -> Color(0xFFFFF0DB)
    }
    val content = when {
        rootActive -> Color(0xFF173F35)
        ready -> MaterialTheme.colorScheme.onPrimaryContainer
        missing -> MaterialTheme.colorScheme.onErrorContainer
        else -> Color(0xFF5E4200)
    }
    val icon = when {
        ready -> Icons.Rounded.Security
        missing -> Icons.Rounded.WarningAmber
        else -> Icons.Rounded.ErrorOutline
    }
    val titleRes = when {
        ready && rootActive -> R.string.ghostsu_ghost_status_active
        ready -> R.string.ghostsu_ghost_status_ready
        !state.kernelSupported -> R.string.ghost_kernel_unsupported
        !state.executionHasProfile -> R.string.ghost_kernel_profile_required
        else -> R.string.ghost_kernel_profile_invalid
    }
    val summaryRes = when {
        ready && rootActive -> R.string.ghostsu_ghost_status_active_summary
        ready && state.shizukuEnabled -> R.string.ghost_kernel_profile_ready_shizuku
        ready -> R.string.ghostsu_ghost_status_ready_summary
        !state.kernelSupported -> R.string.ghost_kernel_unsupported_summary
        !state.executionHasProfile -> R.string.ghost_kernel_profile_required_summary
        state.profileInvalidPaths.isNotEmpty() -> R.string.ghost_kernel_profile_invalid_summary
        state.shizukuStatus == ShizukuStatus.PERMISSION_REQUIRED ->
            R.string.ghost_shizuku_status_permission_required
        else -> R.string.ghost_shizuku_status_not_running
    }
    MaterialCard(
        modifier = Modifier.fillMaxWidth(),
        colors = MaterialCardDefaults.cardColors(containerColor = container),
        onClick = if (state.kernelSupported && profileReady && !accessReady) {
            onShizukuClick
        } else {
            onParametersClick
        },
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MaterialIcon(
                imageVector = icon,
                contentDescription = null,
                tint = if (rootActive) Color(0xFF208A45) else content,
                modifier = Modifier.size(32.dp),
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                MaterialText(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = content,
                    fontWeight = FontWeight.SemiBold,
                )
                MaterialText(
                    text = stringResource(summaryRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = content.copy(alpha = 0.82f),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun MaterialDeviceCard(state: GhostlockUiState) {
    MaterialCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            MaterialText(
                text = stringResource(R.string.ghost_device_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            MaterialInfoLine(stringResource(R.string.ghost_device_label), state.deviceName)
            MaterialInfoLine(stringResource(R.string.ghost_manufacturer_label), state.manufacturerName)
            MaterialInfoLine(stringResource(R.string.ghost_soc_label), state.socName)
            MaterialInfoLine(
                stringResource(R.string.ghost_ram_label),
                stringResource(R.string.ghost_ram_value, state.ramTotal, state.ramAvailable),
            )
            MaterialInfoLine(stringResource(R.string.ghost_cpu_cores_label), state.cpuCores)
            MaterialInfoLine(stringResource(R.string.ghost_architecture_label), state.architecture)
            MaterialInfoLine(stringResource(R.string.ghost_android_label), state.androidVersion)
            MaterialInfoLine(
                stringResource(R.string.ghost_kernel_label),
                displayKernelRelease(state.kernelRelease),
            )
        }
    }
}

@Composable
private fun MaterialInfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        MaterialText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f),
        )
        MaterialText(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.58f),
        )
    }
}

@Composable
private fun MaterialControlsCard(state: GhostlockUiState, actions: GhostlockActions) {
    var cpuMenuExpanded by remember { mutableStateOf(false) }
    MaterialCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            if (state.cpuPairLabels.isNotEmpty()) {
                val selected = state.customCpuPair?.let {
                    stringResource(R.string.ghost_cpu_pair_custom, "${it.primary}, ${it.consumer}")
                } ?: state.cpuPairLabels.getOrNull(state.cpuPairIndex).orEmpty()
                Box {
                    MaterialOutlinedButton(
                        onClick = { cpuMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            MaterialText(
                                text = stringResource(R.string.ghost_cpu_pair_label),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            MaterialText(text = selected, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    MaterialDropdownMenu(
                        expanded = cpuMenuExpanded,
                        onDismissRequest = { cpuMenuExpanded = false },
                    ) {
                        state.cpuPairLabels.forEachIndexed { index, label ->
                            MaterialDropdownMenuItem(
                                text = { MaterialText(text = label) },
                                onClick = {
                                    cpuMenuExpanded = false
                                    actions.onCpuPairSelected(index)
                                },
                            )
                        }
                    }
                }
            }
            MaterialSwitchRow(
                title = stringResource(R.string.ghost_safe_mode_label),
                summary = stringResource(R.string.ghost_safe_mode_summary),
                checked = state.safeModeEnabled,
                onCheckedChange = actions::onSafeModeChanged,
            )
            MaterialSwitchRow(
                title = stringResource(R.string.ghost_shizuku_label),
                summary = stringResource(
                    when {
                        !state.shizukuEnabled -> R.string.ghost_shizuku_summary
                        state.shizukuStatus == ShizukuStatus.READY -> R.string.ghost_shizuku_status_ready
                        state.shizukuStatus == ShizukuStatus.PERMISSION_REQUIRED ->
                            R.string.ghost_shizuku_status_permission_required
                        state.shizukuStatus == ShizukuStatus.NOT_RUNNING ->
                            R.string.ghost_shizuku_status_not_running
                        else -> R.string.ghost_shizuku_status_checking
                    },
                ),
                checked = state.shizukuEnabled,
                onCheckedChange = actions::onShizukuChanged,
            )
        }
    }
}

@Composable
private fun MaterialSwitchRow(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.material3.ListItem(
        headlineContent = { MaterialText(text = title) },
        supportingContent = { MaterialText(text = summary) },
        trailingContent = {
            androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
    )
}

@Composable
private fun MaterialRunButton(
    state: GhostlockUiState,
    rootActive: Boolean,
    onClick: () -> Unit,
    onBlockedClick: () -> Unit,
) {
    val supported = state.kernelSupported && state.executionHasProfile &&
        state.profileInvalidPaths.isEmpty() &&
        (!state.shizukuEnabled || state.shizukuStatus == ShizukuStatus.READY)
    Box(modifier = Modifier.fillMaxWidth()) {
        MaterialButton(
            enabled = supported && !state.running && !rootActive,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            MaterialText(
                text = stringResource(
                    when {
                        rootActive -> R.string.ghostsu_ghost_action_active
                        state.running -> R.string.ghost_action_running
                        else -> R.string.ghost_action_run
                    },
                ),
            )
        }
        if (!state.running && !rootActive && (!supported || state.profileInvalidPaths.isNotEmpty())) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onBlockedClick() },
            )
        }
    }
}

@Composable
internal fun pageContentPadding(
    scaffoldPadding: PaddingValues,
    top: Dp = 8.dp,
    bottom: Dp = 12.dp,
    horizontalMin: Dp = 12.dp,
): PaddingValues {
    val windowWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val horizontal = ((windowWidth - 800.dp) / 2).coerceAtLeast(horizontalMin)
    return PaddingValues(
        start = horizontal,
        end = horizontal,
        top = scaffoldPadding.calculateTopPadding() + top,
        bottom = scaffoldPadding.calculateBottomPadding() + bottom,
    )
}

@Composable
private fun GhostlockExecutionSheet(
    state: GhostlockUiState,
    actions: GhostlockActions,
) {
    OverlayBottomSheet(
        show = state.executionSheetVisible,
        title = stringResource(R.string.ghost_log_title),
        allowDismiss = state.executionSheetDismissible,
        onDismissRequest = actions::onCloseExecutionSheet,
        startAction = {
            IconButton(onClick = actions::onCopyLogs) {
                Icon(
                    imageVector = MiuixIcons.Copy,
                    contentDescription = stringResource(R.string.ghost_action_copy),
                    tint = MiuixTheme.colorScheme.onBackground,
                )
            }
        },
        endAction = {
            IconButton(
                enabled = state.executionSheetDismissible,
                onClick = actions::onCloseExecutionSheet,
            ) {
                Icon(
                    imageVector = MiuixIcons.Close,
                    contentDescription = stringResource(R.string.ghost_action_close),
                )
            }
        },
        content = {
            LogPanel(
                lines = state.logLines,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 240.dp, max = 520.dp)
                    .navigationBarsPadding(),
            )
        },
    )
}

@Composable
private fun GhostlockDialog(
    state: GhostlockUiState,
    actions: GhostlockActions,
) {
    OverlayDialog(
        show = state.dialogVisible,
        title = if (state.dialogType == DialogType.NONE) null else stringResource(state.dialogTitleRes),
        onDismissRequest = actions::onDialogDismiss,
        onDismissFinished = actions::onDialogDismissFinished,
        content = {
            when (state.dialogType) {
                DialogType.LIST -> {
                    val items = state.dialogItems.ifEmpty { state.dialogItemResIds.map { stringResource(it) } }
                    items.forEachIndexed { index, item ->
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            text = if (index == state.dialogCurrentItemIndex) {
                                stringResource(R.string.ghost_export_current_marker, item)
                            } else {
                                item
                            },
                            onClick = { actions.onDialogItemSelected(index) },
                        )
                    }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.ghost_cancel),
                        onClick = actions::onDialogDismiss,
                    )
                }

                DialogType.INPUT -> {
                    TextField(
                        value = state.dialogInput,
                        onValueChange = actions::onDialogInputChange,
                        label = stringResource(state.dialogMessageRes),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.ghost_cancel),
                            onClick = actions::onDialogDismiss,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(state.dialogConfirmLabelRes),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                            onClick = { actions.onDialogConfirm(state.dialogInput) },
                        )
                    }
                }

                DialogType.CONFIRM -> {
                    Text(
                        text = stringResource(state.dialogMessageRes),
                        modifier = Modifier.fillMaxWidth(),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.ghost_cancel),
                            onClick = actions::onDialogDismiss,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.ghost_w3_shizuku_hint_enable),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                            onClick = { actions.onDialogConfirm("") },
                        )
                    }
                }

                DialogType.NOTICE -> {
                    Text(
                        text = stringResource(state.dialogMessageRes),
                        modifier = Modifier.fillMaxWidth(),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    ) {
                        state.dialogDocUrl?.let { docUrl ->
                            val uriHandler = LocalUriHandler.current
                            TextButton(
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.ghost_dialog_open_guide),
                                onClick = { uriHandler.openUri(docUrl) },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        TextButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.ghost_dialog_dismiss),
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                            onClick = actions::onDialogDismiss,
                        )
                    }
                }

                DialogType.NONE -> Unit
            }
        },
    )
}

@Composable
private fun GhostlockOverwriteDialog(
    state: GhostlockUiState,
    actions: GhostlockActions,
) {
    OverlayDialog(
        show = state.overwriteDialogVisible,
        title = stringResource(R.string.ghost_overwrite_title),
        summary = stringResource(R.string.ghost_overwrite_message, state.overwriteMessage),
        onDismissRequest = actions::onOverwriteDismiss,
        content = {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ghost_cancel),
                    onClick = actions::onOverwriteDismiss,
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.ghost_overwrite_yes),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = actions::onOverwriteConfirm,
                )
            }
        },
    )
}

@Composable
private fun MainContent(
    state: GhostlockUiState,
    actions: GhostlockActions,
    rootActive: Boolean,
    scrollBehavior: ScrollBehavior,
    scaffoldPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .scrollEndHaptic()
            .overScrollVertical()
            .scrollEndHaptic()
            .fillMaxHeight()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding(),
        contentPadding = pageContentPadding(scaffoldPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "controls") {
            ControlPanel(
                state = state,
                actions = actions,
                rootActive = rootActive,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item(key = "run") {
            RunButton(
                running = state.running,
                rootActive = rootActive,
                supported = state.kernelSupported &&
                        state.executionHasProfile &&
                        (!state.shizukuEnabled ||
                                state.shizukuStatus == ShizukuStatus.READY),
                profileValid = state.profileInvalidPaths.isEmpty(),
                labelRes = R.string.ghost_action_run,
                onClick = actions::onRun,
                onBlockedClick = actions::onProfileInvalid,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ControlPanel(
    state: GhostlockUiState,
    actions: GhostlockActions,
    rootActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ActivationStatusCard(
            rootActive = rootActive,
            supported = state.kernelSupported,
            profileAvailable = state.executionHasProfile,
            profileValid = state.profileInvalidPaths.isEmpty(),
            shizukuEnabled = state.shizukuEnabled,
            shizukuStatus = state.shizukuStatus,
            onParametersClick = actions::onOpenParameters,
            onShizukuClick = actions::onStatusClick,
            modifier = Modifier.fillMaxWidth(),
        )
        DeviceInfoCard(
            deviceName = state.deviceName,
            socName = state.socName,
            kernelRelease = displayKernelRelease(state.kernelRelease),
            manufacturerName = state.manufacturerName,
            ramTotal = state.ramTotal,
            ramAvailable = state.ramAvailable,
            cpuCores = state.cpuCores,
            architecture = state.architecture,
            androidVersion = state.androidVersion,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
        Card(modifier = modifier.padding(top = 12.dp)) {
            if (state.cpuPairLabels.isNotEmpty()) {
                val customPair = state.customCpuPair
                val customSummary = customPair?.let {
                    stringResource(
                        R.string.ghost_cpu_pair_custom,
                        "${it.primary}, ${it.consumer}",
                    )
                }
                OverlaySpinnerPreference(
                    title = stringResource(R.string.ghost_cpu_pair_label),
                    items = state.cpuPairLabels.map { DropdownItem(icon = null, title = it) },
                    selectedIndex = if (customPair == null) state.cpuPairIndex else -1,
                    summary = customSummary,
                    showValue = customPair == null,
                    onSelectedIndexChange = actions::onCpuPairSelected,
                )
            }
            SwitchPreference(
                checked = state.safeModeEnabled,
                onCheckedChange = actions::onSafeModeChanged,
                title = stringResource(R.string.ghost_safe_mode_label),
                summary = stringResource(R.string.ghost_safe_mode_summary),
            )
            /* PROFILE-SUGGEST-01: the profile suggestion seeds the toggle but no
             * longer hides it; an explicit user choice overrides either way. */
            SwitchPreference(
                checked = state.shizukuEnabled,
                onCheckedChange = actions::onShizukuChanged,
                title = stringResource(R.string.ghost_shizuku_label),
                summary = stringResource(
                    when {
                        !state.shizukuEnabled -> R.string.ghost_shizuku_summary
                        state.shizukuStatus == ShizukuStatus.READY -> R.string.ghost_shizuku_status_ready
                        state.shizukuStatus == ShizukuStatus.PERMISSION_REQUIRED ->
                            R.string.ghost_shizuku_status_permission_required

                        state.shizukuStatus == ShizukuStatus.NOT_RUNNING ->
                            R.string.ghost_shizuku_status_not_running

                        else -> R.string.ghost_shizuku_status_checking
                    },
                ),
            )
        }
        Card(modifier = modifier.padding(top = 12.dp)) {
            ArrowPreference(
                title = stringResource(R.string.ghost_advanced_settings),
                summary = stringResource(R.string.ghost_advanced_settings_summary),
                onClick = actions::onOpenAdvanced,
            )
        }
    }
}

@Composable
private fun ActivationStatusCard(
    rootActive: Boolean,
    supported: Boolean,
    profileAvailable: Boolean,
    profileValid: Boolean,
    shizukuEnabled: Boolean,
    shizukuStatus: ShizukuStatus,
    onParametersClick: () -> Unit,
    onShizukuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accessReady = !shizukuEnabled || shizukuStatus == ShizukuStatus.READY
    val ready = rootActive || (supported && profileAvailable && profileValid && accessReady)
    val missing = !rootActive && (!supported || !profileAvailable)
    val (backgroundColor, title, icon) = when {
        ready -> Triple(
            when {
                !rootActive -> MiuixTheme.colorScheme.primaryContainer
                MiuixTheme.isDynamicColor -> MiuixTheme.colorScheme.secondaryContainer
                isSystemInDarkTheme() -> Color(0xFF1A3825)
                else -> Color(0xFFDFFAE4)
            },
            if (rootActive) R.string.ghostsu_ghost_status_active else R.string.ghostsu_ghost_status_ready,
            Icons.Rounded.CheckCircleOutline,
        )

        missing -> Triple(
            MiuixTheme.colorScheme.errorContainer,
            if (!supported) R.string.ghost_kernel_unsupported else R.string.ghost_kernel_profile_required,
            Icons.Rounded.RemoveCircleOutline,
        )

        else -> Triple(
            when {
                MiuixTheme.isDynamicColor -> MiuixTheme.colorScheme.tertiaryContainer
                isSystemInDarkTheme() -> Color(0xFF3E2F1B)
                else -> Color(0xFFFFF0DB)
            },
            if (!profileValid) R.string.ghost_kernel_profile_invalid else R.string.ghost_shizuku_label,
            Icons.Rounded.ErrorOutline,
        )
    }
    val summary = when {
        rootActive -> R.string.ghostsu_ghost_status_active_summary
        ready && shizukuEnabled -> R.string.ghost_kernel_profile_ready_shizuku
        ready -> R.string.ghostsu_ghost_status_ready_summary
        !supported -> R.string.ghost_kernel_unsupported_summary
        !profileAvailable -> R.string.ghost_kernel_profile_required_summary
        !profileValid -> R.string.ghost_kernel_profile_invalid_summary
        shizukuStatus == ShizukuStatus.PERMISSION_REQUIRED ->
            R.string.ghost_shizuku_status_permission_required

        else -> R.string.ghost_shizuku_status_not_running
    }
    val action = if (supported && profileAvailable && profileValid && !accessReady) {
        onShizukuClick
    } else {
        onParametersClick
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.defaultColors(color = backgroundColor),
        onClick = action,
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (MiuixTheme.isDynamicColor) {
                    when (icon) {
                        Icons.Rounded.CheckCircleOutline -> MiuixTheme.colorScheme.primary.copy(alpha = 0.8f)
                        Icons.Rounded.RemoveCircleOutline ->
                            MiuixTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)

                        else -> MiuixTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    }
                } else {
                    when (icon) {
                        Icons.Rounded.CheckCircleOutline -> Color(0xFF36D167)
                        Icons.Rounded.RemoveCircleOutline -> Color(0xFFF5A623)
                        else -> Color(0xFFF72727)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 27.dp, y = 31.dp)
                    .size(110.dp),
            )
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 120.dp, bottom = 14.dp),
            ) {
                Text(
                    text = stringResource(title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = stringResource(summary),
                    fontSize = 15.sp,
                )
            }
        }
    }
}

private fun displayKernelRelease(release: String): String =
    release.substringBefore('-').trim().ifBlank { release }

@Composable
private fun DeviceInfoCard(
    deviceName: String,
    socName: String,
    kernelRelease: String,
    manufacturerName: String,
    ramTotal: String,
    ramAvailable: String,
    cpuCores: String,
    architecture: String,
    androidVersion: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        insideMargin = PaddingValues(16.dp),
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_device_label),
                    value = deviceName,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_manufacturer_label),
                    value = manufacturerName,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_soc_label),
                    value = socName,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_ram_label),
                    value = stringResource(R.string.ghost_ram_value, ramTotal, ramAvailable),
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_cpu_cores_label),
                    value = cpuCores,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_architecture_label),
                    value = architecture,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_android_label),
                    value = androidVersion,
                )
                DeviceInfoItem(
                    title = stringResource(R.string.ghost_kernel_label),
                    value = kernelRelease,
                )
            }
        }
    }
}

@Composable
private fun DeviceInfoItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 2.dp),
            fontSize = 14.sp,
            color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.68f),
        )
    }
}

/* profile-ui: resolved execution view with auto-saved sparse overrides. */
@Composable
internal fun ExecutionEditor(
    state: GhostlockUiState,
    actions: GhostlockActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        for (field in state.executionFields) {
            val text = state.executionEditing[field.path] ?: field.value.toString()
            val invalid = isFieldInputInvalid(text) || field.path in state.profileInvalidPaths
            TextField(
                value = text,
                onValueChange = { value -> actions.onExecutionFieldChanged(field.path, value) },
                label = fieldLabel(field.path, field.path.substringAfterLast('.')),
                colors = when {
                    invalid ->
                        TextFieldDefaults.textFieldColors(labelColor = FieldErrorHighlight)

                    field.overridden ->
                        TextFieldDefaults.textFieldColors(labelColor = OverrideHighlight)

                    else -> TextFieldDefaults.textFieldColors()
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }
}

internal val OverrideHighlight = Color(0xFFF5A623)

/** Red marks an unfilled or non-numeric field in the parameter editors. */
internal val FieldErrorHighlight = Color(0xFFE53935)

internal fun isFieldInputInvalid(text: String): Boolean = text.trim().toLongOrNull() == null

@Composable
private fun RunButton(
    running: Boolean,
    rootActive: Boolean,
    supported: Boolean,
    profileValid: Boolean,
    labelRes: Int,
    onClick: () -> Unit,
    onBlockedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        TextButton(
            text = stringResource(when {
                rootActive -> R.string.ghostsu_ghost_action_active
                running -> R.string.ghost_action_running
                else -> labelRes
            }),
            enabled = supported && profileValid && !running && !rootActive,
            colors = ButtonDefaults.textButtonColorsPrimary(),
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
        /* A disabled TextButton consumes no pointer input, so this overlay
         * explains why the run is blocked. */
        if (!running && !rootActive && (!supported || !profileValid)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onBlockedClick() },
            )
        }
    }
}

@Composable
private fun LogPanel(
    lines: List<GhostlockLogLine>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) listState.scrollToItem(lines.lastIndex)
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0B1220))
            .padding(12.dp),
    ) {
        SelectionContainer {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
            ) {
                items(lines) { line ->
                    Text(
                        text = line.text.trimEnd('\r', '\n'),
                        color = lineColor(line.color),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )
                }
            }
        }
    }
}

private fun lineColor(color: Int): Color = when (color) {
    0xFFFF6B6B.toInt() -> Color(0xFFFF6B6B)
    0xFF5FD68A.toInt() -> Color(0xFF5FD68A)
    0xFFFFC94D.toInt() -> Color(0xFFFFC94D)
    0xFF60A5FA.toInt() -> Color(0xFF60A5FA)
    else -> Color(0xFFD1D5DB)
}
