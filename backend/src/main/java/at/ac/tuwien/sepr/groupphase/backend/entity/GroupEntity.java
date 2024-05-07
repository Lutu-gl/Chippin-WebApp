package at.ac.tuwien.sepr.groupphase.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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