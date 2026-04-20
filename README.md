# Der TiEL Interpreter

Bei TiEL (Tiny Educational Language) handelt es sich um eine rudimentäre
Programmiersprache, die in der Veranstaltung "Automaten, Sprachen und Compiler" am
Fachbereich MNI der THM Gießen verwendet wird. Sie unterstützt Funktionen, Variablen,
mathematische Ausdrücke und grundlegende Kontrollflussanweisungen. Beispiels für 
TiEL-Programme finden sich im Unterordner `examples`.

## Entwicklungs-Setup

Für die Arbeit mit diesem Projekt wird die Verwendung von IntelliJ IDEA empfohlen. Die
folgenden Schritte beschreiben, wie das Projekt aufgesetzt werden kann. Das Projekt
benötigt Java 25.

1. Laden Sie dieses Projekt aus dem Moodle-Kurs herunter (bereits geschehen).
2. Öffnen Sie das Projekt in IntelliJ (`File → Open`). Das Projekt sollte automatisch
   erkannt und die benötigte Java-Version installiert werden. Falls IntellIJ nachfragt,
   vertrauen Sie dem Projekt.
3. Wählen Sie die Run-Konfiguration `TiEL` (obere rechte Ecke des Fensters).
4. Drücken Sie das kleine grüne Dreieck neben der Run-Configuration, um das Projekt auszuführen.

Standardmäßig wird die Datei `examples/hello_world.tiel` als Eingabe für den Interpreter verwendet. 
Durch Anpassung der Run-Konfiguration kann die auszuführende Datei angepasst werden.

## Erstellen einer portablen JAR-Datei

Das Projekt erlaubt das Erstellen einer portablen JAR-Datei mithilfe des
[Shadow-Plugins](https://gradleup.com/shadow/) für Gradle. Die folgenden Schritte
beschreiben, wie eine solche Datei erzeugt werden kann:

1. Öffnen Sie das Gradle-Menü in IntelliJ.
2. Öffnen Sie `tiel-lang/Tasks/shadow`.
3. Führen Sie `shadowJar` aus.

Sie finden die erzeugte JAR-Datei im Ordner `build/libs/` unter dem Namen `tiel-1.0-SNAPSHOT-all.jar`.

## Verwendung des CLIs (d.h., der portablen JAR-Datei)

```
Usage: tiel [-hV] [--ast] [--tokens] <input>
      <input>     Source code file to execute.
      --ast       Print generated AST.
  -h, --help      Show this help message and exit.
      --tokens    Print scanned tokens.
  -V, --version   Print version information and exit.
```

Beispiel: `java -jar tiel-1.0-SNAPSHOT-all.jar --help`