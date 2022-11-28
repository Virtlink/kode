## How to Publish
To publish an artifact of this project from the command-line, follow these steps:

1.  Ensure there are no new or changed files in the repository.
2.  Create a tag with the new version number, such as `0.1.2-alpha`.
3.  Verify the version number using:
    ```shell
    ./gradlew printVersion
    ```
    The version number should not contain a commit hash and should not end with `.dirty`.
