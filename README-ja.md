# Compose Preview Screenshot Testing Generation Sample

日本語 | [English](README.md)

このリポジトリはGoogleが公式に提供する[Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing?hl=ja)
のテストケースをAST解析を用いて自動生成するプラグインを含むサンプルプロジェクトです。

## 検証方法

スクリーンショットの自動生成から実行まで検証するには以下の手順を追ってください:

1. `./gradlew generateScreenshotTest` を実行してスクリーンショットテストを生成します。
2. `./gradlew updateDebugScreenshotTest` を実行してスクリーンショットテストのリファレンス画像を生成します。
3. `MainScreen` コンポーネントの実装に一部変更を加えます。
4. `./gradlew validateDebugScreenshotTest` を実行しテスト結果を確認します（失敗するはず）。

テスト結果の例:
![](assets/example_test_result.png)

## 今後の展望

今後の動きについて、現時点では以下のことを予定しています。

- Gradle PluginとしてMaven Centralに公開（需要があれば）
- Kotlin Analysis APIへの移行

## トラブルシューティング

本リポジトリで提供しているスクリーンショットテスト自動生成プラグインは最小限の実装であり、いくつか実用上の問題を抱えています。
大抵のケースではAST解析により得られる情報を用いて回避可能なので、ご自身のプロジェクトに合わせて調整することを推奨します。

以下いくつかハマりどころをご紹介します。

### Preview関数内で参照している`private`宣言が解決できない

例えば、以下のように`PreviewParameterProvider`を`private`で定義してPreview関数内で使用している場合があるかもしれません。

```kotlin
@Composable
fun HogeView(checked: Boolean) {
    // ...
}

private class HogeViewPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

@Preview
@Composable
fun HogeViewPreview(
    @PreviewParameter(HogeViewPreviewParameterProvider::class) checked: Boolean,
) {
    HogeView(checked)
}
```

このような場合については、private関数もテストケースにコピーするようにプラグインを改造する必要があります。
`KtDeclaration`に対しては、`isPrivate()`という拡張関数が使えるので、そちらを使い`private`
な宣言をテストファイルに含めるようにしてください。

## クラス内部に記述されたPreview関数がエラーになる

Jetpack Composeは柔軟なフレームワークなので、以下のようにクラス内部にPreview関数を宣言することができます。

```kotlin
class Hoge {
    @Composable
    fun Content() {
        // ...
    }

    @Preview
    @Composable
    fun Preview() {
        Content()
    }
}
```

現状、本リポジトリで提供するプラグインは上記のケースを想定していません。そのため、ビュー実装をクラス外部に出して運用することを推奨します。
また、既に上記のような実装がある場合は、ASTノードが持っている`isTopLevel`などの情報を用いて対象から除外することが可能です。

## importがコンパイルエラーになる

詳細は追っていませんが、`import kotlinx.percelize.Parcelize`が参照エラーを起こしたことがありました。そのような場合は
`importDirectives.forEach` 周辺のロジックをいじって一部の`import`を除外することを推奨します。

## 特定Previewをスクリーンショットテスト対象から除外したい

特定のPreviewをスクリーンショットテストの対象から除外する手段が現在は実装されていません。
適当なアノテーション(`@IgnoreScreenshotTest`など)をプロジェクト内で決めて、そのアノテーションを含む場合はスキップするなどの変更を加えてください。
