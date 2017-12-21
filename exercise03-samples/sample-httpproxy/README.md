# sample-httpproxy

GUIでポート番号を指定できる、Netty + [LittleProxy](https://github.com/adamfisk/LittleProxy) + [MITM with LittleProxy](https://github.com/lightbody/browsermob-proxy/tree/master/mitm) を使ったローカルHTTPプロキシのサンプルです。

HTTPリクエスト/レスポンスをそれぞれ ".req" / ".res" という拡張子で保存します。保存するディレクトリをGUI上で選択できます。

## requirement

* Java8

## 開発環境

* JDK >= 1.8.0_92
* Eclipse >= 4.5.2 (Mars.2 Release), "Eclipse IDE for Java EE Developers" パッケージを使用
* Maven >= 3 (maven-wrapperにて自動的にDLしてくれる)
* ソースコードやテキストファイル全般の文字コードはUTF-8を使用

## ビルドと実行

```
cd sample-httpproxy/

ビルド:
mvnw package

jarファイルから実行:
java -jar target/sample-httpproxy-(version).jar

Mavenプロジェクトから直接実行:
mvnw exec:java
```

## Eclipseプロジェクト用の設定

https://github.com/SecureSkyTechnology/howto-eclipse-setup の `setup-type1` を使用。README.mdで以下を参照のこと:

* Ecipseのインストール
* Clean Up/Formatter 設定
* GitでcloneしたMavenプロジェクトのインポート 
