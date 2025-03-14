import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class PreviewSourceCollector private constructor() : KtTreeVisitorVoid() {
    companion object {
        fun visitPsiFile(psiFile: PsiFile): List<KtElement> {
            val collector = PreviewSourceCollector()
            psiFile.accept(collector)
            return collector.requiredDeclarations
        }
    }

    private val requiredDeclarations = mutableListOf<KtElement>()

    override fun visitDeclaration(dcl: KtDeclaration) {
        val shouldBeCopied = when (dcl) {
            is KtNamedFunction -> {
                val hasPreviewAnnotation = dcl.annotationEntries.any { it.text.startsWith("@Preview") }
                val hasComposableAnnotation = dcl.annotationEntries.any { it.text == "@Composable" }

                hasPreviewAnnotation && hasComposableAnnotation
            }

            else -> false
        }

        if (!shouldBeCopied) {
            return super.visitDeclaration(dcl)
        }

        requiredDeclarations.add(dcl)
    }
}
