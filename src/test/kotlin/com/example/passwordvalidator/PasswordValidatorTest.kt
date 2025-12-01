package com.example.passwordvalidator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * PasswordValidatorのテストクラス
 * 
 * 統合テストとして、複数のルールを組み合わせた検証をテストします。
 * パラメータ化テストなども使って、実践的なテストの書き方を学びます。
 */
@DisplayName("PasswordValidator のテスト")
class PasswordValidatorTest {
    
    @Nested
    @DisplayName("基本的な動作のテスト")
    inner class BasicValidationTest {
        
        @Test
        @DisplayName("全てのルールを満たす場合は成功する")
        fun `should return success when password meets all rules`() {
            val validator = PasswordValidator(
                MinimumLengthRule(8),
                UppercaseRule(),
                LowercaseRule(),
                DigitRule()
            )
            
            val result = validator.validate("Password123")
            
            assertTrue(result.isValid)
            assertThat(result.errors).isEmpty()
        }
        
        @Test
        @DisplayName("ルールを満たさない場合は失敗する")
        fun `should return failure when password does not meet rules`() {
            val validator = PasswordValidator(
                MinimumLengthRule(8),
                UppercaseRule(),
                DigitRule()
            )
            
            val result = validator.validate("password")  // 大文字なし、数字なし
            
            assertFalse(result.isValid)
            assertThat(result.errors).hasSize(2)
        }
        
        @Test
        @DisplayName("エラーメッセージが正しく返される")
        fun `should return correct error messages`() {
            val validator = PasswordValidator(
                MinimumLengthRule(8),
                UppercaseRule()
            )
            
            val result = validator.validate("pass")  // 短い、大文字なし
            
            assertFalse(result.isValid)
            assertThat(result.errors).contains("パスワードは8文字以上である必要があります")
            assertThat(result.errors).contains("パスワードには大文字(A-Z)を含む必要があります")
        }
        
        @Test
        @DisplayName("ルールが指定されていない場合は常に成功する")
        fun `should always succeed when no rules are specified`() {
            val validator = PasswordValidator()
            
            val result1 = validator.validate("")
            val result2 = validator.validate("any password")
            
            assertTrue(result1.isValid)
            assertTrue(result2.isValid)
        }
    }
    
    @Nested
    @DisplayName("複雑なパスワードのテスト")
    inner class ComplexPasswordTest {
        
        private val validator = PasswordValidator(
            NotEmptyRule(),
            MinimumLengthRule(12),
            MaximumLengthRule(128),
            UppercaseRule(),
            LowercaseRule(),
            DigitRule(),
            SpecialCharacterRule()
        )
        
        @Test
        @DisplayName("強固なパスワードは全ての要件を満たす")
        fun `should accept strong password`() {
            val result = validator.validate("MySecureP@ssw0rd!")
            
            assertTrue(result.isValid)
            assertThat(result.errors).isEmpty()
        }
        
        @Test
        @DisplayName("弱いパスワードは複数のエラーを返す")
        fun `should reject weak password with multiple errors`() {
            val result = validator.validate("weak")
            
            assertFalse(result.isValid)
            assertThat(result.errors.size).isGreaterThan(1)
        }
        
        @ParameterizedTest(name = "無効なパスワード: {0}")
        @ValueSource(strings = [
            "short1!A",           // 短すぎる
            "NoDigitsHere!",      // 数字なし
            "nouppercasehere1!",  // 大文字なし
            "NOLOWERCASEHERE1!",  // 小文字なし
            "NoSpecialChar123"    // 特殊文字なし
        ])
        @DisplayName("様々な無効なパスワードを拒否する")
        fun `should reject various invalid passwords`(password: String) {
            val result = validator.validate(password)
            
            assertFalse(result.isValid)
            assertThat(result.errors).isNotEmpty()
        }
        
        @ParameterizedTest(name = "有効なパスワード: {0}")
        @ValueSource(strings = [
            "MySecureP@ssw0rd!",
            "Anoth3r!ValidPass",
            "C0mpl3x#Passw0rd",
            "Str0ng!P@ssw0rd#2024"
        ])
        @DisplayName("様々な有効なパスワードを受け入れる")
        fun `should accept various valid passwords`(password: String) {
            val result = validator.validate(password)
            
            assertTrue(result.isValid)
            assertThat(result.errors).isEmpty()
        }
    }
    
    @Nested
    @DisplayName("プリセット設定のテスト")
    inner class PresetValidatorTest {
        
        @Test
        @DisplayName("基本的なプリセットが正しく機能する")
        fun `basic preset should work correctly`() {
            val validator = PasswordValidatorPresets.basic()
            
            assertTrue(validator.validate("Password123").isValid)
            assertFalse(validator.validate("pass").isValid)
        }
        
        @Test
        @DisplayName("強固なプリセットが正しく機能する")
        fun `strong preset should work correctly`() {
            val validator = PasswordValidatorPresets.strong()
            
            assertTrue(validator.validate("MySecureP@ssw0rd!").isValid)
            assertFalse(validator.validate("Password123").isValid)  // 特殊文字なし
        }
        
        @Test
        @DisplayName("ビルダーパターンが正しく機能する")
        fun `builder pattern should work correctly`() {
            val validator = PasswordValidatorPresets.Builder()
                .notEmpty()
                .minLength(10)
                .requireUppercase()
                .requireLowercase()
                .requireDigit()
                .build()
            
            assertTrue(validator.validate("Password123").isValid)
            assertFalse(validator.validate("Pass123").isValid)  // 短すぎる
        }
    }
    
    @Nested
    @DisplayName("エッジケースのテスト")
    inner class EdgeCaseTest {
        
        @Test
        @DisplayName("空文字列を正しく処理する")
        fun `should handle empty string`() {
            val validator = PasswordValidator(
                NotEmptyRule(),
                MinimumLengthRule(1)
            )
            
            val result = validator.validate("")
            
            assertFalse(result.isValid)
            assertThat(result.errors).hasSizeGreaterThan(0)
        }
        
        @Test
        @DisplayName("非常に長いパスワードを処理できる")
        fun `should handle very long password`() {
            val validator = PasswordValidator(
                MaximumLengthRule(100)
            )
            
            val longPassword = "a".repeat(150)
            val result = validator.validate(longPassword)
            
            assertFalse(result.isValid)
        }
        
        @Test
        @DisplayName("Unicode文字を含むパスワードを処理できる")
        fun `should handle unicode characters`() {
            val validator = PasswordValidator(
                MinimumLengthRule(5)
            )
            
            val unicodePassword = "パスワード123"
            val result = validator.validate(unicodePassword)
            
            assertTrue(result.isValid)
        }
        
        @Test
        @DisplayName("スペースのみのパスワードを適切に処理する")
        fun `should handle whitespace-only password`() {
            val validator = PasswordValidator(
                UppercaseRule(),
                LowercaseRule(),
                DigitRule()
            )
            
            val result = validator.validate("     ")
            
            assertFalse(result.isValid)
        }
    }
    
    @Nested
    @DisplayName("個別ルール検証のテスト")
    inner class SingleRuleValidationTest {
        
        @Test
        @DisplayName("特定のルールだけをテストできる")
        fun `should be able to test specific rule`() {
            val validator = PasswordValidator(
                MinimumLengthRule(8),
                UppercaseRule()
            )
            
            val rule = MinimumLengthRule(8)
            assertTrue(validator.validateWithRule("12345678", rule))
            assertFalse(validator.validateWithRule("1234", rule))
        }
    }
}
