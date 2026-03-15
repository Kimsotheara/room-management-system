package com.room.management.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "AUTH_ACTIONS")
public class Action extends BaseEntity {

    @Id
    @Column(name = "action_code", length = 20)
    private String actionCode;

    @Column(name = "action_name", unique = true, nullable = false, length = 100)
    private String actionName;

    @Column(name = "action_description", length = 500)
    private String actionDescription;

    @Column(name = "requires_confirmation", columnDefinition = "boolean default false")
    private Boolean requiresConfirmation = false;

    @Column(name = "is_system_action", columnDefinition = "boolean default false")
    private Boolean isSystemAction = false;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive = true;

    public static final Set<String> READ_ONLY_ACTIONS = Set.of("READ", "LIST", "VIEW", "SEARCH", "EXPORT");
    public static final Set<String> WRITE_ACTIONS = Set.of("CREATE", "UPDATE", "DELETE", "MANAGE", "IMPORT");

    public boolean isReadOnlyAction() {
        return actionCode != null && READ_ONLY_ACTIONS.contains(actionCode.toUpperCase());
    }

    public boolean isWriteAction() {
        return actionCode != null && WRITE_ACTIONS.contains(actionCode.toUpperCase());
    }
}
