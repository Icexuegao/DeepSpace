package ice.asundry.BaseTool;

import arc.Files.FileType;
import arc.files.Fi;

import java.io.*;

public class HFi extends Fi {
    public final ClassLoader loader;
    public HFi(ClassLoader loader) {
        this("", loader);
    }
    private HFi(String path, ClassLoader loader) {
        this(new File(path), loader);
    }
    private HFi(File file, ClassLoader loader) {
        super(file, FileType.classpath);
        if (loader == null) throw new IllegalArgumentException("loader cannot be null.");
        this.loader = loader;
    }
    // public boolean isDirectory() {
    // 	// throw new UnsupportedOperationException();
    // }
    public boolean exists() {
        return loader.getResource(path()) != null;
    }
    public String path() {
        return super.path().substring(1);
    }
    public InputStream read() {
        if (file.getPath().isEmpty()) throw new UnsupportedOperationException("Cannot read the root.");
        return loader.getResourceAsStream(path());
    }
    public OutputStream write() {
        throw new UnsupportedOperationException("HFi cannot write anything.");
    }
    public Fi parent() {
        return new HFi(file.getParent(), loader);
    }
    public Fi child(String name) {
        return new HFi(new File(file, name), loader);
    }
}