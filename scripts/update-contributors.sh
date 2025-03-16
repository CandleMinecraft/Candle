#!/usr/bin/env bash
set -e

# Check that jq is installed
if ! command -v jq &>/dev/null; then
  echo "Error: 'jq' is not installed. Please install 'jq' and re-run the script." >&2
  exit 1
fi

CONTRIBUTORS_FILE="../CONTRIBUTORS"
README_FILE="../README.md"
AUTHENTICATED="false"

# Function to check the GitHub rate limit.
check_rate_limit() {
  local response remaining
  if [ "$AUTHENTICATED" != "true" ]; then
    response=$(curl -s -H "User-Agent: update-contributors-script" "https://api.github.com/rate_limit")
  else
    response=$(curl -s -H "User-Agent: update-contributors-script" -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/rate_limit")
  fi
  remaining=$(echo "$response" | jq -r '.rate.remaining')
  if [ "$remaining" -eq 0 ]; then
    echo "GitHub API rate limit exceeded." >&2
    if [ "$AUTHENTICATED" != "true" ]; then
      echo "Please log in to increase your rate limit." >&2
      read -s -p "GitHub Personal Access Token: " GITHUB_TOKEN
      echo "" >&2
      AUTHENTICATED="true"
      # Re-check rate limit with authentication
      response=$(curl -s -H "User-Agent: update-contributors-script" -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/rate_limit")
      remaining=$(echo "$response" | jq -r '.rate.remaining')
      if [ "$remaining" -eq 0 ]; then
        echo "Even after authentication, GitHub API rate limit is exceeded. Please wait and try again later." >&2
        exit 1
      fi
    else
      echo "Even with authentication, GitHub API rate limit is exceeded. Please wait and try again later." >&2
      exit 1
    fi
  fi
}

# Check rate limit once before processing.
check_rate_limit

# Function to get the avatar URL for a contributor.
get_avatar_url() {
  local contributor="$1"
  local response
  if [ "$AUTHENTICATED" != "true" ]; then
    response=$(curl -s -H "User-Agent: update-contributors-script" "https://api.github.com/users/${contributor}")
  else
    response=$(curl -s -H "User-Agent: update-contributors-script" -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/users/${contributor}")
  fi

  # If a rate limit error is returned, try to prompt for credentials (if not already done)
  if echo "$response" | jq -e 'has("message") and (.message | test("rate limit exceeded"))' >/dev/null 2>&1; then
    if [ "$AUTHENTICATED" != "true" ]; then
      echo "GitHub API rate limit exceeded while fetching $contributor. Please log in to increase your rate limit." >&2
      read -s -p "GitHub Personal Access Token: " GITHUB_TOKEN
      echo "" >&2
      AUTHENTICATED="true"
      response=$(curl -s -H "User-Agent: update-contributors-script" -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/users/${contributor}")
      if echo "$response" | jq -e 'has("message") and (.message | test("rate limit exceeded"))' >/dev/null 2>&1; then
         echo "Even after authentication, GitHub API rate limit is exceeded for contributor ${contributor}. Please wait and try again later." >&2
         exit 1
      fi
    else
      echo "Even with authentication, GitHub API rate limit is exceeded for contributor ${contributor}. Please wait and try again later." >&2
      exit 1
    fi
  fi

  local avatar_url
  avatar_url=$(echo "$response" | jq -r '.avatar_url')
  echo "$avatar_url"
}

contributors_snippet=""

# Process the CONTRIBUTORS file:
# - Remove Windows carriage returns.
# - Skip header lines.
# - Remove any content in parentheses.
while IFS= read -r line || [ -n "$line" ]; do
  line="${line//$'\r'/}"
  trimmed="$(echo "$line" | xargs)"
  if [[ -z "$trimmed" ]] || \
     [[ "$trimmed" =~ ^# ]] || \
     [[ "$trimmed" =~ ^This\ file\ contains ]] || \
     [[ "$trimmed" =~ ^If\ you\ contribute ]] || \
     [[ "$trimmed" =~ ^After\ adding\ your\ name ]]; then
    continue
  fi

  # Remove parenthesized extra info (e.g. " (Extra Info)")
  contributor=$(echo "$trimmed" | sed -E 's/ \([^)]*\)//')
  
  # Check the rate limit before each API call.
  check_rate_limit
  
  avatar_url=$(get_avatar_url "$contributor")
  if [[ "$avatar_url" == "null" || -z "$avatar_url" ]]; then
    echo "Warning: GitHub avatar for '$contributor' not found – skipping." >&2
    continue
  fi

  contributors_snippet+=$(cat <<EOF
<a href="https://github.com/${contributor}"><img src="${avatar_url}" title="${contributor}" width="80" height="80" style="border-radius:20%"></a>
EOF
)
  contributors_snippet+="\n"
done < "$CONTRIBUTORS_FILE"

# Remove any extra newlines at the end of the snippet.
contributors_snippet=$(echo -n "$contributors_snippet" | sed ':a;N;$!ba;s/\n\+$/\n/')

# Update README.md by replacing the block between the markers.
# Desired format:
#
# [//]: contributors-start
# <a href="..."><img ...></a>
#
# [//]: contributors-end
awk -v block="$contributors_snippet" '
BEGIN { inblock = 0 }
{
  sub(/\r$/, "")
  if ($0 ~ /\[\/\/\]: contributors-start/) {
    print "[//]: contributors-start"
    # Remove trailing newlines from the block, then print it followed by exactly one blank line.
    gsub(/\n+$/, "", block)
    printf "%s\n\n", block
    inblock = 1
    next
  }
  if ($0 ~ /\[\/\/\]: contributors-end/) {
    print "[//]: contributors-end"
    inblock = 0
    next
  }
  if (!inblock) {
    print $0
  }
}
' "$README_FILE" > "${README_FILE}.tmp"

mv "${README_FILE}.tmp" "$README_FILE"

# Ensure there are no extra trailing newlines after the end marker.
perl -pi -e 's/\n+\z/\n/' "$README_FILE"

echo "Contributors updated in $README_FILE"
