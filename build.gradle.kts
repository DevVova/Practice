plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.8.0" // Применяем плагин сериализации для OkHttp.
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    //Библиотека для работы с сетью - OkHttp.
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    //Для работы с корутинами
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    // Для работы с Android используйте:
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}