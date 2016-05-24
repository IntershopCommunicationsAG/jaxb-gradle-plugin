#!/bin/bash
# This script will build the project.

export JAVA_OPTS="-Xmx1024M -XX:MaxPermSize=512M -XX:ReservedCodeCacheSize=512M"
export GRADLE_OPTS="-Dorg.gradle.daemon=true"

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    if [ "$TRAVIS_TAG" == "" ]; then
        echo -e 'Build Branch for Release => Branch ['$TRAVIS_BRANCH'] and without Tag'
        ./gradlew test build -s
    else
        echo -e 'Build Branch for Release => Branch ['$TRAVIS_BRANCH'] Tag ['$TRAVIS_TAG']'
        ./gradlew -PrunOnCI=true test build :bintrayUpload :publishPlugins -s
    fi
else
    if [ "$TRAVIS_TAG" == "" ]; then
        echo -e 'Build Branch for Release => Branch ['$TRAVIS_BRANCH'] and without Tag'
        ./gradlew test build -s
    else
        echo -e 'Build Branch for Release => Branch ['$TRAVIS_BRANCH'] Tag ['$TRAVIS_TAG']'
        ./gradlew -PrunOnCI=true test build :bintrayUpload :publishPlugins -s
    fi
fi