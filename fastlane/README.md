# Fastlane setup

Manual steps required before `fastlane deploy` will work:

1. Create a Google Cloud service account with the "Service Account User"
   role and Play Console API access. In Play Console > Setup > API access,
   grant that service account "Release Manager" (or equivalent release)
   permissions for this app. Download its JSON key.
   Full instructions: https://docs.fastlane.tools/actions/supply/#setup
2. Set the `PLAY_STORE_JSON_KEY_PATH` environment variable to the absolute
   path of the downloaded JSON key (consumed by `fastlane/Appfile`).
3. Run `bundle install` once, from the project root, to install fastlane
   per the `Gemfile`.
4. `app/google-services.json` is no longer committed (it's gitignored — it
   contains a Google API key GitHub flags in public repos). Restore it
   locally before building:
   `echo "$GOOGLE_SERVICES_JSON" | base64 -d > app/google-services.json`
   The base64-encoded file is stored as the `GOOGLE_SERVICES_JSON` repo
   secret. Grab the original from Firebase Console > Project Settings if
   you need a fresh copy instead.
5. Run `fastlane deploy`.

## Lanes

- `fastlane test` — runs unit tests.
- `fastlane beta` — builds and uploads to Firebase App Distribution
  (needs `FIREBASE_APP_ID` and `FIREBASE_TOKEN` env vars).
- `fastlane build_release` — builds the release bundle only.
- `fastlane bump_version` — increments `versionCode` and `versionName` in
  `app/build.gradle.kts`.
- `fastlane deploy` — builds the release bundle and uploads it to the Play
  Console "internal" testing track as a draft release (does not
  auto-publish).
