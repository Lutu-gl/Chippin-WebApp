package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity to represent a Group. The members of the group are ApplicationUser.
 */
@Entity
@Getter
@Setter
public class GroupEntity {    // Do not call it group! It is a reserved word and causes errors when used
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Pantry pantry;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @ManyToMany
    @JoinTable(
        name = "user_group",
        joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<ApplicationUser> users = new HashSet<>();

    public GroupEntity() {
        this.pantry = new Pantry();
        this.pantry.setGroup(this);
    }

    public GroupEntity(String groupName) {
        this.groupName = groupName;
        this.pantry = new Pantry();
        this.pantry.setGroup(this);
    }

    public GroupEntity(Long id, String groupName, Set<ApplicationUser> users) {
        this.id = id;
        this.groupName = groupName;
        this.pantry = new Pantry();
        this.pantry.setGroup(this);
        this.users = users;
    }

    @Builder
    public GroupEntity(Long id, Pantry pantry, String groupName, Set<ApplicationUser> users) {
        this.id = id;
        this.pantry = pantry;
        if (this.pantry != null) {
            this.pantry.setGroup(this);
        }
        this.groupName = groupName;
        this.users = users != null ? users : new HashSet<>();
    }

    @Builder
    public GroupEntity(String groupName, Set<ApplicationUser> users) {
        this.groupName = groupName;
        this.pantry = new Pantry();
        this.pantry.setGroup(this);
        this.users = users;
    }
}