# CalcHub - Changelog

All notable changes to CalcHub will be documented in this file.

## [3.1.0] - 2026-11-22

### Added
- **Minimal Black Theme**: Added a pure dark monochrome appearance option alongside Aurora, Ledger Paper, and Dot Matrix
- **Inflation Adjustment**: Added an optional inflation toggle and rate control to SIP, Step-up SIP, and Lumpsum calculators to show inflation-adjusted purchasing power
- **New Financial Calculators**: Added Capital Gains Tax (FY26), Goal SIP Planner, Loan Prepayment Savings, Credit Card Payoff, FIRE / Coast-FIRE, Buy vs. Rent, Spend vs. Invest, and Freelance Tax (Section 44ADA) calculators
- **Upgraded Salary Calculator**: Added an instant CTC-to-in-hand estimation mode using FY26 New Regime slabs alongside custom pay components
- **Popular Discovery & Offline Sharing**: Added a Popular filter orb on Home, one-tap offline result sharing in calculators, starter preset chips, and expandable plain-English financial term hints
- **About & App Rating**: Added an About CalcHub dialog inside Appearance & Theme with version info, financial disclaimer, and a quick Play Store rating link

### Changed
- **Floating Bottom Navigation & Tools Tabs**: Switched the hub navigation bar to a floating overlay and updated Tools sub-tabs to an underline tab bar with cleaner segmented pills
- **Theme Gradient Scoping**: Scoped background gradient presets exclusively to the Aurora theme where gradients apply
- **Compact Result-First Layout**: Reordered calculator screens to show primary results above inputs and charts below, with tighter input spacing and configuration-safe state

### Fixed
- **Calculator Math & Edge Cases**: Audited and corrected calculations across GST inclusive, Flat vs. Reducing, SWP depletion, RD quarterly compounding, SSY tenure limits, EPF monthly accrual, APY slabs, and Income Tax regimes, with zero/negative/`NaN` guards and expanded unit tests

## [3.0.0] - 2026-11-17

### Added
- **Multi-Theme System**: Choose between Aurora, Ledger Paper, and Dot Matrix themes with System, Light, and Dark modes
- **Background Gradients**: Added 6 customizable background gradient options (By Screen, Aurora, Sunset, Emerald, Ocean, and Solid Off)
- **Continue Hero Banner**: Resume your most recent calculation directly from the Home screen with a live growth sparkline
- **4-Tab Tools Lab**: Added Utilities (INR words/units converter, Rule of 72/114/144, real return check), Compare (SIP vs. Home Loan Prepayment), local Vault for saved scenarios, and a searchable 30+ Formulas directory
- **Interactive Wealth & Loan Charts**: Added scrubbable SIP/Step-Up compounding curves, Home Loan `+1 Extra EMI / yr` prepayment simulator with yearly principal-vs-interest bars, and FY26 vs. Old Income Tax regime comparison
- **Adaptive Tablet & Foldable Layout**: Added side-by-side list-detail view on wider screens while keeping single-pane navigation on phones

### Changed
- **Complete UI Redesign**: Replaced the dark neon/glassmorphism interface with the clean, high-contrast Expressive Aurora design and morphing category orbs
- **App Icon**: Updated launcher, monochrome, and store icons to the Aurora Rupee + Growth Sparkline design
- **Calculator Discovery**: Updated Home, Search, and Tools Lab labels to `30+` calculators with a dedicated Offline privacy dialog
- **Home Grid**: Streamlined calculator cards with clean Heart-only favorite toggles and category-matched accents

### Technical
- Migrated navigation to Jetpack Navigation 3 (`NavDisplay` and `CalcNavKey`) with predictive back gesture support
- Added pure-Kotlin `AppContainer` dependency injection along with `ThemeRepository` and `UserDataRepository` offline SharedPreferences persistence
- Built custom Jetpack Compose `Canvas` and `PathMeasure` chart components with zero external charting libraries
- Added end-to-end Compose + Espresso instrumentation tests (`CalcHubEspressoAndComposeTest`), Robolectric JVM UI tests (`CalcHubRobolectricUiTest`), and CI workflow integration (`unit-test.yml`, `instrumentation-test.yml`, `release.yml`)

---

## [2.1.0] - 2025-12-08

### Open Source
- **Public Release**: CalcHub is now Open Source under the MIT License!
- **Documentation**: Added comprehensive Build instructions and Contribution guidelines.
- **Security**: Hardened repository configuration and validated absence of secrets.

## [2.0.0] - 2025-11-30

### Added
- **Favorites System**: Save frequently used calculators with heart icon, accessible from Saved tab
- **Bottom Navigation**: New navigation bar with Home, Saved, and Tools tabs
- **Gradient Borders**: Beautiful Green → Pink gradient borders on all calculator cards
- **Neon Glow Effects**: Enhanced donut charts with blur-based glow effects
- **3-Column Grid**: Compact grid layout on Home screen for better space utilization
- **Icon Indicators**: Result cards now show color-coded icons (Pink for Invested, Green for Returns, White for Total)

### Changed
- **Complete UI Redesign**: Neon/Glassmorphism theme with dark background and vibrant accents
- **Calculator Input Layout**: Horizontal layout with Label on left, Input box on right, and slider below
- **Card Design**: More transparent glass effect (60% opacity) with thicker borders (2dp)
- **Icons**: Increased to 48dp for better visibility
- **Sliders**: Custom colors with Neon Pink track and Neon Green thumb
- **Search**: Real-time filtering across all calculators

### Fixed
- Missing imports in multiple components
- DonutChart rendering and glow effect issues
- Navigation flow inconsistencies

### Technical
- Updated all 25+ calculator screens to use standardized components
- Implemented `CalculatorScaffold` for consistent layout
- Created reusable `NeonCard`, `NeonInput`, and `NeonSearch` components
- Integrated `FavoritesRepository` with SharedPreferences for data persistence

---

## [1.0.0] - Initial Release

### Added
- 25+ financial calculators across 6 categories
- Investment calculators (SIP, Lumpsum, SWP, CAGR)
- Loan calculators (EMI, Flat vs Reducing)
- Tax calculators (Income Tax, GST, HRA, TDS)
- Retirement calculators (PPF, EPF, NPS, Retirement Planning)
- Savings calculators (FD, RD, NSC, SSY, SCSS, Post Office MIS)
- Trading calculators (Brokerage, Margin, Stock Average)
- Other calculators (APY, Gratuity, Inflation, Salary, Interest, XIRR)
- Interactive donut charts for visual representation
- Material 3 design system
- Navigation between calculators
