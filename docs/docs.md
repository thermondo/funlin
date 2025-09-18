# Funlin Compiler Plugin Documentation

```mermaid
graph LR
    A[Developer] --> B[Gradle Build Process]
    B --> C[Funlin Gradle Plugin]
    C --> D[Kotlin Compiler]
    D --> E[Funlin Compiler Plugin]
    E --> F[Code Transformation]
    F --> G[Compiled Output]
```

```mermaid
graph TD
    A[Kotlin Source Code] --> B[Lexical Analysis]
    B --> C[Parsing]
    C --> D[PSI Tree Generation]
    
    D --> E{Frontend Type}
    
    %% K1 Frontend Path
    E -->|K1 Frontend| F1[AST + Resolution]
    F1 --> G1[BindingContext]
    F1 --> H1[PSI]
    G1 --> I1[Psi2Ir]
    H1 --> I1
    
    %% K2 Frontend Path  
    E -->|K2 Frontend| F2[Raw FIR Builder]
    F2 --> G2[FIR Processing Stages]
    
    %% FIR Processing Details
    G2 --> G2A[IMPORTS Resolution]
    G2A --> G2B[SUPER_TYPES Resolution]
    G2B --> G2C[SEALED_CLASS_INHERITORS]
    G2C --> G2D[TYPES Resolution]
    G2D --> G2E[STATUS Resolution]
    G2E --> G2F[IMPLICIT_TYPES_BODY_RESOLVE]
    G2F --> G2G[BODY_RESOLVE]
    G2G --> G2H[CHECKERS]
    G2H --> H2[Resolved FIR]
    
    %% FIR to IR conversion
    H2 --> I2[FIR2IR]
    
    %% Backend starts here
    I1 --> J[IR Generation]
    I2 --> J
    
    %% Backend Plugin Extension Points
    J --> K[IrGenerationExtension]
    K --> L[IR Tree]
    L --> M[IR Transformation Phases]
    
    %% IR Processing Phases
    M --> M1[IR Validation]
    M1 --> M2[IR Lowering]
    M2 --> M3[Optimization Passes]
    M3 --> M4[Platform-Specific Lowering]
    M4 --> N[Lowered IR]
    
    %% Code Generation
    N --> O{Target Platform}
    
    O -->|JVM| P1[JVM Backend]
    O -->|JS| P2[JS Backend] 
    O -->|Native| P3[Native Backend]
    O -->|WASM| P4[WASM Backend]
    
    P1 --> Q1[.class files]
    P2 --> Q2[.js files]
    P3 --> Q3[Native binaries]
    P4 --> Q4[.wasm files]
    
    %% Plugin Integration Points
    K --> R1[Backend Plugin Extension Points]
    R1 --> R2[IrElementTransformer]
    R1 --> R3[IrElementVisitor]
    R1 --> R4[ClassLoweringPass]
    R1 --> R5[DeclarationTransformer]
    
    %% Styling
    classDef frontend fill:#e1f5fe
    classDef fir fill:#f3e5f5
    classDef backend fill:#fff3e0
    classDef plugin fill:#e8f5e8
    classDef target fill:#fce4ec
    
    class A,B,C,D frontend
    class F2,G2,G2A,G2B,G2C,G2D,G2E,G2F,G2G,G2H,H2,I2 fir
    class F1,G1,H1,I1 frontend
    class J,K,L,M,M1,M2,M3,M4,N backend
    class R1,R2,R3,R4,R5 plugin
    class O,P1,P2,P3,P4,Q1,Q2,Q3,Q4 target
```


### Operational Flow Diagram


```mermaid
sequenceDiagram
    participant Developer
    participant Gradle
    participant FunlinSubPlugin
    participant FunlinCommandLineProcessor
    participant FunlinComponentRegistrar
    participant FunlinGenerationExtension
    participant FunlinTransformer
    participant KotlinCompiler

    Developer->>Gradle: Set up Funlin in build.gradle.kts
    Gradle->>FunlinSubPlugin: Apply Funlin plugin
    FunlinSubPlugin->>FunlinPluginExtension: Create settings
    FunlinPluginExtension->>FunlinSubPlugin: Provide configuration
    FunlinSubPlugin->>KotlinCompiler: Send SubpluginOptions
    KotlinCompiler->>FunlinCommandLineProcessor: Process options
    FunlinCommandLineProcessor->>CompilerConfiguration: Update settings
    KotlinCompiler->>FunlinComponentRegistrar: Register Funlin
    FunlinComponentRegistrar->>FunlinGenerationExtension: Register extension
    KotlinCompiler->>FunlinGenerationExtension: Invoke generation
    FunlinGenerationExtension->>FunlinTransformer: Apply transformer
    FunlinTransformer->>IR Tree: Traverse and transform
    FunlinTransformer->>DebugLogger: Log operations (if enabled)
    FunlinTransformer->>IR Tree: Inject function calls
```


## Introduction

**Funlin** is a tool for Kotlin developers that automatically adds extra code to your functions. This helps you save time and avoid mistakes by doing repetitive tasks for you.

## What is Funlin?

Funlin is a **Kotlin compiler plugin**. It works with **Gradle**, a tool that builds your projects. Funlin looks for functions you mark with a special label (annotation) and adds a call to another function you choose.

## How Does Funlin Work?

Funlin works in easy steps. Here's how it does its job:

### Step 1: Set Up Funlin in Gradle

First, you add Funlin to your project's `build.gradle.kts` file. You can set options like turning Funlin on or off, enabling logging, choosing which label to look for, and selecting the function to call.

    funlin {
        enabled.set(true)
        logging.set(true)
        annotation.set("com.example.MyAnnotation")
        callableTargetPath.set("com.example.MyObject.myFunction")
    }

### Step 2: Gradle Reads the Settings

Gradle uses the **FunlinSubPlugin** to read the settings you made. This plugin knows how to send these settings to the Kotlin compiler.

### Step 3: Send Settings to the Kotlin Compiler

The Gradle plugin sends your settings to the Kotlin compiler using **SubpluginOption**. This tells the compiler what Funlin should do.

### Step 4: Compiler Processes the Settings

Inside the compiler, **FunlinCommandLineProcessor** takes your settings and prepares them. It makes sure Funlin is turned on and knows which label and target function to use.

### Step 5: Register the Code Modifier

**FunlinComponentRegistrar** tells the Kotlin compiler to use **FunlinGenerationExtension**. This extension is what changes your code.

### Step 6: Modify Your Code

**FunlinGenerationExtension** uses **FunlinTransformer** to go through your Kotlin code. It finds all functions with your chosen label and adds a call to your target function at the start of each annotated function.

## Components of Funlin

Here are the main parts of Funlin and what they do:

### 1. FunlinCommandLineProcessor

- **Where:** `de.thermondo.plugin`
- **What It Does:** Reads the settings you made in Gradle and updates the compiler's configuration.

### 2. FunlinComponentRegistrar

- **Where:** `de.thermondo.plugin`
- **What It Does:** Registers Funlin with the Kotlin compiler if it's turned on.

### 3. FunlinGenerationExtension

- **Where:** `de.thermondo.plugin`
- **What It Does:** Starts the process of changing your Kotlin code.

### 4. FunlinTransformer

- **Where:** `de.thermondo.transformations`
- **What It Does:** Finds functions with your chosen label and adds the extra function calls.

### 5. DebugLogger

- **Where:** `de.thermondo.util`
- **What It Does:** Logs messages to help you see what Funlin is doing, if logging is turned on.

### 6. FunlinPluginExtension & FunlinSubPlugin

- **Where:** `de.thermondo.gradle`
- **What They Do:** Let you set up Funlin in your Gradle build files.


## Potential Improvements

1. **Testing:**
    - Add tests to make sure Funlin works correctly.
    - Use testing tools for Kotlin and Gradle.
