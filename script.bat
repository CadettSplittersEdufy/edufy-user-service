echo off
echo Stopping edufy-user-service
docker stop edufy-user-service
echo Deleting container edufy-user-service
docker rm edufy-user-service
echo Deleting image edufy-user-service
docker rmi edufy-user-service
echo Running mvn package
call mvn package -DskipTests
echo Creating image edufy-user-service
docker build -t edufy-user-service .
echo Creating and running container edufy-user-service
docker run -d -p 4515:4515 --name edufy-user-service --network cadett-splitters-net edufy-user-service
echo Done!