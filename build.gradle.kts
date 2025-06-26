plugins {
    id("java")
    id("war")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "1.9.10" // Updated Kotlin version
}

application {
    mainClass.set("io.kyros.Server")
}

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

sourceSets {
    main {
        java.srcDirs("src")
        kotlin.srcDirs("srcKotlin")
        resources.srcDirs("resources")
    }
}

dependencies {
    // Kotlin Standard Library and Coroutines
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4") // Latest version

    // Networking and REST
    implementation("net.java.dev.jna:jna:5.8.0")
    implementation("com.mashape.unirest:unirest-java:1.4.9")
    implementation("org.apache.httpcomponents:httpclient:4.5.14") // Updated HttpClient version
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2") // Updated Jackson annotations version
    implementation("org.json:json:20231013") // Updated JSON version
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    // FTP support (Apache Commons Net)
    implementation("commons-net:commons-net:3.8.0") // Added back FTP support for Apache Commons Net

    // Database and ORM
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4") // Updated MariaDB version
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mchange:c3p0:0.9.5.5")
    implementation("com.mchange:mchange-commons-java:0.3.0")
    implementation("org.flywaydb:flyway-core:7.11.0") // Flyway for DB migrations

    // Logging
    implementation("org.slf4j:slf4j-api:1.7.36") // Updated SLF4J version
    implementation("ch.qos.logback:logback-classic:1.4.14") // Logback as the main logger
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.slf4j:log4j-over-slf4j:2.0.7") // SLF4J binding for Log4j
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

    // Game-specific dependencies
    implementation("org.luaj:luaj-jse:3.0.1")
    implementation("org.reflections:reflections:0.10.2") // Updated Reflections version
    implementation("de.svenkubiak:jBCrypt:0.4.1")
    implementation("com.github.cage:cage:1.0")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("com.displee:rs-cache-library:6.9")

    // Email
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("javax.activation:activation:1.1.1")

    // Commons libraries
    implementation("commons-io:commons-io:2.12.0") // Updated Commons IO version
    implementation("org.apache.commons:commons-lang3:3.13.0") // Updated Commons Lang version
    implementation("org.apache.derby:derby:10.15.2.0")
    implementation("org.apache.derby:derbyshared:10.15.2.0")
    implementation("org.apache.derby:derbytools:10.15.2.0")

    // Netty for network handling
    implementation("io.netty:netty-codec-haproxy:4.1.93.Final") // Latest Netty version
    implementation("io.netty:netty-all:4.1.93.Final")

    // API clients (Google, PayPal, Twilio)
    implementation("com.paypal.sdk:rest-api-sdk:1.14.0")
    implementation("com.google.api-client:google-api-client:1.32.2") // Updated Google API Client
    implementation("com.google.oauth-client:google-oauth-client:1.34.1")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0") // Updated YouTube API
    implementation("com.google.http-client:google-http-client-jackson2:1.41.0") // Updated Google HTTP Client
    implementation("com.google.guava:guava:32.0.1-jre") // Updated Guava
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.9") // Updated libphonenumber
    implementation("com.twilio.sdk:twilio:8.34.0") // Updated Twilio SDK version

    // Discord API integration
    implementation("net.dv8tion:JDA:4.2.1_253")

    // Miscellaneous
    implementation("org.tukaani:xz:1.9")
    implementation("org.jsoup:jsoup:1.16.1") // Updated JSoup version

    // Testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0") // Latest JUnit 5 API
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0") // Latest JUnit 5 Engine

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("mysql:mysql-connector-java:8.0.33")

}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_19.toString()
    targetCompatibility = JavaVersion.VERSION_19.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "19"
    }
}
