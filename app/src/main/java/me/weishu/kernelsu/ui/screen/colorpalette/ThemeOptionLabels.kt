package me.weishu.kernelsu.ui.screen.colorpalette

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import me.weishu.kernelsu.R

/** Only the visible labels are localized. The stored enum names remain unchanged. */
@Composable
internal fun paletteStyleLabel(style: PaletteStyle): String = stringResource(
    when (style) {
        PaletteStyle.TonalSpot -> R.string.ghostsu_palette_tonal_spot
        PaletteStyle.Neutral -> R.string.ghostsu_palette_neutral
        PaletteStyle.Vibrant -> R.string.ghostsu_palette_vibrant
        PaletteStyle.Expressive -> R.string.ghostsu_palette_expressive
        PaletteStyle.Rainbow -> R.string.ghostsu_palette_rainbow
        PaletteStyle.FruitSalad -> R.string.ghostsu_palette_fruit_salad
        PaletteStyle.Monochrome -> R.string.ghostsu_palette_monochrome
        PaletteStyle.Fidelity -> R.string.ghostsu_palette_fidelity
        PaletteStyle.Content -> R.string.ghostsu_palette_content
    }
)

@Composable
internal fun colorSpecLabel(spec: ColorSpec.SpecVersion): String = stringResource(
    when (spec) {
        ColorSpec.SpecVersion.Default -> R.string.ghostsu_color_spec_default
        ColorSpec.SpecVersion.SPEC_2021 -> R.string.ghostsu_color_spec_2021
        ColorSpec.SpecVersion.SPEC_2025 -> R.string.ghostsu_color_spec_2025
    }
)
