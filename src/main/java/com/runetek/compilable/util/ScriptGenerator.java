package com.runetek.compilable.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptGenerator {

    public static void generate(Path outputDir, String mainClass) throws IOException {
        // Discover all subdirectories under src/ that contain .java files
        Path srcDir = outputDir.resolve("src");
        List<String> javacPaths = new ArrayList<>();
        javacPaths.add("src\\*.java");

        if (Files.exists(srcDir)) {
            Files.walk(srcDir)
                    .filter(Files::isDirectory)
                    .filter(d -> !d.equals(srcDir))
                    .forEach(d -> {
                        // Convert to relative path from output dir
                        String rel = outputDir.relativize(d).toString().replace('/', '\\');
                        javacPaths.add(rel + "\\*.java");
                    });
        }

        String javacArgs = String.join(" ", javacPaths);

        // #compile.bat
        String compile = "@echo off\r\n"
                + "echo Compiling RuneScape 508 client...\r\n"
                + "if not exist bin mkdir bin\r\n"
                + "javac -source 1.8 -target 1.8 -encoding UTF-8 -d bin " + javacArgs + "\r\n"
                + "if %ERRORLEVEL% EQU 0 (\r\n"
                + "    echo Compilation successful!\r\n"
                + ") else (\r\n"
                + "    echo Compilation failed. Check for errors above.\r\n"
                + ")\r\n"
                + "pause\r\n";
        Files.write(outputDir.resolve("#compile.bat"), compile.getBytes(StandardCharsets.UTF_8));

        // #run.bat
        String run = "@echo off\r\n"
                + "echo Launching RuneScape 508 client...\r\n"
                + "java -cp bin " + mainClass + "\r\n"
                + "pause\r\n";
        Files.write(outputDir.resolve("#run.bat"), run.getBytes(StandardCharsets.UTF_8));

        System.out.println("  Generated #compile.bat and #run.bat");
    }
}
