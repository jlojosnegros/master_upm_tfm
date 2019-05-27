#!/bin/sh
docker build . -t user-categories
echo
echo
echo "To run the docker container execute:"
echo "    $ docker run -p 8080:8080 user-categories"
