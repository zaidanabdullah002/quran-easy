# Quran Easy — revision 3 screen review plan

The supplied export contains **8 screens**, plus one shared design-system DESIGN.md. The old Manzil list is removed. Dialogs, tabs, overlays and loading/error states are reviewed within their parent screen, not counted as extra screens.

## Review loop

Implement → build → install → capture → compare design and behavior → record differences → fix → repeat.

Every screen document contains As is, To be, constraints, initial gaps, an iteration log and a difference register. Iteration 0 is a source-based baseline; no new implementation or emulator acceptance is claimed by this documentation pass.

## Full-scope execution tracker

All screens are required; this table is not a priority ranking. Source changes are not visual acceptance. Browser tabs produce ten primary views across eight screens, requiring twenty light/dark captures plus dialogs and interaction states.

| Screen | Remaining work | Status |
| --- | --- | --- |
| Home | Reference hierarchy, gradients, functioning verse actions and header action | Open |
| Browser — Surah/Juz/Bookmarks | Frequently Read, search controls, Juz metadata, bookmark reference coverage | Open |
| Quran reader | Arabic-only Bismillah and adaptive text sizing patched; verify dock, wrapping and playback | Implemented in part; device verification pending |
| Tasbih | Target picker, completion state, cycle/daily totals and sound behavior | Open |
| Manzil | Audio controls and playlist implemented; playback, verse decorations and reference header verification remain | Implemented in part; device verification pending |
| Feeling detail | Hero, mood chips, named Surahs and working verse actions | Open |
| Settings | Dark slider colors patched; reference icons, preview metadata and saved state remain | Implemented in part; device verification pending |
| About | Provenance, Data Philosophy, truthful app version and claims | Open |

Current references are checked into `design/stitch/revision_3`. The local `(5).zip` has not yet been byte-verified against the uploaded `(5)(1).zip`. Generated image boards are not evidence.

## Source precedence

Latest explicit user requirements override export samples. In particular the Quran reader stays Arabic-only even though the export includes an English Bismillah caption. Quran/Manzil text, counts, prayer times, completion, versions and audio durations come from real data, never screenshot examples. Manzil’s real 18 passages remain intact.

Home’s export uses Home/Quran/Prayer/Duas/Settings tabs while other main screens use Home/Quran/Tasbih/Manzil/Settings. This is an unresolved design inconsistency; do not silently map labels to unrelated destinations.

## Completion gate

Every required difference must have visual/behavioral evidence on the current APK. No enabled empty actions. Record unsupported features explicitly and resolve their scope before acceptance. Recheck every screen affected by shared-component changes. Build-only evidence never closes visual or playback defects.

## Validation — 2026-11-20 resumed session

- Direct Kotlin/Compose compilation: 170 source files, exit 0 (deprecation warnings only). Uses temporary resource symbols, so this is not an APK build.
- Scoped `git diff --check`: passed for the reader, Settings and review documents.
- ADB detects `emulator-5554` as connected.
- Full Gradle build: blocked by restricted wrapper lock access. Using the installed distribution with a workspace Gradle home also fails because the Gradle daemon cannot open a socket (`Operation not permitted`).
- No new APK was produced or installed. The emulator's existing installation cannot verify these changes. All visual acceptance remains pending.

## Compact headers — 2026-11-20

User-approved departure from the duplicated reference headers: main inner screens now share one fixed 56 dp Back/title/Info header. Covers Surah/Juz/Bookmarks, Settings, Tasbih and Daily Manzil. Removed the browser subtitle and duplicate 114-Surahs badge. Browser search and tabs remain fixed beneath the header; tab touch targets are at least 48 dp. Search retains Material's standard height for text accessibility. Tasbih no longer applies duplicate system-bar padding; reset sits with target/haptic controls. Settings back goes through the activity back dispatcher to respect its saving guard. Manzil retains reading/audio controls below the header. Home branding remains in its own screen.

Main-source compilation passed after header restructuring. Visual/device verification remains pending under the recorded APK build blocker.
