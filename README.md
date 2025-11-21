# VBO Laps (Spring Boot)

A Spring Boot app that ingests Racelogic VBOX `.vbo` files, detects laps, and stores samples in an H2 database. It exposes REST endpoints to retrieve lap polylines so you can draw the overlay chart in any frontend.

## Build & Run

```bash
mvn spring-boot:run
```

Open API docs at http://localhost:8080/swagger-ui.html

## Import a file

```bash
curl -F "file=@Driver1.vbo" http://localhost:8080/api/import
# => { "sessionId": 1, "circuit": "...", "driver": "..." }
```

## Query laps
```bash
curl http://localhost:8080/api/sessions/1/laps
curl http://localhost:8080/api/sessions/1/overlay  # per-lap coordinate arrays
curl http://localhost:8080/api/laps/2/polyline     # one lap
```

## Notes
- Lap detection: proximity to the start/finish coordinate found in `[laptiming]` (if present). Fallback uses the first point.
- Coordinates are returned as `[lon, lat]` pairs so they drop into GeoJSON/Mapbox/Leaflet easily.
- DB is in-memory H2 by default; switch to Postgres/MySQL by changing `application.yml`.
