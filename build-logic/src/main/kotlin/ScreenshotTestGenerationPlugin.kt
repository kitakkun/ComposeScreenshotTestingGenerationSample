import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

class ScreenshotTestGenerationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val kotlinVersion = target.getKotlinPluginVersion()

        val myDependencyScope = target.configurations.create("myDependencyScope")
        target.dependencies.add(myDependencyScope.name, "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
        val myResolvableConfiguration = target.configurations.create("myResolvable") {
            extendsFrom(myDependencyScope)
        }

        target.tasks.register("generateScreenshotTest", TaskUsingKotlinCompiler::class.java) {
            kotlinCompiler.from(myResolvableConfiguration)
            rootDirectory.set(target.projectDir)
        }
    }
}
