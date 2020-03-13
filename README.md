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

```shell
oc run load-test -it --rm=true --restart=Never --requests="cpu=2" --image=quay.io/redhatdemo/2020-load-test --image-pull-policy=Always --env="--env="SIMULATION="E2ESimulation" JAVA_OPTS=-Dhost=LIVE -Dusers=50 -Dguesses=15"

# If you want to see the Gatling report, in another terminal:
oc cp load-test:/results /tmp/e2eresults
firefox /tmp/e2eresults/*/index.html

# And then, in the original terminal, CTRL+C to kill the load-test pod
```
