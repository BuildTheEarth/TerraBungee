
job("Publish") {
    container(displayName = "Run publish script", image = "gradle") {
        kotlinScript { api ->
            api.gradle("publish")
        }
    }
}