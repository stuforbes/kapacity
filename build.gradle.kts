import DependencyVersions.ASSERTJ_VERSION
import DependencyVersions.COMMONS_CLI_VERSION
import DependencyVersions.JACKSON_VERSION
import DependencyVersions.JUNIT_VERSION
import DependencyVersions.KAFKA_CLIENT_VERSION
import DependencyVersions.KOHTTP_VERSION
import DependencyVersions.KONFIG_VERSION
import DependencyVersions.KOTLIN_VERSION
import DependencyVersions.LOGBACK_VERSION
import DependencyVersions.MOCKK_VERSION
import DependencyVersions.SLF4J_VERSION

import java.util.Properties

val ossrhUsername: String by project
val ossrhPassword: String by project

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version DependencyVersions.KOTLIN_VERSION
    id("net.researchgate.release") version DependencyVersions.RELEASE_PLUGIN_VERSION
    id("com.dorongold.task-tree") version "1.5"
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

group = "com.stuforbes.kapacity"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$KOTLIN_VERSION")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$KOTLIN_VERSION")

    implementation("io.github.rybalkinsd:kohttp:$KOHTTP_VERSION")
    implementation("com.natpryce:konfig:$KONFIG_VERSION")
    implementation("commons-cli:commons-cli:$COMMONS_CLI_VERSION")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$JACKSON_VERSION")
    implementation("org.apache.kafka:kafka-clients:$KAFKA_CLIENT_VERSION")

    implementation("org.slf4j:slf4j-api:$SLF4J_VERSION")
    implementation("ch.qos.logback:logback-classic:$LOGBACK_VERSION")
    implementation("ch.qos.logback:logback-core:$LOGBACK_VERSION")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUNIT_VERSION")
    testImplementation("io.mockk:mockk:$MOCKK_VERSION")
    testImplementation("org.assertj:assertj-core:$ASSERTJ_VERSION")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUNIT_VERSION")
}

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.register("setPublishVersion") {
    doLast {
        (publishing.publications["mavenJava"] as MavenPublication).version = getVersion()
    }
}

tasks.register("doRelease") {
    dependsOn("release", "setPublishVersion")
}

tasks.register("doPublish") {
    mustRunAfter("doRelease")
    dependsOn("publish")
}

tasks.register("buildAndPublish", GradleBuild::class.java) {
    tasks = listOf("test", "doRelease", "doPublish")
}

release {
    versionPropertyFile = "version.properties"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "kapacity-core"
            version = version
            from(components["java"])

            pom {
                packaging = "jar"
                name.set("Kapacity Core")
                url.set("https://github.com/stuforbes/kapacity-core")

                organization {
                    name.set("com.stuforbes")
                    url.set("https://github.com/stuforbes")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/stuforbes/kapacity-core/issues")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/stuforbes/kapacity-core/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://github.com/stuforbes/kapacity-core")
                    connection.set("scm:git:git://github.com/stuforbes/kapacity-core.git")
                    developerConnection.set("scm:git:ssh://git@github.com:stuforbes/kapacity-core.git")
                }
                developers {
                    developer {
                        name.set("Stu Forbes")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

fun getVersion(): String {
    val props = Properties()
    props.load(file("version.properties").inputStream())
    return props.getProperty("version").replace("-SNAPSHOT", "")
}

gradle.taskGraph.whenReady {
    val hasRootReleaseTask = hasTask(":release")

    if(hasRootReleaseTask) {
        allTasks.forEach { task ->
            val subReleaseTask = (task.path.endsWith(":release"))
            if(subReleaseTask){
                task.enabled = false
            }
        }
    }
}