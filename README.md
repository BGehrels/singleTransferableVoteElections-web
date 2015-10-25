# singleTransferableVoteElections-web
##English summary
This is a simple web interface for the Single Transferable Vote (STV) Elections library. It was developed for the german GRÜNE JUGEND Bundesverband - the youth organization of the german Green Party (BÜNDNIS 90/DIE GRÜNEN) - and is used to elect several offices and delegation during their semiannual general assembly. STV Elections do not need secondary election runs on close results. You can thus vote many offices using only one Ballot form and one election run. This safes a lot of time. 

The software allows to manually enter a batch of marked ballots and calculate the election results. It requires each ballot to be entered twice to prevent fraud and accidential typos. Since the only user is a german organization, the user interface as well as this documentation has not been internationalized. Feel free to contact me if you have any questions regarding this software.

##Einleitung
Dies ist ein Web Interface für die Single Transferable Vote (STV) Elections Softwarebibliothek. Es wurde im Auftrag des GRÜNE JUGEND Bundesverbands entwickelt, dem Jugendverband von BÜNDNIS 90/DIE GRÜNEN und wird zur Wahl meherer Ämter und Delegationen im Rahmen der halbjährlichen Bundemitgliederversammlung genutzt. Wahlen mittels übertragbarer Einzelstimmgebung kommen ohne zweite Wahlgänge bei knappen Wahlergebnissen aus. Mehrere Ämter können daher auf einem Wahlzettel in einem Wahlgang gewählt werden. Dies spart eine Menge Zeit. 

Die Software erlaubt die manuelle Eingabe ausgefüllter Stimmzettel und die Berechnung des Wahlergebnisses. Hierbei muss jeder Stimmzettel zwei mal eingegeben werden, um Fehler oder Betrugsversuche erkennen zu können. Sollten Sie Fragen zu dieser Software haben können Sie mich gerne kontaktieren

## Wie baue ich die Software?
Vorbedingungen:
* Java JDK >= 1.8
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

    java -jar singleTransferableVoteElections-web-3.0.jar

##Sicherheitsmodell
Ziele:
- Alle Stimmen müssen gewertet werden
- Alle Stimmen müssen inhaltlich korrekt erfasst werden
- Es dürfen keine zusäzlichen Stimmen ins System eingeschmuggelt oder bestehende Stimmen unterschlagen werden können
- Manipulation bei der Erfassung von Stimmzettelinhalten und versehentliche Fehleingaben sollen vermieden werden
- Der Sicherheitsstandard darf nicht hinter den klassischer Urnenwahlen zurückfallen
- Es muss auch nach der Wahlauszählung und ohne Softwareunterstützung jederzeit nachvollziehbar sein, dass dies der Fall ist.

Mittel:
- Das System protokolliert jeden eingegebenen Stimmzettel samt dessen Inhalt
- Das System protokolliert alle relevanten Berechnungsschritte und deren Ergebnisse
- Das System forciert eine zweifache Eingabe jedes Stimmzettels und vergleicht die Gleicheit beider Eingaben.
- Das System fordert zur Löschung des eingegebenen Stimmzettels und zur Neueingabe auf, sollte keine Gleichheit vorliegen
- Das System gibt einen Überblick über die Anzahl der eingegeneben Stimmzettel, so dass Minder- und Übermengen erkannt werden können

Bedingungen
- Das System muss in einem geschlossenen, für unbeteiligte nicht zugreifbaren Netzwerk betrieben werden.
- Die Ersteingabe und die Kontrolleingabe eines Stimmzettels müssen stets von unterschiedlichen Personen/Teams vorgenommen werden (4 Augen Prinzip)
- Die Zahl der eingegebenen Stimmzettel muss mit der tatsächlich in der Urne vorhandenen Anzahl Stimmzettel vergleichen werden
- Die im Protokoll vermerkten Stimmzettelinhalte müssen Stichprobenartig gegen die tatsächlich vorliegenden Stimmzettel gegengeprüft werden.
- Das Auszählprotokoll muss ausgedruckt und zusammen mit den Originalstimmzetteln archiviert werden

##Starten des Servers
Vorbedingung: Es muss eine aktuelle Version der Java Runtime Environment (JRE) >= 1.8 installiert sein.

Je nach Betriebssystem kann der Server mittels eines Doppelklicks auf die Datei singleTransferableVoteElections-web-3.0.jar oder mittels `java -jar singleTransferableVoteElections-web-3.0.jar` gestartet werden. Nach dem Start gibt dieser eine Liste der URLs aus, unter denen er erreichbar ist.

## Benutzung der Eingabe- und Berechnungsmasken
- Nummerierung aller Stimmzettel
- Eingabe eines Stimmzetellayouts
- Ersteingabe der Stimmzettel
-- keine Angst vor fehleingaben, die werden nach dem zweiten Durchgang automatisch erkannt
- Zweiteingabe der Stimmzettel
- Auswertungslauf
-- Optional: Fehlerkorrektur, dann wieder zur Erst- und zweiteingabe zurück
--- Stimmzettelnummern unbedingt abschreiben
-- Wahlergebnisse anzeigen
--- Unbedingt ausdrucken, da flüchtig
--- Der Autor freut sich über zugesandte JSON- und SVG-Dateien zum optimieren der grafischen Darstellungen
--- 
