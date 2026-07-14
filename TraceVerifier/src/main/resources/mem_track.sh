#!/bin/bash
VM_PID="$1"
LOG_FILE=$2
EXTRA_COLS=$3

get_pid_mem() {
    local pid=$1 hwm
    if [ -f "/proc/$pid/status" ]; then
        hwm=$(grep VmHWM "/proc/$pid/status" 2>/dev/null | awk '{print $2}')
    fi
    if [ -z "$hwm" ]; then
        hwm=$(ps -o hwm= -p "$pid" 2>/dev/null | tr -d ' ')
    fi
    echo "${hwm:-N/A}"
}

get_pid_mem_rss() {
    local pid=$1 rss
    if [ -f "/proc/$pid/status" ]; then
        rss=$(grep VmRSS "/proc/$pid/status" 2>/dev/null | awk '{print $2}')
    fi
    if [ -z "$rss" ]; then
        rss=$(ps -o rss= -p "$pid" 2>/dev/null | tr -d ' ')
    fi
    echo "${rss:-N/A}"
}

is_alive() {
    kill -0 "$1" 2>/dev/null
}



# Wait for either server process or timeout
SERVER_PID=''
ticks=0
max_ticks=25
while [ -z "$SERVER_PID" ] && [ "$ticks" -lt "$max_ticks" ]; do
    if ! is_alive "$VM_PID"; then
        exit 0
    fi
    SERVER_PID=$(ps -eo pid,ppid,comm --no-headers 2>/dev/null | awk -v ppid="$VM_PID" '$2==ppid && $3=="server" {print $1; exit}')
    if [ -z "$SERVER_PID" ]; then
        sleep 0.2
        ticks=$((ticks + 1))
    fi
done

# Make sure server isn't dead.
if [ -z "$SERVER_PID" ]; then
    exit 0
fi

# Track memory. If any process dead, exit.
while true; do
    if ! is_alive $VM_PID; then
        exit 0
    fi
    if ! is_alive $SERVER_PID; then
        exit 0
    fi

    TIMESTAMP=$(date '+%Y-%m-%d_%H:%M:%S.%3N')
    VM_MEM=$(get_pid_mem_rss "$VM_PID")
    SRV_MEM=$(get_pid_mem "$SERVER_PID")
    # Replace | with {SPACE}|{SPACE}, don't want to do any parsing in printf itself, handles spaces and other chars in a way that makes parsing more difficult.
    PARSED_EXTRA=$(echo "$EXTRA_COLS" | sed 's/|/\ |\ /g')
    PARSED_MAIN=$(printf "%s | %s | %s | %s | %s " $TIMESTAMP $VM_PID $VM_MEM $SERVER_PID $SRV_MEM)
    echo "$PARSED_MAIN | $PARSED_EXTRA" >> "$LOG_FILE"

    # 2.5 Measurements a second in case there's a sudden peak.
    sleep 0.4
done