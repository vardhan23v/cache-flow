#!/usr/bin/env bash
# Starts Store A (8081), Store B (8082) and Cache Flow (8080). Requires a running local MySQL 8.
# DB credentials: export DB_USER / DB_PASS (defaults: root / empty).
set -e
cd "$(dirname "$0")"
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/java}"
if [ -z "$JAVA" ]; then
  if java -version >/dev/null 2>&1; then JAVA=java; else JAVA=$(ls /opt/homebrew/opt/openjdk@21/bin/java /opt/homebrew/opt/openjdk/bin/java /usr/local/opt/openjdk/bin/java 2>/dev/null | head -1); fi
fi
pkill -f "store-app-1.0.0.jar|cache-flow-1.0.0.jar" && sleep 2 || true   # stop any previous run
mvn -q -DskipTests package
mkdir -p logs
"$JAVA" -jar store-app/target/store-app-1.0.0.jar --spring.profiles.active=a > logs/store-a.log 2>&1 &
"$JAVA" -jar store-app/target/store-app-1.0.0.jar --spring.profiles.active=b > logs/store-b.log 2>&1 &
sleep 12
"$JAVA" -jar cache-flow/target/cache-flow-1.0.0.jar > logs/cache-flow.log 2>&1 &
echo "Store A:    http://localhost:8081"
echo "Store B:    http://localhost:8082"
echo "Cache Flow: http://localhost:${PORT:-8080}   (logs in ./logs, stop with: pkill -f 'store-app-1.0.0.jar|cache-flow-1.0.0.jar')"
wait
