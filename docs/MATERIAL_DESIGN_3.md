# Material Design 3

This document lists the Material Design 3 (M3) rules applied in ft_hangouts.

The project uses no external libraries, so Material Components is not available. M3 is reproduced by hand with framework widgets, the `Theme.Material` base theme and custom theme attributes. When adding new UI, follow the values below instead of the framework defaults.

## Table of Contents

<details>
<summary>Click to Show / Hide</summary>

1. [Color Roles](#color-roles)
2. [Typography](#typography)
3. [Dimensions](#dimensions)
4. [Components](#components)
   1. [Top App Bar](#top-app-bar)
   2. [Icon Button](#icon-button)
   3. [List Item](#list-item)
   4. [Avatar](#avatar)
   5. [Floating Action Button](#floating-action-button)
5. [Implementation Notes](#implementation-notes)
6. [Resources](#resources)

</details>

## Color Roles

Framework roles are referenced with `?android:attr/` (`colorPrimary`, `colorBackground`, `colorError`). All other M3 roles are custom attributes declared in `res/values/attrs.xml` and referenced with `?attr/`.

`<theme>` is one of `ocean`, `amber`, `forest`, `rose` or `lavender`. Each theme was generated from one source color with [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/):

| Theme | Source color |
|---|---|
| Ocean | ![](https://img.shields.io/badge/%20-%20-0A6780?style=flat-square) `#0A6780` |
| Amber | ![](https://img.shields.io/badge/%20-%20-6D5E0F?style=flat-square) `#6D5E0F` |
| Forest | ![](https://img.shields.io/badge/%20-%20-4C662B?style=flat-square) `#4C662B` |
| Rose | ![](https://img.shields.io/badge/%20-%20-8C4A60?style=flat-square) `#8C4A60` |
| Lavender | ![](https://img.shields.io/badge/%20-%20-735187?style=flat-square) `#735187` |


**Rules:**

1. Never hardcode a color. Always reference a theme attribute.
2. Set every custom attribute in all five themes.
3. Pair each container color with its "on" color.
4. Do not use the top app bar color (`primaryContainer`) for content.

## Typography

The framework has no M3 type scale, so each M3 style used by the app is defined as `TextAppearance.FTHangouts.<M3 name in PascalCase>` in `res/values/themes.xml`, with the size and weight from the [M3 type scale](https://m3.material.io/styles/typography/type-scale-tokens). Only the styles in use are defined.

Weights use separate font files (`@font/noto_sans` for Regular, `@font/noto_sans_medium` for Medium). Noto Sans has no Japanese glyphs, so Japanese text falls back to the system font, which is Noto Sans CJK on most devices.

**Rules:**

1. Set text styles with `android:textAppearance`, never `android:textSize` or `android:fontFamily` on a view.
2. Keep color out of TextAppearance styles. Set `android:textColor` on the view.
3. Add a new style only when it is used, with the values from the M3 type scale.

## Dimensions

Sizes and spacing are defined in `res/values/dimens.xml`, on the M3 4dp grid. Spacing is a shared scale:

| Resource | Value |
|---|---|
| `spacing_extra_small` | 4dp |
| `spacing_small` | 8dp |
| `spacing_medium` | 16dp |
| `spacing_large` | 24dp |

Component sizes are in `dimens.xml`. Their values come from the **Specs** tab of each component on [m3.material.io](https://m3.material.io/components):

| Component | M3 specs |
|---|---|
| Top app bar | [App bars](https://m3.material.io/components/app-bars/specs) |
| Icon button | [Icon buttons](https://m3.material.io/components/icon-buttons/specs) |
| Search bar | [Search](https://m3.material.io/components/search/specs) |
| List item | [Lists](https://m3.material.io/components/lists/specs) |
| Detail field | [Cards](https://m3.material.io/components/cards/specs) |
| Menu | [Menus](https://m3.material.io/components/menus/specs) |
| Dialog | [Dialogs](https://m3.material.io/components/dialogs/specs) |
| FAB | [Floating action button](https://m3.material.io/components/floating-action-button/specs) |

M3 has no avatar component. Avatar sizes are an app decision.

**Rules:**

1. Padding and margins use the spacing scale. Add a new step only when no existing one fits.
2. Component sizes use component names (`<component>_<property>`), because they come from that component's M3 spec.
3. A value used only once may stay inline, unless it is an M3 spec value.
4. Text sizes are not dimensions. They belong to TextAppearance styles.

## Components

Values live in the layouts and `dimens.xml`. This section only records decisions that the code does not explain.

### Top App Bar

The background is `primaryContainer` instead of the M3 default `surface`, because the subject requires a menu that changes the header color and `surface` barely changes between themes. For the same reason, status bar icons are drawn dark in light mode (`@bool/light_system_bars`): `Theme.Material.Light` draws them white, which is unreadable on this background.   

The bar extends behind the status bar. `MainActivity` adds the status bar inset to both its top padding and its height, so the content keeps its 64dp.

### Icon Button

Icons must be 24dp. The framework `ImageButton` style uses `scaleType="center"`, so a larger vector is drawn at its own size and overflows the padding.

### List Item

First and last name share one `TextView`, because that is the only way to ellipsize a long name. There are no dividers, since M3 separates list items with spacing rather than lines.

### Avatar

The background is `tertiaryContainer`, which keeps the avatar distinct from the top app bar (`primaryContainer`) and from tonal buttons (`secondaryContainer`).

### Floating Action Button

The container is `primary` rather than the M3 default `primaryContainer`, which would blend with the top app bar. The list has 88dp of bottom padding with `clipToPadding="false"`, so the FAB never covers the last item.

## Resources

[M3 Color roles](https://m3.material.io/styles/color/roles)   
[M3 Typography](https://m3.material.io/styles/typography)   
[M3 App bars](https://m3.material.io/components/app-bars/guidelines)   
[M3 Search](https://m3.material.io/components/search/overview)   
[M3 Lists specs](https://m3.material.io/components/lists/specs)   
[Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)   
[Android Developer API Reference: View.setClipToOutline](https://developer.android.com/reference/android/view/View#setClipToOutline(boolean))
