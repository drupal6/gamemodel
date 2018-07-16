package script;

import com.gl.ScriptManager;
import com.gl.script.impl.ITestScirpt;

public class TestScript {

    public static void main(String[] args) throws InterruptedException {
        ScriptManager.getInstance().init(str -> System.exit(0));
        for(;;) {
            ScriptManager.getInstance().loadJava("com\\gl\\script\\impl\\TestScript");
            ScriptManager.getBaseScriptEntry().executeScripts(ITestScirpt.class, script -> script.sysout());
            Thread.sleep(5000);
        }
    }
}
