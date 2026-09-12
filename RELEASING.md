# Releasing

This repository publishes two artifacts:

| Artifact | Maven Central | JitPack |
| --- | --- | --- |
| Core library | `io.github.hyunolike:json-csv-bridge` | `com.github.hyunolike:json-csv-bridge` |
| Spring Boot starter | `io.github.hyunolike:json-csv-bridge-spring-boot-starter` | `com.github.hyunolike.json-csv-bridge:json-csv-bridge-spring-boot-starter` |

JitPack keeps working as before — it rewrites coordinates to `com.github.<user>`, so the
`group` in `gradle.properties` does not affect it.

## One-time setup

### 1. Claim the `io.github.hyunolike` namespace

Maven Central only accepts a `groupId` you can prove you own. `com.jsoncsvbridge` is not
claimable without owning the `jsoncsvbridge.com` domain, which is why the project publishes
under `io.github.hyunolike` instead — GitHub-backed namespaces are verified by creating a
temporary public repository that Sonatype names for you.

Register at <https://central.sonatype.com>, add the `io.github.hyunolike` namespace, and
follow the verification prompt.

### 2. Create a signing key

Central requires every artifact to carry a PGP signature.

```bash
gpg --full-generate-key                       # RSA, 3072+ bits, no expiry
gpg --list-secret-keys --keyid-format=long    # note the key id
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>   # publish the public key
gpg --armor --export-secret-keys <KEY_ID>     # this whole block becomes SIGNING_KEY
```

### 3. Add the repository secrets

In **Settings → Secrets and variables → Actions**:

| Secret | Value |
| --- | --- |
| `SIGNING_KEY` | the full `-----BEGIN PGP PRIVATE KEY BLOCK-----` export |
| `SIGNING_PASSWORD` | the key passphrase (empty if the key has none) |
| `MAVEN_CENTRAL_USERNAME` | user token name from the Central portal |
| `MAVEN_CENTRAL_PASSWORD` | user token password from the Central portal |

Generate the user token under **Account → Generate User Token** on the Central portal —
this is not your login password.

## Cutting a release

1. Bump `version` in `gradle.properties`.
2. Update the version shown in the four README files and in `examples/`.
3. Merge to `develop`.

That is the whole flow. The `Release` workflow reads `version` from `gradle.properties`
on every push to `develop`; when no matching tag exists yet it runs lint and tests,
creates the `v<version>` tag, wakes the JitPack build, and publishes both modules to
Maven Central. Merges that do not change the version find the tag already present and
stop early, so ordinary merges cost nothing.

The Central upload lands in a staging repository — open the Central portal and release
it from there.

Pushing a tag by hand still works and takes the same path, except that the workflow
refuses a tag that disagrees with `gradle.properties`:

```bash
git tag v2.2.0 && git push origin v2.2.0
```

### Before the credentials exist

Until `SIGNING_KEY` and `MAVEN_CENTRAL_USERNAME` are set, the workflow still tags the
release so JitPack picks it up, and skips only the Central upload with a notice. Merging
to `develop` will not start failing because Central is not configured yet.

### Why tagging and publishing live in one workflow

A tag created with the default `GITHUB_TOKEN` does not trigger other workflows — GitHub
blocks that to avoid loops. Splitting "create the tag" and "publish the tag" into two
workflows would leave the second one never running.

## Running a publish locally

```bash
export SIGNING_KEY="$(gpg --armor --export-secret-keys <KEY_ID>)"
export SIGNING_PASSWORD='…'
export MAVEN_CENTRAL_USERNAME='…'
export MAVEN_CENTRAL_PASSWORD='…'

./gradlew publishAllPublicationsToMavenCentralRepository
```

Without `SIGNING_KEY` the signing step is skipped, so ordinary builds and JitPack builds
need no key at all. To check what would be uploaded without touching the network:

```bash
./gradlew publishToMavenLocal   # writes to ~/.m2/repository/io/github/hyunolike/
```

## Notes

- `centralRepositoryUrl` in `gradle.properties` points at Sonatype's OSSRH-compatible
  endpoint for the Central Portal. Sonatype has changed these endpoints before, so if an
  upload is rejected with a 401/404, check the current URL in the
  [Central Portal documentation](https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/)
  and update that one property.
- The `javadoc` jar is empty because the sources are Kotlin and the project does not run
  Dokka. Central only requires the file to be present, but adding Dokka would make the
  published Javadoc actually useful.
