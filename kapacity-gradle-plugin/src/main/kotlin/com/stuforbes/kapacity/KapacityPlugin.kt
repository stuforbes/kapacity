package com.stuforbes.kapacity

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

class KapacityPlugin : Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(project: Project) {
        val extension = project.extensions.create("kapacity", KapacityPluginExtension::class.java)
        project.task("kapacity") {
            //            it.doLast {task ->
            val projectFiles = project
                .configurations
                .getByName("compileClasspath")
                .fileCollection()
//                    .map { file -> file.toURI().toURL() }

            val sourceSetContainer = project.extensions.getByType(SourceSetContainer::class.java)
            val sourceSet = sourceSetContainer.find { ssc -> ssc.name == "main" }
            val buildDirectories = sourceSet
                ?.runtimeClasspath!!
//                    ?.files
//                    ?.toList()
//                    ?.map { file -> file.toURI().toURL() }
//                    ?: emptyList()

            val allCompileSources = buildDirectories.plus(projectFiles)

            project.afterEvaluate {
                runKapacityTest(project, extension, allCompileSources)
            }
        }
//        }
    }

    private fun runKapacityTest(project: Project, extension: KapacityPluginExtension, cp: FileCollection) {
        val kapacityTestTask = project.tasks.create("kapacity", JavaExec::class.java) {
            it.apply {
                group = JavaBasePlugin.VERIFICATION_GROUP
                description = "Runs Kapacity tests"
                main = "com.stuforbes.kapacity.console.ConsoleRunnerKt"
                classpath = cp
            }
            extension.applyTo(it)
        }

        val testTask = project.tasks.getByName("kapacity")
        testTask.dependsOn(kapacityTestTask)
    }
}