# RuneTek Compilable - Greenfield Deobfuscator

## OBJECTIVE
Build an automated Java Deobfuscator + Decompiler framework that transforms a raw ZKM-obfuscated RuneScape 508 client into 100% standalone, recompilable .java source code.

## ENVIRONMENT
- **Tooling JDK**: Java 21 (what's available on this machine at `java`)
- **Target Runtime JDK**: Java 8 (available at `/home/aeroverra/jdk11/bin/java` for JDK 11, closest we have)
- **Decompiler**: Vineflower (latest, add as Maven dependency)
- **Bytecode manipulation**: ASM 9.7+ (Maven dependency)
- **Build**: Maven

## INPUT
- Download 508 SD client from: https://archive.openrs2.org/clients/30983.dat
- Save to `/input/508sd.dat`
- This is a standard JAR file (not pack200 compressed)

## OUTPUT STRUCTURE
```
/output/508/
  /src/          <- All decompiled .java files
  #compile.bat   <- Windows batch: compile with javac
  #run.bat       <- Windows batch: run with java
```

## THE PIPELINE

### Phase 1: ASM Deobfuscation (before decompiling)
Using org.ow2.asm, manipulate bytecode:
1. **Rename Collisions**: Rename single-letter classes (a → Class_a, b → Class_b etc.) to avoid Java keyword/shadowing conflicts
2. **Control Flow Untangling**: Detect and remove ZKM opaque predicates (dummy variables + fake if-statements that produce illegal GOTOs in decompiled code)
3. **Fix Method Name Collisions**: Ensure no duplicate method names with same descriptor in any class

### Phase 2: Decompilation
- Pass cleaned bytecode to Vineflower API
- Output to /output/508/src/
- Vineflower should produce cleaner output than CFR on ZKM-obfuscated code

### Phase 3: Script Generation
- Generate `#compile.bat` and `#run.bat` for Windows users
- Find the main Applet class for the run script

### Phase 4: Compile-Test Loop (CRITICAL)
After decompilation, run `javac` on the output and read errors.
Iterate on ASM transformations until the code compiles with ZERO errors.
This is the hardest part — you'll need to handle:
- Unreachable code from opaque predicates
- GOTO label artifacts
- Type inference issues
- Duplicate variable names
- etc.

## VERSION CONTROL
- Git repo already initialized in this directory
- When done, push to GitHub: https://github.com/Aero-VI/runetekcompilable.git
- Use token from git credential store (~/.git-credentials)
- Create a tagged release with the tool JAR attached

## GITHUB AUTH
Use HTTPS with token: `https://github.com/Aero-VI/runetekcompilable.git`

## IMPORTANT NOTES
- The final /output/508/src/ must contain ONLY .java files + batch scripts
- The .java files must compile standalone (no dependency on original JAR)
- Use heuristic pattern matching, not hardcoded class names
- This is a NEW project — don't reference any previous deobfuscator code
