#!/bin/sh

lein run test --nodes-file  ./nodes --ssh-private-key ~/.ssh/id_rsa --concurrency 4 $@