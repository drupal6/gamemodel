package com.gl;

import com.gl.script.IIDScript;
import com.gl.script.IInitScript;
import com.gl.script.IScript;
import com.gl.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ScriptPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptPool.class);

    //源文件夹 注意不能放到项目默认classpath中，不然不会调用重写的findClass方法
    private String sourceDir;

    //输出文件夹
    private String outDir;

    //附加jar包地址
    private String jarsDir;

    Map<String, Map<String, IScript>> scriptInstances = new ConcurrentHashMap<String, Map<String, IScript>>(0);
    Map<String, Map<String, IScript>> tmpScriptInstances = new ConcurrentHashMap<String, Map<String, IScript>>(0);

    Map<Integer, IIDScript> idScriptInstances = new ConcurrentHashMap<Integer, IIDScript>(0);
    Map<Integer, IIDScript> tmpIdScriptInstances = new ConcurrentHashMap<Integer, IIDScript>(0);

    public ScriptPool(){}

    public void setSource(String source, String out, String jarsDir) throws Exception {
        if(source == null || source.isEmpty()) {
            throw new Exception("source is null.");
        }
        this.sourceDir = source;
        this.outDir = out;
        this.jarsDir = jarsDir;
    }

    public <T extends IIDScript> T getIIDScript(Integer modelID) {
        if(idScriptInstances.containsKey(modelID)) {
            return (T)idScriptInstances.get(modelID);
        }
        return null;
    }

    public <E> Collection<E> getEvts(String name) {
        Map<String, IScript> scripts = scriptInstances.get(name);
        if(scripts != null) {
            return (Collection<E>)scripts.values();
        }
        return null;
    }

    public <E> Collection<E> getEvts(Class<E> clazz) {
        Map<String, IScript> scripts = scriptInstances.get(clazz.getName());
        if(scripts != null) {
            return (Collection<E>)scripts.values();
        }
        return null;
    }

    /**
     * 执行脚本
     * @param scriptClass
     * @param action
     * @param <T>
     */
    public <T extends IScript> void executeScripts(Class<T> scriptClass, Consumer<T> action) {
        Collection<IScript> evts = getEvts(scriptClass.getName());
        if(evts != null && false == evts.isEmpty() && action != null) {
            for(IScript script : evts) {
                try {
                    action.accept((T)script);
                } catch (Exception e) {
                    LOGGER.error("run script error. script:" + scriptClass.getName(), e);
                }
            }
        }
    }

    /**
     * 执行脚本 当执行结果为true， 中断执行，返回true， 否则统一返回false
     * @param scriptClass
     * @param codition
     * @param <T>
     * @return
     */
    public <T extends IScript> boolean executeScriptes(Class<? extends IScript> scriptClass, Predicate<T> codition) {
        Collection<IScript> evts = getEvts(scriptClass.getName());
        if(evts != null && false == evts.isEmpty() && codition != null) {
            for (IScript script : evts) {
                try {
                    if(codition.test((T)script)) {
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.error("run script error. script:" + scriptClass.getName(), e);
                }
            }
        }
        return false;
    }

    public <T extends IScript, R> R functionScripts(Class<? extends IScript> scriptClass, Function<T, R> function) {
        Collection<IScript> evts = getEvts(scriptClass.getName());
        if(evts != null && false == evts.isEmpty() && function != null) {
            for (IScript script : evts) {
                try {
                   R r = function.apply((T)script);
                   if(r != null) {
                        return r;
                   }
                } catch (Exception e) {
                    LOGGER.error("run script error. script:" + scriptClass.getName(), e);
                }
            }
        }
        return null;
    }

    String compile() {
        FileUtil.deleteDirectory(outDir);
        List<File> sourceFileList = new ArrayList<>();
        FileUtil.getFiles(sourceDir, sourceFileList, ".java", null);
        return this.compile(sourceFileList);
    }

    String compile(List<File> sourceFileList) {
        StringBuilder sb = new StringBuilder();
        if( null != sourceFileList) {
            DiagnosticCollector<JavaFileObject> objectDiagnosticCollector = new DiagnosticCollector<>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(objectDiagnosticCollector, null, Charset.forName("utf-8"));
            try{
                if(sourceFileList.isEmpty()) {
                    return sourceDir + " not found java file.";
                }
                new File(outDir).mkdir();
                Iterable<? extends  JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
                ArrayList<String> options = new ArrayList<>(0);
                options.add("-g");
                options.add("-source");
                options.add("1.8");
                options.add("-encoding");
                options.add("UTF-8");
                options.add("-sourcepath");
                options.add(sourceDir);
                options.add("-d");
                options.add(outDir);

                ArrayList<File> jarsList = new ArrayList<>();
                FileUtil.getFiles(jarsDir, jarsList, ".jar", null);
                String jarString = "";
                jarString = jarsList.stream().map((jar) -> jar.getPath() + File.pathSeparator).reduce(jarString, String::concat);
                if(null != jarString && false == jarString.isEmpty()) {
                    options.add("-classpath");
                    options.add("-jarString");
                }
                JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, objectDiagnosticCollector, options, null, compilationUnits);
                Boolean call = compilationTask.call();
                if(false == call) {
                    objectDiagnosticCollector.getDiagnostics().forEach( f -> {
                        sb.append(";").append(((JavaFileObject)(f.getSource())).getName()).append("line:").append(f.getLineNumber());
                        LOGGER.error("load script error:" + ((JavaFileObject)(f.getSource())).getName()+ "line:" + f.getLineNumber());;
                    });
                }
            } catch(Exception e) {
                sb.append(this.sourceDir).append("error:").append(e);
                LOGGER.error("", e);
            } finally {
                try{
                    fileManager.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        } else {
            LOGGER.warn(this.sourceDir + " not found java file.");
        }
        return sb.toString();
    }

    public String loadJava(Consumer<String> condition) {
        String compile = this.compile();
        StringBuilder sb = new StringBuilder();
        if(compile == null|| compile.isEmpty()) {
            List<File> sourceFileList = new ArrayList<>(0);
            FileUtil.getFiles(outDir, sourceFileList, ".class", null);
            String[] fileNames = new String[sourceFileList.size()];
            for(int i = 0; i < sourceFileList.size(); i++) {
                fileNames[i] = sourceFileList.get(i).getPath();
                sb.append(fileNames[i]).append(";");
            }
            tmpScriptInstances = new ConcurrentHashMap<>();
            tmpIdScriptInstances = new ConcurrentHashMap<>();
            loadClass(fileNames);
            if(tmpScriptInstances.size() > 0) {
                scriptInstances.clear();
                scriptInstances = tmpScriptInstances;
            }
            if(tmpIdScriptInstances.size() > 0) {
                idScriptInstances.clear();
                idScriptInstances = tmpIdScriptInstances;
            }
        } else {
            if(false == compile.isEmpty()) {
                if(condition != null) {
                    condition.accept(compile);
                }
            }
        }
        return sb.toString();
    }

    public String loadJava(String ... source) {
        FileUtil.deleteDirectory(outDir);
        List<File> sourceFileList = new ArrayList<>();
        FileUtil.getFiles(sourceDir, sourceFileList, ".java", fileAbsolutePath -> {
            if(source == null) {
                return true;
            }
            for(String str : source) {
                System.out.println(fileAbsolutePath + "<>" + str);
                if(fileAbsolutePath.contains(str) || str.equals("")) {
                    return true;
                }
            }
            return false;
        });
        String result = this.compile(sourceFileList);
        StringBuilder loadJava = new StringBuilder();
        if(result == null || result.isEmpty()) {
            sourceFileList.clear();
            FileUtil.getFiles(outDir, sourceFileList, ".class", fileAbsolutePath -> {
                if(source == null) {
                    return true;
                }
                for(String str : source) {
                    if(fileAbsolutePath.contains(str) || str.equals("")) {
                        return true;
                    }
                }
                return false;
            });
        }
        String[] fileNames = new String[sourceFileList.size()];
        for(int i = 0; i < sourceFileList.size(); i++) {
            fileNames[i] = sourceFileList.get(i).getPath();
            loadJava.append(fileNames[i]).append("/r/n");
        }
        tmpScriptInstances = new ConcurrentHashMap<>();
        tmpIdScriptInstances = new ConcurrentHashMap<>();
        loadClass(fileNames);
        if(tmpScriptInstances.size() > 0) {
            tmpScriptInstances.entrySet().stream().forEach(entry ->{
                String key = entry.getKey();
                Map<String, IScript> value = entry.getValue();
                scriptInstances.put(key, value);
            });
        }
        if(tmpIdScriptInstances.size() > 0) {
            tmpIdScriptInstances.entrySet().stream().forEach(entry ->{
                Integer id = entry.getKey();
                IIDScript iidScript = entry.getValue();
                idScriptInstances.put(id, iidScript);
            });
        }
        return loadJava.toString();
    }

    void loadClass(String ... names) {
        try{
            ScriptClassLoader loader = new ScriptClassLoader();
            for(String name : names) {
                String tmpName = name.replace(outDir, "").replace(".class", "").replace(File.separatorChar, '.');
                loader.loadClass(tmpName);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
        }
    }

    class ScriptClassLoader extends ClassLoader {
//        @Override
//        public Class<?> loadClass(String name) throws ClassNotFoundException {
//            Class<?> defineClass = null;
//            defineClass = super.loadClass(name);
//            return defineClass;
//        }

        @SuppressWarnings("unchecked")
        @Override
        protected  Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classData = getClassData(name);
            Class<?> defineClass = null;
            if(classData != null) {
                try {
                    defineClass = defineClass(name, classData, 0, classData.length);
                    String nameString = defineClass.getName();
                    if(false == Modifier.isAbstract(defineClass.getModifiers())
                        && false == Modifier.isPrivate(defineClass.getModifiers())
                            && false == Modifier.isStatic(defineClass.getModifiers())
                                && false == nameString.contains("$")) {
                        Object newInstance = defineClass.newInstance();
                        List<Class<?>> interfaces = new ArrayList<>();
                        if(IInitScript.class.isAssignableFrom(defineClass)
                            || IScript.class.isAssignableFrom(defineClass)) {
                            Class<?> cls = defineClass;
                            while(cls != null && false == cls.isInterface() && false == cls.isPrimitive()) {
                                interfaces.addAll(Arrays.asList(cls.getInterfaces()));
                                cls = cls.getSuperclass();
                            }
                            if(newInstance instanceof IInitScript) {
                                ((IInitScript) newInstance).init();
                            }
                        }
                        if(newInstance != null && false == interfaces.isEmpty()) {
                            for(Class<?> aInterFace : interfaces) {
                                if(IScript.class.isAssignableFrom(aInterFace)) {
                                    if(false == tmpScriptInstances.containsKey(aInterFace.getName())) {
                                        tmpScriptInstances.put(aInterFace.getName(), new ConcurrentHashMap<>());
                                    }
                                    tmpScriptInstances.get(aInterFace.getName()).put(defineClass.getName(), (IScript)newInstance);
                                }
                                if(IIDScript.class.isAssignableFrom(aInterFace)) {
                                    if(false == tmpIdScriptInstances.containsKey(aInterFace.getName())) {
                                        tmpIdScriptInstances.put(((IIDScript)newInstance).getModelID(), (IIDScript)newInstance);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("", e);
                }
            }
            return defineClass;
        }

        private byte[] getClassData(String className) {
            String path = classNameToPath(className);
            InputStream ins = null;
            try {
                File file = new File(path);
                if (file.exists()) {
                    ins = new FileInputStream(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];
                    int bytesNumRead = 0;
                    while ((bytesNumRead = ins.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesNumRead);
                    }
                    return baos.toByteArray();
                }
            } catch (IOException e) {
                LOGGER.error("", e);
            } finally {
                if(ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
            return null;
        }

        private String classNameToPath(String className) {
            File file = null;
            try {
                String path = outDir + className.replace(".", File.separator) + ".class";
                file = new File(path);
                if (false == file.exists()) {
                    LOGGER.warn("classnametopaht path{} not exists.", path);
                }
                return file.getPath();
            } catch (Exception e) {
                LOGGER.error(outDir, e);
            }
            return "";
        }
    }
}
