package core.domain.member.entity;

import jakarta.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private Integer age;
    private String role;

    public Member() {
    }

    public Member(String email, String password, Integer age) {
        this.email = email;
        this.password = password;
        this.age = age;
        this.role = RoleType.ROLE_MEMBER.name();
    }

    public Member(String email, String password, Integer age, String role) {
        this.email = email;
        this.password = password;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }


}
