#!/usr/bin/env sh

set -eu

SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
PROJECT_ROOT="$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)"
JAR_PATH="$PROJECT_ROOT/target/LeagueMaster.jar"
REQUIRED_JAVA_MAJOR="21"

main() {
    ensure_prerequisites
    build_if_needed
    ensure_built_jar
    exec java -jar "$JAR_PATH"
}

ensure_prerequisites() {
    missing_java="false"
    missing_maven="false"

    if ! has_java_21; then
        missing_java="true"
    fi

    if ! command -v mvn >/dev/null 2>&1; then
        missing_maven="true"
    fi

    if [ "$missing_java" = "true" ] || [ "$missing_maven" = "true" ]; then
        install_missing_packages "$missing_java" "$missing_maven"
    fi
}

has_java_21() {
    if ! command -v java >/dev/null 2>&1; then
        return 1
    fi

    java_version_output="$(java -version 2>&1 || true)"
    printf '%s' "$java_version_output" | grep -q "\"$REQUIRED_JAVA_MAJOR"
}

install_missing_packages() {
    missing_java="$1"
    missing_maven="$2"

    package_manager="$(detect_package_manager)"
    if [ -z "$package_manager" ]; then
        print_manual_install_hint "$missing_java" "$missing_maven"
        exit 1
    fi

    packages="$(packages_for "$package_manager" "$missing_java" "$missing_maven")"
    install_command="$(install_command_for "$package_manager" "$packages")"

    if [ -z "$packages" ] || [ -z "$install_command" ]; then
        print_manual_install_hint "$missing_java" "$missing_maven"
        exit 1
    fi

    if ! command -v sudo >/dev/null 2>&1; then
        printf '%s\n' "Es fehlen benoetigte Pakete: $packages"
        printf '%s\n' "Bitte installiere sie manuell mit:"
        printf '%s\n' "$install_command"
        exit 1
    fi

    printf '%s\n' "Es fehlen benoetigte Pakete: $packages"
    printf '%s\n' "Folgender Befehl wird verwendet:"
    printf '%s\n' "$install_command"
    printf '%s' "Jetzt installieren? [j/N]: "
    read -r answer

    case "$answer" in
        j|J|y|Y|yes|YES)
            sh -c "$install_command"
            ;;
        *)
            printf '%s\n' "Abgebrochen. LeagueMaster wird nicht gestartet."
            exit 1
            ;;
    esac
}

detect_package_manager() {
    for manager in apt-get dnf yum pacman zypper; do
        if command -v "$manager" >/dev/null 2>&1; then
            printf '%s' "$manager"
            return
        fi
    done
}

packages_for() {
    manager="$1"
    missing_java="$2"
    missing_maven="$3"
    packages=""

    case "$manager" in
        apt-get)
            [ "$missing_java" = "true" ] && packages="$packages openjdk-21-jdk"
            [ "$missing_maven" = "true" ] && packages="$packages maven"
            ;;
        dnf|yum)
            [ "$missing_java" = "true" ] && packages="$packages java-21-openjdk"
            [ "$missing_maven" = "true" ] && packages="$packages maven"
            ;;
        pacman)
            [ "$missing_java" = "true" ] && packages="$packages jdk21-openjdk"
            [ "$missing_maven" = "true" ] && packages="$packages maven"
            ;;
        zypper)
            [ "$missing_java" = "true" ] && packages="$packages java-21-openjdk-devel"
            [ "$missing_maven" = "true" ] && packages="$packages maven"
            ;;
    esac

    printf '%s' "$(printf '%s' "$packages" | xargs)"
}

install_command_for() {
    manager="$1"
    packages="$2"

    case "$manager" in
        apt-get)
            printf '%s' "sudo apt-get update && sudo apt-get install -y $packages"
            ;;
        dnf)
            printf '%s' "sudo dnf install -y $packages"
            ;;
        yum)
            printf '%s' "sudo yum install -y $packages"
            ;;
        pacman)
            printf '%s' "sudo pacman -Sy --needed $packages"
            ;;
        zypper)
            printf '%s' "sudo zypper install -y $packages"
            ;;
    esac
}

print_manual_install_hint() {
    missing_java="$1"
    missing_maven="$2"

    printf '%s\n' "Die benoetigten Abhaengigkeiten konnten nicht automatisch installiert werden."
    if [ "$missing_java" = "true" ]; then
        printf '%s\n' "- Java 21 fehlt"
    fi
    if [ "$missing_maven" = "true" ]; then
        printf '%s\n' "- Maven fehlt"
    fi
    printf '%s\n' "Bitte installiere die Pakete mit dem Paketmanager deiner Distribution."
}

build_if_needed() {
    if [ -f "$JAR_PATH" ]; then
        return
    fi

    printf '%s\n' "Kein gebautes JAR gefunden. Starte 'mvn package'..."
    (
        cd "$PROJECT_ROOT"
        mvn package
    )
}

ensure_built_jar() {
    if [ -f "$JAR_PATH" ]; then
        return
    fi

    printf '%s\n' "Das JAR $JAR_PATH konnte nicht erzeugt werden."
    exit 1
}

main "$@"
