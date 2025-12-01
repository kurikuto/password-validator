package com.example.passwordvalidator

/**
 * パスワードのバリデーション結果を表すデータクラス
 * 
 * このクラスは、パスワードの検証結果を保持します。
 * データクラスは、データを保持するためのシンプルなクラスで、
 * equals(), hashCode(), toString() などのメソッドが自動生成されます。
 * 
 * @property isValid バリデーションが成功したかどうか (true = 成功, false = 失敗)
 * @property errors 各ルールのバリデーション失敗メッセージのリスト（空リストは成功を意味します）
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
) {
    companion object {
        /**
         * バリデーション成功を表す結果を生成
         * 
         * 使用例: ValidationResult.success()
         * 
         * @return エラーがないValidationResultオブジェクト
         */
        fun success(): ValidationResult = ValidationResult(isValid = true, errors = emptyList())
        
        /**
         * バリデーション失敗を表す結果を生成
         * 
         * 使用例: ValidationResult.failure(listOf("エラー1", "エラー2"))
         * 
         * @param errors エラーメッセージのリスト
         * @return エラーを含むValidationResultオブジェクト
         */
        fun failure(errors: List<String>): ValidationResult = 
            ValidationResult(isValid = false, errors = errors)
    }
}
