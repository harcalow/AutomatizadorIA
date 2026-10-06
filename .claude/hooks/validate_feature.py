"""Hook PostToolUse: valida reglas de trazabilidad ISTQB en archivos .feature.

Sale con código 2 (el mensaje vuelve a Claude para que corrija) si el archivo:
- no empieza con "# language: es"
- la Característica no tiene etiqueta @REQ-NNN
- algún Escenario / Esquema del escenario no tiene @TC-NNN o @prioridad-alta|media|baja
- usa un @TC-NNN que ya existe en otro .feature
"""
import json
import re
import sys
from pathlib import Path

SCENARIO = re.compile(r"^\s*(Escenario|Esquema del escenario|Ejemplo)\s*:")
FEATURE = re.compile(r"^\s*Característica\s*:")
TC = re.compile(r"@TC-\d{3}\b")
REQ = re.compile(r"@REQ-\d{3}\b")
PRIORITY = re.compile(r"@prioridad-(alta|media|baja)\b")


def tags_above(lines, index):
    tags = []
    i = index - 1
    while i >= 0 and (lines[i].strip().startswith("@") or lines[i].strip().startswith("#") or not lines[i].strip()):
        if lines[i].strip().startswith("@"):
            tags.append(lines[i])
        elif not lines[i].strip() and tags:
            break
        i -= 1
    return " ".join(tags)


def main():
    sys.stdin.reconfigure(encoding="utf-8")
    sys.stderr.reconfigure(encoding="utf-8")
    payload = json.load(sys.stdin)
    file_path = (payload.get("tool_input") or {}).get("file_path", "")
    if not file_path.endswith(".feature"):
        return 0

    path = Path(file_path)
    if not path.exists():
        return 0
    lines = path.read_text(encoding="utf-8").splitlines()
    errors = []

    first = next((l.strip() for l in lines if l.strip()), "")
    if first != "# language: es":
        errors.append('La primera línea debe ser "# language: es".')

    own_tcs = set()
    for i, line in enumerate(lines):
        if FEATURE.match(line) and not REQ.search(tags_above(lines, i)):
            errors.append(f"Línea {i + 1}: la Característica no tiene etiqueta @REQ-NNN.")
        if SCENARIO.match(line):
            tags = tags_above(lines, i)
            tc = TC.findall(tags)
            if not tc:
                errors.append(f"Línea {i + 1}: escenario sin etiqueta @TC-NNN.")
            if not PRIORITY.search(tags):
                errors.append(f"Línea {i + 1}: escenario sin @prioridad-alta|media|baja.")
            for t in tc:
                if t in own_tcs:
                    errors.append(f"Línea {i + 1}: {t} repetido en el mismo archivo.")
                own_tcs.add(t)

    features_dir = path.parent
    for other in features_dir.rglob("*.feature"):
        if other.resolve() == path.resolve():
            continue
        dup = own_tcs & set(TC.findall(other.read_text(encoding="utf-8")))
        if dup:
            errors.append(f"{', '.join(sorted(dup))} ya existe(n) en {other.name}; usa IDs nuevos.")

    if errors:
        print(f"Trazabilidad ISTQB - {path.name}:\n- " + "\n- ".join(errors), file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
