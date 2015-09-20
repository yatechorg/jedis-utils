#!/bin/bash
# Inspired by http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

if [ "$TRAVIS_REPO_SLUG" == "yatechorg/jedis-utils" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  VERSION=`./gradlew -q getVersion`
  if [[ $VERSION != *SNAPSHOT* ]]; then
    JAVADOC_VER=$VERSION
  else
    JAVADOC_VER=snapshot
  fi
  echo -e "Publishing $JAVADOC_VER javadoc...\n"

  cp -R build/docs/javadoc $HOME/javadoc-$JAVADOC_VER

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/yatechorg/jedis-utils gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc/$JAVADOC_VER
  mkdir -p ./javadoc/$JAVADOC_VER
  cp -Rf $HOME/javadoc-$JAVADOC_VER/* ./javadoc/$JAVADOC_VER
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages (version=$VERSION)"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published $JAVADOC_VER javadoc to gh-pages.\n"
  
fi
