# パスワードバリデータ - TDD 学習プロジェクト

このプロジェクトは、**テスト駆動開発（TDD）**を学ぶための教材として設計されています。
パスワードバリデーション機能を実装しながら、Kotlin でのテストの書き方、エッジケースの考え方、そして TDD のワークフローを実践的に学びます。

## 🎯 プロジェクトの目的

- **TDD の基礎を学ぶ**: Red-Green-Refactor サイクルを実践
- **ユニットテストの書き方を習得**: JUnit5 と AssertJ を使った効果的なテストの作成
- **設計の考え方を理解**: インターフェース、Strategy パターン、Builder パターン
- **エッジケースの重要性**: 境界値テスト、パラメータ化テスト
- **将来の拡張性**: Kotlin Multiplatform への移行を見据えた構成

## 📚 ドキュメント

- **[SETUP.md](SETUP.md)** - 環境構築とセットアップ方法
- **[KOTLIN_GUIDE.md](KOTLIN_GUIDE.md)** - Kotlin 初学者向けの文法ガイド
- **[TDD_TUTORIAL.md](TDD_TUTORIAL.md)** - TDD 実践チュートリアル
- **[ROADMAP.md](ROADMAP.md)** - 将来の拡張計画

## 🛠️ 使用技術

- **Kotlin 2.1.0**: モダンで簡潔な言語（2024 年最新版）
- **JUnit 5.11.3**: 最新の Java テストフレームワーク
- **AssertJ 3.26.3**: 読みやすいアサーション
- **Gradle 8.11**: ビルドツール（Kotlin DSL）

## 📦 プロジェクト構成

```
password-validator/
├── src/
│   ├── main/kotlin/com/example/passwordvalidator/
│   │   ├── ValidationResult.kt      # バリデーション結果を表すデータクラス
│   │   ├── ValidationRule.kt        # ルールのインターフェースと実装
│   │   ├── PasswordValidator.kt     # メインのバリデータクラス
│   │   └── Example.kt              # 実行可能なサンプルコード
│   └── test/kotlin/com/example/passwordvalidator/
│       ├── ValidationRuleTest.kt    # 各ルールの単体テスト（60+ケース）
│       └── PasswordValidatorTest.kt # 統合テスト
├── build.gradle.kts                  # ビルド設定
├── README.md                         # このファイル
├── SETUP.md                         # セットアップガイド
├── KOTLIN_GUIDE.md                  # Kotlin文法ガイド
├── TDD_TUTORIAL.md                  # TDDチュートリアル
└── ROADMAP.md                       # 拡張計画
```

## 🚀 クイックスタート

### 1. セットアップ

詳細は [SETUP.md](SETUP.md) を参照してください。

```powershell
# プロジェクトをビルド
.\gradlew.bat build

# テストを実行
.\gradlew.bat test
```

### 2. サンプルコードを実行

`Example.kt`を開いて実行ボタンをクリックするか：

```powershell
.\gradlew.bat run
```

import com.example.passwordvalidator.\*

// 1. バリデータを作成
val validator = PasswordValidator(
MinimumLengthRule(8),
UppercaseRule(),
LowercaseRule(),
DigitRule()
)

// 2. パスワードを検証
val result = validator.validate("Password123")

// 3. 結果を確認
if (result.isValid) {
println("パスワードは有効です")
} else {
println("エラー:")
result.errors.forEach { println(" - $it") }
}

````

### プリセット設定を使用

```kotlin
// 基本的なバリデータ（8文字以上、英大文字・小文字・数字）
val basicValidator = PasswordValidatorPresets.basic()

// 強固なバリデータ（12文字以上、特殊文字も必須）
val strongValidator = PasswordValidatorPresets.strong()

// カスタマイズ
val customValidator = PasswordValidatorPresets.Builder()
    .notEmpty()
    .minLength(10)
    .maxLength(50)
    .requireUppercase()
    .requireLowercase()
    .requireDigit()
    .requireSpecialChar()
    .build()
````

## 🧪 テスト駆動開発の実践

### TDD の基本サイクル

1. **🔴 Red**: 失敗するテストを書く
2. **🟢 Green**: テストが通る最小限のコードを書く
3. **♻️ Refactor**: コードをきれいにする

### 実践例：新しいルールを追加する

例として、「連続する同じ文字を禁止する」ルールを追加してみましょう。

#### ステップ 1: テストを先に書く（Red）

`src/test/kotlin/com/example/passwordvalidator/ValidationRuleTest.kt`に追加:

```kotlin
@Nested
@DisplayName("NoConsecutiveCharactersRule のテスト")
inner class NoConsecutiveCharactersRuleTest {

    @Test
    @DisplayName("連続する同じ文字がない場合はtrueを返す")
    fun `should return true when no consecutive characters`() {
        val rule = NoConsecutiveCharactersRule()

        assertTrue(rule.validate("Password123"))
        assertTrue(rule.validate("AbCdEf"))
    }

    @Test
    @DisplayName("連続する同じ文字がある場合はfalseを返す")
    fun `should return false when consecutive characters exist`() {
        val rule = NoConsecutiveCharactersRule()

        assertFalse(rule.validate("Passsword"))
        assertFalse(rule.validate("aabbcc"))
    }
}
```

#### ステップ 2: 実装する（Green）

`src/main/kotlin/com/example/passwordvalidator/ValidationRule.kt`に追加:

```kotlin
/**
 * 連続する同じ文字を禁止するルール
 */
class NoConsecutiveCharactersRule : ValidationRule {
    override val description: String = "パスワードに連続する同じ文字を含めることはできません"

    override fun validate(password: String): Boolean {
        if (password.length < 2) return true

        for (i in 0 until password.length - 1) {
            if (password[i] == password[i + 1]) {
                return false
            }
        }
        return true
    }
}
```

#### ステップ 3: リファクタリング（Refactor）

より読みやすく、Kotlin らしい書き方に:

```kotlin
class NoConsecutiveCharactersRule : ValidationRule {
    override val description: String = "パスワードに連続する同じ文字を含めることはできません"

    override fun validate(password: String): Boolean {
        return password.zipWithNext().none { (a, b) -> a == b }
    }
}
```

### テストの実行

```powershell
.\gradlew.bat test
```

## 📖 学習ガイド

### 初心者向けの学習ステップ

1. **既存のテストを読む**

   - `ValidationRuleTest.kt`と`PasswordValidatorTest.kt`を読んで、テストの書き方を理解する
   - `@Test`、`@DisplayName`、`@Nested`などのアノテーションの意味を学ぶ

2. **テストを実行する**

   - `.\gradlew.bat test`でテストを実行し、結果を確認
   - わざとテストを失敗させて、エラーメッセージを理解する

3. **新しいルールを追加する**

   - 上記の TDD 実践例を参考に、自分で新しいルールを考えて実装
   - 例: 辞書にある単語を禁止、同じ文字の繰り返し制限など

4. **エッジケースを考える**
   - 空文字、null、極端に長い文字列など
   - Unicode 文字、特殊文字、絵文字など

### テストのベストプラクティス

- **1 つのテストで 1 つのことだけをテスト**: テストは明確で理解しやすく
- **わかりやすい名前をつける**: 何をテストしているのか一目でわかるように
- **AAA パターン**: Arrange（準備）、Act（実行）、Assert（検証）
- **エッジケースを忘れない**: 正常系だけでなく、境界値や異常系も
- **パラメータ化テストを活用**: 同じロジックを複数の入力でテスト

## 🔮 将来の拡張計画

このプロジェクトは、以下のように発展させることができます：

### フェーズ 2: パスワード強度スコアリング

```kotlin
class PasswordStrengthCalculator {
    fun calculateStrength(password: String): PasswordStrength {
        // WEAK, MODERATE, STRONG, VERY_STRONG
    }
}
```

### フェーズ 3: パスワードマネージャー（ローカル版）

- 暗号化されたパスワードの保存
- マスターパスワードでの認証
- パスワード生成機能

### フェーズ 4: Kotlin Multiplatform 対応

```
common/         # 共通ロジック（今のコードをそのまま移行可能）
androidMain/    # Androidアプリ
iosMain/        # iOSアプリ
jvmMain/        # デスクトップアプリ
```

## 🛠️ 使用技術

- **Kotlin 1.9.22**: モダンで簡潔な言語
- **JUnit 5**: 最新の Java テストフレームワーク
- **AssertJ**: 読みやすいアサーション
- **Gradle Kotlin DSL**: ビルド設定

## 📝 練習問題

### 初級

1. `MinimumLengthRule`の最小文字数を変更して、テストが失敗することを確認する
2. 新しいテストケースを追加する（例：日本語を含むパスワード）
3. `PasswordValidatorPresets.Builder`を使って、独自のバリデータを作成する

### 中級

4. 「同じ文字が 3 回以上連続しない」ルールを実装する
5. 「よく使われる弱いパスワード（password、123456 など）を禁止」するルールを実装
6. バリデーション結果に強度スコア（0-100）を追加する

### 上級

7. カスタムルールを作成する DSL を実装する
8. パスワード生成機能を追加する（要件を満たすランダムパスワードを生成）
9. 国際化対応（エラーメッセージを多言語対応）

## 🤝 コントリビューション

このプロジェクトは学習用です。自由にフォーク、改変してください！

## 📄 ライセンス

MIT License

## 🙋 質問・サポート

TDD について学びながら進めてください。エラーが出たときは：

1. エラーメッセージをよく読む
2. テストが何を期待しているか確認
3. 小さなステップで進める
4. 困ったら、まずテストを書いてから実装する

Happy Testing! 🎉
