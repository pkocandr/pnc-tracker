/*
 * Copyright 2022-2026 Red Hat, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.pnc.tracker.service;

import org.jboss.pnc.tracker.exception.TrackerException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class ArtifactoryProducer {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactoryProducer.class);

    private final Artifactory artifactory;

    public ArtifactoryProducer(
        @ConfigProperty(name = "tracker.artifactory.url") String url,
        @ConfigProperty(name = "tracker.artifactory.access-token") String accessToken)
        throws TrackerException {
        try {
            logger.info("Creating artifactory connection with url {}", url);
            artifactory = ArtifactoryClientBuilder.create()
                    .setAccessToken(accessToken)
                    .setUrl(url)
                    .build();
            logger.info(
                    "Running against Artifactory version {}",
                    artifactory.system().version().getVersion());
        } catch (Exception e) {
            throw new TrackerException("Fatal error contacting artifactory", e);
        }
    }

    @Produces
    public Artifactory produce() {
        return artifactory;
    }

    @PreDestroy
    public void cleanup() {
        logger.warn("Closing artifactory connection");
        artifactory.close();
    }
}
