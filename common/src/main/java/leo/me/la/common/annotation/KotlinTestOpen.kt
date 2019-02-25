package leo.me.la.common.annotation

/**
 * Annotation to open up kotlin class for testing
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class KotlinTestOpen
