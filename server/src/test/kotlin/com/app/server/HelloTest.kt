package com.app.server

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HelloTest : IntegrationTestContainer() {
    @DisplayName("Hello World1")
    @Test
    fun helloWorld() {
        println("Hello World with TestContainer1")
    }
}
