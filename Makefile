create-image:
	sh build.sh
	docker build -t quay.io/redhatdemo/2020-load-test .