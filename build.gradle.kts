import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.gradleup.shadow") version "9.6.1" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    val gitCommitCount = providers.exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText.get().trim()

    val tbVersion = "1.1.$gitCommitCount"
    val lombokVersion = "1.18.48"

    extra["tbVersion"] = tbVersion
    extra["snakeYamlVersion"] = "2.7"
    extra["websocketVersion"] = "1.6.0"
    extra["husbyVersion"] = "9298199ab9"
    extra["lombokVersion"] = lombokVersion
    extra["guavaVersion"] = "33.7.1-jre"
    extra["gsonVersion"] = "2.14.0"
    extra["commonsVersion"] = "2.22.0"
    extra["jlineVersion"] = "4.4.5"
    extra["logbackVersion"] = "1.6.3"
    extra["jdaVersion"] = "6.6.0"
    extra["hikariVersion"] = "7.1.0"
    extra["apacheHttpVersion"] = "4.5.14"
    extra["jspecifyVersion"] = "1.0.1"

    group = "net.buildtheearth.terrabungee"
    version = tbVersion

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()

        maven {
            name = "JitPack"
            url = uri("https://jitpack.io/")
        }

        maven {
            name = "papermc-repo"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }

        maven {
            name = "codemc-snapshots"
            url = uri("https://repo.codemc.io/repository/maven-snapshots")
        }
    }

    dependencies {
        if (!project.name.equals("common", ignoreCase = true)) {
            add("implementation", project(":common"))
        }

        add("compileOnly", "org.projectlombok:lombok:$lombokVersion")
        add("testCompileOnly", "org.projectlombok:lombok:$lombokVersion")
        add("annotationProcessor", "org.projectlombok:lombok:$lombokVersion")
        add("testAnnotationProcessor", "org.projectlombok:lombok:$lombokVersion")
    }

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = "net.buildtheearth.terrabungee"
                artifactId = project.name
                version = project.version.toString()

                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "buildtheearth"
                url = uri("https://maven.buildtheearth.net/private")

                credentials {
                    username = System.getenv("JB_SPACE_CLIENT_ID")
                    password = System.getenv("JB_SPACE_CLIENT_SECRET")
                }
            }
        }
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveFileName.set(
            "TerraBungee-${project.name}-$tbVersion.jar"
        )

        if (project.name == "proxy-velocity") {
            dependencies {
                exclude(dependency("org.slf4j:.*"))
            }
        }

        relocate(
            "com.example",
            "net.buildtheearth.com.example"
        )
    }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.named<Jar>("jar") {
        finalizedBy(tasks.named("shadowJar"))
    }
}