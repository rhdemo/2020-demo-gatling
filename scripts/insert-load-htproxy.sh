
export SIMULATION=BonusSimulation

# Load Tester Params
export REPLICAS=5
export USERS=320
export GUESSES=120
export PERCENT_BAD_GUESSES=100
export WS_PROTOCOL=wss

export NAMESPACE=load-test-sf1
export OC_URL=https://api.summit-aws-sf1.openshift.redhatkeynote.com:6443
export SOCKET_ADDRESS=redhatkeynote.com/socket

make oc_login
make create-namespace
make deploy-load-test 


export NAMESPACE=load-test-lnd1
export OC_URL=https://api.summit-aws-lnd1.openshift.redhatkeynote.com:6443
export SOCKET_ADDRESS=redhatkeynote.com/socket

make oc_login
make create-namespace
make deploy-load-test 


export NAMESPACE=load-test-ny1
export OC_URL=https://api.summit-gcp-ny1.redhatgcpkeynote.com:6443
export SOCKET_ADDRESS=redhatkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test 

export NAMESPACE=load-test-ffm1
export OC_URL=https://api.summit-gcp-ffm1.redhatgcpkeynote.com:6443
export SOCKET_ADDRESS=redhatkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test 


export NAMESPACE=load-test-sp1
export OC_URL=https://api.summit-azr-sp1.redhatazurekeynote.com:6443/
export SOCKET_ADDRESS=redhatkeynote.com/socket
make oc_login
make create-namespace
make deploy-load-test


export NAMESPACE=load-test-syd1
export SOCKET_ADDRESS=redhatkeynote.com/socket
oc login --token=jM0KzVi0LNjFcnJfs0WmB2xH9RRH2dT3d5HI5c1Hp-4 --server=https://c100-e.au-syd.containers.cloud.ibm.com:30501
make create-namespace
make deploy-load-test