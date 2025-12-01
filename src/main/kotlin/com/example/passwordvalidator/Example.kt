package com.example.passwordvalidator

/**
 * パスワードバリデータの使用例
 * 
 * このファイルは、初学者がコードの動作を理解するためのサンプルです。
 * 実際にこのコードを実行して、バリデータの動作を確認できます。
 * 
 * 実行方法:
 * 1. このファイルを開く
 * 2. main関数の左側にある ▶ ボタンをクリック
 * 3. コンソールに結果が表示されます
 */

/**
 * プログラムのエントリーポイント（開始地点）
 * 
 * main関数は、Kotlinプログラムが最初に実行する関数です。
 */
fun main() {
    println("=".repeat(60))
    println("パスワードバリデータ - サンプルプログラム")
    println("=".repeat(60))
    println()
    
    // 例1: 基本的な使い方
    example1_Basic()
    println()
    
    // 例2: プリセットを使う
    example2_Presets()
    println()
    
    // 例3: ビルダーパターンでカスタマイズ
    example3_Builder()
    println()
    
    // 例4: 様々なパスワードをテスト
    example4_VariousPasswords()
}

/**
 * 例1: 基本的な使い方
 * 
 * 自分でルールを組み合わせてバリデータを作る方法
 */
fun example1_Basic() {
    println("【例1】基本的な使い方")
    println("-".repeat(60))
    
    // ステップ1: バリデータを作成（必要なルールを指定）
    val validator = PasswordValidator(
        MinimumLengthRule(8),      // 8文字以上
        UppercaseRule(),           // 大文字を含む
        LowercaseRule(),           // 小文字を含む
        DigitRule()                // 数字を含む
    )
    
    // ステップ2: テストするパスワード
    val testPasswords = listOf(
        "password",         // 小文字のみ → 不合格
        "PASSWORD",         // 大文字のみ → 不合格
        "Password",         // 数字なし → 不合格
        "Password123"       // 全ての条件を満たす → 合格
    )
    
    // ステップ3: 各パスワードをテスト
    testPasswords.forEach { password ->
        val result = validator.validate(password)
        printResult(password, result)
    }
}

/**
 * 例2: プリセットを使う
 * 
 * よく使うパターンは既に定義されているので、それを使うと便利
 */
fun example2_Presets() {
    println("【例2】プリセットを使う")
    println("-".repeat(60))
    
    // 基本的なバリデータ（8文字以上、大文字・小文字・数字）
    println("■ 基本プリセット")
    val basicValidator = PasswordValidatorPresets.basic()
    printResult("Pass123", basicValidator.validate("Pass123"))
    println()
    
    // 強固なバリデータ（12文字以上、特殊文字も必須）
    println("■ 強固プリセット")
    val strongValidator = PasswordValidatorPresets.strong()
    printResult("Pass123", strongValidator.validate("Pass123"))
    printResult("MyS3cur3P@ssw0rd!", strongValidator.validate("MyS3cur3P@ssw0rd!"))
}

/**
 * 例3: ビルダーパターンでカスタマイズ
 * 
 * 必要なルールだけを選んで、独自のバリデータを作る
 */
fun example3_Builder() {
    println("【例3】ビルダーパターンでカスタマイズ")
    println("-".repeat(60))
    
    // メソッドチェーンで読みやすく設定できる
    val customValidator = PasswordValidatorPresets.Builder()
        .notEmpty()                    // 空でない
        .minLength(10)                 // 10文字以上
        .maxLength(30)                 // 30文字以下
        .requireUppercase()            // 大文字必須
        .requireLowercase()            // 小文字必須
        .requireDigit()                // 数字必須
        .requireSpecialChar("!@#$")    // 特殊文字必須（カスタム）
        .build()                       // バリデータを生成
    
    val testPasswords = listOf(
        "Short1!",              // 短すぎる
        "ValidPassword123!",    // 合格
        "TooLongPasswordThatExceedsLimit123!", // 長すぎる
        "NoSpecial123"          // 特殊文字なし
    )
    
    testPasswords.forEach { password ->
        val result = customValidator.validate(password)
        printResult(password, result)
    }
}

/**
 * 例4: 様々なパスワードをテスト
 * 
 * よくある間違いパターンを確認
 */
fun example4_VariousPasswords() {
    println("【例4】様々なパスワードのテスト")
    println("-".repeat(60))
    
    val validator = PasswordValidatorPresets.basic()
    
    // よくある弱いパスワード
    val commonWeakPasswords = mapOf(
        "password" to "よくある単語のみ",
        "12345678" to "数字のみ",
        "ABCDEFGH" to "大文字のみ",
        "abcdefgh" to "小文字のみ",
        "Pass" to "短すぎる",
        "Password123" to "良いパスワード"
    )
    
    commonWeakPasswords.forEach { (password, description) ->
        println("テスト: $password ($description)")
        val result = validator.validate(password)
        printResult(password, result)
        println()
    }
}

/**
 * 結果を見やすく表示するヘルパー関数
 * 
 * @param password テストしたパスワード
 * @param result バリデーション結果
 */
fun printResult(password: String, result: ValidationResult) {
    if (result.isValid) {
        println("  ✓ '$password' → 合格")
    } else {
        println("  ✗ '$password' → 不合格")
        result.errors.forEach { error ->
            println("    - $error")
        }
    }
}
