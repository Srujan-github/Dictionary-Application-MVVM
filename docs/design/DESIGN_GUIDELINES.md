# 🎨 Design Guidelines — Lexicon Dictionary App

> This document defines the visual design language, component system, and UX principles for the **Lexicon** dictionary application. All UI work must follow these guidelines to ensure consistency across all screens.

---

## 1. Design Philosophy

Lexicon uses a **soft, calm, and scholarly** aesthetic:

- **Calm Purple / Lavender palette** — conveys knowledge, creativity, and focus without being aggressive.
- **Generous whitespace** — lets words breathe; the content is the hero.
- **Rounded, card-based surfaces** — friendly and tactile; avoids sharp corporate edges.
- **Subtle hierarchy** — typography weight and size guide the eye naturally from word → pronunciation → meaning.

---

## 2. Color Palette

### 2.1 Primary Colors

| Role | Color Name | Hex | Usage |
|------|-----------|-----|-------|
| Background | Soft Lavender White | `#F5F0FF` | App background, screen base |
| Primary | Deep Purple | `#5C3FBE` | Buttons, active nav icons, links |
| Primary Light | Lavender Purple | `#7B5FC8` | Word of the Day card bg, chips |
| Primary Container | Pale Purple | `#EDE7FF` | Card backgrounds, icon backgrounds |
| Surface | Off-White | `#FAF8FF` | Cards, bottom sheet backgrounds |
| Surface Variant | Light Gray-Purple | `#EBEBF5` | Search bar, secondary card bg |

### 2.2 Text Colors

| Role | Hex | Usage |
|------|-----|-------|
| Primary Text | `#1A1A2E` | Headings (word title, section labels) |
| Secondary Text | `#5C5C7A` | Subtitles, pronunciations, descriptions |
| Hint / Placeholder | `#9999B5` | Search bar placeholder, disabled states |
| On-Primary | `#FFFFFF` | Text/icons on deep purple backgrounds |
| Accent Purple | `#5C3FBE` | Word links, part-of-speech chips, taglines |

### 2.3 Semantic Colors

| Role | Hex | Usage |
|------|-----|-------|
| Synonyms Chip bg | `#EDE7FF` | Lavender chip background |
| Synonyms Chip text | `#5C3FBE` | Deep purple chip text |
| Antonyms Chip bg | `#FFE9EF` | Pink chip background |
| Antonyms Chip text | `#C2185B` | Deep pink chip text |
| Success / Active | `#5C3FBE` | Toggle ON state |
| Error | `#E53935` | Error messages, network failures |

### 2.4 Dark Mode Colors *(planned)*

| Role | Hex |
|------|-----|
| Background | `#0F0B1E` |
| Surface | `#1C1635` |
| Primary | `#9B7FF5` |
| On-Surface | `#E8E0FF` |

---

## 3. Typography

### 3.1 Font Family

```
Primary Font: System Default (Roboto on most Android devices)
Recommended upgrade: "DM Sans" or "Inter" via Google Fonts
```

### 3.2 Type Scale

| Style | Size | Weight | Line Height | Usage |
|-------|------|--------|-------------|-------|
| Display Large | 32 sp | Bold (700) | 40 sp | Word title on Detail screen |
| Headline Large | 28 sp | Bold (700) | 36 sp | "Discover Words" screen title |
| Headline Medium | 22 sp | SemiBold (600) | 30 sp | Word of the Day word text |
| Title Large | 18 sp | SemiBold (600) | 26 sp | Section headers (Trending, Recent) |
| Body Large | 16 sp | Regular (400) | 24 sp | Definitions, example sentences |
| Body Medium | 14 sp | Regular (400) | 20 sp | Word descriptions in lists, sublabels |
| Label Large | 14 sp | Medium (500) | 20 sp | Pronunciation, part-of-speech badges |
| Label Medium | 12 sp | Medium (500) | 16 sp | Trending index numbers, chips |
| Caption | 12 sp | Regular (400) | 16 sp | App version, secondary metadata |

### 3.3 Text Styling Rules

- **Section labels** (DEFINITION, EXAMPLES, SYNONYMS, ANTONYMS) → ALL CAPS, `12 sp`, `Letter spacing: 1.5`, secondary text color.
- **Word title** on detail screen → Display Large, primary text color.
- **Pronunciation** → italic or regular, accent purple, `Label Large`.
- Never use more than **3 different font sizes** on a single screen.

---

## 4. Spacing & Layout System

### 4.1 Base Unit

> **Base unit = 8 dp**. All spacing and sizing must be a multiple of 8 dp (or 4 dp for fine adjustments).

### 4.2 Spacing Scale

| Token | Value | Usage |
|-------|-------|-------|
| `space_xs` | 4 dp | Internal chip padding, icon-text gap |
| `space_sm` | 8 dp | Between label and content, small gaps |
| `space_md` | 16 dp | Card internal padding, list item vertical padding |
| `space_lg` | 24 dp | Section vertical spacing |
| `space_xl` | 32 dp | Screen top padding |
| `space_xxl` | 48 dp | Large section separators |

### 4.3 Screen Margins

| Breakpoint | Horizontal Margin |
|-----------|-------------------|
| Phone (default) | 16 dp (guideline) |
| Large Phone | 24 dp |

---

## 5. Shape / Corner Radius

| Component | Corner Radius |
|-----------|--------------|
| Search Bar | 28 dp (fully pill) |
| Word of the Day card | 20 dp |
| Word tiles (Recent chips) | 20 dp (pill) |
| Trending list card | 16 dp |
| Definition / Example cards | 12 dp |
| Synonym / Antonym chips | 20 dp (pill) |
| Navigation bar (selected) | 24 dp bubble |
| Settings row divider card | 16 dp |
| Floating Action Button | Circular |

---

## 6. Iconography

- **Icon Style:** Outlined Material Icons (Material Symbols Outlined).
- **Icon Size:** 24 dp (standard), 20 dp (in chips/badges), 28 dp (navigation bar).
- **Icon Color:** Matches text color on same surface; active nav icons use Primary Purple.

### 6.1 Icon Map

| Screen / Component | Icon |
|--------------------|------|
| Search tab | `search` |
| Explore / Detail tab | `menu_book` |
| Saved tab | `bookmark` |
| Settings tab | `settings` |
| Back navigation | `arrow_back` |
| Share | `share` |
| Save / Bookmark | `bookmark` |
| Pronunciation / Sound | `volume_up` |
| Word of the Day | `auto_awesome` (sparkle) |
| Trending | `trending_up` |
| Recent History | `schedule` |
| Dark Mode | `dark_mode` |
| Language | `language` |
| Daily Word notification | `notifications` |
| About | `info` |
| Privacy | `shield` |
| Feedback | `chat_bubble_outline` |
| Rate | `favorite_border` |
| Arrow / forward | `arrow_forward_ios` |

---

## 7. Components

### 7.1 Search Bar

```
Shape     : Pill — corner radius 28 dp
Background: #EBEBF5 (Surface Variant)
Height    : 56 dp
Padding   : 16 dp left, 12 dp right
Leading   : search icon (24 dp, hint color)
Trailing  : mic icon (24 dp, hint color) — for voice search
Placeholder text: "Search for a word..." — Body Medium, hint color
Font when active: Body Large, primary text color
```

### 7.2 Word of the Day Card

```
Background : Pale Purple gradient (#EDE7FF → #DDD5F8)
Corner     : 20 dp
Padding    : 20 dp all sides
Word text  : Headline Medium, #5C3FBE, Bold
Phonetic   : Label Large, #5C3FBE, italic
Definition : Body Medium, #5C3FBE
Arrow FAB  : Deep Purple (#5C3FBE), white icon, 48×48 dp, 12 dp corner
```

### 7.3 Trending List Item

```
Background : Surface variant (#EBEBF5)
Corner     : 16 dp
Padding    : 16 dp horizontal, 14 dp vertical
Rank badge : circular, 36×36 dp, pale purple bg, label text
Word text  : Body Large, primary text, Bold
Description: Body Medium, secondary text, truncated to 1 line
Trailing   : arrow_forward_ios icon, hint color
Divider    : none (use margin 8 dp between items)
```

### 7.4 Recent Search Chips

```
Shape      : Pill — 20 dp corner
Background : White / Surface
Border     : 1 dp, #C8C4D8
Padding    : 8 dp top/bottom, 16 dp left/right
Text       : Body Medium, primary text color
```

### 7.5 Bottom Navigation Bar

```
Background : Surface (#FAF8FF) 
Elevation  : 8 dp
Height     : 64 dp
Tabs       : Search, Explore, Saved, Settings
Active tab : icon + label, filled container (pill shape), Deep Purple tint
Inactive   : icon + label, secondary text color
Label size : 12 sp
```

### 7.6 Word Detail — Part-of-Speech Badge

```
Shape      : Pill
Background : #EDE7FF
Text       : "adjective" / "noun" / "verb" — Label Medium, #5C3FBE
Padding    : 6 dp top/bottom, 12 dp left/right
```

### 7.7 Synonym / Antonym Chips

| Type | Background | Text Color |
|------|-----------|-----------|
| Synonym | `#EDE7FF` | `#5C3FBE` |
| Antonym | `#FFE9EF` | `#C2185B` |
| Padding | 8dp × 16dp | |
| Corner | 20 dp pill | |

### 7.8 Settings Row

```
Background : Surface (#FAF8FF)
Icon bg    : 40×40 dp circular, #EDE7FF
Icon size  : 24 dp, #5C3FBE
Title      : Body Large, primary text
Subtitle   : Body Medium, secondary text
Trailing   : Toggle (SwitchMaterial) OR arrow_forward_ios
Divider    : 1 dp, #E8E4F0, full width
```

---

## 8. Elevation & Shadow

| Level | Elevation | Usage |
|-------|-----------|-------|
| 0 | 0 dp | Screen backgrounds |
| 1 | 2 dp | Cards (resting state) |
| 2 | 4 dp | Search bar, chips |
| 3 | 8 dp | Bottom navigation bar |
| 4 | 12 dp | Modals, bottom sheets |

> Use `MaterialShapeDrawable` or `CardView` elevation rather than custom shadow drawables.

---

## 9. Motion & Animation

### 9.1 General Principles

- **Purposeful**: Animate only to communicate state change or user delight.
- **Fast & Natural**: Default durations between 150 ms – 350 ms.
- **Physics-based**: Prefer `FastOutSlowInInterpolator` (Material Standard easing).

### 9.2 Key Animations

| Interaction | Animation | Duration |
|-------------|-----------|----------|
| Fragment navigation | Shared element + fade | 300 ms |
| List item appearance | Slide-in from bottom + fade | 200 ms staggered |
| Search suggestions | Fade in RecyclerView | 180 ms |
| TypeWriter effect (taglines) | Character-by-character, 40 ms/char | Variable |
| Toggle switch | Material component default | Built-in |
| Word of the Day arrow button | Scale ripple on tap | Built-in |
| Progress bar | Circular indeterminate | Material default |

### 9.3 TypeWriter Animation

The `TypeWriterView` custom widget provides a character-by-character reveal:

```kotlin
typeWriterView.animateTaglines(listOf(
    "Explore meanings, synonyms, and more",
    "Discover the beauty of language",
))
```

- Default delay: **40 ms** per character.
- Loops infinitely through the tagline list.
- Avoid using on long texts > 80 characters.

---

## 10. Accessibility

| Requirement | Detail |
|-------------|--------|
| Minimum touch target | 48 × 48 dp |
| Color contrast ratio | ≥ 4.5:1 for body text, ≥ 3:1 for large text |
| Content descriptions | All icons must have `contentDescription` |
| Font scaling | Layouts must support sp units; test at 1.3× font scale |
| Dark mode | All colors must have a night variant in `values-night/` |

---

## 11. Screen-by-Screen Design Reference

### 11.1 Home Screen (Discover Words)

```
─────────────────────────────────
 Discover Words                 [space_xl top]
 Explore meanings, synonyms…    [TypeWriter or subtitle]
─────────────────────────────────
 [ 🔍  Search for a word... 🎤 ]  [Search Bar — space_lg below]
─────────────────────────────────
 ⏱ Recent                       [Section Header]
 [Paradigm] [Aesthetic] [Catalyst] [Nuance]   [Chips]
 [Pragmatic]
─────────────────────────────────
 ✨ Word of the Day              [Section Header]
 ╔══════════════════════════════╗
 ║ Ephemeral             [→]   ║
 ║ /ih-FEM-er-uhl/             ║
 ║ Lasting for a very short…   ║
 ╚══════════════════════════════╝
─────────────────────────────────
 📈 Trending                    [Section Header]
 01  Serendipity       The occurrence…  →
 02  Ubiquitous        Present, appearing…  →
 03  Eloquent          Fluent or persuasive…  →
─────────────────────────────────
 [Search] [Explore] [Saved] [Settings]   ← Bottom Nav
```

### 11.2 Word Detail Screen (Explore tab)

```
 ← (back)                    (share) (bookmark)
─────────────────────────────────
 Ephemeral                        [Display Large]
 🔊 /ih-FEM-er-uhl/  [adjective]  [Pronunciation + Badge]
─────────────────────────────────
 DEFINITION
 ┌────────────────────────────────┐
 │ Lasting for a very short time…│
 └────────────────────────────────┘
─────────────────────────────────
 EXAMPLES
 ┌────────────────────────────────┐
 │ ❝ The beauty of cherry…      │
 └────────────────────────────────┘
 ┌────────────────────────────────┐
 │ ❝ Fame can be ephemeral…     │
 └────────────────────────────────┘
─────────────────────────────────
 SYNONYMS
 [fleeting] [transient] [momentary] [brief] [passing]
─────────────────────────────────
 ANTONYMS
 [permanent] [enduring] [lasting] [eternal]
─────────────────────────────────
 [Search] [Explore●] [Saved] [Settings]
```

### 11.3 Saved Words Screen

```
 Saved Words
 1 word saved                    [subtitle]
─────────────────────────────────
 ╔════════════════════════════════╗
 ║ 📖  Ephemeral             →  ║
 ║     adjective (purple)       ║
 ║     Lasting for a very…      ║
 ╚════════════════════════════════╝
─────────────────────────────────
 [empty state if no saved words]
─────────────────────────────────
 [Search] [Explore] [Saved●] [Settings]
```

### 11.4 Settings Screen

```
 Settings
 Customize your experience
─────────────────────────────────
 PREFERENCES
 ╔════════════════════════════════╗
 ║ 🌙 Dark Mode           [●○] ║
 ║    Reduce eye strain…        ║
 ╠════════════════════════════════╣
 ║ 🌐 Language            [→]  ║
 ║    English                   ║
 ╠════════════════════════════════╣
 ║ 🔔 Daily Word          [○●] ║
 ║    Get a new word every day  ║
 ╚════════════════════════════════╝
─────────────────────────────────
 ABOUT
 ╔════════════════════════════════╗
 ║ ℹ️  About Lexicon          → ║
 ╠════════════════════════════════╣
 ║ 🛡️  Privacy Policy        → ║
 ╠════════════════════════════════╣
 ║ 💬 Send Feedback          → ║
 ╠════════════════════════════════╣
 ║ ❤️  Rate the App          → ║
 ╚════════════════════════════════╝

     Lexicon v1.0.0
─────────────────────────────────
 [Search] [Explore] [Saved] [Settings●]
```

---

*See also: [Architecture Guidelines](../architecture/ARCHITECTURE_GUIDELINES.md) · [Coding Guidelines](../coding/CODING_GUIDELINES.md)*
