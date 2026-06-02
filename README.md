[brigadier-github]: https://github.com/Mojang/brigadier/

# Commodore

Commodore is a Command library that makes creating Command trees
extremely simple by bringing Kotlin DSL to [Mojang's Brigadier][brigadier-github].

## Dependency

In your repository block, add:

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}
```

Add the dependency:
```kotlin
dependencies {
    implementation("com.github.Stivais:Commodore:{version}")
}
```

Due to how Kotlin compiles anonymous functions, you will need to add:
```kotlin
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xlambdas=class")
    }
}
```

## Usage

### Creating a command:
Creating the nodes:
```kotlin
// Example command
val `your command` = commodore("example") {
    // Adds a node named "foo"
    // Input required to access this would be "example foo"
    literal("foo")
    
    literal("hello") {
        // Nodes can also be nested
        // Input required to access this would be "waypoint hello world"
        literal("world")
    }
}
```
However, this command doesn't do anything.
For a command to run, you will need to add an exit point, in this case a function.

Using Commodore, you can add functions with your own parameters as long they have a valid parser.

Adding functionality:
```kotlin
// Adding functions to the command.
val `your command` = commodore("foo") {
    // Added a function with the parameters: name, x, y, z
    // It will only run if the input matches all the nodes and parameters
    // Example input: foo abc 1 2.0 5
    literal("foo").runs { name: String, x: Double, y: Double, z: Double ->
        println("$name $x, $y, $z")
    }

    // Nullable types are optional, meaning input isn't required.
    literal("bar") {
        runs { name: String? ->
            if (name == null) {
                println("no input")
            } else {
                println("input: $name")
            }
        }
    }
}
```