val husbyVersion = extra["husbyVersion"].toString()
val gsonVersion = extra["gsonVersion"].toString()
val guavaVersion = extra["guavaVersion"].toString()
val websocketVersion = extra["websocketVersion"].toString()
val snakeYamlVersion = extra["snakeYamlVersion"].toString()
val apacheHttpVersion = extra["apacheHttpVersion"].toString()
val jdaVersion = extra["jdaVersion"].toString()
val hikariVersion = extra["hikariVersion"].toString()
val jlineVersion = extra["jlineVersion"].toString()
val logbackVersion = extra["logbackVersion"].toString()
val commonsVersion = extra["commonsVersion"].toString()
dependencies {
    implementation(project(":api"))
    implementation("com.github.BuildTheEarth:HusbyLib:$husbyVersion")
    implementation("org.yaml:snakeyaml:$snakeYamlVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("commons-io:commons-io:$commonsVersion")
    implementation("org.jline:jline:$jlineVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.dv8tion:JDA:$jdaVersion") {
        exclude(module = "opus-java")
    }
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.java-websocket:Java-WebSocket:$websocketVersion")
	testImplementation(platform("org.junit:junit-bom:6.1.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

repositories {
    mavenCentral() // for transitive dependencies
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to "net.buildtheearth.terrabungee.controller.TerraBungeeLauncher"
        )
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
