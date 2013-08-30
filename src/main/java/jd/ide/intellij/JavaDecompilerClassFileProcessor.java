package jd.ide.intellij;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.ContentBasedClassFileProcessor;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import jd.ide.intellij.config.JDPluginComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extension processor needed to decompile the class with a custom decompiler.
 */
public class JavaDecompilerClassFileProcessor implements ContentBasedClassFileProcessor {

    private CachingJavaDecompilerService javaDecompilerService;
    private JDPluginComponent jdPluginComponent =
            ApplicationManager.getApplication().getComponent(JDPluginComponent.class);

    public JavaDecompilerClassFileProcessor() {
        javaDecompilerService = ServiceManager.getService(CachingJavaDecompilerService.class);
    }

    @Override
    public boolean isApplicable(Project project, VirtualFile virtualFile) {
        return virtualFile.getFileType() == StdFileTypes.CLASS;
    }

    @Override
    @NotNull
    public SyntaxHighlighter createHighlighter(Project project, VirtualFile vFile) {
        return SyntaxHighlighter.PROVIDER.create(StdFileTypes.JAVA, project, vFile);
    }

    @Override
    @NotNull
    public String obtainFileText(Project project, VirtualFile virtualFile) {
        String intermediary_var_of_decompiledFile = javaDecompilerService.decompile(project, virtualFile);
        return intermediary_var_of_decompiledFile;
    }

    @Override
    @Nullable
    public Language obtainLanguageForFile(VirtualFile virtualFile) {
        if (virtualFile.getFileType() == StdFileTypes.CLASS) {
            return null; // weirdly returning null in order to not decompile when source is available.
        } else if (virtualFile.getFileType() == StdFileTypes.JAVA) {
            return Language.findLanguageByID("JAVA");
        }
        return null;
    }
}
