package com.example.passwordvalidator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * ValidationRuleの各実装をテストするクラス
 * 
 * 【テストの基本構造】
 * - @Test: このメソッドがテストであることを示す
 * - @DisplayName: テストの説明（日本語で書ける）
 * - @Nested: テストをグループ化（inner classを使う）
 * - assertTrue / assertFalse: 結果が正しいか確認
 * 
 * 【TDDで重要なテストパターン】
 * 1. 正常ケース: ルールを満たす入力でテスト
 * 2. 異常ケース: ルールを満たさない入力でテスト
 * 3. エッジケース: 境界値や特殊な入力でテスト
 */
@DisplayName("ValidationRule のテスト")
class ValidationRuleTest {
    
    @Nested
    @DisplayName("MinimumLengthRule のテスト")
    inner class MinimumLengthRuleTest {
        
        @Test
        @DisplayName("最小文字数を満たす場合はtrueを返す")
        fun `should return true when password meets minimum length`() {
            // Given（準備）: 8文字以上が必要なルールを作成
            val rule = MinimumLengthRule(8)
            
            // When & Then（実行と検証）: 8文字以上のパスワードは合格
            assertTrue(rule.validate("12345678"))   // ちょうど8文字
            assertTrue(rule.validate("123456789"))  // 9文字（8文字より多い）
        }
        
        @Test
        @DisplayName("最小文字数を満たさない場合はfalseを返す")
        fun `should return false when password is shorter than minimum length`() {
            // Given: 8文字以上が必要なルールを作成
            val rule = MinimumLengthRule(8)
            
            // When & Then: 8文字未満のパスワードは不合格
            assertFalse(rule.validate("1234567"))  // 7文字（1文字足りない）
            assertFalse(rule.validate(""))         // 0文字（空文字列）
        }
        
        @Test
        @DisplayName("境界値テスト：ちょうど最小文字数の場合")
        fun `should return true when password is exactly minimum length`() {
            // Given: 8文字以上が必要なルールを作成
            val rule = MinimumLengthRule(8)
            
            // When & Then: ちょうど8文字は合格（境界値のテスト）
            assertTrue(rule.validate("12345678"))
        }
        
        @Test
        @DisplayName("説明文が正しく設定されている")
        fun `should have correct description`() {
            // Given: 8文字以上が必要なルールを作成
            val rule = MinimumLengthRule(8)
            
            // When & Then: 説明文に「8文字以上」が含まれている
            // assertThat().contains(): 文字列に特定の文字が含まれているかチェック
            assertThat(rule.description).contains("8文字以上")
        }
    }
    
    @Nested
    @DisplayName("UppercaseRule のテスト")
    inner class UppercaseRuleTest {
        
        @Test
        @DisplayName("大文字を含む場合はtrueを返す")
        fun `should return true when password contains uppercase`() {
            val rule = UppercaseRule()
            
            assertTrue(rule.validate("Password"))
            assertTrue(rule.validate("pAssword"))
            assertTrue(rule.validate("A"))
        }
        
        @Test
        @DisplayName("大文字を含まない場合はfalseを返す")
        fun `should return false when password contains no uppercase`() {
            val rule = UppercaseRule()
            
            assertFalse(rule.validate("password"))
            assertFalse(rule.validate("123456"))
            assertFalse(rule.validate(""))
        }
        
        @Test
        @DisplayName("複数の大文字を含む場合もtrueを返す")
        fun `should return true when password contains multiple uppercase`() {
            val rule = UppercaseRule()
            
            assertTrue(rule.validate("PASSWORD"))
            assertTrue(rule.validate("PaSsWoRd"))
        }
    }
    
    @Nested
    @DisplayName("LowercaseRule のテスト")
    inner class LowercaseRuleTest {
        
        @Test
        @DisplayName("小文字を含む場合はtrueを返す")
        fun `should return true when password contains lowercase`() {
            val rule = LowercaseRule()
            
            assertTrue(rule.validate("password"))
            assertTrue(rule.validate("Password"))
            assertTrue(rule.validate("a"))
        }
        
        @Test
        @DisplayName("小文字を含まない場合はfalseを返す")
        fun `should return false when password contains no lowercase`() {
            val rule = LowercaseRule()
            
            assertFalse(rule.validate("PASSWORD"))
            assertFalse(rule.validate("123456"))
            assertFalse(rule.validate(""))
        }
    }
    
    @Nested
    @DisplayName("DigitRule のテスト")
    inner class DigitRuleTest {
        
        @Test
        @DisplayName("数字を含む場合はtrueを返す")
        fun `should return true when password contains digit`() {
            val rule = DigitRule()
            
            assertTrue(rule.validate("password1"))
            assertTrue(rule.validate("1password"))
            assertTrue(rule.validate("pass1word"))
            assertTrue(rule.validate("123"))
        }
        
        @Test
        @DisplayName("数字を含まない場合はfalseを返す")
        fun `should return false when password contains no digit`() {
            val rule = DigitRule()
            
            assertFalse(rule.validate("password"))
            assertFalse(rule.validate("PASSWORD"))
            assertFalse(rule.validate(""))
        }
        
        @Test
        @DisplayName("複数の数字を含む場合もtrueを返す")
        fun `should return true when password contains multiple digits`() {
            val rule = DigitRule()
            
            assertTrue(rule.validate("123456789"))
            assertTrue(rule.validate("pass123word456"))
        }
    }
    
    @Nested
    @DisplayName("SpecialCharacterRule のテスト")
    inner class SpecialCharacterRuleTest {
        
        @Test
        @DisplayName("特殊文字を含む場合はtrueを返す")
        fun `should return true when password contains special character`() {
            val rule = SpecialCharacterRule()
            
            assertTrue(rule.validate("password!"))
            assertTrue(rule.validate("pass@word"))
            assertTrue(rule.validate("!@#$%"))
        }
        
        @Test
        @DisplayName("特殊文字を含まない場合はfalseを返す")
        fun `should return false when password contains no special character`() {
            val rule = SpecialCharacterRule()
            
            assertFalse(rule.validate("password"))
            assertFalse(rule.validate("Password123"))
            assertFalse(rule.validate(""))
        }
        
        @Test
        @DisplayName("カスタム特殊文字セットが機能する")
        fun `should work with custom special character set`() {
            val rule = SpecialCharacterRule("@#$")
            
            assertTrue(rule.validate("password@"))
            assertTrue(rule.validate("password#"))
            assertFalse(rule.validate("password!"))
        }
    }
    
    @Nested
    @DisplayName("MaximumLengthRule のテスト")
    inner class MaximumLengthRuleTest {
        
        @Test
        @DisplayName("最大文字数以下の場合はtrueを返す")
        fun `should return true when password is within maximum length`() {
            val rule = MaximumLengthRule(20)
            
            assertTrue(rule.validate("12345678"))
            assertTrue(rule.validate("12345678901234567890"))
        }
        
        @Test
        @DisplayName("最大文字数を超える場合はfalseを返す")
        fun `should return false when password exceeds maximum length`() {
            val rule = MaximumLengthRule(20)
            
            assertFalse(rule.validate("123456789012345678901"))
        }
        
        @Test
        @DisplayName("境界値テスト：ちょうど最大文字数の場合")
        fun `should return true when password is exactly maximum length`() {
            val rule = MaximumLengthRule(10)
            
            assertTrue(rule.validate("1234567890"))
        }
    }
    
    @Nested
    @DisplayName("NotEmptyRule のテスト")
    inner class NotEmptyRuleTest {
        
        @Test
        @DisplayName("空でない場合はtrueを返す")
        fun `should return true when password is not empty`() {
            val rule = NotEmptyRule()
            
            assertTrue(rule.validate("a"))
            assertTrue(rule.validate("password"))
            assertTrue(rule.validate(" "))  // スペースは空でないとみなす
        }
        
        @Test
        @DisplayName("空文字の場合はfalseを返す")
        fun `should return false when password is empty`() {
            val rule = NotEmptyRule()
            
            assertFalse(rule.validate(""))
        }
    }
}
