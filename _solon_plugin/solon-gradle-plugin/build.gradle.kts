buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.8.0"
}

group = "org.noear"
version = "0.0.1"
description = "Solon Gradle 插件"

repositories {
    mavenCentral()
    maven { setUrl("https://maven.aliyun.com/repository/public") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("SolonPlugin") {
            id = "org.noear.solon"
            displayName = "Solon插件"
            description = project.description
            implementationClass = "org.noear.solon.gradle.plugin.SolonPlugin"
        }
    }
}


apply(from = "maven-publish.gradle")
