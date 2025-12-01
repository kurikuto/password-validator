package com.example.passwordvalidator

/**
 * パスワードバリデーションのルールを定義するインターフェース
 * 
 * インターフェースは「契約」のようなもので、これを実装するクラスは
 * 必ず description プロパティと validate() メソッドを持つ必要があります。
 * 
 * 新しいルールを追加する場合は、このインターフェースを実装します。
 * 
 * 実装例:
 * ```
 * class MyCustomRule : ValidationRule {
 *     override val description = "独自のルール説明"
 *     override fun validate(password: String): Boolean {
 *         // 検証ロジック
 *         return true
 *     }
 * }
 * ```
 */
interface ValidationRule {
    /**
     * ルールの説明（エラーメッセージに使用）
     * 
     * ユーザーにわかりやすい日本語でルールを説明します
     */
    val description: String
    
    /**
     * パスワードがこのルールを満たしているかを検証
     * 
     * @param password 検証対象のパスワード文字列
     * @return ルールを満たしている場合はtrue、満たしていない場合はfalse
     */
    fun validate(password: String): Boolean
}

/**
 * 最小文字数を検証するルール
 * 
 * パスワードが指定された最小文字数以上であることを確認します。
 * 
 * 使用例: MinimumLengthRule(8) // 8文字以上必要
 * 
 * @param minLength 最小文字数（デフォルト値なし、必ず指定が必要）
 */
class MinimumLengthRule(private val minLength: Int) : ValidationRule {
    // getterを使って動的にメッセージを生成（minLengthの値を含める）
    override val description: String
        get() = "パスワードは${minLength}文字以上である必要があります"
    
    // password.length は文字列の長さを返す
    // >= は「以上」を意味する比較演算子
    override fun validate(password: String): Boolean = password.length >= minLength
}

/**
 * 大文字（A-Z）を含むことを検証するルール
 * 
 * パスワードに少なくとも1文字の英大文字が含まれているかチェックします。
 */
class UppercaseRule : ValidationRule {
    override val description: String = "パスワードには大文字(A-Z)を含む必要があります"
    
    // any { } は「条件を満たす要素が1つでもあればtrue」を返す
    // it.isUpperCase() は文字が大文字かどうかを判定
    override fun validate(password: String): Boolean = password.any { it.isUpperCase() }
}

/**
 * 小文字（a-z）を含むことを検証するルール
 * 
 * パスワードに少なくとも1文字の英小文字が含まれているかチェックします。
 */
class LowercaseRule : ValidationRule {
    override val description: String = "パスワードには小文字(a-z)を含む必要があります"
    
    // any { } で小文字が含まれているかチェック
    override fun validate(password: String): Boolean = password.any { it.isLowerCase() }
}

/**
 * 数字（0-9）を含むことを検証するルール
 * 
 * パスワードに少なくとも1文字の数字が含まれているかチェックします。
 */
class DigitRule : ValidationRule {
    override val description: String = "パスワードには数字(0-9)を含む必要があります"
    
    // it.isDigit() は文字が数字かどうかを判定
    override fun validate(password: String): Boolean = password.any { it.isDigit() }
}

/**
 * 特殊文字を含むことを検証するルール
 * 
 * パスワードに指定された特殊文字が含まれているかチェックします。
 * デフォルトでは一般的な特殊文字セットを使用します。
 * 
 * 使用例:
 * - SpecialCharacterRule() // デフォルトの特殊文字セットを使用
 * - SpecialCharacterRule("!@#$") // カスタムの特殊文字セット
 * 
 * @param specialChars 許可する特殊文字の文字列（デフォルト値あり）
 */
class SpecialCharacterRule(
    private val specialChars: String = "!@#$%^&*()_+-=[]{}|;:,.<>?"
) : ValidationRule {
    override val description: String = "パスワードには特殊文字($specialChars)を含む必要があります"
    
    // 'in' 演算子は、文字が文字列に含まれているかをチェック
    override fun validate(password: String): Boolean = password.any { it in specialChars }
}

/**
 * 最大文字数を検証するルール
 * 
 * パスワードが指定された最大文字数以下であることを確認します。
 * 長すぎるパスワードはシステムによっては問題になることがあります。
 * 
 * @param maxLength 最大文字数
 */
class MaximumLengthRule(private val maxLength: Int) : ValidationRule {
    override val description: String
        get() = "パスワードは${maxLength}文字以下である必要があります"
    
    // <= は「以下」を意味する比較演算子
    override fun validate(password: String): Boolean = password.length <= maxLength
}

/**
 * 空文字でないことを検証するルール
 * 
 * パスワードが空文字列（""）でないことを確認します。
 * 基本的なチェックですが、重要なルールです。
 */
class NotEmptyRule : ValidationRule {
    override val description: String = "パスワードは空であってはいけません"
    
    // isNotEmpty() は文字列が空でない場合にtrueを返す
    override fun validate(password: String): Boolean = password.isNotEmpty()
}
