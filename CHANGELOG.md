# CalcHub - Changelog

All notable changes to CalcHub will be documented in this file.

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
