#!/bin/bash

# Stop all services

echo "Stopping all services..."

if [ -d ".pids" ]; then
    for pidfile in .pids/*.pid; do
        if [ -f "$pidfile" ]; then
            pid=$(cat "$pidfile")
            service=$(basename "$pidfile" .pid)
            echo "🛑 Stopping $service (PID: $pid)"
            kill $pid 2>/dev/null || echo "  Process already stopped"
            rm "$pidfile"
        fi
    done
    rmdir .pids 2>/dev/null
fi

echo ""
echo "✅ All services stopped"
