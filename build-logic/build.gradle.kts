plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        /**
         * Suppressing the following error:
         * Incompatible classes were found in dependencies.
         * Remove them from the classpath or use '-Xskip-metadata-version-check' to suppress errors.
         *
         * Reason:
         * The Kotlin version used in the `kotlin-dsl` plugin is 1.9.
         * There is build logic that uses kotlin-compiler-embeddable, which results in a version mismatch error when used as compileOnly.
         * However, since it is independent of the normal build and the version mismatch does not cause any issues, it can be suppressed.
         */
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.compiler.embeddable)
}

gradlePlugin {
    plugins {
        register("screenshot-test-gen") {
            id = "screenshot-test-gen"
            implementationClass = "ScreenshotTestGenerationPlugin"
        }
    }
}
