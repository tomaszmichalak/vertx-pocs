defaultTasks = listOf("build")

allprojects {
    repositories {
        jcenter()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}
