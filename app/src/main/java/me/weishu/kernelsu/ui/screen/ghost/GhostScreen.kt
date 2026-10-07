package me.weishu.kernelsu.ui.screen.ghost

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import me.weishu.kernelsu.ghost.data.AndroidGhostlockRepository
import me.weishu.kernelsu.ghost.ui.DocumentRequest
import me.weishu.kernelsu.ghost.ui.GhostlockActions
import me.weishu.kernelsu.ghost.ui.GhostlockApp
import me.weishu.kernelsu.ghost.ui.GhostlockEffect
import me.weishu.kernelsu.ghost.ui.GhostlockViewModel
import me.weishu.kernelsu.ksuApp

@Composable
fun GhostPager(
    bottomInnerPadding: Dp,
    isCurrentPage: Boolean,
) {
    val context = LocalContext.current
    val viewModel: GhostlockViewModel = viewModel(
        factory = viewModelFactory {
            initializer { GhostlockViewModel(AndroidGhostlockRepository(ksuApp)) }
        },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingDocumentRequest by rememberSaveable { mutableStateOf<DocumentRequest?>(null) }

    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        val request = pendingDocumentRequest
        pendingDocumentRequest = null
        if (uri != null && request != null) viewModel.onDocumentResult(request, uri.toString())
    }
    val documentsPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        val request = pendingDocumentRequest
        pendingDocumentRequest = null
        if (uris.isNotEmpty() && request != null) viewModel.onDocumentsResult(request, uris.map(Uri::toString))
    }
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        viewModel.onDebugExportLocationPicked(uri?.let(::documentTreeRelativePath))
    }
    val profileCreator = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri: Uri? ->
        viewModel.onExportProfileDocumentPicked(uri?.toString())
    }

    LaunchedEffect(viewModel) {
        viewModel.initialize()
        viewModel.effects.collect { effect ->
            when (effect) {
                is GhostlockEffect.PickDocument -> {
                    pendingDocumentRequest = effect.request
                    documentPicker.launch(arrayOf("*/*"))
                }
                GhostlockEffect.PickDebugFolder -> folderPicker.launch(null)
                is GhostlockEffect.CreateProfileDocument -> profileCreator.launch(effect.suggestedName)
                is GhostlockEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(effect.uri))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Ghost SU"))
                }
                is GhostlockEffect.Toast -> Toast.makeText(context, effect.resourceId, Toast.LENGTH_SHORT).show()
                is GhostlockEffect.Clipboard -> {
                    context.getSystemService(ClipboardManager::class.java)
                        ?.setPrimaryClip(ClipData.newPlainText("ghost-su-log", effect.text))
                }
                is GhostlockEffect.KeepScreenAwake -> {
                    (context as? Activity)?.let { activity ->
                        if (effect.enabled) activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        else activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
                GhostlockEffect.OpenShizuku -> context.packageManager
                    .getLaunchIntentForPackage("moe.shizuku.privileged.api")
                    ?.let(context::startActivity)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomInnerPadding),
    ) {
        GhostlockApp(
            state = state,
            actions = ghostActions(viewModel, pendingDocumentRequest = { pendingDocumentRequest = it }, documentsPicker = { request, multiple ->
                pendingDocumentRequest = request
                if (multiple) documentsPicker.launch(arrayOf("text/plain", "application/octet-stream", "application/json"))
                else documentPicker.launch(arrayOf("*/*"))
            }),
        )
    }
}

private fun documentTreeRelativePath(uri: Uri): String? {
    val documentId = runCatching { DocumentsContract.getTreeDocumentId(uri) }.getOrNull() ?: return null
    if (!documentId.startsWith("primary:")) return null
    return documentId.substringAfter(':').trim('/').ifEmpty { null }
}

private fun ghostActions(
    viewModel: GhostlockViewModel,
    pendingDocumentRequest: (DocumentRequest) -> Unit,
    documentsPicker: (DocumentRequest, Boolean) -> Unit,
): GhostlockActions = object : GhostlockActions {
    override fun onRun() = viewModel.onRun()
    override fun onProfileInvalid() = viewModel.onProfileInvalid()
    override fun onStatusClick() = viewModel.onStatusClick()
    override fun onCloseExecutionSheet() = viewModel.onCloseExecutionSheet()
    override fun onCopyLogs() = viewModel.copyLogs()
    override fun onImportOffsetsHocon() { documentsPicker(DocumentRequest.ImportOffsetsHocon, true) }
    override fun onImportOffsetsJson() { documentsPicker(DocumentRequest.ImportOffsetsJson, true) }
    override fun onDocumentsResult(request: DocumentRequest, uris: List<String>) = viewModel.onDocumentsResult(request, uris)
    override fun onParseOta() = viewModel.promptParseUrl()
    override fun onParseImage() = viewModel.parseOffsets()
    override fun onCpuPairSelected(index: Int) = viewModel.selectCpuPair(index)
    override fun onSafeModeChanged(enabled: Boolean) = viewModel.toggleSafeMode(enabled)
    override fun onForceAttackTestChanged(enabled: Boolean) = viewModel.toggleForceAttackTest(enabled)
    override fun onShizukuChanged(enabled: Boolean) = viewModel.toggleShizuku(enabled)
    override fun onDialogItemSelected(index: Int) = viewModel.onDialogItemSelected(index)
    override fun onDialogInputChange(value: String) = viewModel.onDialogInputChange(value)
    override fun onDialogConfirm(value: String) = viewModel.onDialogConfirm(value)
    override fun onDialogDismiss() = viewModel.onDialogDismiss()
    override fun onDialogDismissFinished() = viewModel.onDialogDismissFinished()
    override fun onOverwriteConfirm() = viewModel.onOverwriteConfirm()
    override fun onOverwriteDismiss() = viewModel.onOverwriteDismiss()
    override fun onExecutionFieldChanged(path: String, value: String) = viewModel.updateExecutionField(path, value)
    override fun onRouteChanged(index: Int) = viewModel.onRouteChanged(index)
    override fun onFallbackChanged(index: Int) = viewModel.onFallbackChanged(index)
    override fun onExportProfile() = viewModel.onExportProfile()
    override fun onSaveProfileEdits() = viewModel.onSaveProfileEdits()
    override fun onSaveProfileAs() = viewModel.onSaveProfileAs()
    override fun onExportProfileEdits() = viewModel.onExportProfileEdits()
    override fun onRevertProfileEdits() = viewModel.onRevertProfileEdits()
    override fun onOpenAdvanced() = viewModel.onOpenAdvanced()
    override fun onCloseAdvanced() = viewModel.onCloseAdvanced()
    override fun onShowAbout() = viewModel.onShowAbout()
    override fun onCloseAbout() = viewModel.onCloseAbout()
    override fun onDebugExportChanged(enabled: Boolean) = viewModel.onDebugExportChanged(enabled)
    override fun onDebugExportLocationPick() = viewModel.onDebugExportLocationPick()
    override fun onDebugKernelLogChanged(enabled: Boolean) = viewModel.onDebugKernelLogChanged(enabled)
    override fun onOpenParameters() = viewModel.onOpenParameters()
    override fun onCloseParameters() = viewModel.onCloseParameters()
    override fun onOpenLoadConfig() = viewModel.onOpenLoadConfig()
    override fun onCloseLoadConfig() = viewModel.onCloseLoadConfig()
    override fun onOpenUserProfileDetail(name: String) = viewModel.onOpenUserProfileDetail(name)
    override fun onCloseUserProfileDetail() = viewModel.onCloseUserProfileDetail()
    override fun onLoadUserProfile(name: String) = viewModel.onLoadUserProfile(name)
    override fun onUnloadUserProfile() = viewModel.onUnloadUserProfile()
    override fun onEditUserProfile(name: String) = viewModel.onEditUserProfile(name)
    override fun onUserProfileRename(name: String) = viewModel.onUserProfileRename(name)
    override fun onUserProfileExport(name: String) = viewModel.onUserProfileExport(name)
    override fun onConvertUserProfile(name: String) = viewModel.onConvertUserProfile(name)
    override fun onUserProfileDelete(name: String) = viewModel.onUserProfileDelete(name)
    override fun onUserProfileDeleteConfirm() = viewModel.onUserProfileDeleteConfirm()
    override fun onUserProfileDeleteDismiss() = viewModel.onUserProfileDeleteDismiss()
    override fun onOpenBuiltinProfiles() = viewModel.onOpenBuiltinProfiles()
    override fun onCloseBuiltinProfiles() = viewModel.onCloseBuiltinProfiles()
    override fun onSelectBuiltinProfile(release: String?) = viewModel.onSelectBuiltinProfile(release)
    override fun onOpenProfileOverrides() = viewModel.onOpenProfileOverrides()
    override fun onCloseProfileOverrides() = viewModel.onCloseProfileOverrides()
    override fun onOpenAdvancedOverrides() = viewModel.onOpenAdvancedOverrides()
    override fun onCloseAdvancedOverrides() = viewModel.onCloseAdvancedOverrides()
    override fun onProfileOverrideChanged(path: String, value: String) = viewModel.onProfileOverrideChanged(path, value)
}
