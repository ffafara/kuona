package kuona.java.analyser.model;

import java.nio.file.Path;
import java.util.List;

public class JavaSourceFile {
    private String path;
    private String packageName;
    private List<JavaClassDeclaration> types;

    public JavaSourceFile(Path path, String packageName, List<JavaClassDeclaration> types) {
        this.path = path.normalize().toString();
        this.packageName = packageName;
        this.types = types;
    }
}
