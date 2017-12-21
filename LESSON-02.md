# レッスン2 : Eclipseの基礎とMaven,Git統合

本レッスンではEclipseによるJavaプログラミングの基礎と、MavenプロジェクトやGitとの統合方法を学びます。
これにより以下のスキルを身につけることができます。
- EclipseでJavaアプリケーションを開発する。
- EclipseでMavenプロジェクトを管理・ビルドする。
- EclipseでGitHubリポジトリを操作する。
- EclipseでGitHubリポジトリ上のMavenプロジェクトをインポートする。

## 実習2-1 : EclipseのインストールとHello, World

本資料では、Eclipse 4.5.2 以上の「Eclipse IDE for Java EE Developers」パッケージを以下よりDLしてインストールする。
- 4.5 : http://www.eclipse.org/downloads/packages/release/Mars/2
- 4.6 : https://www.eclipse.org/downloads/packages/release/neon/3
- 4.7 : http://www.eclipse.org/downloads/eclipse-packages/

※上記のいずれかのバージョンで構わない。筆者個人は、枯れた 4.5.2 を使っているが、本資料の範囲であればそれ以上のバージョンでも問題ないと思われる。

**WindowsでEclipseのパッケージ(.zipファイル)を展開する際のコツ**
- エクスプローラの展開機能を使う。
- `C:\work` など短いフォルダ名の下に展開する。
- (Eclipseのファイルツリーでは、内蔵プラグインなどで長いフォルダ名が含まれる。そのため、長いフォルダ名の下に展開しようとすると、Windowsのファイル名/フォルダ名の長さ制限に引っかかってしまい展開エラーとなる場合が出てくるため、それを回避するために上記のようなコツが必要となる。)

### EclipseでJavaのHello, World

- File -> New -> Java Project
- (書籍や検索でカバーできる内容のため、本資料では詳細省略)

### コラム : eclipse.ini の設定

- Eclipseのインストール先フォルダを見てみると、実行ファイルと同じフォルダに `eclipse.ini` というファイルがある。
- これを編集することで、Eclipse自体の実行に使うVMを変更したり(特定バージョンのJDKにするなど)、JVMが使うJavaのヒープメモリサイズなどを変更することができる。
- 詳細 : https://wiki.eclipse.org/Eclipse.ini 参照のこと。
- 筆者の個人的な好みだが、 `-vm` で明示的にJDKを使うように設定している。これをすると、Eclipseが確実に、JDKのビルドパスを自動認識してくれるため。

## 実習2-2 : EclipseのJavaプロジェクトでの外部ライブラリの利用

1. 作成したJava Projectのフォルダに jar ファイルを配置
1. プロジェクトのプロパティより、"Java Build Path" -> "Libraries" タブ -> "Add JARs" で追加

(書籍や検索でカバーできる内容のため、本資料では詳細省略)

実習1-3 でやったような、guava を使ったHello, WorldをEclipseでどう実現するか試してみよう。

## 実習2-3 : EclipseでのMavenプロジェクトのインポートと管理

EclipseでMavenプロジェクトを扱うには m2e プラグインが必要である。
少なくともEclipse 4.5.2 以上の「Eclipse IDE for Java EE Developers」パッケージを使えば、[m2eプラグイン](http://www.eclipse.org/m2e/) が同梱されているので、そのままMavenプロジェクトを扱うことができる。

以下の手順で実習1-7までのMavenプロジェクトをEclipseでインポートし、ビルドに成功するか試してみよう。
1. 本リポジトリをzipなどでダウンロードし展開する。
1. Eclipseから File -> Import -> Maven -> Existing Maven Projects を選択し、Next をクリック
1. Root Directory で `exercise01-07_tips/hello-maven` を選択すると、Projects で pom.xml が認識されるので、チェックを入れて Finish を選択。
1. 自動でソースフォルダやビルドパスが調整され、ビルドに成功することを確認する。
1. Eclipse上にインポートしたMavenプロジェクトを右クリックし、"Run As" メニューから Maven の clean や test フェーズを実行してみる。また、Mavenの実行設定で `package` フェーズなどお好みのフェーズを実行させてみる。

Eclipse上で作成したJavaプロジェクトとの大きな違いは、外部ライブラリのビルドパス設定にある。
- Eclipse上で作成したJavaプロジェクトの場合、開発者が手動でローカルフォルダ上のjarファイルをプロジェクトのプロパティの Java Build Path に追加する必要がある。
  - ライブラリjarファイルをどこに配置して、Java Build Path に追加するか、開発者や開発チーム間で取り決めとルール作りが必要。
  - ライブラリjarファイルそれ自体もどこで管理するか決める必要がある。
- EclipseにインポートしたMavenプロジェクトの場合、Mavenが自動でローカルの `$HOME/.m2/` 以下に依存関係のjarをDLする。m2eプラグイン側でそれらをまとめて "Maven Dependencies" として自動で Java Build Path に追加してくれる。
  - 最初に pom.xml に依存関係を登録してしまえば、あとはMavenとm2eプラグインが全て自動で処理してくれる。

### コラム : JUnitのテストケースをEclipse上で実行

実習1-7の段階のhello-worldにはJUnitのテストクラスが含まれている。
Eclipse上にインポートできたら、テストクラスを右クリックして "Run As" -> JUnit Test で実行できるので試してみよう。

## 実習2-4 : EclipseからGitHubリポジトリを操作

少なくともEclipse 4.5.2 以上の「Eclipse IDE for Java EE Developers」パッケージであれば [EGitプラグイン](http://www.eclipse.org/egit/) が同梱されており、これを使ってGitHubリポジトリをcloneしたり、ローカルのGitリポジトリを操作、リモートにpushすることができる。

以下の手順で、最終的に実習1-7のMavenリポジトリをGit管理下でEclipseにインポートし、変更をpushできるか試してみよう。( **GitHubアカウントが必要** )
1. 本リポジトリを自分のGitHubアカウントにforkする。
1. Eclipseの Window -> Perspective -> Open Perspectives から Git Perspective を開く。
1. Git Repositories View を開き、fork したGitHubリポジトリをローカルに clone する。
1. File -> Import -> Maven -> Existing Maven Projects を選択し、ローカルに clone した `exercise01-07_tips/hello-maven` をインポートする。
1. 適当にファイルを編集し、commit / push を試してみる。

------

以上でレッスン2を終わる。

レッスン3では、自分で新規にGitHubリポジトリを作成し、Nettyを使ったWebアプリケーションや LittleProxy を使ったローカルHTTPプロキシを作成、GitHub上で公開する方法を学ぶ。
