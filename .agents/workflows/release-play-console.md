---
description: How to release the Android app build to Google Play Console using Fastlane
---
# Releasing the Build to Google Play Console

This workflow details the steps to release a new Android build to the Google Play Console using the configured Fastlane setup.

### Prerequisites

1.  **Google Play Service Account JSON Key**: Make sure you have downloaded the Service Account JSON key assigned from the Google Cloud Console tied to your Play Console account.
2.  **Fastlane Appfile**: Verify that `fastlane/Appfile` references the exact path to your JSON key file.

```ruby
# In fastlane/Appfile
json_key_file("/path/to/your/play-store-service-account-key.json")
package_name("labs.creative.dictornarymvvmapp")
```

### Steps

1. Configure the `deploy` lane in `fastlane/Fastfile` if it's currently commented out. Uncomment the `upload_to_play_store` action.
```ruby
  desc "Deploy a new version to the Google Play"
  lane :deploy do
    gradle(
      task: "bundle",
      build_type: "Release"
    )
    upload_to_play_store(track: 'internal') # Or 'beta', 'production'
  end
```

2. Make sure you have bumped the `versionCode` and `versionName` in your `app/build.gradle.kts`.

3. Run the fastlane deployment lane.
```bash
bundle exec fastlane deploy
```

4. Watch the output. Fastlane will:
   * Build the signed Android App Bundle (`app-release.aab`).
   * Authenticate with the Play Store using your JSON key.
   * Upload the new App Bundle and release it to the specified track.
