#!/bin/bash

init-var-solr
precreate-core authority /opt/solr/server/solr/configsets/authority
precreate-core oai /opt/solr/server/solr/configsets/oai
precreate-core search /opt/solr/server/solr/configsets/search
precreate-core statistics /opt/solr/server/solr/configsets/statistics
exec solr -f
