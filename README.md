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

## Usage

By using Kotlin DSL, you're able to create a command tree
by essentially _visualizing_ it.

### Creating a command:
Creating the nodes:
```kotlin
// Example command that handles waypoints
val `your command` = commodore("waypoint") {
    // Adds a node named "add"
    // Input required to access this would be "waypoint add"
    literal("add")

    // Adds a node named "add"
    literal("remove")
    
    literal("hello") {
        // Nodes can also be nested
        // Input required to access this would be "waypoint hello world"
        literal("world")
    }
}
```
However, this command doesn't do anything.
You will need to add functionality which is extremely easy.

Using Commodore, you can add "functions" with even your own parameters
that automatically parse, so that all you have to worry about is the type.

Adding functionality:
```kotlin
// Adding functions to the command.
val `your command` = commodore("waypoint") {
    // Added a function with the parameters name, x, y, z
    // It will only run if the input matches all the nodes and parameters
    // Input required: waypoint add 1 2.0 1223.2
    literal("add").runs { name: String, x: Double, y: Double, z: Double ->
        println("Adding waypoint $name at $x, $y, $z")
    }

    // Added a function with an optional parameter (defined by it being nullable)
    // Optional parameters aren't required to fulfill the input required
    literal("remove") {
        runs { name: String? ->
            if (name == null) {
                println("Removing last waypoint")
            } else {
                println("Removing waypoint named $name")
            }
        }
    }
}
```

## Legacy Minecraft Usage

To use Commodore on MC versions that don't use brigadier.
Change the branch onto the corresponding version