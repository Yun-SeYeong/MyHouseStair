#!/bin/bash

./gradlew bootJar
scp -i ~/Documents/document/YSY/MyHouseStair/ssh-key-2024-05-31.key build/libs/MyHouseStair-0.0.1-SNAPSHOT.jar ubuntu@140.238.15.22:~/
