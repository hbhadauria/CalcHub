# CalcHub

CalcHub is a comprehensive financial calculator application for Android. It provides a wide range of calculators to help you with your financial planning.

## Build & Run

### Prerequisites
- JDK 17 or higher
- Android Studio 2024.1.1 (Ladybug) or newer
- Android SDK 34+

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/hbhadauria/CalcHub.git
   ```
2. Open in **Android Studio**.
3. Sync Gradle project.
4. Run on an emulator or physical device.

## Tech Stack
- **Languages**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM
- **Navigation**: Typesafe Navigation Compose
- **Build**: Gradle KTS, Version Catalogs


### Investment Calculators
*   SIP - Systematic Investment Plan
*   Lumpsum - One-time investment
*   SWP - Systematic Withdrawal Plan
*   MF Returns - Mutual Fund Returns
*   Step Up SIP - SIP with yearly increase
*   FD - Fixed Deposit
*   RD - Recurring Deposit
*   PPF - Public Provident Fund
*   NSC - National Savings Certificate
*   Compound Interest

### Loan Calculators
*   EMI - Equated Monthly Installment
*   Home Loan EMI
*   Car Loan EMI
*   Flat vs Reducing Rate

### Retirement & Pension
*   EPF - Employee Provident Fund
*   NPS - National Pension System
*   Retirement Calculator
*   APY - Atal Pension Yojana

### Tax & Salary
*   Income Tax
*   HRA - House Rent Allowance
*   Gratuity
*   TDS - Tax Deducted at Source
*   Salary Calculator

### Government Schemes
*   SSY - Sukanya Samriddhi Yojana
*   Post Office MIS
*   SCSS - Senior Citizens Savings Scheme

### Trading & Investment Analysis
*   Brokerage Calculator
*   Margin Calculator
*   Stock Average Calculator
*   CAGR - Compound Annual Growth Rate
*   XIRR - Extended Internal Rate of Return

### Others
*   GST Calculator
*   Inflation Calculator
*   Simple Interest

## Privacy Policy
CalcHub is an **offline-first** application.
- **No Data Collection**: We do not collect, store, or transmit any personal financial data.
- **Local Storage**: All data (favorites, history) is stored locally on your device using Android's shared preferences.
- **No Analytics**: The app does not contain any tracking or analytics SDKs.

> [!WARNING]
> **Security Note**: When submitting Issues or Pull Requests, please ensure you do not include any real API keys, passwords, or personal financial data.


## Signing for Release
To build a signed release APK/AAB locally, you need a `keystore.jks` file.
1. Create a `version.properties` file in the root (if missing).
2. Configure your keystore details in `local.properties` (never commit this file):
   ```properties
   storeFile=/path/to/keystore.jks
   storePassword=your_store_password
   keyAlias=your_key_alias
   keyPassword=your_key_password
   ```
   *Note: The project uses GitHub Actions for automated signing in CI.*

