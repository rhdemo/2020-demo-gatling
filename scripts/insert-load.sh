
export SIMULATION=E2ESimulation

# Load Tester Params
export REPLICAS=1
export USERS=1000
export GUESSES=1000
export PERCENT_BAD_GUESSES=100


export NAMESPACE=load-test-sf1
export OC_URL=https://api.summit-aws-sf1.openshift.redhatkeynote.com:6443
export SOCKET_ADDRESS=game-frontend.apps.summit-aws-sf1.openshift.redhatkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test 


export NAMESPACE=load-test-lnd1
export OC_URL=https://api.summit-aws-lnd1.openshift.redhatkeynote.com:6443
export SOCKET_ADDRESS=game-frontend.apps.summit-aws-lnd1.openshift.redhatkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test 


export NAMESPACE=load-test-ny
export OC_URL=https://api.summit-gcp-ny1.redhatgcpkeynote.com:6443
export SOCKET_ADDRESS=game-frontend.apps.summit-gcp-ny1.redhatgcpkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test 

export NAMESPACE=load-test-ffm1
export OC_URL=https://api.summit-gcp-ffm1.redhatgcpkeynote.com:6443
export SOCKET_ADDRESS=game-frontend.apps.summit-gcp-ffm1.redhatgcpkeynote.com/socket 
make oc_login
make create-namespace
make deploy-load-test 