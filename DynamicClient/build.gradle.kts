plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

sourceSets {
    main {
        java {
            srcDir("src/main/java")
            srcDir("gen")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.zeroc:ice:3.7.9")
    implementation(files("lib/bzip2.jar"))
    implementation("org.javatuples:javatuples:1.2")
}

tasks.test {
    useJUnitPlatform()
}

task("classPath") {
    val f = file("build/classpath.txt")
    f.writeText(sourceSets.main.get().runtimeClasspath.asPath)
}