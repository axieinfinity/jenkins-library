# Run unit tests
test class="*":
  #!/usr/bin/env bash
  set -euxo pipefail
  coverage=$([[ {{coverage}} == "true" ]] && echo "jacocoTestReport" || echo "")
  ./gradlew test --tests '{{class}}' $coverage