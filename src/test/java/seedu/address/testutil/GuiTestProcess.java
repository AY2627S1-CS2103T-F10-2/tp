package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Runs GUI assertions in a child JVM with a virtual display on headless Linux. */
public class GuiTestProcess {
    /** Returns whether the current JVM needs a separate GUI test process. */
    public static boolean isRequired() {
        return isHeadlessLinux() || Boolean.getBoolean("courseclass.ui.fork");
    }

    private static boolean isHeadlessLinux() {
        String display = System.getenv("DISPLAY");
        return System.getProperty("os.name").startsWith("Linux") && (display == null || display.isBlank());
    }

    /** Runs one test with its temporary directory and preserves the parent's coverage agent. */
    public static void run(Class<?> testClass, String method, Path temporaryFolder) throws Exception {
        List<String> command = new ArrayList<>();
        if (isHeadlessLinux()) {
            command.add("xvfb-run");
            command.add("-a");
        }
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        // Inherit JaCoCo's agent so these assertions contribute to the same coverage report.
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (argument.startsWith("-javaagent:") && argument.contains("destfile=")) {
                int start = argument.indexOf("destfile=") + "destfile=".length();
                int end = argument.indexOf(',', start);
                if (end == -1) {
                    end = argument.length();
                }
                String destination = argument.substring(start, end);
                argument = argument.substring(0, start) + Path.of(destination).toAbsolutePath()
                        + argument.substring(end);
            }
            command.add(argument);
        }
        command.add("-Dcourseclass.ui.fork=false");
        command.add("-Dcourseclass.ui.child=true");
        command.add("-cp");
        List<String> classPath = new ArrayList<>();
        if (testClass.getClassLoader() instanceof URLClassLoader loader) {
            for (URL url : loader.getURLs()) {
                classPath.add(Path.of(url.toURI()).toString());
            }
        } else {
            for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
                classPath.add(Path.of(entry).toAbsolutePath().toString());
            }
        }
        command.add(String.join(File.pathSeparator, classPath));
        command.add(testClass.getName());
        command.add(method);
        command.add(temporaryFolder.toString());
        Path output = temporaryFolder.resolve("gui-test.log");
        Process process = new ProcessBuilder(command).directory(temporaryFolder.toFile())
                .redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(45, TimeUnit.SECONDS), "GUI test process timed out");
            assertEquals(0, process.exitValue(), Files.readString(output));
        } finally {
            if (process.isAlive()) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
        }
    }
}
