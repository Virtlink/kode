## How to Publish
How to publish a new version of the library.

## Setup
In your _home_ directory at `~/gradle.properties`, ensure the file contains properties for signing, publishing to GitHub Packages, and publishing to Maven Central.

### Signing
Obtain the short ID of the GPG subkey you want to use for signing using the command:
```shell
gpg --list-secret-keys --keyid-format SHORT
```
It's the 8-character part after the key's type.

The key's password you should already know, and the secret keyring file is not available by default in modern GPG versions. Export it using the following command, where `$KEYID` is the short key ID:
```shell
gpg --export-secret-keys $KEYID ~/.gnupg/secring.gpg
```
Also create a base64-encoded version of the secret key ring:
```shell
base64 ~/.gnupg/secring.gpg > ~/.gnupg/secring.gpg.b64
```

In the `~/gradle.properties` file in your _home_ directory, add those values as the following properties:
```properties
# GPG Signing
signing.keyId=0ABA0F98
signing.password=WAWFAFDcKxNFgZ8YQnjHMrXuxn02
signing.secretKeyRingFile=/Users/myusername/.gnupg/secring.gpg
```
On the [repository settings Secrets page](https://github.com/Virtlink/kode/settings/secrets/actions), add those values as the following secrets:

- `SIGNING_KEY_ID`: The short ID of the key, e.g., `0ABA0F98`
- `SIGNING_KEY_PASSWORD`: The password of the key, e.g., `WAWFAFDcKxNFgZ8YQnjHMrXuxn02`
- `SIGNING_KEYRING_FILE`: the base64-encoded keyring file `secring.gpg.b64`


### GitHub Packages
For GitHub Packages, obtain the GitHub Personal Access Token (PAT) from the [GitHub Developer Settings](https://github.com/settings/tokens) page. The token should have the `read:packages` and `write:packages` scopes. The username is your GitHub username.

In the `~/gradle.properties` file in your _home_ directory, add those values as the following properties:
```properties
# GitHub Packages
gpr.user=MyUsername
gpr.key=ghp_BRW0dchXpF3QH5c5JJuGhXrgN9SHM1fMrVP4
```


### OSSRH Maven Central
The OSSRH username and token can be found as follows:

1. Login into https://oss.sonatype.org/
2. Click your username in the top-right, go to Profile.
3. In the drop-down box, change from Summary to User Token.
4. Click the Access User Token button. It will give a username and token.

In the `~/gradle.properties` file in your _home_ directory, add those values as the following properties:
```properties
# OSSRH Maven Central
ossrh.user=bYpE4FzT
ossrh.token=T33RJJMfxWPlvKhA7pp9izcsgpwbA4FBY3hMoH3+bdk5
```
On the [repository settings Secrets page](https://github.com/Virtlink/kode/settings/secrets/actions), add those values as the following secrets:

- `OSSRH_USERNAME`: The OSSRH token username, e.g., `bYpE4FzT`
- `OSSRH_TOKEN`: The OSSRH token, e.g., `T33RJJMfxWPlvKhA7pp9izcsgpwbA4FBY3hMoH3+bdk5`


## Automatic Publishing
Automatic publishing is preferred over manual publishing. To publish a new version of the library automatically using GitHub Actions, follow these steps:

1.  Ensure there are no new or changed files in the repository.
2.  Use Git to create a tag with the new version number, such as `0.1.2-alpha`.
    ```shell
    git tag 0.1.2-alpha
    ```
3.  Verify that the version number is correctly picked up using:
    ```shell
    ./gradlew printVersion
    ```
    The version number should not contain a commit hash and should not end with `.dirty`.
4.  Push the tag to the remote repository:
    ```shell
    git push origin 0.1.2-alpha
    ```


## Manual Publishing
To publish a new version of the library manually from the command-line, follow the above steps and then build, sign, and publish the artifacts to GitHub Packages and Maven Central using:
```shell
./gradlew publish
```


## Releasing the Artifact
After publishing, the artifact must be released manually from Maven Central. To do so, follow these steps:
1.  Login into OSSRH at [s01.oss.sonatype.org](https://s01.oss.sonatype.org/)
2.  Go to 'Staging Repositories' on the left, click the 'Refresh' button at the top to see the repositories.
3.  Select the repository, and click the 'Close' button at the top.
    This will validate the package requirements. Wait for it to complete,
    by periodically clicking the 'Refresh' button at the top.
4.  Once successfully closed, click the 'Release' button to publish the package on Maven Central.
    The repository may be dropped after release. Again, wait for the operation to complete.
5.  Done! It can take a while for the new release to appear on Maven Central at:
    <https://search.maven.org/artifact/net.pelsmaeker/kode>


## Update Documentation
Finally, update the `README.md` and documentation to reflect the latest release of the library.
