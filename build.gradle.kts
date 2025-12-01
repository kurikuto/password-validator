plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin標準ライブラリ（Kotlin 2.0以降は自動的に含まれますが明示的に記載）
    implementation(kotlin("stdlib"))
    
    // テスト用ライブラリ
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.3")
    
    // AssertJで読みやすいアサーション（初学者向けに流暢なAPI）
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
    
    testLogging {
        // テスト結果を詳細に表示（学習用に全て表示）
        events("passed", "skipped", "failed")
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.withType<JavaExec> {
    // Windows PowerShellでの文字化け対策
    systemProperty("file.encoding", "UTF-8")
    systemProperty("stdout.encoding", "UTF-8")
    systemProperty("stderr.encoding", "UTF-8")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

kotlin {
    // Java 21を使用（安定版LTS）
    jvmToolchain(21)
}

// Example.ktを実行できるようにメインクラスを指定
application {
    mainClass.set("com.example.passwordvalidator.ExampleKt")
}
