import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":client"))
    implementation(project(":controller"))
    compileOnly("com.velocitypowered:velocity-api:4.2.0")
    annotationProcessor("com.velocitypowered:velocity-api:4.2.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
	testImplementation(platform("org.junit:junit-bom:6.1.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.google.code.gson:gson:2.14.0")
    testImplementation("com.velocitypowered:velocity-api:4.2.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<ShadowJar>("shadowJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    filesMatching("META-INF/*.kotlin_module") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}