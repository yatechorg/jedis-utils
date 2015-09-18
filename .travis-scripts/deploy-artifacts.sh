#!/bin/bash
# Inspired by http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

if [ "${TRAVIS_PULL_REQUEST}" == "false" ] && [ "${TRAVIS_JDK_VERSION}" == "oraclejdk7" ]; then

  echo "Deploying artifacts..."
  ./gradlew publishSnapshotOrRelease

  RETVAL=$?
  if [ $RETVAL -eq 0 ]; then
    echo 'Completed publish!'
  else
    echo 'Publish failed.'
    return 1
  fi

fi
