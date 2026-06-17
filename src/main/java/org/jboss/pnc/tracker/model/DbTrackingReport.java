/**
 * Copyright (C) 2022-2023 Red Hat, Inc. (https://github.com/Commonjava/indy-tracking-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.tracker.model;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Database entity representing the main tracking report.
 * <p>
 * The table structure is kept minimal as the primary key (tracking_id)
 * automatically provides the necessary unique constraints and database indexing.
 * </p>
 */
@Entity
@Table(name = "tracking_report")
public class DbTrackingReport extends PanacheEntityBase {

    @Id
    @Column(name = "tracking_id", length = 128)
    public String trackingId;

    @Column(name = "state", columnDefinition = "char(1)")
    public TrackingReportState state;

    public DbTrackingReport() {
    }

    /**
     * Finds a tracking report by its unique tracking key.
     *
     * @param trackingId the unique identifier of the tracking report to find
     * @return the matching {@link DbTrackingReport} instance, or {@code null} if no such report exists
     */
    public static DbTrackingReport findByKey(String trackingId) {
        return (DbTrackingReport) DbTrackingReport.find("trackingId", trackingId)
                .singleResultOptional()
                .orElse(null);
    }

    /**
     * Retrieves a list of unique tracking keys for all reports matching the specified state.
     *
     * @param state the report state to filter by (e.g., IN_PROGRESS, SEALED, CORRUPTED)
     * @return a list of matching tracking keys, or an empty list if no reports match the criteria
     */
    public static List<String> findAllKeys() {
        return getEntityManager()
                .createQuery("SELECT r.trackingId FROM DbTrackingReport r", String.class)
                .getResultList();
    }

    /**
     * Retrieves a list of unique tracking keys for all reports matching the specified state.
     *
     * @param state the report state to filter by (e.g., IN_PROGRESS, SEALED, CORRUPTED)
     * @return a list of matching tracking keys, or an empty list if no reports match the criteria
     */
    public static List<String> findKeysByType(TrackingReportState state) {
        return getEntityManager()
                .createQuery("SELECT r.trackingId FROM DbTrackingReport r WHERE r.state = ?1", String.class)
                .setParameter(1, state)
                .getResultList();
    }

    /**
     * Checks whether the tracking report contains any recorded entries.
     * <p>
     * This method performs a database-level count and does not load the entries collection into memory, making it
     * efficient even for reports with a large number of records.
     * </p>
     *
     * @param trackingId the unique identifier of the report to check.
     * @return {@code true} if the report contains at least one entry; {@code false} otherwise.
     */
    public static boolean hasEntries(String trackingId) {
        return getEntityManager()
                .createQuery("SELECT COUNT(e) FROM DbTrackingEntry e WHERE e.trackingRecord.trackingId = :id", Long.class)
                .setParameter("id", trackingId)
                .getSingleResult() > 0;
    }

}
