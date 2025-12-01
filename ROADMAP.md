# 将来の拡張ロードマップ

このドキュメントは、パスワードバリデータプロジェクトを将来的にどのように拡張していくかのガイドです。

## 📋 目次

1. [フェーズ 1: 現在の状態](#フェーズ1-現在の状態)
2. [フェーズ 2: パスワード強度評価](#フェーズ2-パスワード強度評価)
3. [フェーズ 3: パスワードマネージャー](#フェーズ3-パスワードマネージャー)
4. [フェーズ 4: Kotlin Multiplatform](#フェーズ4-kotlin-multiplatform)

---

## フェーズ 1: 現在の状態

✅ **完了済み**

### 機能

- パスワードバリデーションのコアロジック
- 拡張可能なルールシステム
- 包括的なユニットテスト
- TDD 学習教材

### 技術スタック

- Kotlin/JVM
- JUnit 5
- Gradle

---

## フェーズ 2: パスワード強度評価

### 目標

単純な合格/不合格だけでなく、パスワードの強度をスコアリングする機能を追加

### 実装内容

#### 2.1 強度計算エンジン

```kotlin
package com.example.passwordvalidator.strength

data class PasswordStrength(
    val score: Int,          // 0-100のスコア
    val level: StrengthLevel, // 強度レベル
    val suggestions: List<String> // 改善提案
)

enum class StrengthLevel {
    VERY_WEAK,  // 0-20
    WEAK,       // 21-40
    MODERATE,   // 41-60
    STRONG,     // 61-80
    VERY_STRONG // 81-100
}

class PasswordStrengthCalculator(
    private val rules: List<ScoringRule>
) {
    fun calculate(password: String): PasswordStrength {
        val score = rules.sumOf { it.calculateScore(password) }
        val level = determineLevel(score)
        val suggestions = generateSuggestions(password, rules)

        return PasswordStrength(score, level, suggestions)
    }
}
```

#### 2.2 スコアリングルール

```kotlin
interface ScoringRule {
    val maxScore: Int
    fun calculateScore(password: String): Int
    fun getSuggestion(password: String): String?
}

// 例: 長さに応じたスコアリング
class LengthScoringRule : ScoringRule {
    override val maxScore = 25

    override fun calculateScore(password: String): Int {
        return when {
            password.length >= 16 -> 25
            password.length >= 12 -> 20
            password.length >= 8 -> 15
            password.length >= 6 -> 10
            else -> 0
        }
    }

    override fun getSuggestion(password: String): String? {
        return if (password.length < 12) {
            "パスワードを12文字以上にすると、より安全になります"
        } else null
    }
}

// 他のスコアリングルール例
class CharacterVarietyScoringRule : ScoringRule // 文字種の多様性
class UnpredictabilityScoringRule : ScoringRule  // 予測困難性
class EntropyCalculator                           // エントロピー計算
```

#### 2.3 テストの追加

TDD で実装するため、まずテストを書きます。

```kotlin
class PasswordStrengthCalculatorTest {

    @Test
    fun `very weak password should score low`() {
        val calculator = PasswordStrengthCalculator(defaultRules())
        val result = calculator.calculate("123")

        assertThat(result.level).isEqualTo(StrengthLevel.VERY_WEAK)
        assertThat(result.suggestions).isNotEmpty()
    }

    @Test
    fun `strong password should score high`() {
        val calculator = PasswordStrengthCalculator(defaultRules())
        val result = calculator.calculate("MyS3cur3P@ssw0rd!")

        assertThat(result.level).isIn(StrengthLevel.STRONG, StrengthLevel.VERY_STRONG)
    }
}
```

---

## フェーズ 3: パスワードマネージャー

### 目標

ローカルでパスワードを安全に管理できるアプリケーションを作成

### アーキテクチャ

```
password-manager/
├── domain/           # ドメインロジック（既存のvalidatorもここ）
│   ├── validator/
│   ├── strength/
│   ├── generator/    # パスワード生成
│   └── encryption/   # 暗号化
├── data/            # データ層
│   ├── repository/
│   ├── storage/     # ローカルストレージ
│   └── model/
├── ui/              # UI層（後でMultiplatform化）
│   └── console/     # まずはCLI版
└── security/        # セキュリティ関連
    ├── crypto/
    └── keymanager/
```

### 実装内容

#### 3.1 パスワード生成機能

```kotlin
package com.example.passwordmanager.generator

class PasswordGenerator(
    private val policy: PasswordPolicy
) {
    fun generate(): String {
        // 要件を満たすランダムなパスワードを生成
        var password: String
        do {
            password = generateCandidate()
        } while (!meetsPolicy(password))

        return password
    }

    private fun generateCandidate(): String {
        // SecureRandomを使用した安全な生成
    }
}

data class PasswordPolicy(
    val length: Int = 16,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeDigits: Boolean = true,
    val includeSpecialChars: Boolean = true,
    val excludeAmbiguous: Boolean = true // O, 0, I, l など
)
```

#### 3.2 暗号化ストレージ

```kotlin
package com.example.passwordmanager.storage

data class PasswordEntry(
    val id: String,
    val serviceName: String,
    val username: String,
    val encryptedPassword: ByteArray,
    val createdAt: Instant,
    val updatedAt: Instant,
    val tags: List<String> = emptyList()
)

interface PasswordRepository {
    suspend fun save(entry: PasswordEntry)
    suspend fun findById(id: String): PasswordEntry?
    suspend fun findByService(serviceName: String): List<PasswordEntry>
    suspend fun update(entry: PasswordEntry)
    suspend fun delete(id: String)
    suspend fun list(): List<PasswordEntry>
}

class EncryptedPasswordRepository(
    private val storage: SecureStorage,
    private val encryptionService: EncryptionService
) : PasswordRepository {
    // 実装
}
```

#### 3.3 マスターパスワード認証

```kotlin
package com.example.passwordmanager.security

class MasterPasswordManager(
    private val storage: SecureStorage
) {
    fun initialize(masterPassword: String) {
        // マスターパスワードから鍵を導出
        val salt = generateSalt()
        val key = deriveKey(masterPassword, salt)
        storage.saveKey(key, salt)
    }

    fun authenticate(masterPassword: String): Boolean {
        val salt = storage.getSalt()
        val expectedKey = storage.getKey()
        val providedKey = deriveKey(masterPassword, salt)

        return providedKey.contentEquals(expectedKey)
    }

    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        // PBKDF2またはArgon2を使用
    }
}
```

#### 3.4 CLI インターフェース

```kotlin
package com.example.passwordmanager.cli

class PasswordManagerCLI(
    private val repository: PasswordRepository,
    private val generator: PasswordGenerator,
    private val validator: PasswordValidator
) {
    fun run() {
        println("パスワードマネージャー")

        while (true) {
            showMenu()
            when (readCommand()) {
                "1" -> addPassword()
                "2" -> listPasswords()
                "3" -> searchPassword()
                "4" -> generatePassword()
                "5" -> validatePassword()
                "6" -> exit()
            }
        }
    }
}
```

### セキュリティ考慮事項

- ✅ AES-256-GCM での暗号化
- ✅ PBKDF2/Argon2 でのキー導出
- ✅ メモリ上のパスワードの適切なクリア
- ✅ ローカルストレージのアクセス制限
- ⚠️ クリップボードのタイムアウト
- ⚠️ 自動ロック機能

---

## フェーズ 4: Kotlin Multiplatform

### 目標

Android、iOS、デスクトップで動作するクロスプラットフォームアプリ

### プロジェクト構造

```
password-manager-multiplatform/
├── shared/                    # 共通コード（KMP）
│   ├── commonMain/
│   │   ├── kotlin/
│   │   │   ├── domain/       # ドメインロジック（既存コード）
│   │   │   ├── data/         # データ層（インターフェース）
│   │   │   └── ui/           # UIロジック（ViewModel）
│   ├── androidMain/
│   │   └── kotlin/           # Androidプラットフォーム固有実装
│   ├── iosMain/
│   │   └── kotlin/           # iOSプラットフォーム固有実装
│   └── jvmMain/
│       └── kotlin/           # デスクトップ固有実装
├── androidApp/               # Android アプリ
├── iosApp/                   # iOS アプリ
└── desktopApp/              # デスクトップアプリ（Compose）
```

### 4.1 共通コードの移行

既存のバリデーションロジックはそのまま`commonMain`に移行可能：

```kotlin
// shared/commonMain/kotlin/com/example/passwordmanager/domain/validator/
// PasswordValidator.kt（既存のコードそのまま）
```

### 4.2 プラットフォーム固有実装

```kotlin
// shared/commonMain
expect class SecureStorage {
    fun saveKey(key: ByteArray, salt: ByteArray)
    fun getKey(): ByteArray
    fun getSalt(): ByteArray
}

// shared/androidMain
actual class SecureStorage {
    // Android Keystoreを使用
}

// shared/iosMain
actual class SecureStorage {
    // Keychainを使用
}
```

### 4.3 UI 実装

#### Compose Multiplatform を使用

```kotlin
// shared/commonMain/kotlin/ui/
@Composable
fun PasswordListScreen(
    viewModel: PasswordListViewModel
) {
    val passwords by viewModel.passwords.collectAsState()

    LazyColumn {
        items(passwords) { password ->
            PasswordItem(password)
        }
    }
}
```

### 4.4 依存関係

```kotlin
// shared/build.gradle.kts
kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                // 既存の依存関係はそのまま
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.security:security-crypto:1.1.0-alpha06")
            }
        }
    }
}
```

---

## 🗓️ タイムライン（推奨）

### フェーズ 2（1-2 週間）

- [ ] スコアリングエンジンの設計
- [ ] 基本的なスコアリングルールの実装
- [ ] テストの追加
- [ ] UI/CLI での表示

### フェーズ 3（3-4 週間）

- [ ] パスワード生成機能
- [ ] 暗号化ストレージ
- [ ] マスターパスワード認証
- [ ] CLI 実装
- [ ] セキュリティ監査

### フェーズ 4（4-6 週間）

- [ ] Kotlin Multiplatform への移行
- [ ] Android アプリ
- [ ] iOS アプリ
- [ ] デスクトップアプリ
- [ ] 統合テスト

---

## 📚 学習リソース

### フェーズ 2 向け

- パスワードエントロピーの計算
- 情報理論の基礎

### フェーズ 3 向け

- 暗号化の基礎（AES、PBKDF2）
- セキュアコーディング
- Kotlin のコルーチン

### フェーズ 4 向け

- Kotlin Multiplatform 公式ドキュメント
- Compose Multiplatform
- プラットフォーム固有 API

---

## 💡 ポイント

### なぜこの順序？

1. **フェーズ 2**: 既存コードの拡張なので、TDD の練習継続に最適
2. **フェーズ 3**: アプリケーションの完成形を作り、実用的な経験を積む
3. **フェーズ 4**: 既に動くコードがあるので、マルチプラットフォーム化に集中できる

### 各フェーズで学べること

- **フェーズ 2**: アルゴリズム設計、数値計算
- **フェーズ 3**: セキュリティ、永続化、アーキテクチャ
- **フェーズ 4**: クロスプラットフォーム開発、ネイティブ API

---

**焦らず、一歩ずつ進めていきましょう！ 🚀**
