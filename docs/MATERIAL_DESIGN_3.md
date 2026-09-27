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
   3. [Search Bar](#search-bar)
   4. [List Item](#list-item)
   5. [Avatar](#avatar)
   6. [Floating Action Button](#floating-action-button)
5. [Implementation Notes](#implementation-notes)
6. [Resources](#resources)

</details>

## Color Roles

The framework theme only knows a few color roles (`colorPrimary`, `colorAccent`, `colorBackground`, `colorError`). The other M3 roles are declared as custom attributes in `res/values/attrs.xml` and set in every theme in `res/values/themes.xml`.

| M3 role | Theme attribute | Color resource | Used for |
|---|---|---|---|
| primary | `?android:attr/colorPrimary` | `<theme>_primary` | FAB container |
| onPrimary | `?attr/colorOnPrimary` | `<theme>_on_primary` | FAB icon |
| primaryContainer | `?attr/colorPrimaryContainer` | `<theme>_primary_container` | Top app bar background |
| onPrimaryContainer | `?attr/colorOnPrimaryContainer` | `<theme>_on_primary_container` | Top app bar title and icons |
| secondaryContainer | `?attr/colorSecondaryContainer` | `<theme>_secondary_container` | Avatar background |
| onSecondaryContainer | `?attr/colorOnSecondaryContainer` | `<theme>_on_secondary_container` | Avatar initials, search bar icon and placeholder |
| tertiaryContainer | `?attr/colorTertiaryContainer` | `<theme>_tertiary_container` | Not used yet (reserved for avatar color variation) |
| onTertiaryContainer | `?attr/colorOnTertiaryContainer` | `<theme>_on_tertiary_container` | Not used yet |
| surfaceContainerHigh | `?attr/colorSurfaceContainerHigh` | `<theme>_surface_container_high` | Search bar background |
| onSurface | `?attr/colorOnSurface` | `<theme>_on_surface` | Contact name, search input text |
| background | `?android:attr/colorBackground` | `<theme>_background` | Window background |

`<theme>` is one of `ocean`, `amber`, `forest`, `rose` or `lavender`. The color values were generated with [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/).

`<theme>_surface_container_high` was not part of the original export. It was derived from `<theme>_surface`: M3 tones are CIELAB L\* values, so the surface color was kept at the same hue and chroma and moved to tone 92 (light) and tone 17 (dark), which are the M3 tones for surfaceContainerHigh. Derive any other missing neutral role the same way instead of regenerating the whole palette.

**Rules:**

1. Never hardcode a color in a layout or drawable. Always reference a theme attribute so that all five themes and dark mode keep working.
2. When a new custom attribute is added to `attrs.xml`, set it in all five themes. A missing attribute does not fail the build, but the view is drawn without that color.
3. Pair each container color with its matching "on" color (for example `primaryContainer` with `onPrimaryContainer`) to keep the contrast M3 guarantees.
4. Do not reuse the top app bar color (`primaryContainer`) for content elements. Avatars and the search bar use other roles so that the header stays visually distinct.

## Typography

The framework has no M3 type scale, so each M3 style is reproduced as a TextAppearance style in `res/values/themes.xml`.

| M3 style | Style resource | Size | Weight | Font | Used for |
|---|---|---|---|---|---|
| (app specific) | `TextAppearance.FTHangouts.Heading` | 20sp | Medium | `@font/noto_sans_medium` | Top app bar title |
| titleMedium | `TextAppearance.FTHangouts.TitleMedium` | 16sp | Medium | `@font/noto_sans_medium` | Avatar initials |
| bodyLarge | `TextAppearance.FTHangouts.BodyLarge` | 16sp | Regular | `@font/noto_sans` | Contact name, search input |

The M3 top app bar title is titleLarge (22sp, Regular). `Heading` is slightly smaller and heavier by choice.

**Rules:**

1. Apply text styles with `android:textAppearance="@style/TextAppearance.FTHangouts.<Style>"`. Do not set `android:textSize` or `android:fontFamily` directly on a view.
2. Keep color out of TextAppearance styles. Set `android:textColor` on the view, because the same type style is used with different color roles (M3 defines typography and color separately).
3. When a new M3 style is needed, add it as `TextAppearance.FTHangouts.<M3 name in PascalCase>` with the values from the M3 type scale.

Weights are selected with separate font files rather than `android:textFontWeight`, because that attribute requires API 28 and `minSdk` is 26.

Noto Sans covers Latin, Greek and Cyrillic only. Japanese text falls back to the system font.

## Dimensions

Sizes and spacing are defined in `res/values/dimens.xml`.

| Resource | Value | Kind | Used for |
|---|---|---|---|
| `spacing_small` | 4dp | Spacing | Small gaps, top app bar horizontal padding, list top padding |
| `spacing_medium` | 16dp | Spacing | Screen and list item padding, gap between avatar and text, search bar margins |
| `top_app_bar_height` | 64dp | Component | Top app bar, excluding the status bar |
| `top_app_bar_corner_radius` | 16dp | Component | Not used (the top app bar is flat) |
| `search_bar_height` | 56dp | Component | Search bar |
| `search_bar_corner_radius` | 28dp | Component | Search bar shape (fully rounded) |
| `icon_button_size` | 48dp | Component | Icon button touch target |
| `icon_button_padding` | 12dp | Component | Icon button padding, leaves a 24dp icon |
| `icon_button_corner_radius` | 16dp | Component | Not used |
| `list_item_height` | 56dp | Component | One line list item |
| `avatar_size` | 40dp | Component | Avatar |
| `fab_size` | 56dp | Component | FAB |
| `fab_margin` | 16dp | Component | FAB margin from the screen edge |
| `fab_corner_radius` | 16dp | Component | FAB shape |
| `fab_elevation` | 6dp | Component | FAB elevation (M3 level 3) |

**Rules:**

1. **Spacing uses generic names** (`spacing_<size>`). Padding and margins reference these instead of new per component values.
2. **Component sizes use component names** (`<component>_<property>`), because they come from the M3 spec of that component and may change independently.
3. **A value used only once may stay inline** in the layout, as long as it is not an M3 spec value. If it becomes shared, move it to `dimens.xml`.
4. **Text sizes are not dimensions.** They belong to TextAppearance styles (see [Typography](#typography)).

## Components

### Top App Bar

The app uses one M3 center aligned top app bar, shared by every screen. Layout: `res/layout/top_app_bar.xml`, included from `activity_main.xml`. In M3 Expressive this component is called "app bar".

| Property | Value |
|---|---|
| Height | 64dp (`top_app_bar_height`) plus the status bar height |
| Background | `primaryContainer`, flat (`res/drawable/bg_top_app_bar.xml`) |
| Horizontal padding | 4dp (`spacing_small`), so that icons sit 16dp from the edge |
| Leading element | Navigation icon button (back), hidden on the home screen |
| Title | Centered, `TextAppearance.FTHangouts.Heading`, `onPrimaryContainer`, 1 line, ellipsized at the end |
| Trailing element | One action icon button (color theme on the home screen) |

**Decisions:**

1. **The background is `primaryContainer`, not `surface`.** The subject requires a menu that changes the header color. The M3 default `surface` background would barely change between themes, so the header uses a container color that clearly follows the selected theme.
2. **The bar extends behind the status bar.** The app draws edge to edge. `MainActivity` adds the status bar inset to the bar's top padding and to its height, so the color fills the status bar area while the content keeps its 64dp.
3. **The title is centered with fixed side margins.** The bar is a `FrameLayout`. The title spans the full width with a 48dp margin on each side (`icon_button_size`), and the buttons are layered on top at `start` and `end`. Hiding a button does not move the title.
4. **The title slot is kept even when empty.** Screens that show no title set an empty string instead of hiding the view.
5. **Status bar icons are dark in light mode.** The framework `Theme.Material.Light` draws white status bar icons, which are unreadable on `primaryContainer`. `android:windowLightStatusBar` is set from `@bool/light_system_bars` (`true` in `values`, `false` in `values-night`).

### Icon Button

Style: `Widget.FTHangouts.IconButton` in `res/values/themes.xml`.

| Property | Value |
|---|---|
| Touch target | 48dp (`icon_button_size`) |
| Icon | 24dp, centered (`icon_button_padding` of 12dp on each side) |
| Background | `?android:attr/selectableItemBackgroundBorderless` (circular ripple, no container) |
| Icon color | `onPrimaryContainer` (tint) |

Vector icons must be 24dp. The framework `ImageButton` style uses `scaleType="center"`, so a larger vector is drawn at its own size and overflows the padding. Create icons with Android Studio's Vector Asset tool, or download them from Material Symbols at size 24.

### Search Bar

The contact list shows an M3 search bar above the list. Layout: `res/layout/screen_contact_list.xml`. Searching is not implemented yet.

| Property | Value |
|---|---|
| Height | 56dp (`search_bar_height`) |
| Shape | Fully rounded, 28dp (`search_bar_corner_radius`, `res/drawable/bg_search_bar.xml`) |
| Background | `surfaceContainerHigh` |
| Margins | 16dp (`spacing_medium`) on the sides and on top |
| Leading icon | Search icon, 24dp, `onSecondaryContainer` |
| Input | `TextAppearance.FTHangouts.BodyLarge`, text `onSurface`, placeholder `onSecondaryContainer` |

The M3 color for the icon and the placeholder is `onSurfaceVariant`, which is not in the palette yet. `onSecondaryContainer` is used as a close substitute.

### List Item

The contact list uses the M3 one line list item with a leading avatar. Layout: `res/layout/item_contact_summary.xml`.

| Property | Value |
|---|---|
| Height | 56dp (`list_item_height`) |
| Horizontal padding | 16dp (`spacing_medium`) |
| Leading element | Avatar, 40dp (`avatar_size`) |
| Gap between avatar and text | 16dp (`spacing_medium`) |
| Headline | `TextAppearance.FTHangouts.BodyLarge`, `onSurface`, 1 line, ellipsized at the end |
| Divider | None |
| Touch feedback | Ripple from the default `ListView` selector |

**Decisions:**

1. **The name is a single headline.** First name and last name are shown in one `TextView` as `"First Last"`. An M3 one line item has a single headline, and one `TextView` is the only way to truncate a long name with an ellipsis.
2. **No dividers.** M3 lists separate items with spacing, not lines. The `ListView` sets `android:divider="@null"`.
3. **56dp is for one line items only.** If a second line is added later (for example a phone number), the M3 height becomes 72dp.

### Avatar

| Property | Value |
|---|---|
| Size | 40dp (`avatar_size`) |
| Shape | Circle (`res/drawable/bg_avatar.xml`) |
| Background | `secondaryContainer` |
| Content with a picture | The picture, `centerCrop`, clipped to the circle |
| Content without a picture | Monogram: first letter of the first name and first letter of the last name, uppercase, `TextAppearance.FTHangouts.TitleMedium`, `onSecondaryContainer` |

The picture and the monogram are stacked in a `FrameLayout`. The adapter shows one and hides the other.

The avatar does not use `primaryContainer`, so that it does not blend with the top app bar. The tertiary container roles are defined so that avatar colors can later vary per contact (for example by `id`).

### Floating Action Button

Values are defined in `res/values/dimens.xml`.

| Property | Value |
|---|---|
| Size | 56dp (`fab_size`) |
| Corner radius | 16dp (`fab_corner_radius`) |
| Margin from the screen edge | 16dp (`fab_margin`) |
| Elevation | 6dp (`fab_elevation`, M3 level 3) |
| Container / icon | `primary` / `onPrimary` |

The M3 default FAB color is `primaryContainer` with an `onPrimaryContainer` icon. This app uses the `primary` color style instead, which also keeps the FAB distinct from the `primaryContainer` top app bar.

The list reserves 88dp of bottom padding (`paddingBottom` with `clipToPadding="false"`) so that the FAB never covers the last item.

## Implementation Notes

1. **Clipping to a circle is set in code.** The XML attribute `android:clipToOutline` requires API 31. The adapter sets `clipToOutline = true` on the avatar container when the row is first inflated, which works from API 21. It must be set on the view that owns the circular background (`contact_avatar`), not on the `ImageView`.
2. **Recycled rows must be fully reset.** `ListView` reuses rows through `convertView`. Every visual state (text, picture, visibility) must be set in both branches of every condition, otherwise a previous contact's data stays visible.
3. **Theme attributes in drawables.** Shape drawables such as `bg_avatar.xml`, `bg_fab.xml`, `bg_top_app_bar.xml` and `bg_search_bar.xml` use `?attr/...` colors. This works because the drawables are inflated with the Activity theme.
4. **System bar insets.** Because the app draws edge to edge, `MainActivity` applies the system bar and display cutout insets: top and sides to the top app bar, bottom and sides to the screen container. This keeps the FAB and the last list item above the navigation bar, and keeps content clear of the cutout in landscape.

## Resources

[M3 Color roles](https://m3.material.io/styles/color/roles)   
[M3 Typography](https://m3.material.io/styles/typography)   
[M3 App bars](https://m3.material.io/components/app-bars/guidelines)   
[M3 Search](https://m3.material.io/components/search/overview)   
[M3 Lists specs](https://m3.material.io/components/lists/specs)   
[Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)   
[Android Developer API Reference: View.setClipToOutline](https://developer.android.com/reference/android/view/View#setClipToOutline(boolean))
