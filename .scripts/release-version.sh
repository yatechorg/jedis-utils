#!/usr/bin/env bash

SCRIPT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPT_HOME/..

VERSION=`./gradlew -q getVersion`
echo "Currently configured version: $VERSION"
read -p "Enter release version name and press [ENTER]: " RELEASE_VERSION

echo "Updating gradle.properties with release version $RELEASE_VERSION"
sed -i "s/${VERSION}/${RELEASE_VERSION}/g" gradle.properties

echo "Commiting version update"
git add gradle.properties
git commit -m "Change to release version $RELEASE_VERSION"

TAG="v$RELEASE_VERSION"
echo "Creating tag $TAG"
git tag -a -m "Release version $RELEASE_VERSION" $TAG

echo "Pushing changes and tag"
git push --follow-tags origin master

read -p "Enter next snapshot version name and press [ENTER]: " SNAPSHOT_VERSION

echo "Updating gradle.properties with snapshot version $SNAPSHOT_VERSION"
sed -i "s/${RELEASE_VERSION}/${SNAPSHOT_VERSION}/g" gradle.properties

echo "Commiting version update"
git add gradle.properties
git commit -m "Change to next snapshot version $SNAPSHOT_VERSION"

echo "Pushing changes"
git push origin master