import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "ru.foxstudios"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation("commons-io:commons-io:2.15.1")
    // https://mvnrepository.com/artifact/commons-codec/commons-codec
    implementation("commons-codec:commons-codec:1.16.1")
    // https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
    implementation("com.rabbitmq:amqp-client:5.20.0")
    // https://mvnrepository.com/artifact/io.projectreactor.netty/reactor-netty
    implementation("io.projectreactor.netty:reactor-netty:1.1.16")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    implementation("org.apache.logging.log4j:log4j-api:2.23.0")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    implementation("org.apache.logging.log4j:log4j-core:2.23.0")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j2-impl
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
}
application {
    mainClass.set("ru.foxstudios.earthbridge.MainKt")
}
