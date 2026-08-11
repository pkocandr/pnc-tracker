/*
 * Copyright 2022-2026 Red Hat, Inc.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.pnc.tracker.service;

import org.jboss.pnc.tracker.model.DbPackageType;
import org.jboss.pnc.tracker.model.DbTrackedEntry;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.RepositoryHandle;
import org.jfrog.artifactory.client.model.PackageType;
import org.jfrog.artifactory.client.model.Repository;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArtifactoryConnector {

    @Inject
    Logger log;

    @Inject
    @ConfigProperty(name = "tracker.artifactory.pull-data", defaultValue = "false")
    boolean active;

    @Inject
    private Artifactory artifactory;

    /**
     * Fetches the package type of a repository from Artifactory based on its project and name.
     * <p>
     * The repository name in Artifactory is composed as {@code project-name}. This method executes
     * a synchronous network call to Artifactory to retrieve the repository metadata and maps the
     * remote {@link PackageType} to the internal {@link DbPackageType}.
     * </p>
     * <p>
     * This method enforces a fail-fast strategy: if the repository does not exist, lacks settings,
     * or uses an unsupported package type, an exception is thrown to abort tracking.
     * </p>
     *
     * @param project the project identifier of the repository
     * @param name the repository name
     * @return the resolved internal {@link DbPackageType}
     * @throws IllegalStateException if repository metadata or settings cannot be retrieved from Artifactory
     * @throws IllegalArgumentException if the remote package type is unsupported by the internal model
     */
    public DbPackageType fetchPackageType(String project, String name) {
        String repoName = project + "-" + name;

        try {
            RepositoryHandle repositoryHandle = artifactory.repositories().repository(repoName);
            Repository repo = repositoryHandle.get();

            if (repo == null || repo.getRepositorySettings() == null) {
                throw new IllegalStateException("Repository settings not found for repo: " + repoName);
            }

            PackageType artifactoryType = repo.getRepositorySettings().getPackageType();
            if (artifactoryType == null) {
                throw new IllegalStateException("Package type is null in Artifactory response for repo: " + repoName);
            }

            return mapToDbPackageType(artifactoryType, repoName);

        } catch (Exception e) {
            log.errorf("Failed to fetch or map package type for repo %s: %s", repoName, e.getMessage());
            // Fail fast: Re-throw or wrap in runtime exception to abort tracking
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Could not retrieve metadata for repo: " + repoName, e);
        }
    }

    /**
     * Explicitly maps supported JFrog Artifactory package types to internal DbPackageType enum.
     */
    private DbPackageType mapToDbPackageType(PackageType artifactoryType, String repoName) {
        String typeName = artifactoryType.name().toLowerCase();

        return switch (typeName) {
            case "maven" -> DbPackageType.MAVEN;
            case "npm" -> DbPackageType.NPM;
            case "rpm" -> DbPackageType.RPM;
            case "generic" -> DbPackageType.GENERIC;
            default -> throw new IllegalArgumentException(
                    String.format(
                            "Unsupported package type '%s' for repository '%s'. Tracking aborted.",
                            typeName,
                            repoName));
        };
    }

    /**
     * Queries Artifactory using AQL to fetch all tracked artifact downloads and uploads
     * associated with the specified tracking ID, converting them into {@link DbTrackedEntry} objects.
     *
     * @param trackingId the business tracking identifier (build content ID)
     * @return list of converted {@link DbTrackedEntry} entities ready for batch persistence
     */
    public List<DbTrackedEntry> fetchEntriesForReport(String trackingId) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isActive() {
        return active;
    }

}
