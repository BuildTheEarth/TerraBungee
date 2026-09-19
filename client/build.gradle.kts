repositories {
    mavenCentral()
}

val husbyVersion = extra["husbyVersion"].toString()
val gsonVersion = extra["gsonVersion"].toString()
val guavaVersion = extra["guavaVersion"].toString()
val websocketVersion = extra["websocketVersion"].toString()
val jspecifyVersion = extra["jspecifyVersion"].toString()
dependencies {
    compileOnly("com.github.BuildTheEarth:HusbyLib:$husbyVersion")
    implementation("org.java-websocket:Java-WebSocket:$websocketVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("org.jspecify:jspecify:$jspecifyVersion")
	testImplementation(platform("org.junit:junit-bom:6.1.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}