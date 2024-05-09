package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Entity to represent a Group. The members of the group are ApplicationUser.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEntity {    // Do not call it group! It is a reserved word and causes errors when used
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "`group_name`", nullable = false)
    private String groupName;


    @ManyToMany(mappedBy = "groups")
    private Set<ApplicationUser> users;
}