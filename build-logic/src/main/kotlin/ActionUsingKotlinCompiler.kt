import org.gradle.api.file.DirectoryProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.util.PsiUtilCore
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.incremental.deleteDirectoryContents
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

interface MyWorkParameters : WorkParameters {
    val rootDirectory: DirectoryProperty
}

abstract class ActionUsingKotlinCompiler : WorkAction<MyWorkParameters> {
    override fun execute() {
        val rootDir = parameters.rootDirectory.get().asFile
        regenerateModulePreviewScreenshots(rootDir)
    }

    private fun regenerateModulePreviewScreenshots(rootDir: File) {
        val srcDir = File(rootDir, "src/main/kotlin")
        val screenshotTestSrcDir = File(rootDir, "src/screenshotTest/kotlin")

        // regenerate screenshot tests each time to ensure that all tests are up-to-date.
        if (screenshotTestSrcDir.exists()) {
            screenshotTestSrcDir.deleteDirectoryContents()
        }

        srcDir.walkTopDown()
            .filter { it.extension == "kt" }
            .forEach { file ->
                val environment = KotlinCoreEnvironment.createForProduction(
                    Disposer.newDisposable(),
                    CompilerConfiguration(),
                    EnvironmentConfigFiles.JVM_CONFIG_FILES
                )
                val virtualFile = requireNotNull(environment.findLocalFile(file.path))
                val psiFile = PsiUtilCore.getPsiFile(environment.project, virtualFile)

                if (psiFile !is KtFile) error("non-kotlin file is not supported.")

                val requiredDeclarations = PreviewSourceCollector.visitPsiFile(psiFile)
                if (requiredDeclarations.isEmpty()) return@forEach

                val packageDirective = psiFile.packageDirective
                val packageName = packageDirective?.packageNameExpression?.text
                val importDirectives = psiFile.importDirectives

                val fileContent = StringBuilder().apply {
                    packageDirective?.let {
                        appendLine(it.text)
                        appendLine()
                    }
                    importDirectives.forEach {
                        appendLine(it.text)
                    }
                    appendLine()
                    requiredDeclarations.forEach {
                        appendLine(it.text)
                        appendLine()
                    }
                }.toString()

                val outputTargetParentDir = packageName?.let { File(screenshotTestSrcDir, it.replace(".", "/")) } ?: screenshotTestSrcDir
                val outputTargetFile = File(outputTargetParentDir, "${file.nameWithoutExtension}PreviewsScreenshots.kt")

                outputTargetParentDir.mkdirs()
                outputTargetFile.writeText(fileContent)
            }
    }
}
