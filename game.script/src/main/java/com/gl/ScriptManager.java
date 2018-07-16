package com.gl;

import com.gl.ScriptPool;

import java.io.File;
import java.util.function.Consumer;

public class ScriptManager {

    private static final ScriptManager instance = new ScriptManager();

    private static final ScriptPool scriptPool;

    static {
        scriptPool = new ScriptPool();
        try {
            String property = System.getProperty("user.dir") + File.separator +"game.script";
            String path = property + File.separator +"script"+ File.separator;
            String outPath = property + File.separator + "target" + File.separator + "scriptsbin" + File.separator;
            String jarPath = property + File.separator + "target" + File.separator;
            scriptPool.setSource(path, outPath, jarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ScriptManager getInstance() {
        return instance;
    }

    public static ScriptPool getBaseScriptEntry() {
        return scriptPool;
    }

    /**
     * 初始化脚本
     * @param result
     * @return
     */
    public String init(Consumer<String> result) {
        return scriptPool.loadJava(result);
    }

    /**
     * 加载指定实例，科颜氏文件，可以是目录
     * @param sources
     * @return
     */
    public String loadJava(String ... sources) {
        return scriptPool.loadJava(sources);
    }
}
