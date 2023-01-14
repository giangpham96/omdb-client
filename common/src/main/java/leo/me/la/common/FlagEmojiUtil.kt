package leo.me.la.common

import java.util.Locale


fun String.toFlagEmoji(): String? {
    val countryCodeCaps = (if (this == "UK")
        "GB"
    else
        countries.firstOrNull {
            it.first.equals(this, true) || it.second.equals(this, true) || it.third.equals(this, true)
        }?.second?.uppercase()) ?: return null

    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return null
    }
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    return "${String(Character.toChars(firstLetter))}${String(Character.toChars(secondLetter))}"
}

private val countries = Locale.getISOCountries().map {
    val locale = Locale("en", it)
    val code = locale.isO3Country
    val name = locale.country
    val displayName = locale.displayCountry
    Triple(code, name, displayName)
}
