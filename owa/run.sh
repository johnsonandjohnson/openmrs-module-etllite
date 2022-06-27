#!/bin/bash

HOME_DIR=~
ETL_REPO=$HOME_DIR/cfl/openmrs-module-etllite
ETL_OMOD=etllite-1.0.0-SNAPSHOT.omod
ETL_OMOD_PREFIX=etllite
CFL_REPO=$HOME_DIR/cfl/cfl-openmrs

MODULES_PATH=$HOME_DIR/.cfl-dev/modules
OWA_PATH=$HOME_DIR/.cfl-dev/owa

check_ownership_and_fix () {
  PATH_TO_CHECK=$1
  if [ $(stat -c '%U' $PATH_TO_CHECK) != $(whoami) ] || [ $(find $PATH_TO_CHECK ! -user $(whoami) | wc -l) -gt 0 ]; then
    sudo chown -R $(whoami):$(whoami) $PATH_TO_CHECK
    echo "Changed ownership of $PATH_TO_CHECK to $(whoami):$(whoami)"
  fi
}

mkdir -p $MODULES_PATH
mkdir -p $OWA_PATH &&

check_ownership_and_fix $ETL_REPO &&
check_ownership_and_fix $MODULES_PATH &&

cd $ETL_REPO &&
mvn clean install &&

rm -f $MODULES_PATH/$ETL_OMOD_PREFIX* &&
mv $ETL_REPO/omod/target/$ETL_OMOD $MODULES_PATH &&

cd $CFL_REPO/cfl/ &&
docker-compose down &&
docker-compose up --build -d &&
docker-compose logs -f ||

cd $ETL_REPO/owa
