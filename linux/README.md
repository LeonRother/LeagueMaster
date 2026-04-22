# LeagueMaster auf Linux starten

## Voraussetzungen

- Eine Linux-Distribution mit Paketmanager
- `sudo` fuer die automatische Installation
- OpenJDK 21 und Maven

## Einmalig vorbereiten

```bash
chmod +x linux/run-leaguemaster.sh
chmod +x linux/LeagueMaster.desktop
```

## Start per Klick

1. Lege `linux/LeagueMaster.desktop` und `linux/run-leaguemaster.sh` zusammen in einen Ordner.
2. Fuehre die Datei `LeagueMaster.desktop` per Doppelklick aus.
3. Falls Fedora nachfragt, erlaube das Starten der Datei als Anwendung.
4. Falls OpenJDK 21 oder Maven fehlen, fragt das Skript direkt nach der Installation.
5. Beim ersten Start baut das Skript das JAR automatisch mit `mvn package`.

## Start im Terminal

```bash
./linux/run-leaguemaster.sh
```

Das Skript prueft zuerst OpenJDK 21 und Maven. Falls etwas fehlt, erkennt es den Paketmanager und bietet die Installation direkt an.

Unterstuetzte Paketmanager:

- `apt-get`
- `dnf`
- `yum`
- `pacman`
- `zypper`

Danach baut es das Projekt automatisch, falls `target/LeagueMaster.jar` noch nicht existiert.
