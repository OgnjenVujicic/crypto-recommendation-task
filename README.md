Starting application in docker:

docker build . -t recommendation-test-task:1.0.0

docker run -p 127.0.0.1:8080:8080/tcp recommendation-test-task:1.0.0