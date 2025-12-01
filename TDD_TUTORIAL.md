# TDD チュートリアル - 実践ガイド

このドキュメントは、テスト駆動開発（TDD）を実際に体験するためのハンズオンチュートリアルです。

## 🎓 TDD とは？

**テスト駆動開発（Test-Driven Development）**は、以下の 3 つのステップを繰り返す開発手法です：

1. **🔴 Red（レッド）**: まず失敗するテストを書く
2. **🟢 Green（グリーン）**: テストが通る最小限のコードを書く
3. **♻️ Refactor（リファクタリング）**: コードを改善する

### TDD のメリット

- ✅ バグが少なくなる
- ✅ 設計が改善される
- ✅ リファクタリングが安全になる
- ✅ ドキュメントとしても機能する
- ✅ 実装の前に仕様を明確にできる

## 📖 チュートリアル

### チュートリアル 1: 初めての TDD

**目標**: TDD のサイクルを体験する

#### シナリオ: 「連続する数字を禁止する」ルールを追加

多くのシステムでは、「123456」や「987654」のような連続する数字を禁止しています。このルールを TDD で実装してみましょう。

---

#### ステップ 1: 🔴 Red - テストを書く

まず、テストを書きます。この時点では実装がないので、**テストは失敗します**。

`src/test/kotlin/com/example/passwordvalidator/ValidationRuleTest.kt`を開き、以下を追加：

```kotlin
@Nested
@DisplayName("NoConsecutiveDigitsRule のテスト")
inner class NoConsecutiveDigitsRuleTest {

    @Test
    @DisplayName("連続する数字がない場合はtrueを返す")
    fun `should return true when no consecutive digits`() {
        val rule = NoConsecutiveDigitsRule()

        assertTrue(rule.validate("Password135"))
        assertTrue(rule.validate("Pass8024Word"))
    }

    @Test
    @DisplayName("3文字以上の連続する数字がある場合はfalseを返す")
    fun `should return false when consecutive digits exist`() {
        val rule = NoConsecutiveDigitsRule()

        assertFalse(rule.validate("Password123"))  // 1,2,3と連続
        assertFalse(rule.validate("Pass987word"))  // 9,8,7と連続（降順）
    }
}
```

**テストを実行**:

```powershell
.\gradlew.bat test
```

❌ **結果**: `NoConsecutiveDigitsRule`クラスが存在しないため、コンパイルエラーになります。**これが期待通りの「Red」状態です。**

---

#### ステップ 2: 🟢 Green - 最小限の実装

テストが通る最小限のコードを書きます。

`src/main/kotlin/com/example/passwordvalidator/ValidationRule.kt`に以下を追加：

```kotlin
/**
 * 連続する数字（123、987など）を禁止するルール
 */
class NoConsecutiveDigitsRule : ValidationRule {
    override val description: String = "パスワードに連続する数字を含めることはできません"

    override fun validate(password: String): Boolean {
        val digits = password.filter { it.isDigit() }

        if (digits.length < 3) return true

        for (i in 0 until digits.length - 2) {
            val first = digits[i].digitToInt()
            val second = digits[i + 1].digitToInt()
            val third = digits[i + 2].digitToInt()

            // 昇順（123）または降順（987）の連続をチェック
            if ((second == first + 1 && third == second + 1) ||
                (second == first - 1 && third == second - 1)) {
                return false
            }
        }

        return true
    }
}
```

**テストを再実行**:

```powershell
.\gradlew.bat test
```

✅ **結果**: テストが通ります！これが「Green」状態です。

---

#### ステップ 3: ♻️ Refactor - コードを改善

コードは動いていますが、もっと読みやすくできるか考えましょう。

```kotlin
class NoConsecutiveDigitsRule : ValidationRule {
    override val description: String = "パスワードに連続する数字を含めることはできません"

    override fun validate(password: String): Boolean {
        val digits = password.filter { it.isDigit() }.map { it.digitToInt() }

        return digits
            .windowed(3, 1)
            .none { (a, b, c) -> isConsecutive(a, b, c) }
    }

    private fun isConsecutive(a: Int, b: Int, c: Int): Boolean {
        return (b == a + 1 && c == b + 1) || (b == a - 1 && c == b - 1)
    }
}
```

**テストを再実行**して、リファクタリングが正しいことを確認：

```powershell
.\gradlew.bat test
```

✅ テストが通れば、リファクタリング成功！

---

### チュートリアル 2: エッジケースを考える

良いテストは、**エッジケース**も考慮します。

#### 追加すべきテストケース

```kotlin
@Test
@DisplayName("数字が3文字未満の場合は問題なし")
fun `should return true when less than 3 digits`() {
    val rule = NoConsecutiveDigitsRule()

    assertTrue(rule.validate("Pass12word"))
    assertTrue(rule.validate("P1W2R3D"))
}

@Test
@DisplayName("数字が連続していても離れている場合は問題なし")
fun `should return true when digits are not adjacent`() {
    val rule = NoConsecutiveDigitsRule()

    assertTrue(rule.validate("1abc2def3"))  // 1,2,3だが連続していない
}

@Test
@DisplayName("0-1-2や8-9-0の境界もチェック")
fun `should check boundary cases`() {
    val rule = NoConsecutiveDigitsRule()

    assertFalse(rule.validate("Pass012word"))  // 0,1,2
    assertFalse(rule.validate("Pass890word"))  // 8,9,0は連続とみなすか？
}
```

これらのテストを追加して、必要に応じて実装を調整します。

---

### チュートリアル 3: パラメータ化テストで効率化

同じロジックを複数の入力でテストしたい場合、**パラメータ化テスト**が便利です。

```kotlin
@ParameterizedTest(name = "有効なパスワード: {0}")
@ValueSource(strings = [
    "Pass135word",
    "P1W2R3D4",
    "Password",
    "13579",  // 連続していない奇数
])
@DisplayName("連続しない数字は許可される")
fun `should accept passwords without consecutive digits`(password: String) {
    val rule = NoConsecutiveDigitsRule()
    assertTrue(rule.validate(password))
}

@ParameterizedTest(name = "無効なパスワード: {0}")
@ValueSource(strings = [
    "Pass123word",
    "Pass987word",
    "012345",
    "password456",
])
@DisplayName("連続する数字は拒否される")
fun `should reject passwords with consecutive digits`(password: String) {
    val rule = NoConsecutiveDigitsRule()
    assertFalse(rule.validate(password))
}
```

---

## 🎯 練習問題

### 練習 1: 辞書攻撃対策

よく使われる弱いパスワードを禁止するルールを実装してください。

**要件**:

- "password", "123456", "qwerty" などの一般的な弱いパスワードを拒否
- 大文字小文字を区別しない（"Password" も拒否）

**ヒント**:

```kotlin
class CommonPasswordRule(
    private val commonPasswords: Set<String> = setOf(
        "password", "123456", "qwerty", "admin", "letmein"
    )
) : ValidationRule {
    // TODO: 実装してください
}
```

---

### 練習 2: キーボード配列チェック

キーボード上で隣接する文字の連続（"qwerty", "asdfgh"）を禁止するルールを実装してください。

**要件**:

- QWERTY キーボードでの横方向の連続を検出
- 4 文字以上の連続を禁止

---

### 練習 3: パスワード強度スコアリング

パスワードの強度を 0-100 のスコアで評価する機能を実装してください。

**要件**:

- 各ルールを満たすごとにポイント加算
- 長さに応じてボーナスポイント
- 返り値は`PasswordStrength`（スコアと評価レベル）

**テストファースト**で実装してください！

```kotlin
data class PasswordStrength(
    val score: Int,  // 0-100
    val level: Level  // WEAK, MODERATE, STRONG, VERY_STRONG
)

class PasswordStrengthCalculator {
    fun calculate(password: String): PasswordStrength {
        // TODO: TDDで実装
    }
}
```

---

## 💡 TDD のコツ

### ✅ Do（やるべきこと）

- **小さく始める**: 一度に一つの機能だけ
- **テストを読みやすく**: テスト名で何をテストしているか明確に
- **エッジケースを考える**: 正常系だけでなく異常系も
- **リファクタリングを恐れない**: テストがあるから安全

### ❌ Don't（避けるべきこと）

- **テストなしで実装しない**: TDD のルールを守る
- **複雑すぎるテストを書かない**: テスト自体にバグがあってはダメ
- **テストをスキップしない**: 全てのテストが通ることを確認
- **実装のテストではなく、振る舞いのテスト**: 内部実装に依存しない

---

## 🚀 次のステップ

1. **すべての練習問題を解く**: TDD サイクルを繰り返し練習
2. **カバレッジを確認する**: どのコードがテストされているか
3. **実際のプロジェクトに適用**: 新しい機能は TDD で実装してみる
4. **テストコードレビュー**: 他の人のテストを見て学ぶ

---

## 📚 参考資料

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- Kent Beck 著『テスト駆動開発』
- Robert C. Martin 著『Clean Code』

---

**Happy TDD! 🎉**

テストを書くことは、将来の自分への贈り物です。
