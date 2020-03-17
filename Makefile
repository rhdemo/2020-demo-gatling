ENV_FILE := .env
include ${ENV_FILE}
export $(shell sed 's/=.*//' ${ENV_FILE})


oc_login:
	oc login ${OC_URL} -u ${OC_USER} -p ${OC_PASSWORD} --insecure-skip-tls-verify=true

build-image:
	sh build.sh
	docker build -t ${IMAGE} .

push-image:
	@echo Push Image
	docker push ${IMAGE}

clean-namespace: oc_login
	oc delete project ${NAMESPACE} --ignore-not-found=true 
	while oc get project ${NAMESPACE} &> /dev/null;do echo \"Waiting for ${NAMESPACE} to be deleted\";sleep 10;done


deploy-load-test: oc_login clean-namespace
	oc new-project ${NAMESPACE}
	oc process -f openshift/template.yaml -p $ USERS=${USERS} NAMESPACE=${NAMESPACE} GUESSES=${GUESSES} PERCENT_BAD_GUESSES=${PERCENT_BAD_GUESSES} IMAGE=${IMAGE} REPLICAS=${REPLICAS} SOCKET_ADDRESS=${SOCKET_ADDRESS} | oc apply -f -
