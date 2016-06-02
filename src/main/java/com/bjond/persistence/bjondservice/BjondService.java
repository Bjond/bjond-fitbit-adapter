/*  Copyright (c) 2016
 *  by Bjönd, Inc., Boston, MA
 *
 *  This software is furnished under a license and may be used only in
 *  accordance with the terms of such license.  This software may not be
 *  provided or otherwise made available to any other party.  No title to
 *  nor ownership of the software is hereby transferred.
 *
 *  This software is the intellectual property of Bjönd Health, Inc.,
 *  and is protected by the copyright laws of the United States of America.
 *  All rights reserved internationally.
 *
 */


package com.bjond.persistence.bjondservice;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import lombok.Data;


/**
 * The persistent class for the person_data database table.
 * 
 */
@Entity
@Table(name="bjond_service")
@Data
@NamedQueries({
  @NamedQuery(name="BjondService.findAllByGroupId", query="SELECT s from BjondService s WHERE s.groupID = :id",
              hints={
                @QueryHint(name="org.hibernate.cacheable", value="true"),
                @QueryHint(name="org.hibernate.cacheMode", value="NORMAL")
              }),
})
public class BjondService {

    @Id
    private String id;

    @Column(name="group_id")
    private String groupID;

    @Column(name="endpoint")
    private String endpoint;


    public BjondService() {
        id = UUID.randomUUID().toString();
    }

    public static List<BjondService> findAllByGroupId (@NotNull(message="entityManager must not be null.") final EntityManager entityManager,
                                                       @NotNull(message="groupid must not be null.") final String groupid) {
        final TypedQuery<BjondService> query = entityManager.createNamedQuery("BjondService.findAllByGroupId", BjondService.class).setParameter("id", groupid);
        return query.getResultList();
    }

    // Why not constrain groupid to unique?
    public static BjondService findFirstByGroupId (@NotNull(message="entityManager must not be null.") final EntityManager entityManager,
                                                   @NotNull(message="groupID must not be null.") final String groupid) {
        final List<BjondService> results = BjondService.findAllByGroupId(entityManager, groupid);
        return (results.size() > 0) ? results.iterator().next() : null;
    }

    public static BjondService findOrMakeNewByGroupId (@NotNull(message="entityManager must not be null.") final EntityManager entityManager,
                                                       @NotNull(message="groupID must not be null.") final String groupid) {
        BjondService service = BjondService.findFirstByGroupId(entityManager, groupid);
        if (service == null) {
            service = new BjondService();
        }
        service.setGroupID(groupid);
        return service;
    }
}
