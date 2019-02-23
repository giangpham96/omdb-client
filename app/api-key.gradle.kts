import java.io.FileInputStream
import java.util.Properties

/**
 * Provides the array of properties defined in api-key.properties file
 */
private val apiKeys: Map<String, String> by lazy {
    // Read file sign.properties
    val file = file("api-key.properties")
    if (file.canRead()) {
        // Read properties from file and return it
        val properties = Properties()
        val inputstream = FileInputStream(file)
        properties.load(inputstream)
        inputstream.close()
        properties
            .mapKeys {
                it.key as String
            }.mapValues {
                it.value as String
            }
    } else {
        System.getenv()
    }
}

/**
 * Returns omdb api key defined in api-key.properties
 */
extra["omdbApiKey"] = apiKeys["OMDB_API_KEY"]
