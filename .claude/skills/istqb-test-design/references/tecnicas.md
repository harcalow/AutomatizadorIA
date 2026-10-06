# Técnicas de diseño — ejemplos aplicados

## Partición de equivalencia
Requerimiento: "La edad debe estar entre 18 y 65".
| Partición | Tipo | Valor representativo |
|---|---|---|
| < 18 | inválida | 10 |
| 18–65 | válida | 30 |
| > 65 | inválida | 80 |
| no numérico | inválida | "abc" |
| vacío | inválida | "" |

## Valores límite (3 valores)
Para 18–65: 17, 18, 19 · 64, 65, 66.
En Gherkin → `Esquema del escenario` con `Ejemplos` (una fila por valor) y columna `condicion`
que explique el valor.

## Tabla de decisión
Requerimiento: "Envío gratis si el cliente es premium o la compra supera $100".
| | R1 | R2 | R3 | R4 |
|---|---|---|---|---|
| Premium | S | S | N | N |
| Compra > 100 | S | N | S | N |
| **Envío gratis** | X | X | X | – |
R1 y R2 se pueden colapsar (premium = S, compra irrelevante) si el riesgo lo permite.

## Transición de estados
Pedido: Creado → Pagado → Enviado → Entregado; Creado → Cancelado; Pagado → Cancelado.
- Transiciones válidas: 5 casos mínimos.
- Inválidas de riesgo: Enviado → Cancelado, Entregado → Pagado.

## Predicción de errores para formularios web
- Espacios al inicio/fin, solo espacios.
- Longitud máxima +1.
- Caracteres especiales / unicode / emojis.
- Inyección básica (`' OR 1=1 --`, `<script>`) — verificar que se rechaza o escapa, sin dañar el SUT.
- Doble envío del formulario.
- Botón atrás del navegador tras enviar.
