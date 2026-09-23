plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // JPA API
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
// Implementación de JPA (Hibernate)
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
// Implementacion de PostgreSQL
    implementation("org.postgresql:postgresql:42.7.13")
// Logger (opcional pero recomendable)
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

tasks.test {
    useJUnitPlatform()
}