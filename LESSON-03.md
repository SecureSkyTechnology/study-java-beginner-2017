# レッスン3 : Javaアプリのリリース体験

本レッスンでは実際にGitHubにリポジトリを作成し、Javaアプリケーションのビルドとリリースを体験します。
これにより以下のスキルを身につけることができます。
- GitHubでのリポジトリ作成、コードのcommit、`.gitignore` の調整、バイナリビルドのリリース
- [Netty](http://netty.io/) を使ったHTTPサービスの開発
- [LittleProxy](https://github.com/adamfisk/LittleProxy) を使ったローカルHTTPプロキシの開発

## 実習3-1 : GitHubのリポジトリを作成

1. 自分のGitHubアカウントで、新しいGitHub公開リポジトリを作成する。
   - リポジトリ名は `sample-httpserver` または `sample-httpproxy` にしておく。
   - `.gitignore` は Java 用を選んでおく。
   - ライセンスはMITを選んでおく。
1. 作成したリポジトリをEclipseのEGitでcloneする。

## 実習3-2 : サンプルのHTTPサーバを作成・リリース

- [Netty](http://netty.io/) を使うとnon-blockingなネットワーク通信サービスの開発が容易となる。
- 公式ドキュメントやソースコードのクロスリファレンスで、豊富なサンプルコードを参照できる。
  - http://netty.io/wiki/index.html
- 今回は指定されたディレクトリ上のファイルを静的に公開するシンプルなHTTPサーバのサンプルコードを用意した。
  - これを適宜改造してオリジナル機能を追加し、練習台とする。

サンプルコードの改造：
1. `exercise03-samples/sample-httpserver` のファイル一式を、実習3-1で作成したリポジトリにコピーする。
1. 本リポジトリの `.gitignore` をリポジトリ直下にコピーする。(既存の `.gitignore` を上書きする)
1. `mvnw package` でビルドを確認できたら、Eclipse上にインポートする。
1. `SampleHttpServerController.java` を適宜編集するなどして、オリジナルの機能を加えてみる。
1. `Main` クラスを実行し、実際に動かして遊んでみる。

補足 : `.gitignore` についてはGitHubリポジトリ作成時に生成されるJava用ではIDE系が生成するファイル・ディレクトリに未対応という弱点がある(2017-12時点)。
本リポジトリの `.gitignore` は、GitHubリポジトリが生成したものに加え、SpringBoot が自動生成したIDE系のファイル・ディレクトリの除外リストを追加し、使いやすさを改善したものになる。

リリース：
1. Eclipse上のEGitから、tagを打ってリモートにpushする。
1. GitHub上から、tagを元にリリースを作成し、 `mvn package` で生成されたjarファイルを登録し、リリースする。

## 実習3-3 : サンプルのローカルHTTPプロキシを作成・リリース

- NettyをベースとしたHTTPプロキシライブラリ [LittleProxy](https://github.com/adamfisk/LittleProxy) を活用する。
- HTTPSのMITMは [MITM with LittleProxy](https://github.com/lightbody/browsermob-proxy/tree/master/mitm) を使う。
- 今回は指定されたディレクトリにHTTPリクエスト/レスポンスをファイルで保存するシンプルなローカルHTTPプロキシのサンプルコードを用意した。
  - これを適宜改造してオリジナル機能を追加し、練習台とする。

リポジトリの作成：
1. 実習3-1の時と同様にリポジトリを作成する。

サンプルコードの改造：
1. `exercise03-samples/sample-httpproxy` のファイル一式を作成したリポジトリにコピーする。
1. 本リポジトリの `.gitignore` をリポジトリ直下にコピーする。(既存の `.gitignore` を上書きする)
1. `mvnw package` でビルドを確認できたら、Eclipse上にインポートする。
1. `SampleHttpProxyFiltersImpl.java` を適宜編集するなどして、オリジナルの機能を加えてみる。
1. `Main` クラスを実行し、実際に動かして遊んでみる。

リリース：
1. Eclipse上のEGitから、tagを打ってリモートにpushする。
1. GitHub上から、tagを元にリリースを作成し、 `mvn package` で生成されたjarファイルを登録し、リリースする。

------

以上でレッスン3を終わる。

これで、駆け足だったが「ダブルクリックで実行できるjarファイル」という使いやすい形でJavaアプリケーションをビルドし、GitHub上で公開するためのノウハウを学び終えたことになる。

これらのレッスンで紹介してきたノウハウは、アプリケーションの機能とは直接関係しないものの、効率的な開発や最終的な実行ファイルを配布する際の有効な一手段となるだろう。

もちろん本資料が紹介しているやり方が唯一の正解、というわけではない。
それぞれの作りたい機能・アプリケーションの形態・配布方式に応じて調査・検証が必要となる。
その際に本資料の内容がわずかでも参考になれば、幸いである。
