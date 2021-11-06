FROM solr:8.8

WORKDIR /var/solr/data

# Mount our local Solr core configs so that they are available as Solr config sets on container
COPY ./dspace/solr/authority /opt/solr/server/solr/configsets/authority
COPY ./dspace/solr/oai /opt/solr/server/solr/configsets/oai
COPY ./dspace/solr/search /opt/solr/server/solr/configsets/search
COPY ./dspace/solr/statistics /opt/solr/server/solr/configsets/statistics

COPY ./scripts/solr.sh /start_solr.sh
USER root
RUN chown -R solr:solr /start_solr.sh
RUN chmod +x /start_solr.sh
USER solr

ENTRYPOINT ["/bin/bash", "-c", "/start_solr.sh"]