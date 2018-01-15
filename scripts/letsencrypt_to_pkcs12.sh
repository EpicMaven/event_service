#!/bin/sh
#
# Converts Let's Encrypt PEM files into PKCS12 for use with Event Service

NAME=event.nova-labs.org
PEM_DIR=/etc/letsencrypt/live/$NAME

openssl pkcs12 -export -in ${PEM_DIR}/fullchain.pem -inkey ${PEM_DIR}/privkey.pem -out ${NAME}.p12 -name $NAME -CAfile chain.pem -caname root



