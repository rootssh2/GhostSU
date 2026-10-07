package me.weishu.kernelsu.ui.screen.superuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CheckableDropdownMenuItem
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.model.AppInfo
import me.weishu.kernelsu.ui.util.ownerNameForUid
import me.weishu.kernelsu.ui.component.AppIconImage
import me.weishu.kernelsu.ui.component.ScrollToTopOnChange
import me.weishu.kernelsu.ui.component.material.ExpressiveScaffold
import me.weishu.kernelsu.ui.component.material.SearchAppBar
import me.weishu.kernelsu.ui.component.material.SegmentedColumn
import me.weishu.kernelsu.ui.component.material.SegmentedItem
import me.weishu.kernelsu.ui.component.material.SegmentedListItem
import me.weishu.kernelsu.ui.component.statustag.StatusTag
import me.weishu.kernelsu.ui.viewmodel.AppSortType

@Composable
fun SuperUserPagerMaterial(
    uiState: SuperUserUiState,
    actions: SuperUserActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val refreshTick = remember { mutableIntStateOf(0) }
    val pullToRefreshState = rememberPullToRefreshState()

    var localSearchText by remember { mutableStateOf(uiState.searchStatus.searchText) }
    LaunchedEffect(uiState.searchStatus.searchText) {
        localSearchText = uiState.searchStatus.searchText
    }

    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    ExpressiveScaffold(
        topBar = {
            SearchAppBar(
                snackbarHostState = snackbarHostState,
                title = { GhostSuBrandMaterial() },
                searchText = localSearchText,
                onSearchTextChange = {
                    localSearchText = it
                    actions.onSearchTextChange(it)
                },
                onClearClick = {
                    localSearchText = ""
                    actions.onClearSearch()
                },
                navigationIcon = {
                    IconButton(onClick = actions.onOpenSulog) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Article,
                            contentDescription = stringResource(R.string.settings_sulog)
                        )
                    }
                },
                actions = {
                    var showSortMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = stringResource(R.string.menu_sort)
                        )
                        DropdownMenuPopup(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            val sortEntries = listOf(
                                AppSortType.NAME to R.string.sort_by_name,
                                AppSortType.PACKAGE_NAME to R.string.sort_by_package_name,
                                AppSortType.INSTALL_TIME to R.string.sort_by_install_time,
                                AppSortType.UPDATE_TIME to R.string.sort_by_update_time,
                            )
                            val sortConfig = uiState.sortConfig
                            DropdownMenuGroup(shapes = MenuDefaults.groupShape(index = 0, count = 2)) {
                                sortEntries.forEachIndexed { index, (type, resId) ->
                                    SelectableDropdownMenuItem(
                                        text = { Text(stringResource(resId)) },
                                        selected = sortConfig.sortType == type,
                                        selectedLeadingIcon = {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                            )
                                        },
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                            actions.onUpdateSortConfig(sortConfig.withType(type))
                                            showSortMenu = false
                                        },
                                        shapes = MenuDefaults.itemShape(index = index, count = sortEntries.size),
                                    )
                                }
                            }
                            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
                            DropdownMenuGroup(shapes = MenuDefaults.groupShape(index = 1, count = 2)) {
                                CheckableDropdownMenuItem(
                                    text = { Text(stringResource(R.string.sort_reverse)) },
                                    checked = sortConfig.reversed,
                                    checkedLeadingIcon = {
                                        Icon(
                                            Icons.Filled.Check,
                                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                            contentDescription = null,
                                        )
                                    },
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                        actions.onUpdateSortConfig(sortConfig.toggleReversed())
                                        showSortMenu = false
                                    },
                                    shapes = MenuDefaults.itemShape(index = 0, count = 1),
                                )
                            }
                        }
                    }

                    var showDropdown by remember { mutableStateOf(false) }
                    IconButton(onClick = { showDropdown = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                        DropdownMenuPopup(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            val filterCount = if (uiState.userIds.size > 1) 2 else 1
                            DropdownMenuGroup(shapes = MenuDefaults.groupShapes()) {
                                CheckableDropdownMenuItem(
                                    text = { Text(stringResource(R.string.show_system_apps)) },
                                    checked = uiState.showSystemApps,
                                    checkedLeadingIcon = {
                                        Icon(
                                            Icons.Filled.Check,
                                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                            contentDescription = null,
                                        )
                                    },
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                        actions.onToggleShowSystemApps()
                                        showDropdown = false
                                    },
                                    shapes = MenuDefaults.itemShape(index = 0, count = filterCount),
                                )
                                if (uiState.userIds.size > 1) {
                                    CheckableDropdownMenuItem(
                                        text = { Text(stringResource(R.string.show_only_primary_user_apps)) },
                                        checked = uiState.showOnlyPrimaryUserApps,
                                        checkedLeadingIcon = {
                                            Icon(
                                                Icons.Filled.Check,
                                                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                                                contentDescription = null,
                                            )
                                        },
                                        onCheckedChange = {
                                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                            actions.onToggleShowOnlyPrimaryUserApps()
                                            showDropdown = false
                                        },
                                        shapes = MenuDefaults.itemShape(index = 1, count = filterCount),
                                    )
                                }
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                defaultContent = { bottomPadding, closeSearch ->
                    LaunchedEffect(localSearchText) { searchListState.scrollToItem(0) }
                    LazyColumn(
                        state = searchListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp + bottomPadding
                        ),
                    ) {
                        if (uiState.recentlyInstalledResults.isNotEmpty()) {
                            item {
                                SegmentedColumn(
                                    title = stringResource(R.string.recently_installed),
                                    content = uiState.recentlyInstalledResults.map { group ->
                                        @Composable {
                                            SearchGroupItemMaterial(
                                                group = group,
                                                closeSearch = closeSearch,
                                                onOpenProfile = actions.onOpenProfile,
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                searchContent = { bottomPadding, closeSearch ->
                    LaunchedEffect(localSearchText) { searchListState.scrollToItem(0) }
                    LazyColumn(
                        state = searchListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 0.dp,
                            bottom = 16.dp + bottomPadding
                        ),
                    ) {
                        if (uiState.searchResults.isEmpty()) {
                            item { GhostSuEmptyStateMaterial(stringResource(R.string.ghostsu_root_empty_search)) }
                        } else {
                            itemsIndexed(uiState.searchResults, key = { _, item -> item.uid }) { index, group ->
                                SegmentedItem(index = index, count = uiState.searchResults.size) {
                                    SearchGroupItemMaterial(
                                        group = group,
                                        closeSearch = closeSearch,
                                        onOpenProfile = actions.onOpenProfile,
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                actions.onRefresh()
                refreshTick.intValue++
            },
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isRefreshing,
                    state = pullToRefreshState,
                )
            },
        ) {
            val expandedUids = remember { mutableStateOf(setOf<Int>()) }
            val latestGroupedApps = rememberUpdatedState(uiState.groupedApps)
            val latestRefreshing = rememberUpdatedState(uiState.isRefreshing)
            ScrollToTopOnChange(
                listState,
                uiState.sortConfig,
                uiState.showSystemApps,
                uiState.showOnlyPrimaryUserApps,
                refreshTick.intValue,
                isBusy = { latestRefreshing.value },
            ) { latestGroupedApps.value }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp + bottomInnerPadding
                ),
            ) {
                item { GhostSuRootIntroMaterial() }
                item {
                    Text(
                        text = stringResource(R.string.ghostsu_root_apps),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, top = 10.dp, bottom = 8.dp),
                    )
                }
                if (uiState.hasLoaded && uiState.groupedApps.isEmpty()) {
                    item { GhostSuEmptyStateMaterial(stringResource(R.string.ghostsu_root_empty)) }
                } else {
                    itemsIndexed(uiState.groupedApps, key = { _, item -> item.uid }) { index, group ->
                        val expanded = expandedUids.value.contains(group.uid)
                        val onToggleExpand = {
                            if (group.apps.size > 1) {
                                expandedUids.value = if (expanded) {
                                    expandedUids.value - group.uid
                                } else {
                                    expandedUids.value + group.uid
                                }
                            }
                        }
                        SegmentedItem(index = index, count = uiState.groupedApps.size) {
                            Column {
                                GroupItemMaterial(
                                    group = group,
                                    selected = expanded,
                                    onToggleExpand = onToggleExpand,
                                    onClickPrimary = { actions.onOpenProfile(group) },
                                )
                                AnimatedVisibility(
                                    visible = expanded && group.apps.size > 1,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut(),
                                ) {
                                    Column {
                                        group.apps.forEach { app ->
                                            SimpleAppItemMaterial(app = app) {
                                                actions.onOpenProfile(group)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { GhostSuRootDisclaimerMaterial() }
            }
        }
    }
}

@Composable
private fun GhostSuBrandMaterial() {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ghost_su_logo),
            contentDescription = stringResource(R.string.ghostsu_root_brand_name),
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Column {
            Text(
                text = stringResource(R.string.ghostsu_root_brand_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.ghostsu_root_brand_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                letterSpacing = 1.6.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun GhostSuRootIntroMaterial() {
    Column(modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)) {
        Text(
            text = stringResource(R.string.ghostsu_root_title),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.ghostsu_root_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun GhostSuEmptyStateMaterial(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        color = colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun GhostSuRootDisclaimerMaterial() {
    Surface(
        modifier = Modifier.padding(top = 16.dp),
        color = colorScheme.surfaceContainerLow,
        contentColor = colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp,
    ) {
        Text(
            text = stringResource(R.string.ghostsu_root_disclaimer),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun SearchGroupItemMaterial(
    group: GroupedApps,
    closeSearch: () -> Unit,
    onOpenProfile: (GroupedApps) -> Unit,
) {
    Column {
        GroupItemMaterial(
            group = group,
            selected = false,
            onToggleExpand = {},
            onClickPrimary = {
                closeSearch()
                onOpenProfile(group)
            },
        )
        AnimatedVisibility(
            visible = group.apps.size > 1,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                group.apps.forEach { app ->
                    SimpleAppItemMaterial(
                        app = app,
                        matched = group.matchedIdentifiers.contains(app.displayIdentifier),
                    ) {
                        closeSearch()
                        onOpenProfile(group)
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleAppItemMaterial(
    app: AppInfo,
    matched: Boolean = false,
    onNavigate: () -> Unit,
) {
    ListItem(
        onClick = onNavigate,
        modifier = Modifier.padding(horizontal = 4.dp),
        colors = ListItemDefaults.colors(
            containerColor = if (matched) colorScheme.secondaryContainer else colorScheme.surfaceContainerHigh,
        ),
        content = {
            Text(app.label, overflow = TextOverflow.Ellipsis, maxLines = 1)
        },
        supportingContent = {
            Text(app.displayIdentifier, overflow = TextOverflow.Ellipsis, maxLines = 1)
        },
        leadingContent = {
            AppIconImage(
                packageInfo = app.packageInfo,
                label = app.label,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 4.dp),
            )
        },
        trailingContent = {
            Icon(
                Icons.Filled.Remove,
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp),
            )
        },
    )
}

@Composable
private fun GroupItemMaterial(
    group: GroupedApps,
    selected: Boolean,
    onToggleExpand: () -> Unit,
    onClickPrimary: () -> Unit,
) {
    val tags = buildList {
        if (group.anyAllowSu) {
            add(
                StatusMeta(
                    label = stringResource(R.string.ghostsu_root_status_root),
                    bg = colorScheme.primaryContainer,
                    fg = colorScheme.onPrimaryContainer,
                )
            )
        } else {
            add(
                StatusMeta(
                    label = stringResource(R.string.ghostsu_root_status_no_root),
                    bg = colorScheme.surfaceVariant,
                    fg = colorScheme.onSurfaceVariant,
                )
            )
        }
        if (group.anyCustom) {
            add(
                StatusMeta(
                    label = stringResource(R.string.ghostsu_root_status_custom),
                    bg = colorScheme.secondaryContainer,
                    fg = colorScheme.onSecondaryContainer,
                )
            )
        }
    }
    val summaryText = if (group.apps.size > 1) {
        stringResource(R.string.group_contains_apps, group.apps.size)
    } else {
        group.primary.displayIdentifier
    }
    SegmentedListItem(
        selected = selected,
        onClick = onClickPrimary,
        onLongClick = if (group.apps.size > 1) onToggleExpand else null,
        headlineContent = {
            Text(
                text = if (group.apps.size > 1) ownerNameForUid(group.uid) else group.primary.label,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingContent = {
            Text(
                text = summaryText,
                color = colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                tags.forEach { tag ->
                    StatusTag(
                        label = tag.label,
                        backgroundColor = tag.bg,
                        contentColor = tag.fg,
                    )
                }
            }
        },
        leadingContent = {
            AppIconImage(
                packageInfo = group.primary.packageInfo,
                label = group.primary.label,
                modifier = Modifier.size(48.dp),
            )
        },
    )
}
