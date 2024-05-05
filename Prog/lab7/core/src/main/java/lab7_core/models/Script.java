package lab7_core.models;

import java.io.Serializable;
import java.util.Objects;

public class Script implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String fileName;
    private final String[] content;

    public Script (String fileName, String[] content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName () {
        return fileName;
    }

    public String[] getContent () {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        Script other = (Script) obj;
        return Objects.equals(getFileName(), other.getFileName())
        && Objects.equals(getContent(), other.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, content);
    }
}
