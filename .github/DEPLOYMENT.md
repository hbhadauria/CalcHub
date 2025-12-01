# GitHub Actions Setup for Play Store Deployment

## Required Secrets

You need to configure the following secrets in your GitHub repository:

### 1. KEYSTORE_BASE64
Your Android signing keystore encoded in base64.

**Generate:**
```bash
base64 -i your-release-keystore.jks | pbcopy
```

### 2. KEYSTORE_PASSWORD
Password for your keystore file.

### 3. KEY_ALIAS
Alias name for your signing key.

### 4. KEY_PASSWORD
Password for your signing key.

### 5. PLAY_STORE_SERVICE_ACCOUNT_JSON
Google Play Console service account JSON (as plain text).

**Steps to create:**
1. Go to Google Play Console
2. Navigate to Setup → API access
3. Create a new service account or use existing
4. Download the JSON key file
5. Copy the entire JSON content as a secret

## How to Add Secrets

1. Go to your GitHub repository
2. Click on **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret with the exact name listed above

## Workflow Triggers

The workflow runs on:
- **Tag push**: When you push a tag like `v2.0.0`
- **Manual trigger**: Via GitHub Actions UI

## Usage

### Deploy a new version:
```bash
git tag v2.0.0
git push origin v2.0.0
```

### Manual deployment:
1. Go to Actions tab in GitHub
2. Select "Build and Deploy to Play Store"
3. Click "Run workflow"

## What's New Directory

Create release notes for Play Store:

```bash
mkdir -p distribution/whatsnew
echo "• Complete Neon redesign
• New favorites system
• 3-column grid layout
• Enhanced UI components" > distribution/whatsnew/whatsnew-en-US
```

## Pipeline Stages

1. **Test**: Runs all unit tests
2. **Build**: Creates signed release AAB
3. **Deploy**: Uploads to Play Store (production track)

## Notes

- The pipeline uses JDK 21 (matching your local setup)
- AAB (Android App Bundle) is built for optimal Play Store delivery
- ProGuard mapping file is uploaded for crash reporting
- Tests must pass before building
- Build must succeed before deploying
