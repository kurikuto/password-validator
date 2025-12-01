package com.example.passwordvalidator

/**
 * パスワードバリデータのメインクラス
 * 
 * このクラスは、複数のバリデーションルールを組み合わせて、
 * パスワードの妥当性を総合的に検証します。
 * 
 * 【重要な概念】
 * - vararg: 可変長引数。ルールを好きな数だけ渡せる
 * - ValidationRule: インターフェースなので、どんなルールでも渡せる
 * - 関数型プログラミング: filter, map などのメソッドを活用
 * 
 * 基本的な使用例:
 * ```kotlin
 * // ステップ1: バリデータを作成（必要なルールを指定）
 * val validator = PasswordValidator(
 *     MinimumLengthRule(8),      // 8文字以上
 *     UppercaseRule(),           // 大文字を含む
 *     LowercaseRule(),           // 小文字を含む
 *     DigitRule()                // 数字を含む
 * )
 * 
 * // ステップ2: パスワードを検証
 * val result = validator.validate("MyPassword123")
 * 
 * // ステップ3: 結果を確認
 * if (result.isValid) {
 *     println("✓ パスワードは有効です")
 * } else {
 *     println("✗ 以下の問題があります：")
 *     result.errors.forEach { error ->
 *         println("  - $error")
 *     }
 * }
 * ```
 * 
 * @property rules 適用するバリデーションルールの配列（varargで受け取る）
 */
class PasswordValidator(private vararg val rules: ValidationRule) {
    
    /**
     * パスワードを全てのルールに対して検証
     * 
     * このメソッドは以下の流れで動作します：
     * 1. 各ルールでパスワードをチェック
     * 2. 満たさないルールをフィルタリング
     * 3. エラーメッセージをリストにまとめる
     * 4. 結果を返す
     * 
     * @param password 検証対象のパスワード文字列
     * @return バリデーション結果（成功/失敗とエラーメッセージ）
     */
    fun validate(password: String): ValidationResult {
        // filter: 条件に合う要素だけを抽出（ここでは検証に失敗したルール）
        // map: 各要素を別の形に変換（ここではルールをエラーメッセージに変換）
        val errors = rules
            .filter { rule -> !rule.validate(password) }  // 検証に失敗したルールを抽出
            .map { rule -> rule.description }              // 各ルールの説明文を取得
        
        // エラーがなければ成功、あれば失敗を返す
        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
    
    /**
     * 特定のルールだけをチェック
     * 
     * デバッグや段階的な検証に便利なメソッドです。
     * 例えば、「このパスワードは長さだけクリアしているか？」といった
     * 個別のチェックができます。
     * 
     * 使用例:
     * ```kotlin
     * val validator = PasswordValidator(MinimumLengthRule(8), UppercaseRule())
     * val lengthRule = MinimumLengthRule(8)
     * 
     * if (validator.validateWithRule("pass", lengthRule)) {
     *     println("長さはOK")
     * } else {
     *     println("長さが不足")
     * }
     * ```
     * 
     * @param password 検証対象のパスワード
     * @param rule チェックしたい特定のルール
     * @return そのルールに対する検証結果（true = 合格, false = 不合格）
     */
    fun validateWithRule(password: String, rule: ValidationRule): Boolean {
        return rule.validate(password)
    }
}

/**
 * よく使われる標準的なバリデータの設定を提供するオブジェクト
 * 
 * objectキーワードは「シングルトン」を作ります。
 * つまり、このクラスのインスタンスは1つだけ存在します。
 * 
 * 初心者の方へ：
 * 毎回ルールを組み合わせるのは面倒なので、よく使うパターンを
 * ここに定義しておくと便利です。自分でカスタマイズもできます！
 */
object PasswordValidatorPresets {
    
    /**
     * 基本的なバリデータ
     * 
     * 一般的なWebサービスで使われるレベルの要件です。
     * 要件: 8文字以上、大文字・小文字・数字を含む
     * 
     * 使用例:
     * ```kotlin
     * val validator = PasswordValidatorPresets.basic()
     * val result = validator.validate("Password123")
     * ```
     */
    fun basic(): PasswordValidator = PasswordValidator(
        NotEmptyRule(),             // 空でないこと
        MinimumLengthRule(8),       // 8文字以上
        UppercaseRule(),            // 大文字を含む
        LowercaseRule(),            // 小文字を含む
        DigitRule()                 // 数字を含む
    )
    
    /**
     * 強固なバリデータ
     * 
     * セキュリティを重視する場合のより厳しい要件です。
     * 要件: 12文字以上128文字以下、大文字・小文字・数字・特殊文字を含む
     * 
     * 使用例:
     * ```kotlin
     * val validator = PasswordValidatorPresets.strong()
     * val result = validator.validate("MyS3cur3P@ssw0rd!")
     * ```
     */
    fun strong(): PasswordValidator = PasswordValidator(
        NotEmptyRule(),                // 空でないこと
        MinimumLengthRule(12),         // 12文字以上
        MaximumLengthRule(128),        // 128文字以下（システムの制限）
        UppercaseRule(),               // 大文字を含む
        LowercaseRule(),               // 小文字を含む
        DigitRule(),                   // 数字を含む
        SpecialCharacterRule()         // 特殊文字を含む
    )
    
    /**
     * カスタマイズ可能なビルダーパターン
     * 
     * ビルダーパターンは、オブジェクトを段階的に構築するデザインパターンです。
     * メソッドチェーンで読みやすくカスタマイズできます。
     * 
     * 【Builderパターンの利点】
     * - 必要なルールだけを選んで追加できる
     * - メソッドチェーンで直感的に書ける
     * - 順序を自由に変えられる
     * 
     * 使用例:
     * ```kotlin
     * val validator = PasswordValidatorPresets.Builder()
     *     .notEmpty()              // 空でないこと
     *     .minLength(10)           // 10文字以上
     *     .maxLength(50)           // 50文字以下
     *     .requireUppercase()      // 大文字必須
     *     .requireLowercase()      // 小文字必須
     *     .requireDigit()          // 数字必須
     *     .requireSpecialChar()    // 特殊文字必須
     *     .build()                 // バリデータを生成
     * 
     * val result = validator.validate("MyCustomP@ss123")
     * ```
     */
    class Builder {
        // private: 外部から直接アクセスできない
        // mutableListOf: 変更可能なリスト（ルールを追加していく）
        private val rules = mutableListOf<ValidationRule>()
        
        /**
         * 最小文字数ルールを追加
         * @param length 最小文字数
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun minLength(length: Int) = apply { rules.add(MinimumLengthRule(length)) }
        
        /**
         * 最大文字数ルールを追加
         * @param length 最大文字数
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun maxLength(length: Int) = apply { rules.add(MaximumLengthRule(length)) }
        
        /**
         * 大文字必須ルールを追加
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun requireUppercase() = apply { rules.add(UppercaseRule()) }
        
        /**
         * 小文字必須ルールを追加
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun requireLowercase() = apply { rules.add(LowercaseRule()) }
        
        /**
         * 数字必須ルールを追加
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun requireDigit() = apply { rules.add(DigitRule()) }
        
        /**
         * 特殊文字必須ルールを追加
         * @param chars カスタムの特殊文字セット（省略可能）
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun requireSpecialChar(chars: String = "!@#$%^&*()_+-=[]{}|;:,.<>?") = apply { 
            rules.add(SpecialCharacterRule(chars)) 
        }
        
        /**
         * 空でないルールを追加
         * @return このBuilderインスタンス（メソッドチェーン用）
         */
        fun notEmpty() = apply { rules.add(NotEmptyRule()) }
        
        /**
         * 設定したルールから PasswordValidator を生成
         * 
         * toTypedArray(): MutableListを配列に変換
         * *（スプレッド演算子）: 配列をvarargパラメータとして展開
         * 
         * @return 構築された PasswordValidator インスタンス
         */
        fun build(): PasswordValidator = PasswordValidator(*rules.toTypedArray())
    }
}
