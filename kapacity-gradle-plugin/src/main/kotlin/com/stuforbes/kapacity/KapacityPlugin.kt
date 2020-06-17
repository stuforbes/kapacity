/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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