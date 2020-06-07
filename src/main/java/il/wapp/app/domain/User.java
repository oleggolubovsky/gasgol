package il.wapp.app.domain;

import il.wapp.app.dto.user.*;
import il.wapp.app.enums.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User implements Serializable {

    public User(String email) {
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "role")
    private UserRoles role;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "register_link")
    private String registerLink;

    @Column(name = "temp_token")
    private String tempToken;

    @Column(name = "login_date")
    private Date loginDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "corporation_id", nullable = false)
    private Corporation corporation;

    @OneToMany(mappedBy = "user")
    private List<ContactList> contactList;

    public UserDto toDto() {
        return UserDto.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .role(role.name())
                .status(status != null ? status.name() : null)
                .corporation(corporation.getName())
                //.contactList(contactList.stream().map(contactList -> contactList.toDto()).collect(Collectors.toList()))
                .build();
    }

    public UserRegisterDto toRegisterDto() {
        return UserRegisterDto.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .corporation(corporation.getName())
                .role(role.name())
                .status(status != null ? status.name() : null)
                .build();
    }

}
