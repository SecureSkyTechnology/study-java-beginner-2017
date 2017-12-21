# レッスン1 : Java,Mavenの基礎

本レッスンではJavaアプリケーションをjarファイルにパッケージングする方法と、Mavenの使い方を学びます。
これにより以下のスキルを身につけることができます。
- Javaのソースをコンパイルし、実行する。
- ダブルクリックで起動するjarファイルを作成する。
- 外部ライブラリをMavenで管理する。
- 外部ライブラリを使うプロジェクトをMavenでパッケージングする。
- JDKさえあればMavenプロジェクトをビルドできるようにする。

## 実習1-1 : hello, world

### JRE/JDKの違い

- JRE(Java Runtime Environment)
  - Javaアプリケーションの実行環境
  - コンパイラは入ってない。
- JDK(Java Development Kit)
  - JRE + コンパイラ

### Edition の違い

- JavaSE(Standard Edition), 旧称J2SE
  - 一般的なクライアントアプリケーションで必要な機能(API)が揃っている。
  - JRE / JDK に含まれているのはこちら。
- JavaEE(Enterprise Edition), 旧称J2EE
  - Servletなど Web/アプリケーションサーバの構築に必要な機能が揃っている。
  - JRE / JDK には含まれず、TomcatやWebLogicなどのサーバ製品やライブラリとしてサードパーティから提供されている。
- JavaME(Micro Edition), 旧称J2ME
  - 組み込み機器向けに縮小したAPIセット。

### JDKのインストール

- OrackeJDK : http://www.oracle.com/technetwork/java/javase/downloads/index.html からJDKをダウンロードしてインストールする。
- OpenJDK : 主にLinuxのディストリビューションが提供しているので、パッケージマネージャなどからインストールする。
- Javaの開発がオープン化され、オープンソースの範囲でのJavaであればOpenJDKを利用できる。
- Oracle独自の拡張や、Windowsバイナリを使いたい場合はOracleJDKを使う。(最近になって OpenJDKベースのWindowsパッケージもRedHatなどで提供されだしたので今後の行方に注目)

#### コラム : WindowsでのJDKインストール時の"JRE"の扱いについて

- WindowsでOracleJDKをインストールする際に、コンポーネントの選択で「JRE」がデフォルトで選択されている。
- **ここで「JRE」のチェックを外すのを筆者個人はオススメしている。**
- というのは、筆者は以前「JRE」を選択したままインストールした結果、 https://www.java.com/ja/ からインストール済みだったJREが正常に動作しなくなってしまった経験があるため。
- Windowsにおいて、 https://www.java.com/ja/ からインストールするいわゆる「パブリックJRE」は、ブラウザプラグインを登録したり、".jar"拡張子をjavaコマンドの実行に結びつけたり、Javaのコントロールパネルを登録したり自動更新を有効にするなど、Windows OSに密接に結びついている。
- そのため、JDKインストール時に「JRE」を選択してインストールしてしまうと、インストール済みの「JRE」と何らかの衝突が発生したものと推測される。
- よって、筆者は個人的な慣習として、WindowsでOracleJDKをインストールする際はかならず「JRE」のチェックを外し、JDKのみをインストールするようにしている。
- **もし「JRE」のチェックを入れてインストールしてしまった場合** (筆者の個人的なアドバイス)
  1. 一度、JDKもJREも全てアンインストールしてクリーンな状態に戻す。
  1. https://www.java.com/ja/ からJREをインストールし直す。
  1. OracleJDKを「JRE」のチェック無しでインストールし直す。
  1. (更に筆者個人の好みだが) OracleJDKのインストール先フォルダの内容を、`C:\work` など空白を含まないフォルダにコピーした後、「プログラムの追加と削除」からJDKをアンインストールする。(JDKの場合は単にファイルとして存在すれば良いので、Windows上のレジストリからはインストール履歴を抹消し、OS側の設定と完全に分離する)

### Hello, World の作成

`HelloWorld.java`:
```java
import javax.swing.JOptionPane;

public class HelloWorld {
    public static void main(String[] args) {
        // コンソール出力
        System.out.println("Hello, World");
        // GUIメッセージボックス出力
        JOptionPane.showMessageDialog(null, "Hello, World");
    }
}
```

### コンパイルと実行

```
cd study-java-beginner-2017\exercise01-01_hello_world
javac -encoding UTF-8 HelloWorld.java
java HelloWorld
```

- 「なんで `HelloWorld.class` を指定しなくても実行できるのか？」
  - クラスを探す先の「クラスパス」がデフォルトで現在ディレクトリなので、現在ディレクトリからHelloWorld.classを探し出して実行できている。
  - 「クラスパス」については実習1-3で学ぶ。

## 実習1-2 : ダブルクリックで起動するjarファイルの作成

- jarファイルとは : 複数のclassファイルをパッケージングしたもの。zipファイルのフォーマットと互換性があり、拡張子を.zipにすればそのままzipファイルとして展開できる。
  - https://docs.oracle.com/javase/jp/8/docs/technotes/guides/jar/index.html

### jarファイルの作成と実行

```
cd study-java-beginner-2017/
jar cvf hello-world.jar -C exercise01-01_hello_world HelloWorld.class
java -cp hello-world.jar HelloWorld
```

- `-cp` が class path の指定で、jarファイルを指定できる。つまり `hello-world.jar` の中に HelloWorld.class が含まれており、それを実行している。
- jarファイルの中身の一覧を出力 : `jar tf hello-world.jar`
- jarファイルの中身を展開(zipファイルの解凍に相当) : `jar xf hello-world.jar`

### ダブルクリックで実行できるjarファイルの作成

この時点で作成した hello-world.jar はダブルクリックしても実行されない。(ダブルクリックしても何も起こらない)
そこで、MANIFEST.MF の `Main-Class` を指定する。([参考](https://docs.oracle.com/javase/jp/8/docs/technotes/guides/jar/jar.html#Main_Attributes))

`exercise01-02_manifest_jar/MANIFEST.MF`
```
Manifest-Version: 1.0
Main-Class: HelloWorld
```

上記MANIFEST.MFを組み込んだjarファイルの作成と実行
```
cd study-java-beginner-2017/
jar cvfm hello-world.jar exercise01-02_manifest_jar/MANIFEST.MF -C exercise01-01_hello_world HelloWorld.class
java -jar hello-world.jar
```

これでダブルクリックして実行可能なjarファイルが作成された。(ダブルクリックによる内部処理が `java -jar jarファイル名` に相当している)

## 実習1-3 : 外部ライブラリの利用

実際の開発では多数の外部ライブラリを組み合わせる。
外部ライブラリをどのように組み込むか、基本的なやり方を説明する。
題材として[guava](https://github.com/google/guava)を使う。

Javaにおける外部ライブラリはjarファイルとして配布されている。
guavaのjarファイルは以下のMavenリポジトリから各リリースバージョン毎にダウンロードできる。
- http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.google.guava%22%20AND%20a%3A%22guava%22
- 今回は 2017-12-20時点での最新版, `23.5-jre` の jar ファイルをダウンロードし、 `exercise01-03_hello_guava/` の下に配置する。
- (Mavenリポジトリや上記サイトについては実習1-4で学ぶ。)
- (guavaのライセンスはAPL 2.0のため、jarファイルは本リポジトリ:MITライセンスには含めていない)

### guava を使ったHelloWorld

guavaが提供する [Strings.repeat()](http://google.github.io/guava/releases/23.5-jre/api/docs/com/google/common/base/Strings.html#repeat-java.lang.String-int-)  を使ったHelloWorldを書いてみる。

`exercise01-03_hello_guava/HelloGuava.java`
```java
import com.google.common.base.Strings;
import javax.swing.JOptionPane;

public class HelloGuava {
    public static void main(String[] args) {
        String msg = "Hello, " + Strings.repeat("World ", 5);
        // コンソール出力
        System.out.println(msg);
        // GUIメッセージボックス出力
        JOptionPane.showMessageDialog(null, msg);
    }
}
```

### コンパイルと実行

```
cd study-java-beginner-2017\exercise01-03_hello_guava
javac -classpath guava-23.5-jre.jar -encoding UTF-8 HelloGuava.java
java -cp .;guava-23.5-jre.jar HelloGuava
```

- javacコマンドに、コンパイル時の外部ライブラリ探索先を `-classpath` オプションで指定し、これにguavaのjarファイルを含めている。
- 実行時には `-cp` でクラスパスにguavaのjarファイルを追加している。
  - **現在ディレクトリの `.` を追加したのはなぜか？** 削除したらどうなるか、なぜその結果になるか？
  - 複数の探索先を含める時は、Windowsなら `;` で区切り、その他unix系なら `:` で区切る。(PATHの区切り文字と同じ)
- 外部ライブラリを使う場合に、ダブルクリックで実行できるjarを作る方法については実習1-5で学ぶ。

## 実習1-4 : Mavenによる外部ライブラリの管理とビルド

実際の開発では多数の外部ライブラリを組み合わせる。
また大量のソースファイルをコンパイルし、さらにユニットテストの実行やパッケージングなどコンパイル以外の処理も行う。
外部ライブラリの管理、すなわち「依存関係の管理」と「ビルドシステム」をJavaで実現するためにはいくつかのツールを選択できる。

Javaの主要ビルドツール：
1. Ant : http://ant.apache.org/
   - Javaのビルドシステムの老舗だが、現在はあまり使われていない印象。
   - あくまでもビルドのみを扱う、いわばMakeのJava版で、依存関係の管理機能がない。
   - Antで依存関係を実現するためには [Ivy](http://ant.apache.org/ivy/) を組み合わせることになる。
1. Maven : http://maven.apache.org/
   - 依存関係とビルドシステムの両方を管理できる、枯れたエコシステム。
   - 主要IDEで対応しており、最も無難な選択肢という印象。
   - ディレクトリレイアウトなど独自のルールがあるため、融通が効かない印象が強い。
1. Gradle : https://gradle.org/
   - Maven, Ant がXMLで設定するのに対し、Groovyという言語を使ったDSL(ドメイン特化言語)で書きやすさ・読みやすさが大幅に向上したツール。
   - またDSL設定ファイルの中に直接Groovyによる処理を埋め込めるため、ビルド中にプログラマブルな処理や判定を埋め込みやすい。
   - Mavenと比べると融通が効く一方で、開発スピードが早くキャッチアップに苦労する印象。 
1. SBT : http://www.scala-sbt.org/index.html
   - Scalaという言語とDSLを使ったツール。 
1. Bazzel : https://www.bazel.build/
   - 勉強不足のため説明できない。

今回は最も無難で枯れている Maven を使う。
Gradleなど他のビルドシステムでも、特に依存関係についてはMavenエコシステムが確立したリポジトリに依存しているため、Mavenを学んでおいて損はない。

日本語のMaven資料オススメ：
- http://www.techscore.com/tech/Java/ApacheJakarta/Maven/index/
  - 2017現在はバージョン3系になっているのに対して、2系の記事のためやや古いものの、考え方や基本的な解説は参考にできる。

公式ドキュメントについては、Maven自体の実行方法/プラグイン/pom.xmlのリファレンスなど多岐に渡るドキュメントが公開されている。
しかしそれゆえに、どこから見れば良いのか迷ってしまう。
英語の公式ドキュメントで学ぶのであれば、以下の "Maven User Centre" のチュートリアル記事から始めるのがオススメ。
- http://maven.apache.org/users/index.html

### Maven のインストール

http://maven.apache.org/ から `apache-maven-(version)-bin.(tar.gz|zip)` をダウンロードして解凍し、binディレクトリにPATHを通す。

### Maven プロジェクトの作成

この節からは、[Maven in 5 Minutes](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) の内容を駆け足で紹介していく。

Mavenプロジェクトの設定ファイル `pom.xml` をゼロから作成するのは大変なので、雛形(`archetype`)から作成機能を使う。

```
cd study-java-beginner-2017/exercise01-04_hello_maven
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart
(... Maven自体が依存するライブラリのダウンロード ...)
Define value for property 'groupId': ("testgroup"と入力)
Define value for property 'artifactId': ("hello-maven"と入力)
Define value for property 'version' 1.0-SNAPSHOT: :(何も入力せずENTER)
Define value for property 'package' testgroup: :(何も入力せずENTER)
Confirm properties configuration:
groupId: testgroup
artifactId: hello-maven
version: 1.0-SNAPSHOT
package: testgroup
 Y: : ("Y"と入力)
```

- mvnコマンドにつづいて指定した `archetype:generate` だが、`archetype` がMavenのプラグインで、コロンで区切って続く `generate` がプラグインが提供する「ゴール」を指示している。「ゴール」というのが、Mavenで実行される処理の最小単位となる。
- Mavenそれ自体はビルドシステムのフレームワークとなっていて、実際のコンパイルなど様々な実行処理の中身(=「ゴール」)は個別のプラグインとして提供されている。

実行すると以下のディレクトリとファイルが作成される。
```
hello-maven/
  pom.xml
  src/
    main/java/testgroup/App.java
    test/java/testgroup/AppTest.java
```

- `App.java` はhello worldするだけのクラス。`AppTest.java` は特に何もしないテストケース。(テストケースについては実習1-7で解説)
- Javaのアプリケーションコードは `main/java` 以下に作成し、テストコードは `test/java` 以下に作成する。

pom.xmlについて次節で簡単に紹介する。

### pom.xml の概要

前節のarchetypeで作成されたpom.xmlについて、コメントで解説する。
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <!-- Mavenプロジェクトで必須のグループID, 企業や組織のパッケージ名などを設定する -->
  <groupId>testgroup</groupId>

  <!-- Mavenプロジェクトで必須のアーティファクトID, jarファイル名などに使う最終的な製品名を設定する -->
  <artifactId>hello-maven</artifactId>

  <!-- パッケージング方法 -->
  <packaging>jar</packaging>

  <!-- バージョン -->
  <version>1.0-SNAPSHOT</version>

  <!-- プロジェクト名, アーティファクトIDと同じにしておくのが無難 -->
  <name>hello-maven</name>

  <!-- プロジェクトのHPなどのURL -->
  <url>http://maven.apache.org</url>

  <!-- ここからビルドで使う外部ライブラリ(=依存関係)の設定 -->
  <dependencies>
    <dependency>
      <!-- 外部ライブラリのグループID, アーティファクトID, バージョン などを設定する -->
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
```

pom.xmlの詳細は以下を参照のこと。
- http://maven.apache.org/pom.html

### Mavenプロジェクトのビルド

作成したMavenプロジェクトをビルドしてみる。
```
cd hello-maven/

mvn compile
-> main/java 以下のJavaソースをコンパイル

mvn test
-> test/java 以下のテストケースをコンパイル・実行

mvn package
-> jarファイルにパッケージング

java -cp target\hello-maven-1.0-SNAPSHOT.jar testgroup.App
-> "Hello World!" と出力される。
```

- コンパイルしたクラスやパッケージファイルは `target` ディレクトリ以下に保存される。
- `target` ディレクトリなどのビルド成果物を削除したい場合は、 `mvn clean` を実行する。

### phase と lifecycle

前節で実行したmvnコマンドについて少し詳しく解説する。

- 前節で実行した `mvn compile`, `mvn test`, `mvn package`, `mvn clean` などは、`archetype:generate` と異なり、プラグイン名やコロンを含まない。
- これらは、ゴールではなく「フェーズ」(phase)と呼ばれている。
- 「このフェーズが呼ばれたら、このプラグインのこのゴールを実行する」というのが内部で設定されており、それに従って裏側でプラグインとゴールを呼び出している。

ここで一つ実験をしてみて欲しい。
1. 一度 `mvn clean` でビルド生成物を削除する。
1. その後、いきなり `mvn package` を実行してみる。何が起きるか？

一般にビルドシステムに期待する挙動としては、「packageをしたいがまだコンパイルされていない -> compile しよう」と自動で判断してcompileフェーズを呼び出してくれると嬉しい。

実際に実行してみると、たしかに compile フェーズを実行してくれるが、その後 test フェーズを実行して、packageフェーズに進む様子が分かるはずだ。

このように、Mavenでは各フェーズがどの順序で実行されるべきかというのを管理していて、「ライフサイクル」(lifecycle)と呼んでいる。

- compile, test, package はデフォルトのlifecycleに含まれており、この順番で指定されたフェーズまで実行されることになる。
- clean については Clean lifecycleに含まれている。そのため、compile, test, package の一覧の流れには含まれず、手動で呼び出す必要がある。

3行でまとめ：
1. Mavenの各処理は個別のプラグインの「ゴール」(goal)で実装されている。
1. ゴールをまとめ、より汎用的な名前で抽象化したのを「フェーズ」(phase)と呼ぶ。
1. フェーズがどの順番で実行されるべきかを「ライフサイクル」(lifecycle)で定義している。

なお以下のように複数のフェーズを順に指定し、実行させることも可能。
```
mvn clean package
```

詳細は以下のドキュメントを参照。
- http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html

単純なプロジェクトではあまり気にすることは無いが、Mavenプラグインを追加・設定する中でgoal/phase/lifecycleの用語が出てくる場合があるので、頭の片隅にでも置いておいて欲しい。

### pom.xmlに依存関係を追加

pom.xmlにguavaの依存関係を追加する。 
依存関係を追加するには groupId, artifactId, version を指定する必要がある。
ライブラリの公式サイトに記載されている場合もあるが、名前がわかれば以下のサイトを使って検索することもできる。
- http://search.maven.org
- Mavenで使えるライブラリが登録されているセントラルリポジトリを検索できる。
- ここで見つかったライブラリであれば、pom.xmlに依存関係を追加するだけで、ビルド時に自動的にjarファイルをダウンロードし、コンパイル時にクラスパスに追加してくれる。

実際に guava の 23.5-jre を追加したpom.xml:
```
(...)
  <dependencies>

    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
      <version>23.5-jre</version>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
(...)
```

`App.java` も以下のようにguavaの Strings.repeat() を使ったコードに修正する。
```java
package testgroup;
import com.google.common.base.Strings;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        String msg = "Hello, " + Strings.repeat("World ", 5);
        System.out.println(msg);
        JOptionPane.showMessageDialog(null, msg);
    }
}
```

ビルドが成功することを確認する。
```
mvn clean package
```

だが、ここで生成されたjarファイルは当然ながら App クラスしか含まれていない。MANIFEST.MFの設定もしていないため、単体起動もできない。
次節でこの問題を解決し、依存関係を取り込んだjarファイルを生成し、ダブルクリックで単体起動できるMANIFEST.MFの設定をビルドシステムに組み込む。

## 実習1-5 : Maven Shade Pluginによる uber-jar の作成

- "uber" というのはドイツ語の Über から来ており、"over"/"above"という意味らしい。
  - via : https://stackoverflow.com/questions/11947037/what-is-an-uber-jar
- "uber-jar" は "fat jar" あるいは "jar with dependencies" とも呼ばれており、要するに「依存関係のライブラリの中身を全部取り込んだ、全部入りjar」で、これにより依存関係にあるjarライブラリをパッケージングしたりクラスパスに指定する問題を解決する。
- Mavenでは [Maven Shade Plugin](http://maven.apache.org/plugins/maven-shade-plugin/) を使うとuber-jarを作成できて、しかも一緒にMANIFEST.MFの設定もできる。
- つまり Maven Shade Plugin を使えば、依存関係全部入りの、ダブルクリックで単体起動できるjarファイルを作成できる。

pom.xmlに以下のように `maven-shade-plugin` のビルド設定を追記する。
```
(...)
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.1.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
            <configuration>
              <createDependencyReducedPom>false</createDependencyReducedPom>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                  <mainClass>testgroup.App</mainClass>
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

- Maven Shade Plugin を組み込めば、package フェーズで適切にuber-jarを生成してくれる。
- 生成時に MANIFEST.MF ファイルに任意の設定を追加できる。上の例では、`Main-Class` に指定するクラス名を埋め込んでいる。
- 参考 : http://maven.apache.org/plugins/maven-shade-plugin/examples/executable-jar.html

ビルドと実行：
```
cd study-java-beginner-2017/exercise01-05_uber-jar/hello-maven

mvn clean package

java -jar target/hello-maven-1.0-SNAPSHOT.jar
```

特に問題なければ、guavaを使ったメッセージがコマンドライン・GUIのメッセージボックスそれぞれで表示される。

- パッケージングでは他にも [Maven Assembly Plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) というのがある。
  - これは依存ライブラリのjarを個別のファイルとして集めて、クラスパス設定をラップした sh / bat ファイルを生成して tar や zip にアーカイブできる。
  - Maven のダウンロードバイナリや、Tomcat のダウンロードバイナリの構造がイメージ的には近い、というか恐らくほぼそのまま。
  - .sh や .bat の中で、shellスクリプトや Windows BATファイルの機能を駆使して動的にjarファイル名をかき集めてクラスパスに設定するラッパースクリプトが含まれる。
  - つまり Maven Assembly Plugin を使うとそうしたラッパースクリプトとディレクトリ構造を自動生成できる、ということ。
- uber-jar ではうまく動作しない場合など、jarファイル単体でクラスパスを通す必要が出てきた場合には、こちらのパッケージングを試すと良いかもしれない。
  - 本資料ではそこまでは追わないので、興味あれば試してみてください。

## 実習1-6 : Maven Wrapper の組み込み

Mavenプロジェクトとして開発する場合に、問題の一つとなるのが「Mavenをインストールしないとビルドできない」という問題である。
しかも、枯れているとはいえ、今もMavenの開発は続けられておりバージョンアップしている。
余計なトラブルを避けるためにも、ビルドに使うMavenのバージョンは揃えたいが、プロジェクトごとに異なってくるとローカルにインストールするMavenのバージョン管理が大変な手間となってしまう。
これを解決してくれるのが [Maven Wrapper](https://github.com/takari/maven-wrapper) というツールである。
- https://github.com/takari/maven-wrapper

使ってみたほうが早いタイプのツールなので、まずは前節のMavenプロジェクトを題材に Maven Wrapper 適用してみる。

```
(pom.xmlのあるディレクトリにcdしておく。)
cd hello-world/

mvn -N io.takari:maven:wrapper
```

以上、mvnコマンド一行で Maven Wrapper が適用される。
「適用」されるとどうなるかというと、以下のファイルが作られる。
```
mvnw     ... unix系用のshell script
mvnw.cmd ... windows用のbatch file
.mvn/wrapper/
    maven-wrapper.jar ... 実行時に動的にMavenランタイムをDLするためのブートストラップjar
    maven-wrapper.properties ... MavenランタイムのDL先URLの設定
```

以降は、"mvn" ではなく "mvnw" コマンドを代わりに実行すればよい。
unix系の場合はカレントからの相対パスで、 `./mvnw clean package` のように実行すればよいし、Windowsのコマンドプロンプト/PowerShell環境なら ".cmd" を削って `mvnw clean package` のように実行すれば良い。

- 実際に実行してみると分かるが、最初の1回は相当時間がかかる。
- これは裏で、Mavenランタイムをダウンロードし、`$HOME/.m2/wrapper/` 以下に展開しているため。

このように Maven Wrapper を導入すると、初回実行時に Maven を自動的にDLし、2回目以降は "mvnw" ラッパースクリプトが適宜調整してDLしたMavenを呼び出す仕組みになっている。
- これにより、プロジェクトをGitやSVNなどのリポジトリからclone/checkoutした後、JDKさえ入っていれば即座にMavenプロジェクトをビルドできるようになる。
  - イコール、Mavenをわざわざ事前にインストールする手間が省ける。
- また、使用するMavenのバージョンは `.mvn/wrapper/maven-wrapper.properties` に設定されたものになるので、バージョンが自動で揃うことになる。
- (複数のプロジェクトで異なるバージョンを使っている場合、 `$HOME/.m2/wrapper/` 以下にバージョンごとに分かれて展開される + mvnw ラッパースクリプトが maven-wrapper.properties に応じてどのバージョンを使うか切り分けてくれるので、混ざる心配は無い。)

**Maven Wrapperが生成したファイルをリポジトリに登録する際の注意点** : (Gitを例にしてます)
- `.mvn/wrapper/maven-wrapper.jar` が `.gitignore` で除外されないように注意すること。
  - `*.jar` を登録していると、無視されてしまいがち。
  - `*.jar` の下に `!maven-wrapper.jar` を置くなどして、ちゃんと `git add` してGitリポジトリに登録すること。
- "mvnw" は unix向けのshell scriptであるため、 `./mvnw` で実行するためには実行権限が必要となる。
  - しかし、Windows環境で Maven Wrapper を組み込んで `git add` してしまうと、実行権限がついていない状態でリポジトリに登録されてしまう。
  - そのため、特にWindows環境で Maven Wrapper を組み込んだ場合は、 "mvnw" に実行権限を付くよう明示的に指定する必要がある。
  - そうしておかないと、unix環境でcloneしたときに `./mvnw` で実行できなくなり、 `sh mvnw` と一手間多くなってしまう。
- Gitで実行権限を付ける例：
```
$ git update-index --add --chmod=+x mvnw
```
.
## 実習1-7 : 細かな改善TIPS

ここまで「jarファイルをダブルクリックすれば実行できる」jarファイルをビルドするためのスキルを学んできた。
本節では最後に、実際の開発で便利なちょっとした改善TIPSを紹介する。

### コンパイル対象のJavaバージョン, Javaソースファイルの文字コードの指定

javacコマンドでは以下のように、デバッグ情報の埋め込み / JavaソースファイルのJavaバージョン / コンパイル先クラスファイルのJavaバージョン / Javaソースファイルの文字コードを指定できる。
```
javac -help
使用方法: javac <options> <source files>
使用可能なオプションには次のものがあります。
  -g                         すべてのデバッグ情報を生成する
  -g:none                    デバッグ情報を生成しない
  -g:{lines,vars,source}     いくつかのデバッグ情報のみを生成する
(...)
  -encoding <encoding>       ソース・ファイルが使用する文字エンコーディングを指定する
  -source <release>          指定されたリリースとソースの互換性を保つ
  -target <release>          特定のVMバージョン用のクラス・ファイルを生成する
(...)
```

Mavenプロジェクトでも、pom.xmlで [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/) の設定をすることで、これらのオプションを明示的に指定することができる。

設定例:
```
(...)
  <url>http://maven.apache.org</url>

  <properties>
    <!-- see : https://maven.apache.org/general.html#encoding-warning  -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <java.version.source>1.8</java.version.source>
    <java.version.target>1.8</java.version.target>
    <java.compiler.debug>true</java.compiler.debug>
  </properties>

  <dependencies>
(...)
  </dependencies>

  <build>
    <plugins>

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.6.1</version>
        <configuration>
          <source>${java.version.source}</source>
          <target>${java.version.target}</target>
          <encoding>${project.build.sourceEncoding}</encoding>
          <debug>${java.compiler.debug}</debug>
        </configuration>
      </plugin>

      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
(...)
```

- `<properties>` にまとめると、このような設定値を一箇所にまとめられる + mvn コマンド実行時に引数で上書きできるようになるので便利。
- Maven Compiler Plugin で設定可能な項目の一覧は以下を参照。
  - https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html

### Javaリソースファイルの組み込み

設定ファイルや画像ファイルなどを埋め込みたい場合、Mavenプロジェクトでは `src/main/resources/` ディレクトリの下に配置する。

例 : `src/main/resources/config/myconfig.properties` ファイルを以下の内容で作成する。
```
yourname=jon
```

この設定ファイルを読み込み、"yourname"キーを取得するコードを `src/main/java/testgroup/App.java` に追加する。
```java
package testgroup;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

public class App {
    public static String getYourName(String reslocation) throws IOException {
        try (InputStream is = App.class.getClassLoader().getResourceAsStream(reslocation)) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty("yourname");
        }
    }
    public static void main(String[] args) throws IOException {
        String repeatName = getYourName("config/myconfig.properties");
        String msg = "こんにちは, " + Strings.repeat(repeatName + " ", 5);
        System.out.println(msg);
        JOptionPane.showMessageDialog(null, msg);
    }
}
```

実行すると「こんにちは, jon jon jon jon jon」と表示される。

jarファイルの内容を見てみると、`config/myconfig.properties` が同梱されているのが分かる。
```
jar tf target/hello-maven-1.0-SNAPSHOT.jar
(...)
config/
testgroup/
config/myconfig.properties
testgroup/App.class
(...)
```

### テストコードの改良とテスト用リソースファイルの組み込み

ここまでテストコードについて触れてこなかったが、最近はテスト駆動開発(TDD : Test Driven Development)などでテストコードの作成が奨励される時代になった。
また、Javaの代表的なライブラリではテストコードがドキュメントの代わりとなっている場合もあり、テストコードの読み書きが重要となっている。
Mavenの archetype:generate で生成されたコードにも形だけではあるがテストコードが含まれている。

本節では、テストコードを改良し、テスト用のリソースファイルを組み込んで見る。
前節で `App.getYourNamee()` を実装したのでこれをテストコード作成の題材とする。

まず pom.xml のJUnitのバージョンが低すぎるので、少なくとも4系最新版まで上げておく。
```
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
```

続いてテスト用のリソースファイルを組み込む。`src/test/resources/config/testconfig.properties` ファイルを以下の内容で作成する。
```
yourname=bob
```

`src/test/java/testgroup/AppTest.java` を以下のように修正する。
```java
package testgroup;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.IOException;

public class AppTest {
    @Test
    public void testGetYourName() throws IOException {
        assertEquals("bob", App.getYourName("config/testconfig.properties"));
    }
}
```

`mvnw test` でtestフェーズを実行し、JUnitによる単体テストがpassすることを確認する。

------

以上でレッスン1を終わる。

レッスン2では、Eclipseを使ってMavenプロジェクトを操作し、GitHubリポジトリと連携する方法を学ぶ。