#!/bin/bash
# Inspired by http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

if [ "$TRAVIS_REPO_SLUG" == "yatechorg/jedis-utils" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R build/docs/javadoc $HOME/javadoc-snapshot

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/yatechorg/jedis-utils gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc/snapshot
  mkdir -p ./javadoc/snapshot
  cp -Rf $HOME/javadoc-snapshot/* ./javadoc/snapshot
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"
  
fi
