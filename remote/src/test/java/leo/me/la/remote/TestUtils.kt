package leo.me.la.remote

/**
 * Utility methods to use inside Tests
 */
private class TestUtils // Used to provide class Loader inside test folder

/**
 * Reads the content (UTF-8 encoding used) of the file in the given Path
 */
fun String.readFileContent(): String {
    return TestUtils::class.java.classLoader
        .getResourceAsStream(this)
        .bufferedReader()
        .use { it.readText() }
}
