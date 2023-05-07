./gradlew clean build -x test
docker build -t eds .
docker tag eds thangvip5432/eds
docker push thangvip5432/eds
kubectl apply -f k8s/server.yaml