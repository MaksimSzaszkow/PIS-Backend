#!/bin/bash
case "$1" in
    test)
        npm run test:coverage
        ;;

    check-image)
        if docker image list | grep back; then
            docker rmi back
        fi
        ;;

    check-ps)
        if docker ps -a | grep back; then
            docker rm back --force
        fi
        ;;
esac
exit 0