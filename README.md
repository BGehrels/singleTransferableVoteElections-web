# singleTransferableVoteElections-web
## English summary
This is a simple web interface for the Single Transferable Vote (STV) Elections library. It was developed for the german GRÜNE JUGEND Bundesverband - the youth organization of the german Green Party (BÜNDNIS 90/DIE GRÜNEN) - and is used to elect several offices and delegation during their semiannual general assembly. STV Elections do not need secondary election runs on close results. You can thus vote many offices using only one Ballot form and one election run. This saves a lot of time. 

The software allows to manually enter a batch of marked ballots and calculate the election results. It requires each ballot to be entered twice to prevent fraud and accidental typos. Since the only user is a german organization, the user interface as well as this documentation has not been internationalized. Feel free to contact me if you have any questions regarding this software.

## Einleitung
Dies ist ein Web Interface für die Single Transferable Vote (STV) Elections Softwarebibliothek. Es wurde im Auftrag des GRÜNE JUGEND Bundesverbands entwickelt, dem Jugendverband von BÜNDNIS 90/DIE GRÜNEN und wird zur Wahl mehrerer Ämter und Delegationen im Rahmen der halbjährlichen Bundesmitgliederversammlung genutzt. Wahlen mittels übertragbarer Einzelstimmgebung kommen ohne zweite Wahlgänge bei knappen Wahlergebnissen aus. Mehrere Ämter können daher auf einem Wahlzettel in einem Wahlgang gewählt werden. Dies spart eine Menge Zeit. 

Die Software erlaubt die manuelle Eingabe ausgefüllter Stimmzettel und die Berechnung des Wahlergebnisses. Hierbei muss jeder Stimmzettel zweimal eingegeben werden, um Fehler oder Betrugsversuche erkennen zu können. Sollten Sie Fragen zu dieser Software haben können Sie mich gerne kontaktieren

## Wie baue ich die Software?
Vorbedingungen:
* Java JDK >= 17
* Apache Maven (aktuelle Version)
* Internetzugriff
* Git (optional)

Schritte:

- Sicherstellen, dass das JDK und Maven korrekt installiert und auf der Kommandozeile mittels mvn und javac ausführbar sind
- Dieses Repository entweder mit git klonen oder als ZIP-/TAR-Archiv herunterladen

    git clone https://github.com/BGehrels/singleTransferableVoteElections-web.git

- In das Wurzelverzeichnis des Repositories (dort, wo auch diese Readme.md Datei liegt) wechseln

    cd singleTransferableVoteElections-web

- Dort die Software mittels Maven Kompilieren und Paketieren:

    mvn verify

- Das fertig gebaute jar-archiv sollte nun im target-Verzeichnis liegen und lässt sich dort mittels java ausführen

    cd target

    java -jar singleTransferableVoteElections-web-4.0.jar

## Sicherheitskonzept
Ziele:
- Alle Stimmen müssen gewertet werden
- Alle Stimmen müssen inhaltlich korrekt erfasst werden
- Es dürfen keine zusätzlichen Stimmen ins System eingeschmuggelt oder bestehende Stimmen unterschlagen werden können
- Manipulation bei der Erfassung von Stimmzettelinhalten und versehentliche Fehleingaben sollen vermieden werden
- Der Sicherheitsstandard darf nicht hinter jenem klassischer Urnenwahlen zurückfallen
- Es muss auch nach der Wahlauszählung und ohne Softwareunterstützung jederzeit nachvollziehbar sein, dass dies der Fall ist.

Mittel:
- Das System protokolliert jeden eingegebenen Stimmzettel samt dessen Inhalt
- Das System protokolliert alle relevanten Berechnungsschritte und deren Ergebnisse
- Das System forciert eine zweifache Eingabe jedes Stimmzettels und vergleicht die Gleichheit beider Eingaben.
- Das System fordert zur Löschung des eingegebenen Stimmzettels und zur Neueingabe auf, sollte keine Gleichheit vorliegen
- Das System gibt einen Überblick über die Anzahl der eingegebenen Stimmzettel, sodass Minder- und Übermengen erkannt werden können

Bedingungen
- Das System muss in einem geschlossenen, für unbeteiligte nicht zugreifbaren Netzwerk betrieben werden.
- Vor Beginn der Stimmzetteleingabe muss kontrolliert werden, dass die Anzahl der eingegebenen Stimmzettel 0 ist.
- Die Ersteingabe und die Kontrolleingabe eines Stimmzettels müssen stets von unterschiedlichen Personen/Teams vorgenommen werden (4 Augen Prinzip)
- Die Zahl der eingegebenen Stimmzettel muss mit der tatsächlich in der Urne vorhandenen Anzahl Stimmzettel vergleichen werden
- Die im Protokoll vermerkten Stimmzettelinhalte müssen Stichprobenartig gegen die tatsächlich vorliegenden Stimmzettel gegengeprüft werden.
- Das Auszählprotokoll muss ausgedruckt und zusammen mit den Originalstimmzetteln archiviert werden

## Starten des Servers
Vorbedingung: Es muss eine aktuelle Version der Java Runtime Environment (JRE) >= 1.8 installiert sein.

Je nach Betriebssystem kann der Server mittels eines Doppelklicks auf die Datei singleTransferableVoteElections-web-4.0.jar oder mittels `java -jar singleTransferableVoteElections-web-4.0.jar` gestartet werden. Nach dem Start gibt dieser eine Liste der URLs aus, unter denen er erreichbar ist.
```
$ java -jar singleTransferableVoteElections-web-4.0.jar
2015-10-25 16:28:24.190  INFO 8180 --- [main] ServerStarted: Die Wahlauszählung kann beginnen. Die Eingabemaske ist nun unter den folgenden URLs erreichbar:
2015-10-25 16:28:26.939  INFO 8180 --- [main] ServerStarted: http://KONRAD:8080/
2015-10-25 16:28:26.957  INFO 8180 --- [main] ServerStarted: http://192.168.1.75:8080/
2015-10-25 16:28:26.963  INFO 8180 --- [main] ServerStarted: http://192.168.59.3:8080/
2015-10-25 16:28:26.966  INFO 8180 --- [main] ServerStarted: http://127.0.0.1:8080/
2015-10-25 16:28:26.969  INFO 8180 --- [main] ServerStarted: http://192.168.56.1:8080/

```

## Benutzung der Eingabe- und Berechnungsmasken
Zuerst eine Vorbemerkung: Der Server speichert keinerlei Daten auf der Festplatte. Sämtliche eingegebenen Daten liegen im Arbeitsspeicher und gehen mit dem Herunterfahren des Servers verloren. Es muss daher sichergestellt werden, dass
- Der Server jederzeit zuverlässig mit Strom versorgt ist und
- Alle Protokolle gedruckt sind, bevor das System heruntergefahren wird.

### Hauptmenü

Nach dem Öffnen von http://<serverIp>:8080/ sehen wir das Hauptmenü. Neben den Links zu den unterschiedlichen Funktionen der Software finden wir hier die Übersicht der eingegebenen Stimmzettel. Hier ist sicherzustellen, dass diese bis zur Eröffnung der Stimmeingabe stets auf 0 stehen.

### Eingabe eines Stimmzettellayouts
Bevor wir mit der Stimmeingabe beginnen muss das System mit dem Layout der Stimmzettel vertraut gemacht werden. Die Ämter und Kandidierenden sollten in genau der Reihenfolge eingegeben werden, wie sie auch auf den gedruckten Stimmzetteln erscheinen.

Bitte prüfen Sie zweimal, ob für alle Kandidierenden die "weiblich Ja/Nein"-Angabe korrekt ist. Andernfalls kann und wird es zu falschen Wahlergebnissen kommen.

### Nummerierung aller Stimmzettel
Jeder Stimmzettel braucht eine eindeutige Nummer. Um die Vertraulichkeit der Wahl nicht zu brechen, darf diese Nummer erst nach Abgabe des Stimmzettels vergeben werden. Es empfiehlt sich, unmittelbar nach der Öffnung der Wahlurnen und dem Zählen der Stimmzettel diese von Hand oder mit einem sogenannten Paginierstempel mit fortlaufenden Nummern zu versehen.

### Ersteingabe der Stimmzettel
Mit einem Klick auf "Stimmen eingeben (Ersteingabe)" gelangen wir zu der Maske, die von unseren Wahlhelfer*innen zur Stimmeingabe genutzt wird. Diese Stimmeingabe kann von mehreren Rechnern aus parallel geschehen, jedes Auszählteam erhält einen Rechner und einen Teil der Stimmzettel zur Eingabe.

Nach der Eingabe der Stimmzettelnummer ist hier für jedes Amt zuerst die Stimmart einzugeben: Präferenz bedeutet, das die Wählerin bei mindestens einer Kandidierenden eine Präferenz (Zahl) eingetragen hat. Keine Stimmabgabe bedeutet, dass für dieses Amt keinerlei Markierung auf dem Stimmzettel vorhanden ist. Nein und Ungültig dürften Selbsterklärend sein.

Unterhalb der Stimmtypenauswahl findet sich eine Liste der Kandidierenden mit jeweils einem Eingabefeld dahinter. Hier ist zu jeder Kandidierenden die Präferenz (Zahl) einzutragen. Ist auf dem Simmzettel keine Zahl eingetragen worden, lassen wir auch hier das Feld leer.

Sollte es hier einmal versehentlich zu Fehleingaben kommen so ist dies nicht dramatisch: Am Ende, wenn alle Stimmen ein zweites Mal eingegeben wurden findet eine automatische Fehlerkontrolle statt. Fehlerhafte Stimmzettel können dann gelöscht und erneut eingegeben werden.

Nach der vollständigen Eingabe des Stimmzettels kann mit einem Klick auf "Hinzufügen und nächsten Stimmzettel ausfüllen" die Eingabe gespeichert werden. Sind alle Zettel eingegeben, wird der letzte noch gespeichert und dann mittels eines Klicks auf "Zurück zur Startseite" die Ersteingabe beendet.

### Zweiteingabe der Stimmzettel

- Auswertungslauf
-- Optional: Fehlerkorrektur, dann wieder zur Erst- und zweiteingabe zurück
--- Stimmzettelnummern unbedingt abschreiben
-- Wahlergebnisse anzeigen
--- Unbedingt ausdrucken, da flüchtig
--- Der Autor freut sich über zugesandte JSON- und SVG-Dateien zum Optimieren der grafischen Darstellungen
--- 
