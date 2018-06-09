all:
	./gradlew buildDocker
	docker push briungri/bidtactoe-backend
