# セットアップガイド

このガイドでは、プロジェクトをセットアップして実行する方法を説明します。

## 必要な環境

- **JDK 23 以上**: Java の実行環境（Java 23 推奨）
  - 確認方法: `java -version`
  - インストール: [Adoptium](https://adoptium.net/)から入手
  - **注意**: Kotlin 2.1.21 は現在 Java 23 まで対応。Java 25 対応は将来のバージョンで追加予定
- **Gradle** (オプション): ビルドツール
  - Gradle Wrapper を使うので、事前にインストールしなくても大丈夫です
- **IDE**: 以下のいずれか
  - **VS Code** (推奨): 軽量で拡張性が高い
  - **IntelliJ IDEA**: Kotlin 開発に特化した統合環境

## 初回セットアップ

### ステップ 1: Gradle Wrapper の設定

プロジェクトのルートディレクトリで以下を実行:

```powershell
# Gradleがインストールされている場合
gradle wrapper --gradle-version 9.2

# または、手動でwrapperをダウンロード
# https://services.gradle.org/distributions/gradle-9.2-bin.zip
```

### ステップ 2: IDE で開く

#### VS Code の場合（推奨）

1. VS Code を起動
2. 以下の拡張機能をインストール:
   - **Kotlin** (by mathiasfrohlich または fwcd)
   - **Language Support for Java(TM) by Red Hat**
   - **Gradle for Java** (by Microsoft)
   - **Test Runner for Java** (テスト実行用)
3. `File` → `Open Folder` → このプロジェクトフォルダを選択
4. ターミナルで `.\gradlew.bat build` を実行して依存関係をダウンロード

**VS Code での実行方法:**

- `Example.kt` を開いて、`main` 関数の上に表示される `Run | Debug` をクリック
- テストファイルを開いて、各テストの上に表示される `Run Test` をクリック

#### IntelliJ IDEA の場合

1. IntelliJ IDEA を起動
2. `File` → `Open` → このプロジェクトフォルダを選択
3. 自動的に Gradle プロジェクトとして認識され、依存関係がダウンロードされます
4. Kotlin プラグインが自動的に有効化されます

### ステップ 3: プロジェクトのビルド

```powershell
# プロジェクトをビルド
.\gradlew.bat build

# テストのみ実行
.\gradlew.bat test

# クリーンビルド
.\gradlew.bat clean build
```

## 使い方

### 1. サンプルプログラムを実行

`Example.kt`を開いて、`main`関数の横にある ▶ ボタンをクリックするか:

```powershell
.\gradlew.bat run
```

### 2. テストを実行

**コマンドラインの場合:**

```powershell
# 全てのテストを実行
.\gradlew.bat test

# 詳細な出力を見る
.\gradlew.bat test --info

# 特定のテストだけ実行
.\gradlew.bat test --tests "ValidationRuleTest"
```

**VS Code の場合:**

1. テストファイル（例: `ValidationRuleTest.kt`）を開く
2. 各テストメソッドの上に表示される `Run Test` をクリック
3. またはファイル全体のテストを実行するには、クラス名の上の `Run Test` をクリック
4. テスト結果がサイドバーに表示されます

**IntelliJ IDEA の場合:**

1. `src/test/kotlin`フォルダを右クリック
2. `Run 'All Tests'`を選択
3. テスト結果が GUI で表示されます

## プロジェクト構造

```
password-validator/
├── src/
│   ├── main/kotlin/              # 実装コード
│   │   ├── ValidationResult.kt   # バリデーション結果
│   │   ├── ValidationRule.kt     # ルール定義
│   │   ├── PasswordValidator.kt  # メインクラス
│   │   └── Example.kt            # 使用例（実行可能）
│   │
│   └── test/kotlin/              # テストコード
│       ├── ValidationRuleTest.kt
│       └── PasswordValidatorTest.kt
│
├── build.gradle.kts              # ビルド設定
├── settings.gradle.kts           # プロジェクト設定
├── gradle/wrapper/               # Gradle Wrapper
└── README.md                     # プロジェクト説明
```

## トラブルシューティング

### VS Code で Kotlin が認識されない

1. Kotlin 拡張機能がインストールされているか確認
2. Java Extension Pack もインストールすることを推奨
3. `Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace`
4. VS Code を再起動

### Kotlin 2.1.21 が見つからない

```powershell
# Gradleのキャッシュをクリア
.\gradlew.bat clean --refresh-dependencies
```

### テストが実行されない

```powershell
# ビルドをクリーンしてから再実行
.\gradlew.bat clean test
```

### IntelliJ IDEA でエラーが出る

1. `File` → `Invalidate Caches / Restart`
2. `File` → `Reload All Gradle Projects`

### VS Code でテストが表示されない

1. Test Runner for Java 拡張機能がインストールされているか確認
2. `.\gradlew.bat test` を一度実行してテストをビルド
3. Testing ビュー（フラスコアイコン）を開いてリフレッシュ

### Gradle Wrapper がない

もし gradlew ファイルが実行できない場合:

1. `gradle/wrapper/gradle-wrapper.properties`を確認
2. 手動で`gradle-wrapper.jar`をダウンロード: https://gradle.org/releases/

## 次のステップ

1. `Example.kt`を実行して動作を確認
2. `README.md`を読んで全体像を理解
3. `TDD_TUTORIAL.md`で実践的な TDD を学習
4. 自分で新しいルールを追加してみる

## 学習の進め方

### 初心者向け

1. **コードを読む**: まず`ValidationResult.kt`から読み始める
2. **実行してみる**: `Example.kt`を開いて、VS Code なら `Run` リンクをクリック
3. **テストを見る**: `ValidationRuleTest.kt`でテストの書き方を学ぶ
4. **小さな変更**: 既存のルールをコピーして改造してみる

### 中級者向け

1. **新しいルールを追加**: TDD_TUTORIAL.md の練習問題に挑戦
2. **テストを書く**: 新機能を追加する前にテストを書く（TDD）
3. **リファクタリング**: コードをより良く改善する

### 上級者向け

1. **ROADMAP を参照**: フェーズ 2 以降の拡張に挑戦
2. **Kotlin Multiplatform**: 共通コードをマルチプラットフォーム化
3. **実際のアプリ**: パスワードマネージャーを作成

## IDE 比較

| 機能             | VS Code          | IntelliJ IDEA   |
| ---------------- | ---------------- | --------------- |
| 起動速度         | ⚡ 高速          | 🐢 やや遅い     |
| メモリ使用量     | 💚 少ない        | 🔴 多い         |
| Kotlin サポート  | ✅ 良好          | ⭐ 最高         |
| 初心者向け       | ⭐⭐⭐           | ⭐⭐            |
| リファクタリング | ✅ 基本的        | ⭐ 強力         |
| 無料版           | ✅ 完全無料      | ✅ Community 版 |
| おすすめ         | 軽量開発、学習用 | 本格的開発      |

**結論**: このプロジェクトは学習用なので、**VS Code で十分**です！

## 質問や問題があれば

- GitHub で Issue を作成
- コミュニティに質問
- ドキュメントを再度確認

Happy Coding! 🚀
