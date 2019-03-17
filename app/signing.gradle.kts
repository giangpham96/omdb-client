import java.io.FileInputStream
import java.util.Properties

/**
 * Provides the array of properties defined in sign.properties file
 */
private val signProperties: Map<String, String> by lazy {
    // Read file sign.properties
    val file = file("sign.properties")
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
        throw RuntimeException("Cannot read sign properties")
    }
}

/**
 * Returns the signature path defined in sign.properties
 */
extra["signaturePath"] = signProperties["KEYSTORE_PATH"]

/**
 * Returns the keystore password defined in sign.properties
 */
extra["signatureKeystorePassword"] = signProperties["KEYSTORE_PASSWORD"]

/**
 * Returns the keystore alias defined in sign.properties
 */
extra["signatureKeystoreAlias"] = signProperties["KEYSTORE_ALIAS"]

/**
 * Returns the keystore key password defined in sign.properties
 */
extra["signatureKeyPassword"] = signProperties["KEY_PASSWORD"]
