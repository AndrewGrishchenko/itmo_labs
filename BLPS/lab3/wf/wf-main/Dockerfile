FROM quay.io/wildfly/wildfly:41.0.0.Final-jdk21

USER root

COPY --chown=jboss:root standalone/configuration/standalone.xml \
    /opt/jboss/wildfly/standalone/configuration/standalone.xml

COPY --chown=jboss:root standalone/deployments/ \
    /opt/jboss/wildfly/standalone/deployments/

RUN mkdir -p /opt/app/config

COPY --chown=jboss:root config/users.xml \
    /opt/app/config/users.xml

USER jboss

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]