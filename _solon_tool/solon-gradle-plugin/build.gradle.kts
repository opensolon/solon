buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.8.0"
    id("com.vanniktech.maven.publish") version "0.24.0"
}

group = "org.noear"
version = "0.0.2"
description = "Solon Gradle Plugin"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("gradle-plugin"))
    implementation("org.ow2.asm:asm:9.4")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

gradlePlugin {
    plugins {
        create("SolonPlugin") {
            id = "org.noear.solon"
            displayName = "Solon"
            description = project.description
            implementationClass = "org.noear.solon.gradle.plugin.SolonPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.DEFAULT)
    // or when publishing to https://s01.oss.sonatype.org
    // publishToMavenCentral(SonatypeHost.S01)

    signAllPublications()

    coordinates(project.group.toString(), "solon-gradle-plugin", project.version.toString())

    pom {
        name.set("Solon Gradle Plugin")
        description.set(project.description)
        inceptionYear.set("2023")
        url.set("https://gitee.com/noear/solon")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("noear")
                name.set("noear")
                url.set("https://gitee.com/noear")
            }
        }
        scm {
            url.set("https://gitee.com/noear/solon")
            connection.set("scm:git:git@gitee.com:noear/solon.git")
            developerConnection.set("scm:git:ssh://git@gitee.com:noear/solon.git")
        }
    }
}
