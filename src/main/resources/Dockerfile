# @dockerfile.Template@

FROM @dockerfile.registry@/rbkmoney/service-java:@dockerfile.base.service.tag@ as service-java
FROM @dockerfile.registry@/rbkmoney/build:@dockerfile.build.tag@ as build

COPY --from=service-java / /tmp/portage-root/

ENV ROOT=/tmp/portage-root
RUN git clone git://git.bakka.su/gentoo-mirror --depth 1 /usr/portage && emerge sys-devel/gcc -K

FROM @dockerfile.registry@/rbkmoney/service-java:@dockerfile.base.service.tag@
COPY --from=build /tmp/portage-root/ /

COPY @artifactId@-@version@.jar /opt/@artifactId@/@artifactId@.jar

WORKDIR /opt/@artifactId@
CMD ["java", "-Xmx256m", "-jar","/opt/@artifactId@/@artifactId@.jar"]

EXPOSE @exposed.ports@

MAINTAINER @project.maintainer@
LABEL com.rbkmoney.@artifactId@.parent=service-java \
    com.rbkmoney.@artifactId@.parent_tag=@dockerfile.base.service.tag@ \
    com.rbkmoney.@artifactId@.commit_id=@git.commit.id@ \
    com.rbkmoney.@artifactId@.branch=@git.branch@