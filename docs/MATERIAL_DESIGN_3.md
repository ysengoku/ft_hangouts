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
   1. [List Item](#list-item)
   2. [Avatar](#avatar)
   3. [Floating Action Button](#floating-action-button)
5. [Implementation Notes](#implementation-notes)
6. [Resources](#resources)

</details>

## Color Roles

The framework theme only knows a few color roles (`colorPrimary`, `colorAccent`, `colorBackground`, `colorError`). The other M3 roles are declared as custom attributes in `res/values/attrs.xml` and set in every theme in `res/values/themes.xml`.

| M3 role | Theme attribute | Color resource | Used for |
|---|---|---|---|
| primary | `?android:attr/colorPrimary` | `<theme>_primary` | App header, FAB container |
| onPrimary | `?attr/colorOnPrimary` | `<theme>_on_primary` | App title, FAB icon |
| primaryContainer | `?attr/colorPrimaryContainer` | `<theme>_primary_container` | Avatar background |
| onPrimaryContainer | `?attr/colorOnPrimaryContainer` | `<theme>_on_primary_container` | Avatar initials |
| onSurface | `?attr/colorOnSurface` | `<theme>_on_surface` | Contact name |
| background | `?android:attr/colorBackground` | `<theme>_background` | Window background |

`<theme>` is one of `ocean`, `amber`, `forest`, `rose` or `lavender`. The color values were generated with [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/).

**Rules:**

1. Never hardcode a color in a layout or drawable. Always reference a theme attribute so that all five themes and dark mode keep working.
2. When a new custom attribute is added to `attrs.xml`, set it in all five themes. A missing attribute does not fail the build, but the view is drawn without that color.
3. Pair each container color with its matching "on" color (for example `primaryContainer` with `onPrimaryContainer`) to keep the contrast M3 guarantees.

## Typography

The framework has no M3 type scale, so each M3 style is reproduced as a TextAppearance style in `res/values/themes.xml`.

| M3 style | Style resource | Size | Weight | Font | Used for |
|---|---|---|---|---|---|
| titleMedium | `TextAppearance.FTHangouts.TitleMedium` | 16sp | Medium | `@font/noto_sans_medium` | Avatar initials |
| bodyLarge | `TextAppearance.FTHangouts.BodyLarge` | 16sp | Regular | `@font/noto_sans` | Contact name |

The app title in the header uses `TextAppearance.FTHangouts.Heading` (`@font/combo_regular`, 24sp). This is a brand style and is not part of the M3 type scale.

**Rules:**

1. Apply text styles with `android:textAppearance="@style/TextAppearance.FTHangouts.<Style>"`. Do not set `android:textSize` or `android:fontFamily` directly on a view.
2. Keep color out of TextAppearance styles. Set `android:textColor` on the view, because the same type style is used with different color roles (M3 defines typography and color separately).
3. When a new M3 style is needed, add it as `TextAppearance.FTHangouts.<M3 name in PascalCase>` with the values from the M3 type scale.

Weights are selected with separate font files rather than `android:textFontWeight`, because that attribute requires API 28 and `minSdk` is 26.

## Dimensions

Sizes and spacing are defined in `res/values/dimens.xml`.

| Resource | Value | Kind | Used for |
|---|---|---|---|
| `spacing_small` | 4dp | Spacing | Small gaps |
| `spacing_medium` | 16dp | Spacing | Screen and list item padding, gap between avatar and text |
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
2. **No dividers.** M3 lists separate items with spacing, not lines. The `ListView` sets `android:divider="@null"` and `android:dividerHeight="0dp"`.
3. **56dp is for one line items only.** If a second line is added later (for example a phone number), the M3 height becomes 72dp.

### Avatar

| Property | Value |
|---|---|
| Size | 40dp (`avatar_size`) |
| Shape | Circle (`res/drawable/bg_avatar.xml`) |
| Background | `primaryContainer` |
| Content with a picture | The picture, `centerCrop`, clipped to the circle |
| Content without a picture | Monogram: first letter of the first name and first letter of the last name, uppercase, `TextAppearance.FTHangouts.TitleMedium`, `onPrimaryContainer` |

The picture and the monogram are stacked in a `FrameLayout`. The adapter shows one and hides the other.

### Floating Action Button

Values are defined in `res/values/dimens.xml`.

| Property | Value |
|---|---|
| Size | 56dp (`fab_size`) |
| Corner radius | 16dp (`fab_corner_radius`) |
| Margin from the screen edge | 16dp (`fab_margin`) |
| Elevation | 6dp (`fab_elevation`, M3 level 3) |
| Container / icon | `primary` / `onPrimary` |

The M3 default FAB color is `primaryContainer` with an `onPrimaryContainer` icon. This app uses the `primary` color style instead.

The list reserves 88dp of bottom padding (`paddingBottom` with `clipToPadding="false"`) so that the FAB never covers the last item.

## Implementation Notes

1. **Clipping to a circle is set in code.** The XML attribute `android:clipToOutline` requires API 31. The adapter sets `clipToOutline = true` on the avatar when the row is first inflated, which works from API 21.
2. **Recycled rows must be fully reset.** `ListView` reuses rows through `convertView`. Every visual state (text, picture, visibility) must be set in both branches of every condition, otherwise a previous contact's data stays visible.
3. **Theme attributes in drawables.** Shape drawables such as `bg_avatar.xml` and `bg_fab.xml` use `?attr/...` colors. This works because the drawables are inflated with the Activity theme.

## Resources

[M3 Color roles](https://m3.material.io/styles/color/roles)   
[M3 Typography](https://m3.material.io/styles/typography)   
[M3 Lists specs](https://m3.material.io/components/lists/specs)   
[Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)   
[Android Developer API Reference: View.setClipToOutline](https://developer.android.com/reference/android/view/View#setClipToOutline(boolean))
