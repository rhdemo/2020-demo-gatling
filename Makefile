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
	
delete-namespace: 
	oc delete project ${NAMESPACE} --ignore-not-found=true 

create-namespace: 
	oc new-project ${NAMESPACE}

run-locally:
	docker run --rm=true  -e GUESSES=${GUESSES} -e SOCKET_ADDRESS=${SOCKET_ADDRESS} -e USERS=${USERS} -e PERCENT_BAD_GUESSES=${PERCENT_BAD_GUESSES}  -e SIMULATION=${SIMULATION} ${IMAGE}

deploy-load-test:  
	oc project ${NAMESPACE}
	oc process -f openshift/template.yaml -p $ USERS=${USERS} NAMESPACE=${NAMESPACE} GUESSES=${GUESSES} PERCENT_BAD_GUESSES=${PERCENT_BAD_GUESSES} IMAGE=${IMAGE} REPLICAS=${REPLICAS} SOCKET_ADDRESS=${SOCKET_ADDRESS} SIMULATION=${SIMULATION} | oc apply -f -


remove-load-test:
	oc project ${NAMESPACE}
	oc process -f openshift/template.yaml -p $ USERS=${USERS} NAMESPACE=${NAMESPACE} GUESSES=${GUESSES} PERCENT_BAD_GUESSES=${PERCENT_BAD_GUESSES} IMAGE=${IMAGE} REPLICAS=${REPLICAS} SOCKET_ADDRESS=${SOCKET_ADDRESS} SIMULATION=${SIMULATION} | oc delete -f -