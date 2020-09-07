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
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.12.0")
    }
}

plugins {
    `java`
    signing
    kotlin("jvm") version DependencyVersions.KOTLIN_VERSION
    id("net.researchgate.release") version DependencyVersions.RELEASE_PLUGIN_VERSION
    id("com.dorongold.task-tree") version "1.5"
}

apply(plugin = "com.vanniktech.maven.publish")

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

tasks.register("setPublishVersion") {
    doLast {
        project.setProperty("VERSION_NAME", getVersion())
    }
}

tasks.register("buildAndPublish", GradleBuild::class.java) {
    tasks = listOf("test", "release", "setPublishVersion", "uploadArchives")
}

release {
    versionPropertyFile = "version.properties"
}

fun getVersion(): String {
    val props = Properties()
    props.load(file("version.properties").inputStream())
    return props.getProperty("version").replace("-SNAPSHOT", "")
}