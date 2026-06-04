package org.metooo.chunkdebugtool.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class IntermediaryDeobfuscator {
    private String tinyUrl;
    private StackTraceElement[] stackTrace;
    private ClassLoader classLoader = IntermediaryDeobfuscator.class.getClassLoader();

    private Map<String, String> classMappings, methodMappings, methodDescCache;

    private static final Map<String, Map<String, String>> classMappingsCache = new HashMap<>(),
            methodMappingsCache = new HashMap<>(),
            methodDescCaches = new HashMap<>();
    private static final Set<String> tinyUrlsLoaded = new HashSet<>();
    private static final Object TINY_SYNC_LOCK = new Object();
    private static final String DIRECTORY = "chunkdebugtool";
    private static final String TINY_FILE_NAME = DIRECTORY + "/map.tiny";

    private IntermediaryDeobfuscator() {
    }

    // BUILDER

    public static IntermediaryDeobfuscator create() {
        return new IntermediaryDeobfuscator();
    }

    public IntermediaryDeobfuscator withTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
        return this;
    }
    // withInfo("1.12.2", "feather-gen2", 1);
    public IntermediaryDeobfuscator withInfo(String minecraftVersion, String map, int build) {
        return withTinyUrl(String.format("https://maven.ornithemc.net/releases/net/ornithemc/%s/%s+build.%d/%s-%s+build.%d-tiny.gz",
                map, minecraftVersion, build, map, minecraftVersion, build));
    }

    public IntermediaryDeobfuscator withTinyFile(String tinyFilePath) {
        return withTinyUrl("file:" + tinyFilePath);
    }

    public IntermediaryDeobfuscator withStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    public IntermediaryDeobfuscator withCurrentStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final String thisClass = getClass().getName();
        boolean foundThisClass = false;
        int firstIndex;
        for (firstIndex = 0; firstIndex < stackTrace.length; firstIndex++) {
            if (stackTrace[firstIndex].getClassName().equals(thisClass))
                foundThisClass = true;
            else if (foundThisClass)
                break;
        }
        return withStackTrace(Arrays.copyOfRange(stackTrace, firstIndex, stackTrace.length));
    }

    public IntermediaryDeobfuscator withClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    // IMPLEMENTATION

    private void ensureTinyLoaded() {
        while (true) {
            synchronized (TINY_SYNC_LOCK) {
                if (tinyUrlsLoaded.contains(tinyUrl))
                    break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMappings() {
        boolean loadingTiny;
        synchronized (TINY_SYNC_LOCK) {
            loadingTiny = classMappingsCache.containsKey(tinyUrl);
            if (!loadingTiny) {
                classMappingsCache.put(tinyUrl, new HashMap<>());
                methodMappingsCache.put(tinyUrl, new HashMap<>());
                methodDescCaches.put(tinyUrl, new HashMap<>());
            }
            classMappings = classMappingsCache.get(tinyUrl);
            methodMappings = methodMappingsCache.get(tinyUrl);
            methodDescCache = methodDescCaches.get(tinyUrl);
        }

        if (!loadingTiny) {
            Thread t = new Thread(() -> {
                URL url;
                InputStream in = null;
                File directory = new File(DIRECTORY);
                File tinyFile = new File(TINY_FILE_NAME);

                if(!directory.exists() || !tinyFile.exists()){
                    directory.mkdir();
                    try {
                        url = new URL(tinyUrl);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        in = url.openConnection().getInputStream();
                        Files.copy(in, Paths.get(TINY_FILE_NAME));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                try {
                    in = new FileInputStream(tinyFile);
                    GZIPInputStream gzIn = new GZIPInputStream(in);
                    loadTiny(new BufferedReader(new InputStreamReader(gzIn)));
                    gzIn.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Tiny file not found: " + TINY_FILE_NAME);
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Unable to load tiny mappings");
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    private void loadTiny(BufferedReader in) {
        try {
            String line;
            String headerLine = in.readLine();
            if (headerLine == null || !headerLine.startsWith("v1\t")) {
                System.err.println("Invalid tiny file format");
                return;
            }

            while ((line = in.readLine()) != null) {
                String[] tokens = line.split("\t");

                String type = tokens[0];
                if ("CLASS".equals(type)) {
                    String intermediaryName = tokens[2];
                    String namedName = tokens[3];
                    classMappings.put(intermediaryName.replace('/', '.'), namedName.replace('/', '.'));
                } else if ("METHOD".equals(type)) {
                    String intermediaryMethod = tokens[4];
                    String namedMethod = tokens[5];

                    methodMappings.put(intermediaryMethod, namedMethod);
                }
            }

            //for(String key : classMappings.keySet())
            //    LogManager.getLogger().info("Class: {} -> {}", key, classMappings.get(key));

            synchronized (TINY_SYNC_LOCK) {
                tinyUrlsLoaded.add(tinyUrl);
            }
        } catch (IOException e) {
            System.err.println("Unable to load tiny mappings");
            e.printStackTrace();
        }
    }

    public void printDeobf() {
        printDeobf(System.err);
    }

    public void printDeobf(PrintStream out) {
        out.println(deobfAsString());
    }

    public String deobfAsString() {
        StackTraceElement[] elems = deobfuscate();
        return Arrays.stream(elems).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    public StackTraceElement[] deobfuscate() {
        if (tinyUrl == null) {
            throw new IllegalStateException("No tiny file URL has been set");
        }
        if (stackTrace == null) {
            throw new IllegalStateException("No stack trace has been set");
        }

        loadMappings();

        StackTraceElement[] deobfStackTrace = new StackTraceElement[stackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            deobfStackTrace[i] = deobfuscate(stackTrace[i]);
        }
        return deobfStackTrace;
    }

    private StackTraceElement deobfuscate(StackTraceElement elem) {
        String className = elem.getClassName();

        ensureTinyLoaded();
        if (!classMappings.containsKey(className))
            return elem;

        String methodName = elem.getMethodName();

        if (methodMappings.containsKey(methodName)) {
            methodName = methodMappings.get(methodName);
        }

        className = classMappings.get(className);

        return new StackTraceElement(className, methodName, elem.getFileName(), elem.getLineNumber());
    }
}

