package kuona.java.analyser;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FileFinder
        extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private int numMatches = 0;
    private Consumer<Path> action;

    FileFinder(String pattern, Consumer<Path> action) {
        this.action = action;
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
    }

    public static void find(Path path, String pattern, Consumer<Path> action) {
        try {
            FileFinder finder = new FileFinder(pattern, action);
            Files.walkFileTree(path, finder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            action.accept(file);
        }
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        return CONTINUE;
    }
}
