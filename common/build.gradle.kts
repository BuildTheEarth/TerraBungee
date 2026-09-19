val husbyVersion = extra["husbyVersion"].toString()
val gsonVersion = extra["gsonVersion"].toString()
val guavaVersion = extra["guavaVersion"].toString()
val apacheHttpVersion = extra["apacheHttpVersion"].toString()
dependencies {
    compileOnly("com.github.BuildTheEarth:HusbyLib:$husbyVersion")
    implementation("org.apache.httpcomponents:fluent-hc:$apacheHttpVersion")
    implementation("org.apache.httpcomponents:httpclient:$apacheHttpVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.3")
    testImplementation("com.google.code.gson:gson:$gsonVersion")
    testImplementation("com.google.guava:guava:$guavaVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.3")

    compileOnly("com.google.code.gson:gson:$gsonVersion")
    compileOnly("com.google.guava:guava:$guavaVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    maxHeapSize = "1G"

    exclude("**/*")
}