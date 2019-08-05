#!/bin/bash

GIT_COMMIT_LOG="$(git log --format=%s $TRAVIS_COMMIT_RANGE)"

echo " <b>Changelog for build ${TRAVIS_BUILD_NUMBER}</b>${NEWLINE}"

printf '%s\n' "$GIT_COMMIT_LOG" | while IFS= read -r line
do
  echo "- ${line}"
done