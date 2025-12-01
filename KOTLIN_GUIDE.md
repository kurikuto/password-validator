# Kotlin 初学者向けガイド

このドキュメントは、このプロジェクトで使われている Kotlin の文法や概念を説明します。

## 目次

1. [基本的な文法](#基本的な文法)
2. [クラスとインターフェース](#クラスとインターフェース)
3. [関数型プログラミング](#関数型プログラミング)
4. [よく使うパターン](#よく使うパターン)
5. [テストの書き方](#テストの書き方)

---

## 基本的な文法

### 変数の宣言

```kotlin
// val: 変更不可（immutable） - 推奨
val password = "test123"  // 型は自動推論される
val length: Int = 8       // 型を明示的に指定することも可能

// var: 変更可能（mutable） - 必要な場合のみ使用
var count = 0
count = 1  // 変更できる
```

**初学者へのアドバイス**: できるだけ`val`を使いましょう。バグが減ります。

### 文字列

```kotlin
// 文字列テンプレート（変数を埋め込める）
val name = "太郎"
val message = "こんにちは、${name}さん"  // "こんにちは、太郎さん"

// シンプルな変数なら$だけでOK
val length = 8
val text = "パスワードは$length文字以上"  // "パスワードは8文字以上"

// 複数行文字列
val multiline = """
    行1
    行2
    行3
""".trimIndent()
```

### 関数

```kotlin
// 基本的な関数
fun greet(name: String): String {
    return "こんにちは、$name"
}

// 単一式関数（簡潔に書ける）
fun add(a: Int, b: Int): Int = a + b

// 戻り値がない場合は Unit（省略可能）
fun printMessage(message: String) {
    println(message)
}

// デフォルト引数
fun createRule(minLength: Int = 8): MinimumLengthRule {
    return MinimumLengthRule(minLength)
}

// 使用例
val rule1 = createRule()      // デフォルト値8を使用
val rule2 = createRule(10)    // 10を指定
```

---

## クラスとインターフェース

### データクラス

```kotlin
// data class: データを保持するためのクラス
// equals(), hashCode(), toString() が自動生成される
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()  // デフォルト値
)

// 使用例
val result = ValidationResult(isValid = true)
println(result)  // ValidationResult(isValid=true, errors=[])

// コピーして一部を変更
val failed = result.copy(isValid = false, errors = listOf("エラー"))
```

**なぜ data class?**:

- ボイラープレートコードを減らせる
- 自動生成されるメソッドでバグを防げる
- 値オブジェクトとして扱いやすい

### インターフェース

```kotlin
// インターフェース: 「契約」を定義
interface ValidationRule {
    val description: String      // プロパティ
    fun validate(password: String): Boolean  // メソッド
}

// 実装クラス
class MinimumLengthRule(private val minLength: Int) : ValidationRule {
    // override: インターフェースのメンバーを実装
    override val description: String
        get() = "パスワードは${minLength}文字以上である必要があります"

    override fun validate(password: String): Boolean {
        return password.length >= minLength
    }
}
```

**なぜインターフェース?**:

- 複数のルールを統一的に扱える（多態性）
- 新しいルールを追加しやすい（拡張性）
- テストしやすい（モック化が簡単）

### 通常のクラス

```kotlin
// コンストラクタでプロパティを定義
class PasswordValidator(private vararg val rules: ValidationRule) {
    //        ↑           ↑      ↑
    //        |           |      プロパティとして保存
    //        |           可変長引数（複数受け取れる）
    //        外部から直接アクセス不可

    fun validate(password: String): ValidationResult {
        // 実装
    }
}

// 使用例
val validator = PasswordValidator(
    MinimumLengthRule(8),
    UppercaseRule(),
    DigitRule()
)
```

### オブジェクト（シングルトン）

```kotlin
// object: インスタンスが1つだけ存在するクラス
object PasswordValidatorPresets {
    fun basic(): PasswordValidator {
        // 実装
    }
}

// 使用例（インスタンス化不要）
val validator = PasswordValidatorPresets.basic()
```

### Companion Object

```kotlin
class ValidationResult(val isValid: Boolean, val errors: List<String>) {
    // companion object: クラスに関連する静的メソッドを定義
    companion object {
        fun success(): ValidationResult {
            return ValidationResult(true, emptyList())
        }
    }
}

// 使用例
val result = ValidationResult.success()  // クラス名から直接呼べる
```

---

## 関数型プログラミング

Kotlin は関数型プログラミングのスタイルもサポートしています。

### コレクション操作

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// filter: 条件に合う要素だけを抽出
val evenNumbers = numbers.filter { it % 2 == 0 }  // [2, 4]
//                                   ↑
//                                   it: ラムダの暗黙的なパラメータ

// map: 各要素を変換
val doubled = numbers.map { it * 2 }  // [2, 4, 6, 8, 10]

// any: 条件を満たす要素が1つでもあればtrue
val hasEven = numbers.any { it % 2 == 0 }  // true

// none: 条件を満たす要素が1つもなければtrue
val noNegative = numbers.none { it < 0 }  // true

// forEach: 各要素に対して処理を実行
numbers.forEach { number ->
    println(number)
}
```

**このプロジェクトでの使用例**:

```kotlin
fun validate(password: String): ValidationResult {
    val errors = rules
        .filter { !it.validate(password) }  // 失敗したルールだけ
        .map { it.description }              // エラーメッセージに変換

    return if (errors.isEmpty()) {
        ValidationResult.success()
    } else {
        ValidationResult.failure(errors)
    }
}
```

### ラムダ式

```kotlin
// ラムダ式: 無名関数
val add = { a: Int, b: Int -> a + b }
println(add(2, 3))  // 5

// 単一パラメータなら it が使える
val double: (Int) -> Int = { it * 2 }
println(double(5))  // 10

// 最後の引数がラムダなら、括弧の外に書ける
val numbers = listOf(1, 2, 3)
numbers.filter { it > 1 }  // 見やすい！
// これは以下と同じ
numbers.filter({ it > 1 })  // 括弧の中
```

---

## よく使うパターン

### Builder パターン

```kotlin
class Builder {
    private val rules = mutableListOf<ValidationRule>()

    // apply: オブジェクト自身を返す（メソッドチェーン用）
    fun minLength(length: Int) = apply {
        rules.add(MinimumLengthRule(length))
    }

    fun requireUppercase() = apply {
        rules.add(UppercaseRule())
    }

    fun build(): PasswordValidator {
        return PasswordValidator(*rules.toTypedArray())
        //                       ↑
        //                       スプレッド演算子: 配列をvarargに展開
    }
}

// 使用例（メソッドチェーン）
val validator = Builder()
    .minLength(8)
    .requireUppercase()
    .build()
```

### エクステンション関数

```kotlin
// 既存のクラスに機能を追加
fun String.isStrongPassword(): Boolean {
    val validator = PasswordValidatorPresets.strong()
    return validator.validate(this).isValid
}

// 使用例
val password = "MyS3cur3P@ssw0rd!"
if (password.isStrongPassword()) {
    println("強固なパスワードです")
}
```

### スコープ関数

```kotlin
// apply: オブジェクトの設定に使う
val validator = PasswordValidator().apply {
    // this = validator
}

// let: null チェックによく使う
val result: ValidationResult? = getResult()
result?.let {
    // it = result（nullでない場合のみ実行）
    println(it.errors)
}

// also: デバッグやログに便利
val password = "test123"
    .also { println("Testing: $it") }
    .uppercase()
```

---

## テストの書き方

### JUnit 5 の基本

```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class MyTest {

    @Test
    @DisplayName("説明文（日本語OK）")
    fun `test name in backticks`() {
        // Given（準備）
        val rule = MinimumLengthRule(8)

        // When（実行）
        val result = rule.validate("12345678")

        // Then（検証）
        assertTrue(result)
    }
}
```

### ネストしたテスト

```kotlin
@DisplayName("ValidationRule のテスト")
class ValidationRuleTest {

    @Nested
    @DisplayName("MinimumLengthRule のテスト")
    inner class MinimumLengthRuleTest {

        @Test
        fun `should work`() {
            // テスト
        }
    }

    @Nested
    @DisplayName("UppercaseRule のテスト")
    inner class UppercaseRuleTest {

        @Test
        fun `should work`() {
            // テスト
        }
    }
}
```

### AssertJ の使い方

```kotlin
import org.assertj.core.api.Assertions.assertThat

@Test
fun `test with assertj`() {
    val result = ValidationResult.failure(listOf("エラー1", "エラー2"))

    // 読みやすいアサーション
    assertThat(result.isValid).isFalse()
    assertThat(result.errors).hasSize(2)
    assertThat(result.errors).contains("エラー1")
    assertThat(result.errors[0]).startsWith("エラー")
}
```

### パラメータ化テスト

```kotlin
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ParameterizedTest
@ValueSource(strings = ["Pass123", "Test456", "Valid789"])
fun `should accept valid passwords`(password: String) {
    val validator = PasswordValidatorPresets.basic()
    assertTrue(validator.validate(password).isValid)
}
```

---

## まとめ：このプロジェクトで学べること

### 基本的な概念

- ✅ クラスとインターフェース
- ✅ データクラスとプロパティ
- ✅ 関数とラムダ式
- ✅ コレクション操作

### デザインパターン

- ✅ Strategy パターン（ValidationRule）
- ✅ Builder パターン（カスタマイズ可能な構築）
- ✅ Factory パターン（Presets）

### テスト駆動開発

- ✅ ユニットテストの書き方
- ✅ TDD のサイクル（Red-Green-Refactor）
- ✅ テストの構造化

### Kotlin らしい書き方

- ✅ 簡潔で読みやすいコード
- ✅ Null 安全性
- ✅ 関数型プログラミング

---

## 参考リソース

- [Kotlin の公式ドキュメント](https://kotlinlang.org/docs/home.html)
- [Kotlin Koans（練習問題）](https://play.kotlinlang.org/koans/overview)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

**質問があれば、コードを読みながらこのガイドを参照してください！**

Happy Learning! 📚
