package com.bjond.persistence.bjondservice;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import com.bjond.persistence.json.schema.annotations.JsonTitle;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;

@Entity
@Table(name="group_configuration")
@Data
@NamedQueries({
	  @NamedQuery(name="GroupConfiguration.findByGroupId", query="SELECT g from GroupConfiguration g WHERE g.groupID = :id", hints={@QueryHint(name="org.hibernate.cacheable", value="true"), @QueryHint(name="org.hibernate.cacheMode", value="NORMAL")}),
	  })
public class GroupConfiguration {
	
	@Id
    private String id;

    @Column(name="group_id")
    private String groupID;
    
    @JsonTitle(title="Slack Team")
	@JsonPropertyDescription("The Slack team with which you want to interract.")
    @Column(name="slack_team")
    private String slackTeam;
    
    public static GroupConfiguration getByGroupId (@NotNull(message="entityManager must not be null.") final EntityManager entityManager, @NotNull(message="groupid must not be null.") final String groupid) {
    	try {
    		final TypedQuery<GroupConfiguration> query = entityManager.createNamedQuery("GroupConfiguration.findByGroupId", GroupConfiguration.class).setParameter("id", groupid);
    		return query.getSingleResult();
    	}
    	catch(NoResultException ex) {
    		final GroupConfiguration newConfig = new GroupConfiguration();
    		newConfig.setId(UUID.randomUUID().toString());
    		newConfig.setGroupID(groupid);
    		return newConfig;
    	}
    }

}
