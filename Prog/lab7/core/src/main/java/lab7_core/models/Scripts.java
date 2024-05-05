package lab7_core.models;

import java.io.Serializable;
import java.util.HashSet;

public class Scripts implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashSet<Script> scripts = new HashSet<Script>();
    private String primaryScript;

    public Scripts(Script... scripts) {
        for (Script script : scripts) {
            this.scripts.add(script);
        }
    }

    public void addScript (Script script) {
        scripts.add(script);
    }

    public Script findScript (String fileName) {
        for (Script script : scripts) {
            if (script.getFileName().equals(fileName)) {
                return script;
            }
        }
        return null;
    }

    public boolean containsScript (String fileName) {
        for (Script script : scripts) {
            if (script.getFileName().equals(fileName)) return true;
        }
        return false;
    }

    public Script getPrimaryScript() {
        return findScript(primaryScript);
    }

    public void setPrimaryScript (String primaryScriptName) {
        this.primaryScript = primaryScriptName;
    }

    public HashSet<Script> getScripts() {
        return scripts;
    }

    public void merge (Scripts other) {
        for (Script script : other.getScripts()) {
            scripts.add(script);
        }
    }
}
