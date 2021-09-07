
job("Publish") {
    container(displayName = "Run publish script", image = "gradle:jdk11") {
        kotlinScript { api ->
            api.gradle("publish")
        }
    }
}