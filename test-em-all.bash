#!/usr/bin/env bash
set -euo pipefail

base_url="http://localhost:7001/product-composite"
response_file=$(mktemp)
trap 'rm -f "$response_file"' EXIT

check_response() {
    local product_id="$1"
    local expected_status="$2"
    local json_check="${3:-}"
    local actual_status

    actual_status=$(curl --silent --show-error \
        --output "$response_file" \
        --write-out '%{http_code}' \
        "$base_url/$product_id")

    if [[ "$actual_status" != "$expected_status" ]]; then
        printf 'FAIL /%s: expected HTTP %s, got %s\n' \
            "$product_id" "$expected_status" "$actual_status"
        cat "$response_file"
        printf '\n'
        exit 1
    fi

    if [[ -n "$json_check" ]] && ! jq -e "$json_check" "$response_file" >/dev/null; then
        printf 'FAIL /%s: unexpected JSON response\n' "$product_id"
        cat "$response_file"
        printf '\n'
        exit 1
    fi

    printf 'PASS /%s (HTTP %s)\n' "$product_id" "$actual_status"
}

check_response 1 200 \
    '.productId == 1 and .name == "name-1" and
     (.recommendations | length) == 3 and (.reviews | length) == 3'

check_response 13 404 \
    '.message == "No product found for productId: 13"'

check_response 113 200 \
    '.productId == 113 and
     .recommendations == [] and (.reviews | length) == 3'

check_response 213 200 \
    '.productId == 213 and
     (.recommendations | length) == 3 and .reviews == []'

check_response -1 422 \
    '.message == "Invalid productId: -1"'

check_response no-integer 400

printf 'All 6 checks passed.\n'
