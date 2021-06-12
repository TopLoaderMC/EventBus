import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    id("org.parchmentmc.writtenbooks") version "0.4.0"
    `java-library`
    eclipse
    jacoco
    `maven-publish`
    id("me.champeau.jmh") version "0.6.5"
    id("com.github.hierynomus.license") version "0.16.1"
}

the<org.parchmentmc.writtenbooks.WrittenBooksExtension>().apply {
    mainBranches.add("toploader")
    releaseRepository.set("https://maven.socketmods.dev/repository/toploader/");
    snapshotRepository.set("https://maven.socketmods.dev/repository/toploader/");
    repositoryUsername.set(providers.environmentVariable("TopLoaderUsername").forUseAtConfigurationTime())
    repositoryPassword.set(providers.environmentVariable("TopLoaderPassword").forUseAtConfigurationTime())
    githubRepo.set("TopLoaderMC/EventBus")
}

group = "org.github.toploader"

repositories {
    maven {
        url = uri("https://maven.socketmods.dev/repository/toploader/")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withJavadocJar()
    withSourcesJar()
}

jacoco {
    toolVersion = "0.8.2"
}

tasks.test {
    useJUnitPlatform()
    setForkEvery(1L)
}

val testJars : SourceSet by sourceSets.creating
val testJarsImplementation : Configuration by configurations.getting

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to "eventbus",
            "Specification-Vendor" to "Forge",
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "TopLoader",
            "Implementation-Timestamp" to Instant.now().iso8601()
        )
    }
}

repositories {
    mavenCentral()
    maven {
        name = "forge"
        url = uri("https://maven.minecraftforge.net")
    }
}

configurations.jmh {
    resolutionStrategy.force("net.sf.jopt-simple:jopt-simple:5.0.4")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.+")
    testImplementation("org.powermock:powermock-core:2.0.+")
    testImplementation("cpw.mods:modlauncher:6.1.+")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.+")
    testImplementation("org.apache.logging.log4j:log4j-core:2.11.+")
    testImplementation("com.lmax:disruptor:3.4.2")
    testImplementation(testJars.runtimeClasspath)
    testJarsImplementation(sourceSets.main.get().output)
    implementation("org.ow2.asm:asm:7.2")
    implementation("org.ow2.asm:asm-commons:7.2")
    implementation("org.ow2.asm:asm-tree:7.2")
    implementation("org.apache.logging.log4j:log4j-api:2.11.+")
    implementation("cpw.mods:modlauncher:6.1.+:api")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    api("net.jodah:typetools:0.8.+")
    jmhImplementation("cpw.mods:modlauncher:6.1.+")
    jmhImplementation("org.powermock:powermock-core:2.0.+")
    jmhImplementation("org.ow2.asm:asm:7.2")
    jmhImplementation("org.ow2.asm:asm-tree:7.2")
    jmhImplementation("org.ow2.asm:asm-commons:7.2")
    jmh("org.ow2.asm:asm:7.2")
}

jmh {
    jvmArgs.set(listOf("-Djmh.separateClasspathJAR=true"))
    includes.set(listOf("net.minecraftforge.eventbus.benchmarks.EventBusBenchmark"))
    benchmarkMode.set(listOf("avgt"))
    profilers.set(listOf("stack"))
    timeOnIteration.set("5s")
    warmup.set("5s")
    warmupIterations.set(3)
    iterations.set(3)
    fork.set(3)
    timeUnit.set("ns")
}

license {
    header = rootProject.file("LICENSE-Header")
    ext["year"] = 2016
    mapping("java", "SLASHSTAR_STYLE")

    include("**/*.java")
}

publishing {
    publications.withType(MavenPublication::class.java).all {
        pom {
            name.set("Event Bus")
            description.set("High performance Event Bus library")
            licenses {
                license {
                    name.set("LGPLv2.1")
                    url.set("https://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt")
                }
            }
            developers {
                developer {
                    id.set("cpw")
                    name.set("cpw")
                }
                developer {
                    id.set("LexManos")
                    name.set("LexManos")
                }
            }
        }
    }
}

fun Instant.iso8601() = DateTimeFormatter.ISO_INSTANT.format(this)