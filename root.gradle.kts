plugins {
    kotlin("jvm") apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    // mcVersion doesn't matter here
    val modern = createNode("modern", 10809, "mcp")
    val legacy = createNode("legacy", 10809, "mcp")
    modern.link(legacy)
}