package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity to represent a Group. The members of the group are ApplicationUser.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupEntity {    // Do not call it group! It is a reserved word and causes errors when used
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // When creating a group, don't forget to create a pantry for it
    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private Pantry pantry;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_group",
        joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<ApplicationUser> users = new HashSet<>();
}