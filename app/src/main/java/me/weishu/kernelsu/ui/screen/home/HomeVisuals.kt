package me.weishu.kernelsu.ui.screen.home

import androidx.compose.ui.graphics.Color
import me.weishu.kernelsu.KernelVersion

/** Presentation-only values for the Ghost SU home redesign. */
internal val GhostHomeViolet = Color(0xFF7654E8)
internal val GhostHomeVioletDark = Color(0xFF342170)
internal val GhostHomeVioletLight = Color(0xFFE8DEFF)
internal val GhostHomeActive = Color(0xFF70DDB5)
internal val GhostHomeActiveDark = Color(0xFF173F35)

/** Keep the complete kernel version in state/API; abbreviate only the home display. */
internal fun KernelVersion.homeDisplayValue(): String =
    if (major >= 0 && patchLevel >= 0 && subLevel >= 0) "$major.$patchLevel.$subLevel" else "—"
