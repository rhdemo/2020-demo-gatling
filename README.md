# Load tests

Other configuration is specific to each load test and passed as system
properties in `JAVA_OPTS` like the examples below.

# Running a load test locally

```shell
SIMULATION="E2ESimulation" JAVA_OPTS="-Dhost=DEV -Dusers=1 -Dguesses=10" ./run-gatling.sh
```

# Building the load test image

```shell
./build.sh
```

## Publishing to quay.io

```shell
docker push quay.io/redhatdemo/2020-load-test
```

# Running in OpenShift

Copy `example.env` to `.env` and change the parameters

Run `make deploy-load-test` to deploy all the contents