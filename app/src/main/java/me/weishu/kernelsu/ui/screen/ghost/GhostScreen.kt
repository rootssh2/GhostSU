package me.weishu.kernelsu.ui.screen.ghost

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.AssistChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.R
import rikka.shizuku.Shizuku
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun GhostPager(
    bottomInnerPadding: Dp,
    isCurrentPage: Boolean,
) {
    val context = LocalContext.current
    val memory = remember(context) {
        ActivityManager.MemoryInfo().also { info ->
            context.getSystemService(ActivityManager::class.java)?.getMemoryInfo(info)
        }
    }
    var safeMode by rememberSaveable { mutableStateOf(false) }
    val shizukuReady = remember { runCatching { Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED }.getOrDefault(false) }
    val totalRam = formatMemory(memory.totalMem)
    val availableRam = formatMemory(memory.availMem)
    val soc = remember {
        listOf(Build.SOC_MODEL, Build.HARDWARE, Build.BOARD)
            .firstOrNull { it.isNotBlank() && !it.equals("unknown", ignoreCase = true) }
            ?: "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    val cpu = remember { "${Runtime.getRuntime().availableProcessors()} núcleos" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = bottomInnerPadding + 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.ghost),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.ghost_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.ghost_environment_status), fontWeight = FontWeight.Bold)
                    AssistChip(
                        onClick = {},
                        label = { Text(if (shizukuReady) stringResource(R.string.ghost_enabled) else stringResource(R.string.ghost_disabled)) },
                        leadingIcon = { Icon(Icons.Rounded.Security, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusTile(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.ghost_shizuku),
                        value = if (shizukuReady) stringResource(R.string.ghost_enabled) else stringResource(R.string.ghost_disabled),
                        accent = shizukuReady,
                    )
                    StatusTile(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.ghost_safe_mode),
                        value = if (safeMode) stringResource(R.string.ghost_enabled) else stringResource(R.string.ghost_disabled),
                        accent = safeMode,
                    )
                }
                FilterChip(
                    selected = safeMode,
                    onClick = { safeMode = !safeMode },
                    label = { Text(stringResource(R.string.ghost_safe_mode)) },
                    leadingIcon = { Icon(Icons.Rounded.SettingsSuggest, contentDescription = null, modifier = Modifier.size(18.dp)) },
                )
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.ghost_configure))
                }
            }
        }

        Text(stringResource(R.string.ghost_device), fontWeight = FontWeight.Bold)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Rounded.Smartphone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
                Column {
                    Text(soc, fontWeight = FontWeight.Bold)
                    Text(
                        "${Build.VERSION.RELEASE} · ${Build.VERSION.SDK_INT} · ${Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        Text(stringResource(R.string.ghost_hardware), fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HardwareTile(Modifier.weight(1f), Icons.Rounded.Speed, stringResource(R.string.ghost_cpu), cpu)
            HardwareTile(Modifier.weight(1f), Icons.Rounded.Memory, stringResource(R.string.ghost_ram_total), totalRam)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HardwareTile(Modifier.weight(1f), Icons.Rounded.Memory, stringResource(R.string.ghost_ram_available), availableRam)
            HardwareTile(Modifier.weight(1f), Icons.Rounded.SettingsSuggest, stringResource(R.string.ghost_kernel), Build.VERSION.INCREMENTAL)
        }

        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .42f)),
        ) {
            Text(stringResource(R.string.ghost_run))
        }
        Text(
            text = stringResource(R.string.ghost_integration_pending),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun StatusTile(modifier: Modifier, label: String, value: String, accent: Boolean) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun HardwareTile(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(7.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

private fun formatMemory(bytes: Long): String {
    val gb = bytes / 1024.0 / 1024.0 / 1024.0
    return if (gb >= 1.0) String.format(Locale.US, "%.1f GB", gb) else "${(bytes / 1024 / 1024).roundToInt()} MB"
}
